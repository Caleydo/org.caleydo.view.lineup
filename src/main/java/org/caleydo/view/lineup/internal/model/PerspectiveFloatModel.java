/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.model;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.lineup.internal.model.data.DoubleDataAdapter;
import org.caleydo.vis.lineup.data.IDoubleInferrer;
import org.caleydo.vis.lineup.model.DoubleRankColumnModel;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public class PerspectiveFloatModel extends DoubleRankColumnModel implements IPerspectiveColumn {

	private final Perspective perspective;

	public PerspectiveFloatModel(Perspective perspective, Color color, PiecewiseMapping mapping,
			IDoubleInferrer missingValue) {
		super(new DoubleDataAdapter(perspective), new HeaderRenderer(perspective), color,
 new Color(.95f, .95f, .95f),
				mapping,
				missingValue,
 null);
		this.perspective = perspective;
	}

	public PerspectiveFloatModel(PerspectiveFloatModel copy) {
		super(copy);
		this.perspective = copy.perspective;
	}

	/**
	 * @return the perspective, see {@link #perspective}
	 */
	@Override
	public Perspective getPerspective() {
		return perspective;
	}

	@Override
	public PerspectiveFloatModel clone() {
		return new PerspectiveFloatModel(this);
	}
}
