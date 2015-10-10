/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.model.util.type;

import org.opendaylight.yangtools.yang.model.api.type.EmptyTypeDefinition;
import org.opendaylight.yangtools.yang.model.util.BaseTypes;

final class BaseEmptyType extends AbstractBaseType<EmptyTypeDefinition> implements EmptyTypeDefinition {
    static final BaseEmptyType INSTANCE = new BaseEmptyType();

    private BaseEmptyType() {
        super(BaseTypes.EMPTY_QNAME);
    }
}