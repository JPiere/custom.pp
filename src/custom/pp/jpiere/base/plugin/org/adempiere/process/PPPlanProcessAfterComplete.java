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
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFProcess;

import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPDoc;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPFact;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlan;


/**
 * JPIERE-0501:JPiere PP Plan after complete Process
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class PPPlanProcessAfterComplete extends SvrProcess {

	private int p_JP_PP_Plan_ID = 0;

	@Override
	protected void prepare()
	{
		p_JP_PP_Plan_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{
		String msg = "@OK@";

		MPPPlan ppPlan = new MPPPlan(getCtx(), p_JP_PP_Plan_ID, get_TrxName());

		if(!ppPlan.getDocStatus().equals(DocAction.STATUS_Completed))
		{
			msg = Msg.getMsg(getCtx(),"JP_Not_Completed_Document");
			addBufferLog(0, null, null, msg + " - "+ ppPlan.getDocumentNo(), MPPFact.Table_ID ,ppPlan.getJP_PP_Plan_ID());
			return msg;
		}

		MPPDoc parent = ppPlan.getParent();
		if(parent.isCompleteAutoJP())
		{
			MPPPlan[] ppPlans = parent.getPPPlans(true, null);
			boolean isAllProcessed = true;
			for(MPPPlan plan : ppPlans)
			{
				if(!plan.isProcessed())
				{
					isAllProcessed = false;
					break;
				}
			}

			if(isAllProcessed)
			{
				Timestamp now = Timestamp.valueOf(LocalDateTime.now());
				if(parent.getJP_PP_Start() == null)
				{
					parent.setJP_PP_Start(now);
					parent.setJP_PP_StartProcess("Y");
				}


				if(parent.getJP_PP_End() == null)
				{
					parent.setJP_PP_End(now);
					parent.setJP_PP_EndProcess("Y");
				}

				String wfStatus = MWFActivity.getActiveInfo(Env.getCtx(), MPPDoc.Table_ID, parent.getJP_PP_Doc_ID());
				if (Util.isEmpty(wfStatus))
				{
					ProcessInfo pInfo = getProcessInfo();
					pInfo.setPO(parent);
					pInfo.setRecord_ID(parent.getJP_PP_Doc_ID());
					pInfo.setTable_ID(MPPDoc.Table_ID);
					MColumn docActionColumn = MColumn.get(getCtx(), MPPDoc.Table_Name, MPPDoc.COLUMNNAME_DocAction);
					MProcess process = MProcess.get(docActionColumn.getAD_Process_ID());
					MWFProcess wfProcess = ProcessUtil.startWorkFlow(Env.getCtx(), pInfo, process.getAD_Workflow_ID());
					if(wfProcess.getWFState().equals(MWFProcess.WFSTATE_Terminated))
					{
						msg = wfProcess.getTextMsg();
						addLog(msg);
					}

					return msg;

				}else {

					//Active Workflow for this Record exists (complete first):
					msg = Msg.getMsg(getCtx(), "WFActiveForRecord");
					addLog(msg);

					return msg;
				}
			}
		}

		return msg;
	}

}
