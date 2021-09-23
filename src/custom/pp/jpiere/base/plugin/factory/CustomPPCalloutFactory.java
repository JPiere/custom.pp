/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package custom.pp.jpiere.base.plugin.factory;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.adempiere.base.ICalloutFactory;
import org.adempiere.base.ServiceQuery;
import org.adempiere.base.equinox.EquinoxExtensionLocator;
import org.compiere.model.Callout;
import org.compiere.util.CLogger;

/**
 *  JPIERE-0501 JP PP Doc
 *  Custom PP Callout Factory
 *
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class CustomPPCalloutFactory implements ICalloutFactory {

	private final static CLogger log = CLogger.getCLogger(CustomPPCalloutFactory.class);

	/**
	 * default constructor
	 */
	public CustomPPCalloutFactory() {
	}

	/* (non-Javadoc)
	 * @see org.adempiere.base.ICalloutFactory#getCallout(java.lang.String)
	 */
	@Override
	public Callout getCallout(String className, String methodName) {


		if(className.startsWith("custom.pp."))
		{
			Callout callout = null;
			callout = EquinoxExtensionLocator.instance().locate(Callout.class, Callout.class.getName(), className, (ServiceQuery)null).getExtension();
			if (callout == null) {
				//Get Class
				Class<?> calloutClass = null;
				//use context classloader if available
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				if (classLoader != null)
				{
					try
					{
						calloutClass = classLoader.loadClass(className);
					}
					catch (ClassNotFoundException ex)
					{
						if (log.isLoggable(Level.FINE))log.log(Level.FINE, className, ex);
					}
				}
				if (calloutClass == null)
				{
					classLoader = this.getClass().getClassLoader();
					try
					{
						calloutClass = classLoader.loadClass(className);
					}
					catch (ClassNotFoundException ex)
					{
						log.log(Level.WARNING, className, ex);
						return null;
					}
				}

				if (calloutClass == null) {
					return null;
				}

				//Get callout
				try
				{
					callout = (Callout)calloutClass.getDeclaredConstructor().newInstance();
				}
				catch (Exception ex)
				{
					log.log(Level.WARNING, "Instance for " + className, ex);
					return null;
				}

				//Check if callout method does really exist
				Method[] methods = calloutClass.getDeclaredMethods();
				for (int i = 0; i < methods.length; i++) {
			        if (methods[i].getName().equals(methodName)) {
			        	return callout;
			        }
				}
			}
			log.log(Level.FINE, "Required method " + methodName + " not found in class " + className);
		}

		return null;
	}

}
