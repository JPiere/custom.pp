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

import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPDoc;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPFact;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlan;


/**
 * JPIERE-0501:JPiere PP Start Process
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class PPStartProcess extends SvrProcess {

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
		if(po.get_ValueAsBoolean(MPPDoc.COLUMNNAME_Processed))
		{
			msg = Msg.getElement(getCtx(), MPPDoc.COLUMNNAME_Processed);
			return msg;
		}

		po.set_ValueNoCheck("JP_PP_Start", Timestamp.valueOf(LocalDateTime.now()));
		po.set_ValueNoCheck("JP_PP_Status", MPPPlan.JP_PP_STATUS_WorkInProgress);

		if(po instanceof DocAction)
		{
			DocAction doc = (DocAction)po;
			if(doc.getDocStatus().equals(DocAction.STATUS_Drafted)
					|| doc.getDocStatus().equals(DocAction.STATUS_Invalid)
					|| doc.getDocStatus().equals(DocAction.STATUS_InProgress))
			{
				if(!doc.processIt(DocAction.ACTION_Prepare))
				{
					throw new Exception(doc.getProcessMsg());
				}
			}else {
				;
			}
		}

		po.set_ValueNoCheck(MPPDoc.COLUMNNAME_JP_PP_StartProcess, "Y");
		po.saveEx(get_TrxName());

		if(po instanceof MPPFact)
		{
			MPPFact ppFact = (MPPFact)po;
			MPPPlan ppPlan = new MPPPlan(getCtx(), ppFact.getJP_PP_Plan_ID(), get_TrxName());
			if(ppPlan.getJP_PP_Status().equals(MPPPlan.JP_PP_STATUS_NotYetStarted))
			{
				Timestamp now =	Timestamp.valueOf(LocalDateTime.now());
				ppPlan.setJP_PP_Start(now);
				ppPlan.setJP_PP_Status(MPPPlan.JP_PP_STATUS_WorkInProgress);
				ppPlan.saveEx(get_TrxName());

				MPPDoc ppDoc = ppPlan.getParent();
				if(ppDoc.getJP_PP_Status().equals(MPPPlan.JP_PP_STATUS_NotYetStarted))
				{
					ppDoc.setJP_PP_Start(now);
					ppDoc.setJP_PP_Status(MPPDoc.JP_PP_STATUS_WorkInProgress);
					ppDoc.setJP_PP_StartProcess("Y");
					ppDoc.saveEx(get_TrxName());
				}

			}

		}

		return msg;
	}

}
