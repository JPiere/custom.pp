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



/**
 * JPIERE-0501: JPiere PP Doc
 *
 * @author Hideaki Hagiwara
 *
 */
public class MPPFactLineQT extends X_JP_PP_FactLineQT {

	public MPPFactLineQT(Properties ctx, int JP_PP_FactLineQT_ID, String trxName)
	{
		super(ctx, JP_PP_FactLineQT_ID, trxName);
	}

	public MPPFactLineQT(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//SetAD_Org_ID
		if(newRecord)
		{
			setAD_Org_ID(getParent().getAD_Org_ID());
		}

		return true;
	}

	private MPPFactLine m_PPFactLine = null;

	public MPPFactLine getParent()
	{
		if(m_PPFactLine == null)
			m_PPFactLine = new MPPFactLine(getCtx(), getJP_PP_FactLine_ID(), get_TrxName());
		else
			m_PPFactLine.set_TrxName(get_TrxName());

		return m_PPFactLine;
	}
}
