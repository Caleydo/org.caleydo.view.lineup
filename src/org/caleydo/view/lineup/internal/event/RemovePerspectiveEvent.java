/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.event;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.ADirectedEvent;

/**
 * @author Samuel Gratzl
 *
 */
public class RemovePerspectiveEvent extends ADirectedEvent {
	private final Perspective perspective;

	/**
	 * @param perspective
	 */
	public RemovePerspectiveEvent(Perspective perspective) {
		super();
		this.perspective = perspective;
	}

	/**
	 * @return the perspective, see {@link #perspective}
	 */
	public Perspective getPerspective() {
		return perspective;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
