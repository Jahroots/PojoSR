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

import java.net.URL;
import java.util.Map;

public class BundleDescriptor
{
    private final ClassLoader m_loader;
    private final URL m_url;
    private final Map<String, String> m_headers;

    public BundleDescriptor(ClassLoader loader, URL url,
            Map<String, String> headers)
    {
        m_loader = loader;
        m_url = url;
        m_headers = headers;
    }

    public ClassLoader getClassLoader()
    {
        return m_loader;
    }

    public URL getUrl()
    {
        return m_url;
    }

    public String toString()
    {
        return m_url.toExternalForm();
    }

    public Map<String, String> getHeaders()
    {
        return m_headers;
    }
}
