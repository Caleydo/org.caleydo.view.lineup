/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.event;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.vis.lineup.model.ARankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class AddColumnEvent extends ADirectedEvent {
	private ARankColumnModel model;

	/**
	 * @param model
	 */
	public AddColumnEvent(ARankColumnModel model) {
		this.model = model;
	}

	/**
	 * @return the model, see {@link #model}
	 */
	public ARankColumnModel getModel() {
		return model;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
