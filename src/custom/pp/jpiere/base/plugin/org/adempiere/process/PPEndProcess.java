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

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.adempiere.util.ProcessUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MProcess;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFProcess;

import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPDoc;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlan;


/**
 * JPIERE-0501:JPiere PP End Process
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class PPEndProcess extends SvrProcess {

	private int record_ID = 0;
	private MTable m_Table = null;

	@Override
	protected void prepare()
	{
		record_ID = getRecord_ID();
	    m_Table = MTable.get(getTable_ID());
	}

	@Override
	protected String doIt() throws Exception
	{
		String msg = "@OK@";

		PO po = m_Table.getPO(record_ID, get_TrxName());

		if(!po.get_ValueAsString(MPPDoc.COLUMNNAME_JP_PP_StartProcess).equals("Y"))
		{
			//Please perform PP Start Process before PP End process.
			msg = Msg.getMsg(getCtx(), "JP_PP_RunEndProcessStartCheck");
			throw new Exception(msg);
		}

		if(po.get_ValueAsBoolean(MPPDoc.COLUMNNAME_Processed)
				|| po.get_ValueAsString(MPPDoc.COLUMNNAME_JP_PP_EndProcess).equals("Y") )
		{
			msg = Msg.getElement(getCtx(), MPPDoc.COLUMNNAME_Processed);
			throw new Exception(msg);
		}

		po.set_ValueNoCheck("JP_PP_End", Timestamp.valueOf(LocalDateTime.now()));
		po.set_ValueNoCheck("JP_PP_Status", MPPDoc.JP_PP_STATUS_Completed);

		if(po.get_ValueAsBoolean(MPPDoc.COLUMNNAME_IsCompleteAutoByEndProcessJP))
		{

			if(po instanceof DocAction)
			{
				if(po instanceof MPPDoc)
				{
					MPPDoc ppDoc = (MPPDoc)po;
					MPPPlan[] ppPlans = ppDoc.getPPPlans(true, null);
					for(MPPPlan ppPlan : ppPlans)
					{
						if(!ppPlan.isProcessed())
						{
							//You cannot be completed PP Doc because there is an unprocessed PP Plan.
							throw new Exception(Msg.getMsg(getCtx(), "JP_PP_NotCompletePPDocForUnprocessedPPPlan"));
						}
					}
				}


				String wfStatus = MWFActivity.getActiveInfo(Env.getCtx(), m_Table.getAD_Table_ID(), record_ID);
				if (Util.isEmpty(wfStatus))
				{
					ProcessInfo pInfo = getProcessInfo();
					pInfo.setPO(po);
					MColumn docActionColumn = MColumn.get(getCtx(), m_Table.getTableName(), MPPDoc.COLUMNNAME_DocAction);
					MProcess process = MProcess.get(docActionColumn.getAD_Process_ID());
					MWFProcess wfProcess = ProcessUtil.startWorkFlow(Env.getCtx(), pInfo, process.getAD_Workflow_ID());
					if(wfProcess.getWFState().equals(MWFProcess.WFSTATE_Terminated))
					{
						msg = ((DocAction) po).getProcessMsg();
						if(Util.isEmpty(msg))
						{
							msg = wfProcess.getTextMsg();
						}

						throw new Exception(msg);
					}

				}else {

					msg = Msg.getMsg(getCtx(), "WFActiveForRecord");

				}
			}
		}

		po.set_ValueNoCheck(MPPDoc.COLUMNNAME_JP_PP_EndProcess, "Y");
		po.saveEx(get_TrxName());

		return msg;
	}

}
