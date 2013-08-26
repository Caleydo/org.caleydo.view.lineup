/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.model.data;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.view.lineup.internal.model.IDRow;
import org.caleydo.vis.lineup.data.ADoubleFunction;
import org.caleydo.vis.lineup.model.IRow;

/**
 * @author Samuel Gratzl
 *
 */
public class DoubleDataAdapter extends ADoubleFunction<IRow> {
	private final IDType colType;
	private final Iterable<Integer> cols;
	private final IDType rowType;
	private final ATableBasedDataDomain dataDomain;

	public DoubleDataAdapter(Perspective column) {
		this.cols = column.getVirtualArray();
		this.colType = column.getIdType();
		ATableBasedDataDomain d = (ATableBasedDataDomain) column.getDataDomain();
		this.rowType = d.getOppositeIDType(this.colType);
		this.dataDomain = d;
	}

	@Override
	public double applyPrimitive(IRow in) {
		IDRow r = (IDRow) in;
		double s = 0;
		int c = 0;
		for (Integer id : r.getIDAs(rowType)) {
			for (Integer col : cols) {
				try {
					float si = dataDomain.getNormalizedValue(colType, col, rowType, id);
					if (!Float.isNaN(si)) {
						c++;
						s += si;
					}
				} catch (IndexOutOfBoundsException e) {
					System.err.println(e);
				}
			}
		}
		return c == 0 ? Double.NaN : s / c;
	}
}
