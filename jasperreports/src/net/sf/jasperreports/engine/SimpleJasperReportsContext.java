/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2011 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JRCloneable.java 4595 2011-09-08 15:55:10Z teodord $
 */
public class SimpleJasperReportsContext implements JasperReportsContext
{
	/**
	 *
	 */
	private JasperReportsContext parent;
	
	private Map<String, Object> values = new HashMap<String, Object>();
	private Map<String, String> properties;
	private Map<Class<?>, List<?>> extensionsMap;

	/**
	 *
	 */
	public SimpleJasperReportsContext()
	{
		this(DefaultJasperReportsContext.getInstance());//FIXMECONTEXT consider setting null parent after solving JRS spring bean
	}

	/**
	 *
	 */
	public SimpleJasperReportsContext(JasperReportsContext parent)
	{
		this.parent = parent;
	}

	/**
	 *
	 */
	public void setParent(JasperReportsContext parent)
	{
		this.parent = parent;
	}

	/**
	 *
	 */
	public Object getValue(String key)
	{
		if (values.containsKey(key))
		{
			return values.get(key);
		}
		if (parent != null)
		{
			return parent.getValue(key);
		}
		return null;
	}

	/**
	 *
	 */
	public void setValue(String key, Object value)
	{
		values.put(key, value);
	}
	
	/**
	 * Returns a list of extension objects for a specific extension type.
	 * 
	 * @param extensionType the extension type
	 * @param <T> generic extension type
	 * @return a list of extension objects
	 */
	public <T> List<T> getExtensions(Class<T> extensionType)
	{
		if (extensionsMap == null || !extensionsMap.containsKey(extensionType))
		{
			if (parent == null)
			{
				return null;
			}
			else
			{
				return parent.getExtensions(extensionType);
			}
		}
		else
		{
			@SuppressWarnings("unchecked")
			List<T> extensionsList = (List<T>)extensionsMap.get(extensionType);
			if (parent == null)
			{
				return extensionsList;
			}
			else
			{
				List<T> parentExtensions = parent.getExtensions(extensionType);
				if (extensionsList == null || extensionsList.isEmpty())
				{
					if (parentExtensions == null || parentExtensions.isEmpty())
					{
						return null;
					}
					else
					{
						return parentExtensions;
					}
				}
				else
				{
					if (parentExtensions == null || parentExtensions.isEmpty())
					{
						return extensionsList;
					}
					else
					{
						List<T> returnedList = new ArrayList<T>();
						returnedList.addAll(extensionsList);
						returnedList.addAll(parentExtensions);
						return returnedList;
					}
				}
			}
		}
	}
	
	/**
	 *
	 */
	public <T> void setExtensions(Class<T> extensionType, List<T> extensions)
	{
		if (extensionsMap == null)
		{
			extensionsMap = new HashMap<Class<?>, List<?>>();
		}
		extensionsMap.put(extensionType, extensions);
	}
	
	/**
	 *
	 */
	public void setExtensions(Map<Class<?>, List<?>> extensions)
	{
		extensionsMap = extensions;
	}
	
	/**
	 * Returns the value of the property.
	 * 
	 * @param key the key
	 * @return the property value
	 */
	public String getProperty(String key)
	{
		if (properties != null && properties.containsKey(key))
		{
			return properties.get(key);
		}
		else
		{
			if (parent == null)
			{
				return null;
			}
			else
			{
				return parent.getProperty(key);
			}
		}
	}
	
	/**
	 * 
	 */
	public void setProperty(String key, String value)
	{
		if (properties == null)
		{
			properties = new HashMap<String, String>();
		}
		
		properties.put(key, value);
	}
	
	/**
	 * 
	 */
	public Map<String, String> getProperties()
	{
		if (parent != null)
		{
			return parent.getProperties();
		}
		return null;
	}
}
