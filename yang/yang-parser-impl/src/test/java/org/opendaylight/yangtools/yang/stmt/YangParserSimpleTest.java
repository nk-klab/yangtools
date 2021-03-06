/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.stmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.AnyXmlSchemaNode;
import org.opendaylight.yangtools.yang.model.api.ConstraintDefinition;
import org.opendaylight.yangtools.yang.model.api.ContainerSchemaNode;
import org.opendaylight.yangtools.yang.model.api.GroupingDefinition;
import org.opendaylight.yangtools.yang.model.api.LeafListSchemaNode;
import org.opendaylight.yangtools.yang.model.api.ListSchemaNode;
import org.opendaylight.yangtools.yang.model.api.Module;
import org.opendaylight.yangtools.yang.model.api.MustDefinition;
import org.opendaylight.yangtools.yang.model.api.SchemaPath;
import org.opendaylight.yangtools.yang.model.api.Status;
import org.opendaylight.yangtools.yang.model.api.TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.UsesNode;

public class YangParserSimpleTest {
    private final URI snNS = URI.create("urn:opendaylight:simple-nodes");
    private Date snRev;
    private final String snPref = "sn";

    private final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Set<Module> modules;

    @Before
    public void init() throws Exception {
        snRev = simpleDateFormat.parse("2013-07-30");
        modules = TestUtils.loadModules(getClass().getResource("/simple-test").toURI());
    }

    @Test
    public void testParseAnyXml() {
        final Module testModule = TestUtils.findModule(modules, "simple-nodes");
        final AnyXmlSchemaNode data = (AnyXmlSchemaNode) testModule.getDataChildByName(QName.create(testModule.getQNameModule(), "data"));
        assertNotNull("'anyxml data not found'", data);

        // test SchemaNode args
        final QName qname = data.getQName();
        assertEquals("data", qname.getLocalName());
        assertEquals(snNS, qname.getNamespace());
        assertEquals(snRev, qname.getRevision());
        assertEquals("anyxml desc", data.getDescription());
        assertEquals("data ref", data.getReference());
        assertEquals(Status.OBSOLETE, data.getStatus());
        assertEquals(0, data.getUnknownSchemaNodes().size());
        // test DataSchemaNode args
        assertFalse(data.isAugmenting());
        assertFalse(data.isConfiguration());
        final ConstraintDefinition constraints = data.getConstraints();
        assertEquals("class != 'wheel'", constraints.getWhenCondition().toString());
        final Set<MustDefinition> mustConstraints = constraints.getMustConstraints();
        assertEquals(2, constraints.getMustConstraints().size());

        final String must1 = "ifType != 'ethernet' or (ifType = 'ethernet' and ifMTU = 1500)";
        final String errMsg1 = "An ethernet MTU must be 1500";
        final String must2 = "ifType != 'atm' or (ifType = 'atm' and ifMTU <= 17966 and ifMTU >= 64)";
        final String errMsg2 = "An atm MTU must be  64 .. 17966";

        boolean found1 = false;
        boolean found2 = false;
        for (final MustDefinition must : mustConstraints) {
            if (must1.equals(must.toString())) {
                found1 = true;
                assertEquals(errMsg1, must.getErrorMessage());
            } else if (must2.equals(must.toString())) {
                found2 = true;
                assertEquals(errMsg2, must.getErrorMessage());
                assertEquals("anyxml data error-app-tag", must.getErrorAppTag());
                assertEquals("an error occured in data", must.getDescription());
                assertEquals("data must ref", must.getReference());
            }
        }
        assertTrue(found1);
        assertTrue(found2);

        assertTrue(constraints.isMandatory());
        assertTrue(0 == constraints.getMinElements());
        assertTrue(Integer.MAX_VALUE == constraints.getMaxElements());
    }

