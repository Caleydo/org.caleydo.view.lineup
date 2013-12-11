/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.lineup.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.id.IDType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.GLThreadListenerWrapper;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementDecorator;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.view.AMultiTablePerspectiveElementView;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElementView;
import org.caleydo.view.lineup.internal.event.AddColumnEvent;
import org.caleydo.view.lineup.internal.event.RemovePerspectiveEvent;
import org.caleydo.view.lineup.internal.model.IDRow;
import org.caleydo.view.lineup.internal.model.IPerspectiveColumn;
import org.caleydo.view.lineup.internal.serial.SerializedLineUpView;
import org.caleydo.view.lineup.internal.ui.ConfigurerPopup;
import org.caleydo.vis.lineup.config.RankTableConfigBase;
import org.caleydo.vis.lineup.config.RankTableUIConfigs;
import org.caleydo.vis.lineup.layout.RowHeightLayouts;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.RankRankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;
import org.caleydo.vis.lineup.ui.RankTableKeyListener;
import org.caleydo.vis.lineup.ui.RankTableUI;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * basic view based on {@link GLElement} with a {@link ASingleTablePerspectiveElementView}
 *
 * @author Samuel Gratzl
 *
 */
public class GLLineUpView extends AMultiTablePerspectiveElementView {
	private static final Logger log = Logger.create(GLLineUpView.class);
	public static final String VIEW_TYPE = "org.caleydo.view.lineup";
	public static final String VIEW_NAME = "LineUp";

	private final RankTableModel table;
	private final ConfigurerPopup configurer;
	private IGLKeyListener keyListener;

	public GLLineUpView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
		this.table = new RankTableModel(new RankTableConfigBase());

		this.table.add(new RankRankColumnModel());
		this.table.add(new StringRankColumnModel(GLRenderers.drawText("Label", VAlign.CENTER),
				StringRankColumnModel.DEFAULT));
		this.table.addPropertyChangeListener(RankTableModel.PROP_DESTROYED, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ARankColumnModel m = (ARankColumnModel) evt.getOldValue();
				onDestroyed(m);
			}
		});

		configurer = new ConfigurerPopup(this);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedLineUpView serializedForm = new SerializedLineUpView(this);
		return serializedForm;
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	protected void initScene() {
		super.initScene();

		RankTableUI root = new RankTableUI();
		root.init(table, RankTableUIConfigs.DEFAULT, RowHeightLayouts.UNIFORM, RowHeightLayouts.FISH_EYE);
		this.keyListener = GLThreadListenerWrapper.wrap(new RankTableKeyListener(table, root.findBody()));
		this.canvas.addKeyListener(eventListeners.register(keyListener));

		getRootDecorator().setContent(root);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		canvas.removeKeyListener(keyListener);
		super.dispose(drawable);
	}

	@Override
	protected RankTableUI getContent() {
		return (RankTableUI) super.getContent();
	}

	@ListenTo(sendToMe = true)
	private void onAddColumn(AddColumnEvent event) {
		table.add(event.getModel());
	}

	@ListenTo(sendToMe = true)
	private void onRemovePerspective(RemovePerspectiveEvent event) {
		Perspective perspective = event.getPerspective();
		for (TablePerspective t : this.getTablePerspectives()) {
			if (t.getDimensionPerspective().equals(perspective) || t.getRecordPerspective().equals(perspective)) {
				removeTablePerspective(t);
				break;
			}
		}
	}

	/**
	 * @param m
	 */
	protected void onDestroyed(ARankColumnModel m) {
		if (m instanceof IPerspectiveColumn) {
			onRemovePerspective(new RemovePerspectiveEvent(((IPerspectiveColumn) m).getPerspective()));
		}
	}

	@Override
	protected void applyTablePerspectives(GLElementDecorator root, List<TablePerspective> all,
			List<TablePerspective> added, List<TablePerspective> removed) {
		if (table.getDataSize() == 0 && !added.isEmpty()) {
			TablePerspective first = added.get(0);
			initRecords(first.getRecordPerspective());
		}
		configurer.addAll(added, this);
		configurer.removed(removed);

		if (!removed.isEmpty()) {
			Builder<Perspective> builder = ImmutableSet.builder();
			for(TablePerspective p : removed) {
				builder.add(p.getDimensionPerspective());
				builder.add(p.getRecordPerspective());
			}
			Set<Perspective> toRemove = builder.build();

			List<ARankColumnModel> toHide = new ArrayList<>();
			for (ARankColumnModel r : table.getFlatColumns()) {
				if (r instanceof IPerspectiveColumn && toRemove.contains(((IPerspectiveColumn) r).getPerspective()))
					toHide.add(r);
			}
			for (ARankColumnModel r : toHide) {
				if (!r.destroy() && !r.hide()) {
					log.warn("can't remove: " + r);
				}
			}
		}
	}


	private void initRecords(Perspective recordPerspective) {
		IDType type = recordPerspective.getIdType();
		List<IDRow> data = new ArrayList<>();
		ATableBasedDataDomain d = (ATableBasedDataDomain) recordPerspective.getDataDomain();
		for(Integer id : recordPerspective.getVirtualArray()) {
			data.add(new IDRow(type, id, d.getRecordLabel(id)));
		}
		table.addData(data);
		configurer.setDataType(type);
	}

}
