/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.model;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

/**
 * @author Samuel Gratzl
 *
 */
public class HeaderRenderer implements IGLRenderer {
	private final Perspective perspective;

	public HeaderRenderer(Perspective perspective) {
		this.perspective = perspective;
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.drawText(perspective, 0, 0, w, h);
	}

}
