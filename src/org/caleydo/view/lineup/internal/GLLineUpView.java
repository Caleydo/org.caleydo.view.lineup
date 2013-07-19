/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.lineup.internal;

import java.util.List;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.AGLElementDecorator;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.view.AMultiTablePerspectiveElementView;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElementView;
import org.caleydo.vis.rank.config.RankTableConfigBase;
import org.caleydo.vis.rank.config.RankTableUIConfigs;
import org.caleydo.vis.rank.layout.RowHeightLayouts;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.ui.RankTableUI;
import org.caleydo.vis.rank.ui.RankTableUIMouseKeyListener;

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

	public GLLineUpView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
		this.table = new RankTableModel(new RankTableConfigBase());

		this.table.add(new RankRankColumnModel());
		this.table.add(new StringRankColumnModel(GLRenderers.drawText("Label", VAlign.CENTER),
				StringRankColumnModel.DEFAULT));
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedLineUpView serializedForm = new SerializedLineUpView(this);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);

		RankTableUI root = new RankTableUI();
		root.init(table, RankTableUIConfigs.DEFAULT, RowHeightLayouts.UNIFORM, RowHeightLayouts.FISH_EYE);
		RankTableUIMouseKeyListener l = new RankTableUIMouseKeyListener(root.findBody());
		this.canvas.addMouseListener(l);
		this.canvas.addKeyListener(l);

		getRootDecorator().setContent(root);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		super.dispose(drawable);
	}

	@Override
	protected RankTableUI getContent() {
		return (RankTableUI) super.getContent();
	}

	@Override
	protected void applyTablePerspectives(AGLElementDecorator root, List<TablePerspective> all,
			List<TablePerspective> added, List<TablePerspective> removed) {
		// TODO Auto-generated method stub

	}



}
