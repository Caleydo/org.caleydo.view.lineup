/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.lineup.internal;

import org.caleydo.core.view.ARcpGLElementViewPart;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.view.lineup.internal.serial.SerializedLineUpView;

/**
 *
 * @author Samuel Gratzl
 *
 */
public class RcpGLLineUpView extends ARcpGLElementViewPart {

	public RcpGLLineUpView() {
		super(SerializedLineUpView.class);
	}

	@Override
	protected AGLElementView createView(IGLCanvas canvas) {
		return new GLLineUpView(canvas);
	}
}
