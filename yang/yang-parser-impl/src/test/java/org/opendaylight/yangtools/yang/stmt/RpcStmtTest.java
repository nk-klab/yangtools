/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.yangtools.yang.stmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.AnyXmlSchemaNode;
import org.opendaylight.yangtools.yang.model.api.ContainerSchemaNode;
import org.opendaylight.yangtools.yang.model.api.Module;
import org.opendaylight.yangtools.yang.model.api.RpcDefinition;
import org.opendaylight.yangtools.yang.parser.spi.meta.ReactorException;
import org.opendaylight.yangtools.yang.parser.stmt.reactor.CrossSourceStatementReactor;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.YangInferencePipeline;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.YangStatementSourceImpl;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.effective.EffectiveSchemaContext;

public class RpcStmtTest {

    private static final YangStatementSourceImpl RPC_MODULE = new YangStatementSourceImpl("/model/baz.yang", false);
    private static final YangStatementSourceImpl IMPORTED_MODULE = new YangStatementSourceImpl("/model/bar.yang",
            false);

    @Test
    public void rpcTest() throws ReactorException {
        final CrossSourceStatementReactor.BuildAction reactor = YangInferencePipeline.RFC6020_REACTOR.newBuild();
        StmtTestUtils.addSources(reactor, RPC_MODULE, IMPORTED_MODULE);

        final EffectiveSchemaContext result = reactor.buildEffective();
        assertNotNull(result);

        final Module testModule = result.findModuleByName("baz", null);
        assertNotNull(testModule);

        assertEquals(1, testModule.getRpcs().size());

        final RpcDefinition rpc = testModule.getRpcs().iterator().next();
        assertEquals("get-config", rpc.getQName().getLocalName());

        final ContainerSchemaNode input = rpc.getInput();
        assertNotNull(input);
        assertEquals(2, input.getChildNodes().size());

        final ContainerSchemaNode container = (ContainerSchemaNode) input.getDataChildByName(QName.create(testModule.getQNameModule(), "source"));
        assertNotNull(container);
        AnyXmlSchemaNode anyXml = (AnyXmlSchemaNode) input.getDataChildByName(QName.create(testModule.getQNameModule(), "filter"));
        assertNotNull(anyXml);

        final ContainerSchemaNode output = rpc.getOutput();
        assertNotNull(output);
        assertEquals(1, output.getChildNodes().size());

        anyXml = (AnyXmlSchemaNode) output.getDataChildByName(QName.create(testModule.getQNameModule(), "data"));
        assertNotNull(anyXml);
    }
}
