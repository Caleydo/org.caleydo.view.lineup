/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.model.data;

import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
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
public class CategoricalGroupingAdapter implements Function<IRow, Set<Integer>> {
	private final VirtualArray groups;

	public CategoricalGroupingAdapter(Perspective records) {
		this.groups = records.getVirtualArray();
	}

	@Override
	public Set<Integer> apply(IRow in) {
		IDRow r = (IDRow) in;
		IDType idType = groups.getIdType();
		Builder<Integer> builder = ImmutableSet.builder();

		for (Integer id : r.getIDAs(idType)) {
			List<Group> gs = groups.getGroupOf(id);
			if (gs == null)
				continue;
			for (Group g : gs) {
				builder.add(g.getGroupIndex());
			}
		}
		return builder.build();
	}
}
