/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.kalpatec.pojosr.framework;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.felix.framework.ServiceRegistry;
import org.apache.felix.framework.util.EventDispatcher;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;
import org.osgi.service.startlevel.StartLevel;

import de.kalpatec.pojosr.framework.launch.BundleDescriptor;
import de.kalpatec.pojosr.framework.launch.ClasspathScanner;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistryFactory;

public class PojoSR implements PojoServiceRegistry
{
    private final BundleContext m_context;
    private final EventDispatcher m_dispatcher = EventDispatcher.start();
    private final ServiceRegistry m_reg = new ServiceRegistry(
            new ServiceRegistry.ServiceRegistryCallbacks()
            {

                public void serviceChanged(ServiceEvent event,
                        Dictionary oldProps)
                {
                    m_dispatcher.fireServiceEvent(event, oldProps, null);
                }
            });
    private final Map<Long, Bundle> m_bundles =new HashMap<Long, Bundle>();
    private final Map<String, Bundle> m_symbolicNameToBundle = new HashMap<String, Bundle>();

    public PojoSR(Map config) throws Exception
    {
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put(Constants.BUNDLE_SYMBOLICNAME,
                "de.kalpatec.pojosr.framework");
        headers.put(Constants.BUNDLE_VERSION, "0.0.1");
        headers.put(Constants.BUNDLE_NAME, "System Bundle");
        headers.put(Constants.BUNDLE_MANIFESTVERSION, "2");
		headers.put(Constants.BUNDLE_VENDOR, "kalpatec");
        final Bundle b = new PojoSRBundle(new Revision()
        {

            @Override
            public long getLastModified()
            {
                // TODO Auto-generated method stub
                return System.currentTimeMillis();
            }

            @Override
            public Enumeration getEntries()
            {
                return new Properties().elements();
            }

            @Override
            public URL getEntry(String entryName)
            {
                return getClass().getClassLoader().getResource(entryName);
            }
        }, headers, new Version(0, 0, 1), "file:pojosr", m_reg, m_dispatcher,
                null, 0, "de.kalpatec.pojosr.framework", m_bundles, getClass()
                        .getClassLoader())
        {
            @Override
            public synchronized void stop() throws BundleException
            {
                for (Bundle b : m_bundles.values())
                {
                    try
                    {
                        if (b != this)
                        {
                            b.stop();
                        }
                    }
                    catch (Throwable t)
                    {
                        t.printStackTrace();
                    }
                }
                m_dispatcher.shutdown();
            }
        };
        m_symbolicNameToBundle.put(b.getSymbolicName(), b);
        b.start();
        b.getBundleContext().registerService(StartLevel.class.getName(),
                new StartLevel()
                {

                    public void setStartLevel(int startlevel)
                    {
                        // TODO Auto-generated method stub

                    }

                    public void setInitialBundleStartLevel(int startlevel)
                    {
                        // TODO Auto-generated method stub

                    }

                    public void setBundleStartLevel(Bundle bundle,
                            int startlevel)
                    {
                        // TODO Auto-generated method stub

                    }

                    public boolean isBundlePersistentlyStarted(Bundle bundle)
                    {
                        // TODO Auto-generated method stub
                        return true;
                    }

                    public boolean isBundleActivationPolicyUsed(Bundle bundle)
                    {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    public int getStartLevel()
                    {
                        // TODO Auto-generated method stub
                        return 1;
                    }

                    public int getInitialBundleStartLevel()
                    {
                        // TODO Auto-generated method stub
                        return 1;
                    }

                    public int getBundleStartLevel(Bundle bundle)
                    {
                        // TODO Auto-generated method stub
                        return 1;
                    }
                }, null);

        b.getBundleContext().registerService(PackageAdmin.class.getName(),
                new PackageAdmin()
                {

                    public boolean resolveBundles(Bundle[] bundles)
                    {
                        // TODO Auto-generated method stub
                        return true;
                    }

                    public void refreshPackages(Bundle[] bundles)
                    {
                        m_dispatcher.fireFrameworkEvent(new FrameworkEvent(
                                FrameworkEvent.PACKAGES_REFRESHED, b, null));
                    }

                    public RequiredBundle[] getRequiredBundles(
                            String symbolicName)
                    {
                        return null;
                    }

                    public Bundle[] getHosts(Bundle bundle)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public Bundle[] getFragments(Bundle bundle)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public ExportedPackage[] getExportedPackages(String name)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public ExportedPackage[] getExportedPackages(Bundle bundle)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public ExportedPackage getExportedPackage(String name)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public Bundle[] getBundles(String symbolicName,
                            String versionRange)
                    {
					    Bundle result = m_symbolicNameToBundle.get((symbolicName != null) ? symbolicName.trim() : symbolicName);
						if (result != null) {
							return new Bundle[] {result};
						}
						return null;
                    }

                    public int getBundleType(Bundle bundle)
                    {
                        return 0;
                    }

                    public Bundle getBundle(Class clazz)
                    {
                        return m_context.getBundle();
                    }
                }, null);
        m_context = b.getBundleContext();

        List<BundleDescriptor> scan = (List<BundleDescriptor>) config
                .get(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS);

        if (scan != null)
        {
            startBundles(scan);
		}
    }
	
	public void startBundles(List<BundleDescriptor> scan) throws Exception {
	 for (BundleDescriptor desc : scan)
            {
                URL u = new URL(desc.getUrl().toExternalForm()
                        + "META-INF/MANIFEST.MF");
                Revision r;
                if (u.toExternalForm().startsWith("file:"))
                {
                    File root = new File(URLDecoder.decode(desc.getUrl()
                            .getFile(), "UTF-8"));
                    u = root.toURL();
                    r = new DirRevision(root);
                }
                else
                {
                    URLConnection uc = u.openConnection();
                    if (uc instanceof JarURLConnection)
                    {
                        r = new JarRevision(
                                ((JarURLConnection) uc).getJarFile(),
                                ((JarURLConnection) uc).getJarFileURL(),
                                uc.getLastModified());
                    }
                    else
                    {
                        r = new URLRevision(desc.getUrl(), desc.getUrl()
                                .openConnection().getLastModified());
                    }
                }
                Map<String, String> bundleHeaders = desc.getHeaders();
				Version osgiVersion = null;
				try {
					osgiVersion = Version.parseVersion(bundleHeaders.get(Constants.BUNDLE_VERSION));
				} catch (Exception ex) {
					ex.printStackTrace();
					osgiVersion = Version.emptyVersion;
				}
                String sym = bundleHeaders.get(Constants.BUNDLE_SYMBOLICNAME);
                    if (sym != null)
                    {
                        int idx = sym.indexOf(';');
                        if (idx > 0)
                        {
                            sym = sym.substring(0, idx);
                        }
						sym = sym.trim();
                    }

                if ((sym == null)
                        || !m_symbolicNameToBundle.containsKey( sym ))
                {
                    // TODO: framework - support multiple versions
                    Bundle bundle = new PojoSRBundle(r, bundleHeaders,
                            osgiVersion, desc.getUrl().toExternalForm(), m_reg,
                            m_dispatcher,
                            bundleHeaders.get(Constants.BUNDLE_ACTIVATOR),
                            m_bundles.size(),
                            sym,
                            m_bundles, desc.getClassLoader());
                    if (sym != null)
                    {
                        m_symbolicNameToBundle.put(bundle.getSymbolicName(),
                                bundle);
                    }
                }

            }
        

        for (long i = 1; i < m_bundles.size(); i++)
        {
            try
            {
                m_bundles.get(i).start();
            }
            catch (Throwable e)
            {
                System.out.println("Unable to start bundle: " + i);
                e.printStackTrace();
            }
        }

	}

    public static void main(String[] args) throws Exception
    {
        Map config = new HashMap();
        config.put(
                PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS,
                (args.length == 1) ? new ClasspathScanner()
                        .scanForBundles(args[0]) : new ClasspathScanner()
                        .scanForBundles());
        new PojoServiceRegistryFactoryImpl().newPojoServiceRegistry(config);
    }

    public BundleContext getBundleContext()
    {
        return m_context;
    }

    public void addServiceListener(ServiceListener listener, String filter)
            throws InvalidSyntaxException
    {
        m_context.addServiceListener(listener, filter);
    }

    public void addServiceListener(ServiceListener listener)
    {
        m_context.addServiceListener(listener);
    }

    public void removeServiceListener(ServiceListener listener)
    {
        m_context.removeServiceListener(listener);
    }

    public ServiceRegistration registerService(String[] clazzes,
            Object service, Dictionary properties)
    {
        return m_context.registerService(clazzes, service, properties);
    }

    public ServiceRegistration registerService(String clazz, Object service,
            Dictionary properties)
    {
        return m_context.registerService(clazz, service, properties);
    }

    public ServiceReference[] getServiceReferences(String clazz, String filter)
            throws InvalidSyntaxException
    {
        return m_context.getServiceReferences(clazz, filter);
    }

    public ServiceReference getServiceReference(String clazz)
    {
        return m_context.getServiceReference(clazz);
    }

    public Object getService(ServiceReference reference)
    {
        return m_context.getService(reference);
    }

    public boolean ungetService(ServiceReference reference)
    {
        return m_context.ungetService(reference);
    }
}
