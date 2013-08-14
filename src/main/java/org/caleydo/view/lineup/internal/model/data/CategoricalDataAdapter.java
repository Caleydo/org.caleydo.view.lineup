/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.model.data;

import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.view.lineup.internal.model.IDRow;
import org.caleydo.vis.lineup.model.IRow;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoricalDataAdapter implements Function<IRow, Set<String>> {
	private final IDType colType;
	private final Iterable<Integer> cols;
	private final IDType rowType;
	private final ATableBasedDataDomain dataDomain;

	public CategoricalDataAdapter(Perspective column) {
		this.cols = column.getVirtualArray();
		this.colType = column.getIdType();
		ATableBasedDataDomain d = (ATableBasedDataDomain) column.getDataDomain();
		this.rowType = d.getOppositeIDType(this.colType);
		this.dataDomain = d;
	}

	@Override
	public Set<String> apply(IRow in) {
		IDRow r = (IDRow) in;
		Builder<String> builder = ImmutableSet.builder();
		for (Integer id : r.getIDAs(rowType)) {
			for (Integer col : cols) {
				Object raw = getRaw(col, id);
				if (raw == null)
					continue;
				builder.add(raw.toString());
			}
		}
		return builder.build();
	}

	private Object getRaw(Integer col, Integer id) {
		if (dataDomain.getDimensionIDType() == colType) {
			return dataDomain.getTable().getRaw(col, id);
		} else {
			return dataDomain.getTable().getRaw(id, col);
		}
	}
}
