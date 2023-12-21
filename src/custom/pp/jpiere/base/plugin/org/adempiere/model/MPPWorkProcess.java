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
package custom.pp.jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.util.CCache;

/**
 * JPIERE-0501: JPiere PP Doc
 * JPIERE-0502: JPiere PP Doc Template
 * JPIERE-0609: Workprocess & Create Material Movement From PP Fact Doc.
 *
 * @author Hideaki Hagiwara
 *
 */
public class MPPWorkProcess extends X_JP_PP_WorkProcess {

	public MPPWorkProcess(Properties ctx, int JP_PP_WorkProcess_ID, String trxName) 
	{
		super(ctx, JP_PP_WorkProcess_ID, trxName);
	}

	public MPPWorkProcess(Properties ctx, int JP_PP_WorkProcess_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_PP_WorkProcess_ID, trxName, virtualColumns);
	}

	public MPPWorkProcess(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	/**	Cache				*/
	private static CCache<Integer,MPPWorkProcess>	s_cache = new CCache<Integer,MPPWorkProcess>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_PP_WorkProcess_ID id
	 *	@return MPPWorkProcess
	 */
	public static MPPWorkProcess get (Properties ctx, int JP_PP_WorkProcess_ID)
	{
		Integer ii = Integer.valueOf(JP_PP_WorkProcess_ID);
		MPPWorkProcess retValue = (MPPWorkProcess)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MPPWorkProcess (ctx, JP_PP_WorkProcess_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_PP_WorkProcess_ID, retValue);
		return retValue;
	}	//	get
}
