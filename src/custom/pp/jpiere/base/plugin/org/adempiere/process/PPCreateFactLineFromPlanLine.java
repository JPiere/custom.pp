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

import java.util.List;
import java.util.logging.Level;

import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Util;

import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPFact;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPFactLine;

/**
 * JPIERE-0501: JPiere PP Doc - Create Fact Line From Plan Line
 *
 *
 * @author Hideaki Hagiwara
 *
 */
public class PPCreateFactLineFromPlanLine extends SvrProcess {

	private boolean p_Recreate = true;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if ("Recreate".equals(name))
				p_Recreate = para[i].getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

	}

	@Override
	protected String doIt() throws Exception
	{
		if(p_Recreate)
		{
			List<MPPFactLine> list = new Query(Env.getCtx(), "JP_PP_FactLine", "JP_PP_Fact_ID = ?", get_TrxName())
									.setOnlyActiveRecords(false)
									.setApplyAccessFilter(false)
									.setClient_ID()
									.setParameters(getRecord_ID())
									.list();

			for(MPPFactLine ppFLine : list)
			{
				ppFLine.deleteEx(false);
			}
		}

		MPPFact ppFact = new MPPFact(getCtx(), getRecord_ID(), get_TrxName());
		String msg = ppFact.createFactLineFromPlanLine(get_TrxName());
		if(!Util.isEmpty(msg))
		{
			throw new Exception(msg);
		}

		ppFact.setIsCreated("Y");
		ppFact.saveEx(get_TrxName());

		return "@Success@";
	}

}