    @Test
    public void testParseContainer() {
        final Module test = TestUtils.findModule(modules, "simple-nodes");

        final ContainerSchemaNode nodes = (ContainerSchemaNode) test.getDataChildByName(QName.create(test.getQNameModule(), "nodes"));
        // test SchemaNode args
        final QName expectedQName = QName.create(snNS, snRev, "nodes");
        assertEquals(expectedQName, nodes.getQName());
        final SchemaPath expectedPath = TestUtils.createPath(true, snNS, snRev, snPref, "nodes");
        assertEquals(expectedPath, nodes.getPath());
        assertEquals("nodes collection", nodes.getDescription());
        assertEquals("nodes ref", nodes.getReference());
        assertEquals(Status.CURRENT, nodes.getStatus());
        assertEquals(0, nodes.getUnknownSchemaNodes().size());
        // test DataSchemaNode args
        assertFalse(nodes.isAugmenting());
        assertFalse(nodes.isConfiguration());

        // constraints
        final ConstraintDefinition constraints = nodes.getConstraints();
        assertEquals("class != 'wheel'", constraints.getWhenCondition().toString());
        final Set<MustDefinition> mustConstraints = constraints.getMustConstraints();
        assertEquals(2, constraints.getMustConstraints().size());

        final String must1 = "ifType != 'atm' or (ifType = 'atm' and ifMTU <= 17966 and ifMTU >= 64)";
        final String errMsg1 = "An atm MTU must be  64 .. 17966";
        final String must2 = "ifId != 0";

        boolean found1 = false;
        boolean found2 = false;
        for (final MustDefinition must : mustConstraints) {
            if (must1.equals(must.toString())) {
                found1 = true;
                assertEquals(errMsg1, must.getErrorMessage());
            } else if (must2.equals(must.toString())) {
                found2 = true;
                assertNull(must.getErrorMessage());
                assertNull(must.getErrorAppTag());
                assertNull(must.getDescription());
                assertNull(must.getReference());
            }
        }
        assertTrue(found1);
        assertTrue(found2);

        assertFalse(constraints.isMandatory());
        assertTrue(0 == constraints.getMinElements());
        assertTrue(Integer.MAX_VALUE == constraints.getMaxElements());
        assertTrue(nodes.isPresenceContainer());

        // typedef
        final Set<TypeDefinition<?>> typedefs = nodes.getTypeDefinitions();
        assertEquals(1, typedefs.size());
        final TypeDefinition<?> nodesType = typedefs.iterator().next();
        final QName typedefQName = QName.create(snNS, snRev, "nodes-type");
        assertEquals(typedefQName, nodesType.getQName());
        final SchemaPath nodesTypePath = TestUtils.createPath(true, snNS, snRev, snPref, "nodes", "nodes-type");
        assertEquals(nodesTypePath, nodesType.getPath());
        assertNull(nodesType.getDescription());
        assertNull(nodesType.getReference());
        assertEquals(Status.CURRENT, nodesType.getStatus());
        assertEquals(0, nodesType.getUnknownSchemaNodes().size());

        // child nodes
        // total size = 8: defined 6, inserted by uses 2
        assertEquals(8, nodes.getChildNodes().size());
        final LeafListSchemaNode added = (LeafListSchemaNode)nodes.getDataChildByName(QName.create(test.getQNameModule(), "added"));
        assertEquals(createPath("nodes", "added"), added.getPath());
        assertEquals(createPath("mytype"), added.getType().getPath());

        final ListSchemaNode links = (ListSchemaNode) nodes.getDataChildByName(QName.create(test.getQNameModule(), "links"));
        assertFalse(links.isUserOrdered());

        final Set<GroupingDefinition> groupings = nodes.getGroupings();
        assertEquals(1, groupings.size());
        final GroupingDefinition nodeGroup = groupings.iterator().next();
        final QName groupQName = QName.create(snNS, snRev, "node-group");
        assertEquals(groupQName, nodeGroup.getQName());
        final SchemaPath nodeGroupPath = TestUtils.createPath(true, snNS, snRev, snPref, "nodes", "node-group");
        assertEquals(nodeGroupPath, nodeGroup.getPath());

        final Set<UsesNode> uses = nodes.getUses();
        assertEquals(1, uses.size());
        final UsesNode use = uses.iterator().next();
        assertEquals(nodeGroupPath, use.getGroupingPath());
    }


    private final URI ns = URI.create("urn:opendaylight:simple-nodes");
    private Date rev;
    private final String prefix = "sn";

    private SchemaPath createPath(final String... names) {
        try {
            rev = new SimpleDateFormat("yyyy-MM-dd").parse("2013-07-30");
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        final List<QName> path = new ArrayList<>();
        for (final String name : names) {
            path.add(QName.create(ns, rev, name));
        }
        return SchemaPath.create(path, true);
    }

}
