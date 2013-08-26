/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.ui;

import java.util.List;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IPopupLayer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.GLComboBox;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.lineup.internal.Resources;
import org.caleydo.view.lineup.internal.event.AddColumnEvent;
import org.caleydo.view.lineup.internal.model.PerspectiveCategoricalModel;
import org.caleydo.view.lineup.internal.model.PerspectiveFloatModel;
import org.caleydo.view.lineup.internal.model.PerspectiveIntegerModel;
import org.caleydo.view.lineup.internal.model.data.CategoricalDataAdapter;
import org.caleydo.view.lineup.internal.model.data.CategoricalGroupingAdapter;
import org.caleydo.vis.lineup.data.DoubleInferrers;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class ConfigurerPopup extends GLElementContainer {

	private final Object receiver;
	private IDType dataType;

	/**
	 *
	 */
	public ConfigurerPopup(Object receiver) {
		setLayout(GLLayouts.flowVertical(10));
		this.receiver = receiver;
		setRenderer(GLRenderers.fillRect(new Color(0.95f, .95f, .95f, 0.8f)));

		this.add(new HeaderRow());
	}

	/**
	 * @param added
	 */
	public void addAll(List<TablePerspective> added, IGLElementContext context) {
		for (TablePerspective t : added) {
			if (dataType.resolvesTo(t.getDimensionPerspective().getIdType())
					|| dataType.resolvesTo(t.getRecordPerspective().getIdType()))
				this.add(new ConfigurePerspectiveElement(t, inferPossibleTypes(t))); // opposite possible
		}

		if (size() > 1 && getParent() == null && context != null) {
			context.getPopupLayer().show(this, new Rect(Float.NaN, Float.NaN, 410, size() * 30),
					IPopupLayer.FLAG_ALL & ~IPopupLayer.FLAG_CLOSEABLE);
		} else {
			setSize(getSize().x(), size() * 30);
		}
	}

	/**
	 * @param t
	 * @return
	 */
	private List<EModelType> inferPossibleTypes(TablePerspective t) {
		Builder<EModelType> builder = ImmutableList.builder();
		boolean colMatch = dataType.resolvesTo(t.getDimensionPerspective().getIdType());
		boolean rowMatch = dataType.resolvesTo(t.getRecordPerspective().getIdType());
		EDataClass type = t
				.getDataDomain()
				.getTable()
				.getDataClass(t.getDimensionPerspective().getVirtualArray().get(0),
						t.getRecordPerspective().getVirtualArray().get(0));
		switch (type) {
		case REAL_NUMBER:
			builder.add(EModelType.AVERAGE_VALUE);
			break;
		case NATURAL_NUMBER:
			builder.add(EModelType.INTEGER_VALUE);
			break;
		case CATEGORICAL:
			builder.add(EModelType.CATEGORICAL);
			break;
		default:
			break;
		}
		if ((colMatch && t.getDimensionPerspective().getVirtualArray().getGroupList().size() > 1)
				|| (rowMatch && t.getRecordPerspective().getVirtualArray().getGroupList().size() > 1)) {
			builder.add(EModelType.CATEGORICAL_GROUP);
		}
		return builder.build();

	}

	/**
	 * @param removed
	 */
	public void removed(List<TablePerspective> removed) {
		for (ConfigurePerspectiveElement elem : Iterables.filter(this, ConfigurePerspectiveElement.class)) {
			TablePerspective perspective = elem.getTablePerspective();
			if (removed.contains(perspective)) {
				remove(elem);
				break;
			}
		}

		if (size() == 1 && context != null) {
			hide();
		}
	}

	/**
	 *
	 */
	private void hide() {
		context.getPopupLayer().hide(this);
	}

	/**
	 * @param type
	 */
	public void setDataType(IDType type) {
		this.dataType = type;
	}

	/**
	 * @param configurePerspectiveElement
	 */
	public void configured(ConfigurePerspectiveElement elem) {
		ARankColumnModel model = createModel(elem);
		EventPublisher.trigger(new AddColumnEvent(model).to(receiver));
		remove(elem);
		if (size() == 1) {
			hide();
		}
	}

	/**
	 * @return
	 */
	public ARankColumnModel createModel(ConfigurePerspectiveElement elem) {
		TablePerspective t = elem.getTablePerspective();
		boolean colMatch = dataType.resolvesTo(t.getDimensionPerspective().getIdType());
		Perspective matching = colMatch ? t.getDimensionPerspective() : t.getRecordPerspective();
		Perspective opposite = !colMatch ? t.getDimensionPerspective() : t.getRecordPerspective();

		Color color = elem.getColor();
		switch (elem.getType()) {
		case CATEGORICAL:
			return new PerspectiveCategoricalModel<String>(opposite, new CategoricalDataAdapter(opposite),
					getCategories(opposite), color);
		case CATEGORICAL_GROUP:
			ImmutableMap.Builder<Integer,String> builder = ImmutableMap.builder();
			for(Group g : matching.getVirtualArray().getGroupList()) {
				builder.put(g.getGroupIndex(),g.getLabel());
			}
			return new PerspectiveCategoricalModel<>(matching, new CategoricalGroupingAdapter(matching),
					builder.build(),
					color);
		case AVERAGE_VALUE:
			return new PerspectiveFloatModel(opposite, color, new PiecewiseMapping(0, 1), DoubleInferrers.fix(Float.NaN));
		case INTEGER_VALUE:
			return new PerspectiveIntegerModel(opposite, color);
		default:
			break;
		}
		throw new IllegalStateException();
	}

	/**
	 * @param opposite
	 * @return
	 */
	private ImmutableMap<String, String> getCategories(Perspective p) {
		ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) p.getDataDomain();
		Integer id = p.getVirtualArray().get(0);
		CategoricalClassDescription<?> desc;
		if (dataDomain.getRecordIDType() == p.getIdType()) {
			desc = (CategoricalClassDescription<?>) dataDomain.getTable().getDataClassSpecificDescription(0, id);
		} else {
			desc = (CategoricalClassDescription<?>) dataDomain.getTable().getDataClassSpecificDescription(id, 0);
		}
		assert desc != null;
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for (CategoryProperty<?> c : desc.getCategoryProperties()) {
			builder.put(c.getCategory().toString(), c.getCategoryName());
		}
		return builder.build();
	}

	private static class HeaderRow extends GLElementContainer {

		/**
		 * @param tablePerspective
		 */
		public HeaderRow() {
			super(GLLayouts.flowHorizontal(5));
			this.add(new GLElement(GLRenderers.drawText("Name", VAlign.CENTER)).setSize(180, -1));
			this.add(new GLElement(GLRenderers.drawText("Type", VAlign.CENTER)).setSize(100, -1));
			this.add(new GLElement(GLRenderers.drawText("Color", VAlign.CENTER)).setSize(100, -1));
			// this.add(new GLElement(GLRenderers.drawText("Color",VAlign.CENTER)).setSize(100, -1));
			setSize(326, 16);
		}
	}

	private enum EModelType {
		AVERAGE_VALUE, INTEGER_VALUE, CATEGORICAL, CATEGORICAL_GROUP
	}

	private static class ConfigurePerspectiveElement extends GLElementContainer implements ISelectionCallback {
		private final TablePerspective tablePerspective;
		private final GLComboBox<EModelType> typeUI;
		private final ColorChooserButton colorUI;

		/**
		 * @param tablePerspective
		 */
		public ConfigurePerspectiveElement(TablePerspective tablePerspective, List<EModelType> types) {
			super(GLLayouts.flowHorizontal(5));
			this.tablePerspective = tablePerspective;
			this.add(new GLElement(GLRenderers.drawText(tablePerspective.getLabel(), VAlign.LEFT, new GLPadding(5, 2)))
					.setSize(180, -1));
			typeUI = new GLComboBox<>(types, GLComboBox.DEFAULT, GLRenderers.fillRect(Color.WHITE));
			typeUI.setSelected(0);
			this.add(typeUI.setSize(100, -1));
			colorUI = new ColorChooserButton(Color.GRAY);
			this.add(colorUI.setSize(100, -1));
			this.add(new GLButton().setCallback(this).setRenderer(GLRenderers.fillImage(Resources.ICON_ADD))
					.setSize(16, -1));
			setSize(Float.NaN, 16);
		}

		@Override
		public void onSelectionChanged(GLButton button, boolean selected) {
			if (typeUI.getSelectedItem() == null)
				return;
			((ConfigurerPopup) getParent()).configured(this);
		}

		public Color getColor() {
			return colorUI.getColor();
		}

		public EModelType getType() {
			return typeUI.getSelectedItem();
		}

		/**
		 * @return the tablePerspective, see {@link #tablePerspective}
		 */
		public TablePerspective getTablePerspective() {
			return tablePerspective;
		}
	}

}
