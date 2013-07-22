/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.model;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.view.opengl.canvas.GLContextLocal;
import org.caleydo.vis.rank.model.ARow;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableSet;

/**
 * @author Samuel Gratzl
 *
 */
public class IDRow extends ARow implements ILabeled {
	private final IDType idType;
	private final int id;
	private final String label;
	/**
	 * shared cache id {@link IIDTypeMapper}
	 */
	private final GLContextLocal<Cache<IDType, IIDTypeMapper<Integer, Integer>>> mappers = GLContextLocal
			.getOrCreateShared("rowmapper", new SafeCallable<Cache<IDType, IIDTypeMapper<Integer, Integer>>>() {
				@Override
				public Cache<IDType, IIDTypeMapper<Integer, Integer>> call() {
					final IDMappingManager m = IDMappingManagerRegistry.get().getIDMappingManager(idType);
					return CacheBuilder.newBuilder().initialCapacity(5)
							.build(new CacheLoader<IDType, IIDTypeMapper<Integer, Integer>>() {
							@Override
								public IIDTypeMapper<Integer, Integer> load(IDType arg0) throws Exception {
									return m.getIDTypeMapper(idType, arg0);
							}
						});
				}

			});
	/**
	 * cache of foreign ids
	 */
	private final Cache<IDType, Set<Integer>> foreignIds = CacheBuilder.newBuilder().initialCapacity(5)
			.build(new CacheLoader<IDType, Set<Integer>>() {
				@Override
				public Set<Integer> load(IDType arg0) throws Exception {
					Set<Integer> r = mappers.get().getUnchecked(arg0).apply(id);
					return r == null ? Collections.<Integer> emptySet() : ImmutableSet.copyOf(r);
				}

			});

	public IDRow(IDType idType, int id, String label) {
		super();
		this.idType = idType;
		this.id = id;
		this.label = label;
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public int getId() {
		return id;
	}

	public Set<Integer> getIDAs(IDType type) {
		if (!type.resolvesTo(this.idType))
			return ImmutableSet.of();
		try {
			return foreignIds.get(type);
		} catch (ExecutionException e) {
			return ImmutableSet.of();
		}
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public IDType getIdType() {
		return idType;
	}


	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return label;
	}
}
