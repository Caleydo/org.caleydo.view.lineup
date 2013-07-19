/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.lineup.internal;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;

/**
 *
 * @author Samuel Gratzl
 * 
 */
@XmlRootElement
@XmlType
public class SerializedLineUpView extends ASerializedMultiTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedLineUpView() {
	}

	public SerializedLineUpView(IMultiTablePerspectiveBasedView view) {
		super(view);
	}

	@Override
	public String getViewType() {
		return GLLineUpView.VIEW_TYPE;
	}
}
