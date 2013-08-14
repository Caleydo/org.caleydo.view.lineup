/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.model;

import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.MultiCategoricalRankColumnModel;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class PerspectiveCategoricalModel<T extends Comparable<T>> extends MultiCategoricalRankColumnModel<T> implements
		IPerspectiveColumn {
	private final Perspective perspective;

	public PerspectiveCategoricalModel(Perspective perspective, Function<IRow, Set<T>> data, Map<T, String> metaData,
			Color color) {
		super(new HeaderRenderer(perspective), data, metaData, color, new Color(.95f, .95f, .95f), "");
		this.perspective = perspective;
	}

	/**
	 * @return the perspective, see {@link #perspective}
	 */
	@Override
	public Perspective getPerspective() {
		return perspective;
	}
}
