/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.event;

import org.caleydo.core.event.ADirectedEvent;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Samuel Gratzl
 *
 */
public class ColorChosenEvent extends ADirectedEvent {
	private final RGB rgb;

	public ColorChosenEvent(RGB rgb) {
		this.rgb = rgb;
	}

	/**
	 * @return the rgb, see {@link #rgb}
	 */
	public RGB getRgb() {
		return rgb;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
