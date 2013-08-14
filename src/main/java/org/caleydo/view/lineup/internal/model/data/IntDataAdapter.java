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
import org.caleydo.vis.lineup.model.IRow;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class IntDataAdapter implements Function<IRow, Integer> {
	private final IDType colType;
	private final Iterable<Integer> cols;
	private final IDType rowType;
	private final ATableBasedDataDomain dataDomain;

	public IntDataAdapter(Perspective column) {
		this.cols = column.getVirtualArray();
		this.colType = column.getIdType();
		ATableBasedDataDomain d = (ATableBasedDataDomain) column.getDataDomain();
		this.rowType = d.getOppositeIDType(this.colType);
		this.dataDomain = d;
	}

	@Override
	public Integer apply(IRow in) {
		IDRow r = (IDRow) in;
		for (Integer id : r.getIDAs(rowType)) {
			for (Integer col : cols) {
				Number n = getRaw(col, id);
				if (n == null || Float.isNaN(n.floatValue()) || n.intValue() == Integer.MIN_VALUE)
					continue;
				return n.intValue();
			}
		}
		return Integer.valueOf(-1);
	}

	private Number getRaw(Integer col, Integer id) {
		if (dataDomain.getDimensionIDType() == colType) {
			return dataDomain.getTable().getRaw(col, id);
		} else {
			return dataDomain.getTable().getRaw(id, col);
		}
	}
}
