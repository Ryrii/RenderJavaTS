/* $Id$
 *****************************************************************************
 * Copyright (c) 2007-2012 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    maurelio1234 - Initial implementation
 *    thn
 *    euluis
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2007-2008 The Regents of the University of California. All
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

package org.argouml.profile;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import org.argouml.kernel.ProfileConfiguration;
import org.argouml.model.Model;
import org.argouml.profile.internal.ocl.InvalidOclException;

/**
 * Represents a profile defined by the user.
 *
 * @author maurelio1234
 */
public class UserDefinedProfile extends Profile {

    

    private String displayName;

    private File modelFile;

    private Collection profilePackages = null;
    private Object packageLock = new Object();

    private UserDefinedFigNodeStrategy figNodeStrategy
        = new UserDefinedFigNodeStrategy();

    private ProfileReference reference;

    private ProfileManager profileManager;

    private boolean criticsLoaded = false;

    private class UserDefinedFigNodeStrategy implements FigNodeStrategy {

        private Map<String, Image> images = new HashMap<String, Image>();

        public Image getIconForStereotype(Object stereotype) {
            return images.get(Model.getFacade().getName(stereotype));
        }

        /**
         * Adds a new descriptor to this strategy
         *
         * @param fnd
         */
        public void addDesrciptor(FigNodeDescriptor fnd) {
            images.put(fnd.stereotype, fnd.img);
        }
    }

    private class FigNodeDescriptor {
        private String stereotype;

        private Image img;

        private String src;

        private int length;

        /**
         * @return if this descriptor is valid
         */
        public boolean isValid() {
            return stereotype != null && src != null && length > 0;
        }
    }

    /**
     * The default constructor for this class
     *
     * @param file the file from where the model should be read
     * @param manager the profile manager which will be used to resolve
     *        dependencies
     * @throws ProfileException if the profile could not be loaded
     */
    public UserDefinedProfile(File file, ProfileManager manager)
        throws ProfileException {

        

        displayName = file.getName();
        modelFile = file;
        try {
            reference = new UserProfileReference(file.getPath());
        } catch (MalformedURLException e) {
            throw new ProfileException(
                    "Failed to create the ProfileReference.", e);
        }
        profileManager = manager;
    }

    /**
     * The default constructor for this class
     *
     * @param file the file from where the model should be read
     * @throws ProfileException if the profile could not be loaded
     * @deprecated for 0.30 by euluis. Use
     * {@link UserDefinedProfile#UserDefinedProfile(File, ProfileManager)}
     * instead.
     */
    @Deprecated
    public UserDefinedProfile(File file) throws ProfileException {
        this(file, getSomeProfileManager());
    }

    private static class NullProfileManager implements ProfileManager {

        public void addSearchPathDirectory(String path) {
        }

        public void addToDefaultProfiles(Profile profile) {
        }

        public void applyConfiguration(ProfileConfiguration pc) {
        }

        public List<Profile> getDefaultProfiles() {
            return new ArrayList<Profile>();
        }

        public Profile getProfileForClass(String className) {
            return null;
        }

        public List<Profile> getRegisteredProfiles() {
            return new ArrayList<Profile>();
        }

        public List<String> getSearchPathDirectories() {
            return new ArrayList<String>();
        }

        public Profile getUMLProfile() {
            return null;
        }

        public Profile lookForRegisteredProfile(String profile) {
            return null;
        }

        public void refreshRegisteredProfiles() {
        }

        public void registerProfile(Profile profile) {
        }

        public void removeFromDefaultProfiles(Profile profile) {
        }

        public void removeProfile(Profile profile) {
        }

        public void removeSearchPathDirectory(String path) {
        }

    }

    /**
     * A constructor that reads a file from an URL
     *
     * @param url the URL
     * @param manager the profile manager which will be used to resolve
     *        dependencies
     * @throws ProfileException if the profile can't be read or is not valid
     */
    public UserDefinedProfile(URL url, ProfileManager manager)
        throws ProfileException {

        

        reference = new UserProfileReference(url.getPath(), url);
        profileManager = manager;
    }

    /**
     * A constructor that reads a file from an URL
     *
     * @param url the URL
     * @throws ProfileException if the profile could not be loaded
     * @deprecated for 0.30 by euluis. Use
     * {@link UserDefinedProfile#UserDefinedProfile(URL, ProfileManager)}
     * instead.
     */
    @Deprecated
    public UserDefinedProfile(URL url) throws ProfileException {
        this(url, getSomeProfileManager());
    }

    

    

    private static ProfileManager getSomeProfileManager() {
        if (ProfileFacade.isInitiated()) {
            return ProfileFacade.getManager();
        }
        return new NullProfileManager();
    }

