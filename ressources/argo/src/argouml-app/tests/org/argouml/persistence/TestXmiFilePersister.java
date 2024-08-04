/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2007 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.persistence;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.InitializeModel;
import org.argouml.model.Model;
import org.argouml.model.XmiReferenceException;
import org.argouml.profile.ProfileFacade;
import org.argouml.profile.init.InitProfileSubsystem;

/**
 * Testclass for the XMIReader. Placeholder for all saving/loading tests
 * concerning XMIReader (like the dreaded ClassCastException issues).
 *
 * @author jaap.branderhorst@xs4all.nl
 * @since Jan 17, 2003
 */
public class TestXmiFilePersister extends TestCase {

    /**
     * Constructor for TestXMIReader.
     * @param arg0 is the name of the test case.
     */
    public TestXmiFilePersister(String arg0) {
        super(arg0);
    }

    /*
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception {
        super.setUp();
        if (!Model.isInitiated()) {
            InitializeModel.initializeMDR();
        }
        new InitProfileSubsystem().init();
    }
    

    @Override
    protected void tearDown() throws Exception {
        ProfileFacade.reset();
        super.tearDown();
    }

    /**
     * This is a regression test for issue 1504.
     * Test basic serialization to XMI file.
     * 
     * @throws Exception if saving fails.
     */
    public void testSave() throws Exception {
        Project p = ProjectManager.getManager().makeEmptyProject();
        Object clazz = Model.getCoreFactory().buildClass(
                Model.getModelManagementFactory().getRootModel());
        Object returnType = ProjectManager.getManager()
            	.getCurrentProject().getDefaultReturnType();
        Object oper = Model.getCoreFactory().buildOperation(clazz, returnType);
        Model.getCoreHelper().setType(
                Model.getFacade().getParameter(oper, 0),
                p.findType("String"));
        File file = new File("test.xmi");
        XmiFilePersister persister = new XmiFilePersister();
        p.preSave();
        persister.save(p, file);
        p.postSave();
    }
    
    

    private Object checkFoo(Object theClass) {
        assertNotNull(theClass);
        Object att = Model.getFacade().getAttributes(theClass).get(0);
        assertNotNull(att);
        Object attType = Model.getFacade().getType(att);
        assertNotNull(attType);
        return attType;
    }

    /**
     * This is a regression test for issue 1504.
     * Test loading from minimal XMI file.
     * 
     * @throws Exception if loading project fails
     */
    public void testLoadProject() throws Exception {
        testSave(); // Create file
        File file = new File("test.xmi");

        XmiFilePersister persister = new XmiFilePersister();

        Project project = ProjectManager.getManager().makeEmptyProject();
        ProjectManager.getManager().setCurrentProject(project);

        persister.doLoad(file);
        
        ProjectManager.getManager().removeProject(project);
    }

    /**
     * Test loading a UML1.3 XMI file.
     * 
     * @throws Exception if loading project fails
     */
    public void testLoadProject13() throws Exception {
        ProjectFile file = new ProjectFile("xmi");
        XmiFilePersister persister = new XmiFilePersister();
        Project project = ProjectManager.getManager().makeEmptyProject();
        ProjectManager.getManager().setCurrentProject(project);

        persister.doLoad(file.getFile());

        ProjectManager.getManager().removeProject(project);
    }

    /**
     * Test loading an XMI file with a bad external reference (HREF).
     *
     * @throws Exception if loading project fails
     */
    public void testLoadBadHref() throws Exception {
        ProjectFile file = new ProjectFile("href");

        XmiFilePersister persister = new XmiFilePersister();

        Project project = ProjectManager.getManager().makeEmptyProject();
        ProjectManager.getManager().setCurrentProject(project);

        try {
            persister.doLoad(file.getFile());
            fail("Expected exception not thrown");
        } catch (OpenException e) {
            // Success - expected exception
            final Throwable cause = e.getCause();
            if (cause instanceof XmiReferenceException) {
                XmiReferenceException xre =
                    (XmiReferenceException) cause;
                assertTrue(xre.getReference().contains("bad-reference"));
            } else {
                throw new AssertionError("Unexpected exception cause", cause);
            }
        } finally {
            ProjectManager.getManager().removeProject(project);
        }
    }
}
