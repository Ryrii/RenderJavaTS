/* $Id$
 *****************************************************************************
 * Copyright (c) 2008-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    maurelio1234 - Initial implementation
 *    Thomas Neustupny
 *    Tom Morris - lazy loading
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2009 The Regents of the University of California. All
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

package org.argouml.profile.internal;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.argouml.model.Model;
import org.argouml.profile.CoreProfileReference;
import org.argouml.profile.DefaultTypeStrategy;
import org.argouml.profile.FormatingStrategy;
import org.argouml.profile.Profile;
import org.argouml.profile.ProfileException;
import org.argouml.profile.ProfileModelLoader;
import org.argouml.profile.ProfileReference;
import org.argouml.profile.ResourceModelLoader;
import org.argouml.profile.internal.ocl.InvalidOclException;

/**
 * This class represents the default UML profile.
 *
 * @author maurelio1234
 */
public class ProfileUML extends Profile {

    

    private static final String PROFILE_UML14_FILE = "default-uml14.xmi";
    private static final String PROFILE_UML22_FILE = "default-uml22.xmi";

    static final String NAME_UML14 = "UML 1.4";
    static final String NAME_UML22 = "UML 2.2";

    private FormatingStrategy formatingStrategy;
    private ProfileModelLoader profileModelLoader;
    private Collection model;

    

    private ProfileReference profileReference = null;

    /**
     * Construct a Profile for UML modeling.
     * @throws ProfileException
     */
    @SuppressWarnings("unchecked")
    ProfileUML() throws ProfileException {
        formatingStrategy = new FormatingStrategyUML();
        try {
            if (Model.getFacade().getUmlVersion().charAt(0) == '1') {
                profileReference =
                    new CoreProfileReference(PROFILE_UML14_FILE);
            } else {
                //TODO: this profile isn't used anymore, see getModel()
                CoreProfileReference.setProfileDirectory("uml22");
                profileReference =
                    new CoreProfileReference(PROFILE_UML22_FILE);
            }
        } catch (MalformedURLException e) {
            throw new ProfileException(
                "Exception while creating profile reference.", e);
        }
    }

    private Collection getModel() {
        if (model == null) {
            if (Model.getFacade().getUmlVersion().charAt(0) == '1') {
                profileModelLoader = new ResourceModelLoader();
                try {
                    model = profileModelLoader.loadModel(profileReference);
                } catch (ProfileException e) {
                    
                }
            } else {
                // We have our own UML2 profile, but it is not used. Instead,
                // by the following line the build-in eclipse UML2 standard
                // profile and primitive types implementation are used.
                model = Model.getUmlFactory().getExtentPackages(
                        "pathmap://UML_PROFILES/Standard.profile.uml");
            }

            if (model == null) {
                model = new ArrayList();
                model.add(Model.getModelManagementFactory().createProfile());
            }
        }
        return model;
    }

    

    @Override
    public FormatingStrategy getFormatingStrategy() {
        return formatingStrategy;
    }

    @Override
    public String getDisplayName() {
        if (Model.getFacade().getUmlVersion().charAt(0) == '1') {
            return NAME_UML14;
        }
        return NAME_UML22;
    }


    

    @Override
    public Collection getProfilePackages() {
        return Collections.unmodifiableCollection(getModel());
    }


    @Override
    public Collection<Object> getLoadedPackages() {
        if (model == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableCollection(model);
        }
    }

    @Override
    public DefaultTypeStrategy getDefaultTypeStrategy() {
        return new DefaultTypeStrategy() {
            private Collection model = getModel();
            public Object getDefaultAttributeType() {
                return getDefaultType();
            }

            public Object getDefaultParameterType() {
                return getDefaultType();
            }

            public Object getDefaultReturnType() {
                return null;
            }

            private Object getDefaultType() {
                if (Model.getFacade().getUmlVersion().charAt(0) == '1') {
                    return ModelUtils.findTypeInModel("Integer", model
                            .iterator().next());
                }
                // no default type for UML2
                return null;
            }
        };
    }
}
