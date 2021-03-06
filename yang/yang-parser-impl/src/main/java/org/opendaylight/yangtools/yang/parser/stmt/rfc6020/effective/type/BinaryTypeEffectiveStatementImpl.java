/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.yangtools.yang.parser.stmt.rfc6020.effective.type;

import org.opendaylight.yangtools.yang.model.api.meta.EffectiveStatement;
import org.opendaylight.yangtools.yang.model.api.stmt.TypeEffectiveStatement;
import org.opendaylight.yangtools.yang.model.api.stmt.TypeStatement;
import org.opendaylight.yangtools.yang.model.api.type.BinaryTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.LengthConstraint;
import org.opendaylight.yangtools.yang.model.util.type.InvalidLengthConstraintException;
import org.opendaylight.yangtools.yang.model.util.type.LengthRestrictedTypeBuilder;
import org.opendaylight.yangtools.yang.model.util.type.RestrictedTypes;
import org.opendaylight.yangtools.yang.parser.spi.meta.StmtContext;
import org.opendaylight.yangtools.yang.parser.spi.source.SourceException;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.TypeUtils;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.effective.DeclaredEffectiveStatementBase;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.effective.UnknownEffectiveStatementImpl;

public final class BinaryTypeEffectiveStatementImpl extends DeclaredEffectiveStatementBase<String, TypeStatement>
        implements TypeEffectiveStatement<TypeStatement> {
    private final BinaryTypeDefinition typeDefinition;

    public BinaryTypeEffectiveStatementImpl(
            final StmtContext<String, TypeStatement, EffectiveStatement<String, TypeStatement>> ctx,
            final BinaryTypeDefinition baseType) {
        super(ctx);

        final LengthRestrictedTypeBuilder<BinaryTypeDefinition> builder =
                RestrictedTypes.newBinaryBuilder(baseType, TypeUtils.typeEffectiveSchemaPath(ctx));

        for (EffectiveStatement<?, ?> stmt : effectiveSubstatements()) {
            if (stmt instanceof LengthEffectiveStatementImpl) {
                builder.setLengthAlternatives(((LengthEffectiveStatementImpl)stmt).argument());
            }
            if (stmt instanceof UnknownEffectiveStatementImpl) {
                builder.addUnknownSchemaNode((UnknownEffectiveStatementImpl)stmt);
            }
        }

        try {
            typeDefinition = builder.build();
        } catch (InvalidLengthConstraintException e) {
            final LengthConstraint c = e.getOffendingConstraint();
            throw new SourceException(ctx.getStatementSourceReference(), e, "Invalid length constraint: <%s, %s>",
                c.getMin(), c.getMax());
        }
    }

    @Override
    public BinaryTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }
}
