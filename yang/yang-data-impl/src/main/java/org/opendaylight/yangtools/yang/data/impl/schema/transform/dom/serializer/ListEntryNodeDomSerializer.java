/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.impl.schema.transform.dom.serializer;

import com.google.common.base.Preconditions;
import java.util.Collections;
import org.opendaylight.yangtools.yang.data.api.schema.DataContainerNode;
import org.opendaylight.yangtools.yang.data.impl.codec.xml.XmlDocumentUtils;
import org.opendaylight.yangtools.yang.data.impl.schema.transform.base.serializer.ListEntryNodeBaseSerializer;
import org.opendaylight.yangtools.yang.data.impl.schema.transform.base.serializer.NodeSerializerDispatcher;
import org.opendaylight.yangtools.yang.model.api.ListSchemaNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * Common class for list entry node serialization (MapEntry, UnkeyedListEntry nodes)
 *
 * @param <N>
 *            concrete entry node type
 */
abstract class ListEntryNodeDomSerializer<N extends DataContainerNode<?>> extends
        ListEntryNodeBaseSerializer<Element, N> {
    private final NodeSerializerDispatcher<Element> dispatcher;
    private final Document doc;

    ListEntryNodeDomSerializer(final Document doc, final NodeSerializerDispatcher<Element> dispatcher) {
        this.doc = Preconditions.checkNotNull(doc);
        this.dispatcher = Preconditions.checkNotNull(dispatcher);
    }

    @Override
    public Iterable<Element> serialize(ListSchemaNode schema, N node) {
        Element itemEl = XmlDocumentUtils.createElementFor(doc, node);

        for (Element element : super.serialize(schema, node)) {
            itemEl.appendChild(element);
        }

        return Collections.singletonList(itemEl);
    }

    @Override
    protected NodeSerializerDispatcher<Element> getNodeDispatcher() {
        return dispatcher;
    }
}