    /**
     * Reads the informations defined as TaggedValues
     * @param manager the profile manager which will be used to resolve
     *        dependencies
     */
    private void loadModel() {
        synchronized (packageLock) {
            if (profilePackages == null) {
                try {
                    if (modelFile != null) {
                        profilePackages = new FileModelLoader()
                                .loadModel(reference);
                    } else {
                        profilePackages = new URLModelLoader()
                                .loadModel(reference);
                    }
                } catch (ProfileException e1) {
                    
                    profilePackages = Collections.emptySet();
                    return;
                }
            }

            Collection packagesInProfile = filterPackages(profilePackages);

            for (Object obj : packagesInProfile) {
                // if there is only one package in the model,
                // we should suppose it's the profile model,
                // if there is more than one, we take the ones
                // marked as <<profile>>
                if (Model.getFacade().isAModelElement(obj)
                        && (Model.getFacade().isAProfile(obj)
                                || (packagesInProfile.size() == 1))) {

                    // load profile name
                    String name = Model.getFacade().getName(obj);
                    if (name != null) {
                        displayName = name;
                    } else {
                        if (displayName == null) {
                            displayName = "default-display-name";
                        }
                    }
                    

                    loadDependentProfiles(obj);
                }
            }

            loadFigNodes(packagesInProfile);
        }
    }


    private void loadDependentProfiles(Object obj) {
        if (Model.getFacade().getUmlVersion().charAt(0) == '1') {
            loadDependentProfilesUml1(obj);
        }
        // TODO: profile dependencies for UML2
    }


    /**
     * For ArgoUML UML 1.4 profiles dependencies are encoded as a list of
     * profile names in the TaggedValue named "Dependency" on the profile pkg.
     *
     * @param pkg profile package
     */
    private void loadDependentProfilesUml1(Object pkg) {
        // load profile dependencies
        String dependencyListStr = Model.getFacade().getTaggedValueValue(pkg,
                "Dependency");
        StringTokenizer st = new StringTokenizer(dependencyListStr, " ,;:");

        String dependencyName = null;

        while (st.hasMoreTokens()) {
            dependencyName = st.nextToken();
            if (dependencyName != null) {
                
                Profile profile = profileManager
                        .lookForRegisteredProfile(dependencyName);
                if (profile != null) {
                    addProfileDependency(profile);
                } else {
                    
                }
            }
        }
    }


    /**
     * Load FigNodes from profile packages.
     *
     * @param packagesInProfile
     */
    private void loadFigNodes(Collection packagesInProfile) {
        Collection allStereotypes = Model.getExtensionMechanismsHelper()
                .getStereotypes(packagesInProfile);
        for (Object stereotype : allStereotypes) {
            Collection tags = Model.getFacade().getTaggedValuesCollection(stereotype);

            for (Object tag : tags) {
                String tagName = Model.getFacade().getTag(tag);
                if (tagName == null) {
                    
                } else if (tagName.toLowerCase().equals("figure")) {
                    

                    String value = Model.getFacade().getValueOfTag(tag);
                    File f = new File(value);
                    FigNodeDescriptor fnd = null;
                    try {
                        fnd = loadImage(Model.getFacade().getName(stereotype)
                                .toString(), f);
                        figNodeStrategy.addDesrciptor(fnd);
                    } catch (IOException e) {
                        
                    }
                }
            }
        }
    }

    

    /**
     * @return the packages in the <code>profilePackages</code>
     */
    private Collection filterPackages(Collection packages) {
        Collection ret = new ArrayList();

        // TODO: All this profile loading/handling needs to
        // move someplace in model subsystem probably

        for (Object object : packages) {
            if (Model.getFacade().isAPackage(object)) {
                ret.add(object);
            }
        }
        return ret;
    }

    

    

    @SuppressWarnings("unchecked")
    private Collection<Object> getAllCommentsInModel(Collection objs) {
        Collection<Object> col = new ArrayList<Object>();
        for (Object obj : objs) {
            if (Model.getFacade().isAComment(obj)) {
                col.add(obj);
            } else if (Model.getFacade().isANamespace(obj)) {
                Collection contents = Model
                        .getModelManagementHelper().getAllContents(obj);
                if (contents != null) {
                    col.addAll(contents);
                }
            }
        }
        return col;
    }

    /**
     * @return the string that should represent this profile in the GUI.
     */
    public String getDisplayName() {
        // TODO: Seems like overkill to load the model just to get the display
        // name, but that's where it's stored currently - tfm
        loadModel();
        return displayName;
    }

    /**
     * Returns null. This profile has no formatting strategy.
     *
     * @return null.
     */
    @Override
    public FormatingStrategy getFormatingStrategy() {
        return null;
    }

    /**
     * Returns null. This profile has no figure strategy.
     *
     * @return null.
     */
    @Override
    public FigNodeStrategy getFigureStrategy() {
        return figNodeStrategy;
    }

    /**
     * @return the file passed at the constructor
     */
    public File getModelFile() {
        return modelFile;
    }

    /**
     * @return the name of the model and the file name
     */
    @Override
    public String toString() {
        File str = getModelFile();
        return super.toString() + (str != null ? " [" + str + "]" : "");
    }

    @Override
    public Collection getProfilePackages() {
        loadModel();
        return profilePackages;
    }

    @Override
    public Collection getLoadedPackages() {
        if (profilePackages == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableCollection(profilePackages);
        }
    }

    private FigNodeDescriptor loadImage(String stereotype, File f)
        throws IOException {
        FigNodeDescriptor descriptor = new FigNodeDescriptor();
        descriptor.length = (int) f.length();
        descriptor.src = f.getPath();
        descriptor.stereotype = stereotype;

        BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(f));

        byte[] buf = new byte[descriptor.length];
        try {
            bis.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        descriptor.img = new ImageIcon(buf).getImage();

        return descriptor;
    }
}
