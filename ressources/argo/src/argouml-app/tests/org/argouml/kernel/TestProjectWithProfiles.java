/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2010 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    euluis
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2007-2009 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argouml.kernel;

import static org.argouml.model.Model.getCoreFactory;
import static org.argouml.model.Model.getExtensionMechanismsHelper;
import static org.argouml.model.Model.getFacade;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.argouml.FileHelper;
import org.argouml.application.helpers.ApplicationVersion;
import org.argouml.model.InitializeModel;
import org.argouml.model.Model;
import org.argouml.model.XmiReferenceException;
import org.argouml.model.XmiReferenceRuntimeException;
import org.argouml.persistence.AbstractFilePersister;
import org.argouml.persistence.OpenException;
import org.argouml.persistence.PersistenceManager;
import org.argouml.persistence.SaveException;
import org.argouml.profile.Profile;
import org.argouml.profile.ProfileException;
import org.argouml.profile.ProfileFacade;
import org.argouml.profile.ProfileManager;
import org.argouml.profile.ProfileMother;
import org.argouml.profile.UserDefinedProfile;
import org.argouml.profile.init.InitProfileSubsystem;

/**
 * Tests the {@link ProjectImpl} with profiles, specifically this enables the 
 * testing of the org.argouml.profile subsystem API for the project and the 
 * interactions between {@link ProfileConfiguration} and the {@link Project}.
 *
 * @author Luis Sergio Oliveira (euluis)
 */
public class TestProjectWithProfiles extends TestCase {

    private File testCaseDir;

    /*
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        InitializeModel.initializeMDR();
        new InitProfileSubsystem().init();
        
        initAppVersion();
        assertNotNull(ApplicationVersion.getVersion());
        String testCaseDirNamePrefix = getClass().getPackage().getName();
        testCaseDir = FileHelper.setUpDir4Test(testCaseDirNamePrefix);
    }
    
    @Override
    protected void tearDown() throws Exception {
        FileHelper.delete(testCaseDir);
        super.tearDown();
    }

    /**
     * Basic test that a new project contains a {@link ProfileConfiguration} 
     * and that this contains the default profiles.
     */
    public void testCreatedProjectContainsProfileConfiguration() {
        List<Profile> defaultProfiles 
            = ProfileFacade.getManager().getDefaultProfiles();               
        
        Project project = ProjectManager.getManager().makeEmptyProject();
        ProfileConfiguration profileConfiguration = 
            project.getProfileConfiguration();
                        
        assertNotNull(profileConfiguration);
        assertNotNull(profileConfiguration.getProfiles());
        
        for (Profile profile : defaultProfiles) {
            assertTrue(profileConfiguration.getProfiles().contains(profile));
        }
    }
    
    

    /**
     * @param project the ArgoUML {@link Project} to save in file.
     * @param file the {@link File} in which an ArgoUML {@link Project} will
     * be persisted.
     * @return the persister used and usable for file.
     * @throws SaveException if saving the file goes wrong.
     * @throws InterruptedException if an interrupt occurs while saving.
     * TODO: move this to an helper class.
     */
    public static AbstractFilePersister saveProject(Project project, File file)
        throws SaveException, InterruptedException {
        AbstractFilePersister persister = getProjectPersister(file);
        project.setVersion(ApplicationVersion.getVersion());
        persister.save(project, file);
        return persister;
    }

    /**
     * Get an {@link AbstractFilePersister} for file.
     *
     * @param file the {@link File} in which an ArgoUML {@link Project} will
     * be persisted.
     * @return the appropriate persister for file or null if the file's
     * extension doesn't match a supported persister.
     * TODO: move this to an helper class.
     */
    public static AbstractFilePersister getProjectPersister(File file) {
        AbstractFilePersister persister = 
            PersistenceManager.getInstance().getPersisterFromFileName(
                file.getAbsolutePath());
        return persister;
    }

    /**
     * Initialize the ArgoUML application version, so that
     * {@link ApplicationVersion#getVersion()} doesn't return null.
     *
     * @throws Exception if something goes wrong...
     * TODO: move this to an helper class.
     */
    @SuppressWarnings("unchecked")
    public static void initAppVersion() throws Exception {
        if (ApplicationVersion.getVersion() == null) {
            Class argoVersionClass = 
                Class.forName("org.argouml.application.ArgoVersion");
            Method initMethod = argoVersionClass.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(null);
        }
    }

    private File createUserProfileFile(File directory, String filename)
        throws IOException {
        ProfileMother mother = new ProfileMother();
        Object profileModel = mother.createSimpleProfileModel();
        Model.getCoreHelper().setName(profileModel, filename);
        File userDefinedProfileFile = new File(directory, filename);
        mother.saveProfileModel(profileModel, userDefinedProfileFile);
        // Clean up after ourselves by deleting profile model
        Model.getUmlFactory().delete(profileModel);
        return userDefinedProfileFile;
    }

}
