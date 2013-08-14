/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.model;

import java.text.NumberFormat;
import java.util.Locale;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.lineup.internal.model.data.FloatDataAdapter;
import org.caleydo.vis.lineup.data.IFloatInferrer;
import org.caleydo.vis.lineup.model.FloatRankColumnModel;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public class PerspectiveFloatModel extends FloatRankColumnModel implements IPerspectiveColumn {

	private final Perspective perspective;

	public PerspectiveFloatModel(Perspective perspective, Color color, PiecewiseMapping mapping,
			IFloatInferrer missingValue) {
		super(new FloatDataAdapter(perspective), new HeaderRenderer(perspective), color,
 new Color(.95f, .95f, .95f),
				mapping,
				missingValue,
				NumberFormat.getInstance(Locale.ENGLISH));
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
