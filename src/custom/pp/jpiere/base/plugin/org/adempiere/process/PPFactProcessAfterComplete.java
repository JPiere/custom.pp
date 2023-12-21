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

import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPFact;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPMMPlanLine;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlan;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPWorkProcess;


/**
 * JPIERE-0501:JPiere PP Fact after complete Process
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class PPFactProcessAfterComplete extends SvrProcess {

	private int p_JP_PP_Fact_ID = 0;

	@Override
	protected void prepare()
	{
		p_JP_PP_Fact_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{
		String msg = "@OK@";

		MPPFact ppFact = new MPPFact(getCtx(), p_JP_PP_Fact_ID, get_TrxName());
		if(!ppFact.getDocStatus().equals(DocAction.STATUS_Completed))
		{
			msg = Msg.getMsg(getCtx(),"JP_Not_Completed_Document");
			addBufferLog(0, null, null, msg + " - "+ppFact.getDocumentNo(), MPPFact.Table_ID ,ppFact.getJP_PP_Fact_ID());
			return msg;
		}

		MPPPlan parent = ppFact.getParent();
		boolean isCompleteAutoJP = parent.isCompleteAutoJP();
		boolean isSplitWhenDifferenceJP = parent.isSplitWhenDifferenceJP();
		String JP_PP_WorkProcessType = parent.getJP_PP_WorkProcessType();
		if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialProduction.equals(JP_PP_WorkProcessType))
		{
			if(isCompleteAutoJP)
			{
				isCompleteAutoJP = parent.getJP_ProductionQtyFact().compareTo(parent.getProductionQty()) >= 0;
			}
			
		}else if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialMovement.equals(JP_PP_WorkProcessType)) {
			
			MPPMMPlanLine[] m_PPMMLines = parent.getPPMMPlanLines();
			boolean isDiff = false;
			for(MPPMMPlanLine m_PPMMLine: m_PPMMLines)
			{
				if(m_PPMMLine.getMovementQty().compareTo(m_PPMMLine.getJP_MovementQtyFact()) > 0)
				{
					isDiff = true;
					continue;
				}
			}
			
			if(isDiff)
			{
				isCompleteAutoJP = false;
				
			}
			
		}else if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_NotCreateDocument.equals(JP_PP_WorkProcessType)) {
			
			isSplitWhenDifferenceJP = false;
			
		}

		if(isCompleteAutoJP)
		{
			MPPFact[] ppFacts = parent.getPPFacts(true, null);
			for(MPPFact fact : ppFacts)
			{
				if(!fact.isProcessed())
				{
					//You cannot be completed PP Plan because there is an unprocessed PP Fact.
					msg = Msg.getMsg(getCtx(), "JP_PP_NotCompletePPPlanForUnprocessedPPFact");
					addLog(msg);

					return msg;
				}
			}

			String wfStatus = MWFActivity.getActiveInfo(Env.getCtx(), MPPPlan.Table_ID, parent.getJP_PP_Plan_ID());
			if (Util.isEmpty(wfStatus))
			{
				ProcessInfo pInfo = getProcessInfo();
				pInfo.setPO(parent);
				pInfo.setRecord_ID(parent.getJP_PP_Plan_ID());
				pInfo.setTable_ID(MPPPlan.Table_ID);
				MColumn docActionColumn = MColumn.get(getCtx(), MPPPlan.Table_Name, MPPPlan.COLUMNNAME_DocAction);
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

		}else {

			if(isSplitWhenDifferenceJP)
			{
				msg = parent.createFact(get_TrxName());
				if(!Util.isEmpty(msg))
					addLog(msg);

			}else {

				;//Noting to do
			}

		}


		return msg;
	}

}