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
import org.caleydo.view.lineup.internal.model.data.IntDataAdapter;
import org.caleydo.vis.rank.model.IntegerRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class PerspectiveIntegerModel extends IntegerRankColumnModel implements IPerspectiveColumn {

	private final Perspective perspective;

	public PerspectiveIntegerModel(Perspective perspective, Color color) {
		super(new HeaderRenderer(perspective), new IntDataAdapter(perspective), color, color.brighter(), NumberFormat
				.getInstance(Locale.ENGLISH));
		this.perspective = perspective;
	}

	public PerspectiveIntegerModel(PerspectiveIntegerModel copy) {
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
	public PerspectiveIntegerModel clone() {
		return new PerspectiveIntegerModel(this);
	}
}
