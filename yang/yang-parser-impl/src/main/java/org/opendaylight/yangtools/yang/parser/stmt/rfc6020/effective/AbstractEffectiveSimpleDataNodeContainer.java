/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.parser.stmt.rfc6020.effective;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.AugmentationSchema;
import org.opendaylight.yangtools.yang.model.api.AugmentationTarget;
import org.opendaylight.yangtools.yang.model.api.ConstraintDefinition;
import org.opendaylight.yangtools.yang.model.api.DataNodeContainer;
import org.opendaylight.yangtools.yang.model.api.DataSchemaNode;
import org.opendaylight.yangtools.yang.model.api.SchemaPath;
import org.opendaylight.yangtools.yang.model.api.UnknownSchemaNode;
import org.opendaylight.yangtools.yang.model.api.meta.DeclaredStatement;
import org.opendaylight.yangtools.yang.model.api.meta.EffectiveStatement;
import org.opendaylight.yangtools.yang.parser.spi.meta.StmtContext;
import org.opendaylight.yangtools.yang.parser.spi.meta.StmtContext.TypeOfCopy;

abstract class AbstractEffectiveSimpleDataNodeContainer<D extends DeclaredStatement<QName>> extends
        AbstractEffectiveDocumentedDataNodeContainer<QName, D> implements DataNodeContainer, AugmentationTarget,
        DataSchemaNode {

    private final SchemaPath path;

    // :FIXME should be private and final
    boolean augmenting;
    private final boolean addedByUses;
    private final boolean configuration;
    private final ConstraintDefinition constraints;

    private final Set<AugmentationSchema> augmentations;
    private final List<UnknownSchemaNode> unknownNodes;

    public AbstractEffectiveSimpleDataNodeContainer(final StmtContext<QName, D, ?> ctx) {
        super(ctx);

        this.path = ctx.getSchemaPath().get();
        this.constraints = EffectiveConstraintDefinitionImpl.forParent(this);
        this.configuration = ctx.isConfiguration();

        // initSubstatementCollectionsAndFields

        List<UnknownSchemaNode> unknownNodesInit = new ArrayList<>();
        Set<AugmentationSchema> augmentationsInit = new LinkedHashSet<>();
        for (EffectiveStatement<?, ?> effectiveStatement : effectiveSubstatements()) {
            if (effectiveStatement instanceof UnknownSchemaNode) {
                unknownNodesInit.add((UnknownSchemaNode) effectiveStatement);
            }
            if (effectiveStatement instanceof AugmentationSchema) {
                augmentationsInit.add((AugmentationSchema) effectiveStatement);
            }
        }
        this.unknownNodes = ImmutableList.copyOf(unknownNodesInit);
        this.augmentations = ImmutableSet.copyOf(augmentationsInit);

        // initCopyType
        List<TypeOfCopy> copyTypesFromOriginal = ctx.getCopyHistory();
        if (copyTypesFromOriginal.contains(TypeOfCopy.ADDED_BY_USES_AUGMENTATION)) {
            this.addedByUses = this.augmenting = true;
        } else {
            this.augmenting = copyTypesFromOriginal.contains(TypeOfCopy.ADDED_BY_AUGMENTATION);
            this.addedByUses = copyTypesFromOriginal.contains(TypeOfCopy.ADDED_BY_USES);
        }
    }

    @Override
    public QName getQName() {
        return path.getLastComponent();
    }

    @Override
    public SchemaPath getPath() {
        return path;
    }

    @Override
    public boolean isAugmenting() {
        return augmenting;
    }

    @Override
    public boolean isAddedByUses() {
        return addedByUses;
    }

    @Override
    public boolean isConfiguration() {
        return configuration;
    }

    @Override
    public ConstraintDefinition getConstraints() {
        return constraints;
    }

    @Override
    public Set<AugmentationSchema> getAvailableAugmentations() {
        return augmentations;
    }

    @Override
    public List<UnknownSchemaNode> getUnknownSchemaNodes() {
        return unknownNodes;
    }

}
