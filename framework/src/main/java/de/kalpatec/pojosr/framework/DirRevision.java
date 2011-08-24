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
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import org.apache.felix.framework.util.StringMap;

class DirRevision extends Revision
{
    private final File m_file;

    public DirRevision(File file)
    {
        m_file = file;
    }

    @Override
    public long getLastModified()
    {
        return m_file.lastModified();
    }

    public Enumeration getEntries()
    {
        return new FileEntriesEnumeration(m_file);
    }

    @Override
    public URL getEntry(String entryName)
    {
        try
        {
		    if (entryName != null) {
            File file = (new File(m_file, (entryName.startsWith("/")) ? entryName.substring(1) : entryName));
            if (file.exists()) {
                return file.toURL();
            } 
			}
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
            return null;

    }

}
