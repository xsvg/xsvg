package cc.cnplay.core.util;

import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

public abstract class BeanUtils
{

	private final static Logger log = Logger.getLogger(BeanUtils.class);

	public static void copyProperties(Object dest, Object orig)
	{
		if (dest == null)
		{
			log.debug("", new IllegalArgumentException("No destination bean specified"));
			return;
		}

		if (orig == null)
		{
			log.debug("", new IllegalArgumentException("No origin bean specified"));
			return;
		}

		log.debug("BeanUtils.copyProperties(" + dest.getClass().getName() + ", " + orig.getClass().getName() + ")");

		if (orig instanceof DynaBean)
		{
			DynaProperty[] origDescriptors = ((DynaBean) orig).getDynaClass().getDynaProperties();

			for (int i = 0; i < origDescriptors.length; ++i)
			{
				String name = origDescriptors[i].getName();

				if ((!(PropertyUtils.isReadable(orig, name))) || (!(PropertyUtils.isWriteable(dest, name))))
					continue;
				Object value = ((DynaBean) orig).get(name);
				try
				{
					org.apache.commons.beanutils.BeanUtils.copyProperty(dest, name, value);
				}
				catch (Throwable e)
				{
					log.error("BeanUtils.copyProperties(" + dest.getClass().getName() + ", " + orig.getClass().getName() + ")" + name + "=" + value + " " + origDescriptors[i].getType(), e);
				}
			}
		}
		else if (orig instanceof Map)
		{
			Iterator<?> entries = ((Map<?, ?>) orig).entrySet().iterator();
			while (entries.hasNext())
			{
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) entries.next();
				String name = (String) entry.getKey();
				if (PropertyUtils.isWriteable(dest, name))
				{
					try
					{
						org.apache.commons.beanutils.BeanUtils.copyProperty(dest, name, entry.getValue());
					}
					catch (Throwable e)
					{
						log.error("BeanUtils.copyProperties(" + dest.getClass().getName() + ", " + orig.getClass().getName() + ")" + name + "=" + entry.getValue(), e);
					}
				}
			}
		}
		else
		{
			PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(orig);

			for (int i = 0; i < origDescriptors.length; ++i)
			{
				String name = origDescriptors[i].getName();
				if ("class".equals(name))
				{
					continue;
				}
				if ((!(PropertyUtils.isReadable(orig, name))) || (!(PropertyUtils.isWriteable(dest, name))))
				{
					continue;
				}
				Object value = null;
				try
				{
					value = PropertyUtils.getSimpleProperty(orig, name);
					org.apache.commons.beanutils.BeanUtils.copyProperty(dest, name, value);
				}
				catch (Throwable e)
				{
					// Log.e(BeanUtils.class.getName(), name + "=" + value + " " + e.getMessage());
					try
					{
						PropertyUtils.setProperty(dest, name, value);
					}
					catch (Throwable ex)
					{
						log.error("BeanUtils.copyProperties(" + dest.getClass().getName() + ", " + orig.getClass().getName() + ")" + name + "=" + value + " " + origDescriptors[i].getPropertyType(), ex);
					}
				}
			}
		}
	}
}
