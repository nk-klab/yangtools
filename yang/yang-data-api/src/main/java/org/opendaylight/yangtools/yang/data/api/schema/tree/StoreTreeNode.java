/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.api.schema.tree;

import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.PathArgument;

import com.google.common.base.Optional;

/**
 * A tree node which has references to its child leaves. This are typically
 * internal non-data leaves, such as containers, lists, etc.
 *
 * @param <C> Final node type
 */
public interface StoreTreeNode<C extends StoreTreeNode<C>> {

    /**
     * Returns a direct child of the node
     *
     * @param child Identifier of child
     * @return Optional with node if the child is existing, {@link Optional#absent()} otherwise.
     */
    Optional<C> getChild(PathArgument child);
}
