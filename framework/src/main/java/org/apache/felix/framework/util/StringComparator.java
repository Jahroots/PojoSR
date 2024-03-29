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
package org.apache.felix.framework.util;

import java.util.Comparator;

public class StringComparator implements Comparator
{
    private final boolean m_isCaseSensitive;

    public StringComparator(boolean b)
    {
        m_isCaseSensitive = b;
    }

    public int compare(Object o1, Object o2)
    {
        if (m_isCaseSensitive)
        {
            return o1.toString().compareTo(o2.toString());
        }
        else
        {
            return o1.toString().compareToIgnoreCase(o2.toString());
        }
    }

    public boolean isCaseSensitive()
    {
        return m_isCaseSensitive;
    }
}