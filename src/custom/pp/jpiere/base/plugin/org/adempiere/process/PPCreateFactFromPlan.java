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
package custom.pp.jpiere.base.plugin.org.adempiere.process;

import org.compiere.process.SvrProcess;
import org.compiere.util.Util;

import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlan;


/**
 * JPIERE-0501:Create PP Fact from PP Plan
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class PPCreateFactFromPlan extends SvrProcess {

	private int p_JP_PP_Plan_ID = 0;


	@Override
	protected void prepare()
	{
		p_JP_PP_Plan_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{
		MPPPlan ppPLan = new MPPPlan(getCtx(),p_JP_PP_Plan_ID, get_TrxName());

		String msg = ppPLan.createFact(get_TrxName());

		if(!Util.isEmpty(msg))
			throw new Exception(msg);

		return "@Success@";
	}
}
