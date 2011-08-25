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
package de.kalpatec.pojosr.framework.launch;

import java.util.Dictionary;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public interface PojoServiceRegistry
{
    public BundleContext getBundleContext();
	
	public void startBundles(List<BundleDescriptor> bundles) throws Exception;

    public void addServiceListener(ServiceListener listener, String filter)
            throws InvalidSyntaxException;

    public void addServiceListener(ServiceListener listener);

    public void removeServiceListener(ServiceListener listener);

    public ServiceRegistration registerService(String[] clazzes,
            Object service, @SuppressWarnings("rawtypes") Dictionary properties);

    public ServiceRegistration registerService(String clazz, Object service,
            @SuppressWarnings("rawtypes") Dictionary properties);

    public ServiceReference[] getServiceReferences(String clazz, String filter)
            throws InvalidSyntaxException;

    public ServiceReference getServiceReference(String clazz);

    public Object getService(ServiceReference reference);

    public boolean ungetService(ServiceReference reference);
}
