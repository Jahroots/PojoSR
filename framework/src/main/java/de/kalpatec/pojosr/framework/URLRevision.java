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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

class URLRevision extends Revision
{
    private final URL m_url;
    private final long m_lastModified;

    public URLRevision(URL url, long lastModified)
    {
        m_url = url;
        if (lastModified > 0)
        {
            m_lastModified = lastModified;
        }
        else
        {
            m_lastModified = System.currentTimeMillis();
        }
    }

    @Override
    public long getLastModified()
    {
        return m_lastModified;
    }

    public Enumeration getEntries()
    {
        return new Properties().elements();
    }

    @Override
    public URL getEntry(String entryName)
    {
        // TODO Auto-generated method stub
        try
        {
            return new URL(m_url, entryName);
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}