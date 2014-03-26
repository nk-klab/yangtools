/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.impl.schema.nodes;

import org.opendaylight.yangtools.concepts.Immutable;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.CompositeNode;
import org.opendaylight.yangtools.yang.data.api.InstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public abstract class AbstractImmutableNormalizedNode<K extends InstanceIdentifier.PathArgument,V>
        implements NormalizedNode<K, V>, Immutable {

    protected final K nodeIdentifier;
    protected final V value;

    protected AbstractImmutableNormalizedNode(K nodeIdentifier, V value) {
        this.nodeIdentifier = Preconditions.checkNotNull(nodeIdentifier, "nodeIdentifier");
        this.value = Preconditions.checkNotNull(value, "value");
    }

    @Override
    public final QName getNodeType() {
        return getIdentifier().getNodeType();
    }

    @Override
    public final K getIdentifier() {
        return nodeIdentifier;
    }

    @Override
    public final CompositeNode getParent() {
        throw new UnsupportedOperationException("Deprecated");
    }

    @Override
    public final QName getKey() {
        return getNodeType();
    }

    @Override
    public final V getValue() {
        return value;
    }

    @Override
    public final V setValue(V value) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public final String toString() {
        return Objects.toStringHelper(this)
                .add("nodeIdentifier", nodeIdentifier)
                .add("value", value)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractImmutableNormalizedNode)) return false;

        AbstractImmutableNormalizedNode<?, ?> that = (AbstractImmutableNormalizedNode<?, ?>) o;

        if (!nodeIdentifier.equals(that.nodeIdentifier)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeIdentifier.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}