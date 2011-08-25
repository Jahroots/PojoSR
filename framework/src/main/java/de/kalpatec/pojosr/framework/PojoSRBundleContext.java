/* 
 * Copyright 2011 Karl Pauls karlpauls@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.kalpatec.pojosr.framework;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.framework.ServiceRegistry;
import org.apache.felix.framework.capabilityset.SimpleFilter;
import org.apache.felix.framework.util.EventDispatcher;
import org.osgi.framework.AllServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

class PojoSRBundleContext implements BundleContext
{
    private final Bundle m_bundle;
    private final ServiceRegistry m_reg;
    private final EventDispatcher m_dispatcher;
    private final Map<Long, Bundle> m_bundles;

    public PojoSRBundleContext(Bundle bundle, ServiceRegistry reg,
            EventDispatcher dispatcher, Map<Long, Bundle> bundles)
    {
        m_bundle = bundle;
        m_reg = reg;
        m_dispatcher = dispatcher;
        m_bundles = bundles;
    }

    public boolean ungetService(ServiceReference reference)
    {
        return m_reg.ungetService(m_bundle, reference);
    }

    public void removeServiceListener(ServiceListener listener)
    {
        m_dispatcher.removeListener(m_bundle, ServiceListener.class,
                    listener);
    }

    public void removeFrameworkListener(FrameworkListener listener)
    {
        m_dispatcher
                .removeListener(m_bundle, FrameworkListener.class, listener);
    }

    public void removeBundleListener(BundleListener listener)
    {
        m_dispatcher.removeListener(m_bundle, BundleListener.class, listener);
    }

    public ServiceRegistration registerService(String clazz, Object service,
            Dictionary properties)
    {
        return m_reg.registerService(m_bundle, new String[] { clazz }, service,
                properties);
    }

    public ServiceRegistration registerService(String[] clazzes,
            Object service, Dictionary properties)
    {
        return m_reg.registerService(m_bundle, clazzes, service, properties);
    }

    public Bundle installBundle(String location) throws BundleException
    {
        throw new BundleException("pojosr can't do that");
    }

    public Bundle installBundle(String location, InputStream input)
            throws BundleException
    {

        throw new BundleException("pojosr can't do that");
    }

    public ServiceReference[] getServiceReferences(String clazz, String filter)
            throws InvalidSyntaxException
    {
        return getAllServiceReferences(clazz, filter);
    }

    public ServiceReference getServiceReference(String clazz)
    {
        try
        {
            return getBestServiceReference(getAllServiceReferences(clazz, null));
        }
        catch (InvalidSyntaxException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private ServiceReference getBestServiceReference(ServiceReference[] refs)
    {
        if (refs == null)
        {
            return null;
        }

        if (refs.length == 1)
        {
            return refs[0];
        }

        // Loop through all service references and return
        // the "best" one according to its rank and ID.
        ServiceReference bestRef = refs[0];
        for (int i = 1; i < refs.length; i++)
        {
            if (bestRef.compareTo(refs[i]) < 0)
            {
                bestRef = refs[i];
            }
        }

        return bestRef;
    }

    public Object getService(ServiceReference reference)
    {
        return m_reg.getService(m_bundle, reference);
    }

    public String getProperty(String key)
    {
        return System.getProperty(key);
    }

    public File getDataFile(String filename)
    {
        File root = new File("bundle" + m_bundle.getBundleId());
        if (System.getProperty("org.osgi.framework.storage") != null)
        {
            root = new File(new File(System.getProperty("org.osgi.framework.storage")), root.getName());
        } 
        root.mkdirs();
        return filename.trim().length() > 0 ? new File(root, filename) : root;
    }

    public Bundle[] getBundles()
    {
        Bundle[] result = m_bundles.values().toArray(
                new Bundle[m_bundles.size()]);
        Arrays.sort(result, new Comparator<Bundle>()
        {

            public int compare(Bundle o1, Bundle o2)
            {
                return (int) (o1.getBundleId() - o2.getBundleId());
            }
        });
        return result;
    }

    public Bundle getBundle(long id)
    {
        return m_bundles.get(id);
    }

    public Bundle getBundle()
    {
        return m_bundle;
    }

    public ServiceReference[] getAllServiceReferences(String clazz,
            String filter) throws InvalidSyntaxException
    {
        SimpleFilter simple = null;
        if (filter != null)
        {
            try
            {
                simple = SimpleFilter.parse(filter);
            }
            catch (Exception ex)
            {
                throw new InvalidSyntaxException(ex.getMessage(), filter);
            }
        }
        List<ServiceReference> result = m_reg.getServiceReferences(clazz,
                simple);
        return result.isEmpty() ? null : result
                .toArray(new ServiceReference[result.size()]);
    }

    public Filter createFilter(String filter) throws InvalidSyntaxException
    {
        return FrameworkUtil.createFilter(filter);
    }

    public void addServiceListener(ServiceListener listener)
    {
        try
        {
            addServiceListener(listener, null);
        }
        catch (InvalidSyntaxException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

     public void addServiceListener(final ServiceListener listener, String filter)
            throws InvalidSyntaxException
    {
		 m_dispatcher.addListener(m_bundle, ServiceListener.class, listener,
                filter == null ? null : FrameworkUtil.createFilter(filter));
    }

    public void addFrameworkListener(FrameworkListener listener)
    {
        m_dispatcher.addListener(m_bundle, FrameworkListener.class, listener,
                null);
    }

    public void addBundleListener(BundleListener listener)
    {
        m_dispatcher
                .addListener(m_bundle, BundleListener.class, listener, null);
    }
}
