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

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MPeriod;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOM;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0501:JPiere PP Doc
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MPPDoc extends X_JP_PP_Doc implements DocAction,DocOptions
{
	private static final long serialVersionUID = 6288239655685825138L;
	
	public static final String JP_PP_UOM_STDPRECISION = "JP_PP_UOM_STDPRECISION";

	public MPPDoc(Properties ctx, int JP_PP_Doc_ID, String trxName)
	{
		super(ctx, JP_PP_Doc_ID, trxName);
	}

	public MPPDoc(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}


	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//Rounding Production Qty
		if(newRecord || is_ValueChanged(MPPDoc.COLUMNNAME_ProductionQty))
		{
			boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
			MUOM uom = MUOM.get(getC_UOM_ID());
			setProductionQty(getProductionQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
		}


		//Check Doc Type
		if(newRecord || is_ValueChanged(MPPDoc.COLUMNNAME_C_DocType_ID))
		{
			if(!getC_DocType().getDocBaseType().equals("JDD"))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_DifferentDocType")) ;
				return false;
			}
		}


		//For Manual Entory JP_PP_Start
		if(newRecord || is_ValueChanged(COLUMNNAME_JP_PP_Start))
		{
			if(!getDocStatus().equals(DOCSTATUS_Completed)
					&& !getDocStatus().equals(DOCSTATUS_Closed)
					&& !getDocStatus().equals(DOCSTATUS_Voided)
					&& !getDocStatus().equals(DOCSTATUS_Reversed))
			{
				if(getJP_PP_Start() == null)
				{
					setJP_PP_StartProcess("N");
					if(getJP_PP_End() == null)
					{
						setJP_PP_Status(JP_PP_STATUS_NotYetStarted);
					}else {
						setJP_PP_Status(JP_PP_STATUS_Completed);
					}

				}else {

					setJP_PP_StartProcess("Y");
					if(getJP_PP_End() == null)
					{
						setJP_PP_Status(JP_PP_STATUS_WorkInProgress);
					}else {
						setJP_PP_Status(JP_PP_STATUS_Completed);
					}
				}
			}
		}

		//For Manual Entory JP_PP_End
		if(newRecord || is_ValueChanged(COLUMNNAME_JP_PP_End))
		{
			if(!getDocStatus().equals(DOCSTATUS_Completed)
					&& !getDocStatus().equals(DOCSTATUS_Closed)
					&& !getDocStatus().equals(DOCSTATUS_Voided)
					&& !getDocStatus().equals(DOCSTATUS_Reversed))
			{

				if(getJP_PP_End() == null)
				{
					setJP_PP_EndProcess("N");
					if(getJP_PP_Start() == null)
					{
						setJP_PP_Status(JP_PP_STATUS_NotYetStarted);
					}else {
						setJP_PP_Status(JP_PP_STATUS_WorkInProgress);
					}

				}else {

					setJP_PP_EndProcess("Y");
					if(getJP_PP_Status().equals(JP_PP_STATUS_WorkInProgress)
							|| getJP_PP_Status().equals(JP_PP_STATUS_NotYetStarted))
					{
						setJP_PP_Status(JP_PP_STATUS_Completed);
					}
				}
			}
		}


		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		return success;
	}

	@Override
	protected boolean beforeDelete()
	{
		MPPFact[] facts = getPPFacts();
		if(facts.length > 0)
		{
			//You can't delete this PP Doc, because there are some PP Fact.
			log.saveError("Error", Msg.getMsg(getCtx(), "JP_PP_CannotDeleteDoc")) ;
			return false;
		}

		return true;
	}

	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		return getDocumentNo() + "_" + getValue() + "_" + getName();
	}

	/**
	 * 	Create PDF
	 *	@return File or null
	 */
	public File createPDF ()
	{
		return null;
	}

	/**
	 * 	Create PDF file
	 *	@param file output file
	 *	@return file if success
	 */
	public File createPDF (File file)
	{
		return null;
	}


	/**************************************************************************
	 * 	Process document
	 *	@param processAction document action
	 *	@return true if performed
	 */
	public boolean processIt (String processAction)
	{
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}

	/**	Process Message 			*/
	private String		m_processMsg = null;
	/**	Just Prepared Flag			*/
	private boolean		m_justPrepared = false;

	/**
	 * 	Unlock Document.
	 * 	@return true if success
	 */
	public boolean unlockIt()
	{
		setProcessing(false);
		return true;
	}

	/**
	 * 	Invalidate Document
	 * 	@return true if success
	 */
	public boolean invalidateIt()
	{
		setDocAction(DOCACTION_Prepare);
		return true;
	}

	/**
	 *	Prepare Document
	 * 	@return new status (In Progress or Invalid)
	 */
	public String prepareIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;


		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());

		//	Std Period open?
		if (!MPeriod.isOpen(getCtx(), getDateAcct(), dt.getDocBaseType(), getAD_Org_ID()))
		{
			m_processMsg = "@PeriodClosed@";
			return DocAction.STATUS_Invalid;
		}

		MPPPlan[] ppPlans = getPPPlans(true, null);
		if(ppPlans.length == 0)
		{
			m_processMsg = Msg.getMsg(getCtx(), "JP_PP_NoPPPlan");
			return DocAction.STATUS_Invalid;
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}

	/**
	 * 	Approve Document
	 * 	@return true if success
	 */
	public boolean  approveIt()
	{
		setIsApproved(true);
		return true;
	}

	/**
	 * 	Reject Approval
	 * 	@return true if success
	 */
	public boolean rejectIt()
	{
		setIsApproved(false);
		return true;
	}

	/**
	 * 	Complete Document
	 * 	@return new status (Complete, In Progress, Invalid, Waiting ..)
	 */
	public String completeIt()
	{
		//	Re-Check
		if (!m_justPrepared)
		{
			String status = prepareIt();
			m_justPrepared = false;
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}

//		 setDefiniteDocumentNo();

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Implicit Approval
		if (!isApproved())
			approveIt();
		if (log.isLoggable(Level.INFO)) log.info(toString());
		//

//		if(!getJP_PP_StartProcess().equals("Y"))
//		{
//			//Please perform PP Start Process before PP End process.
//			m_processMsg = Msg.getMsg(getCtx(), "JP_PP_RunEndProcessStartCheck");
//			return DocAction.STATUS_Invalid;
//		}

		MPPPlan[] ppPlans = getPPPlans(true, null);
		for(MPPPlan ppPlan : ppPlans)
		{
			if(!ppPlan.isProcessed())
			{
				//You cannot be completed PP Doc because there is an unprocessed PP Plan.
				m_processMsg = Msg.getMsg(getCtx(), "JP_PP_NotCompletePPDocForUnprocessedPPPlan");
				return DocAction.STATUS_InProgress;
			}
		}

		Timestamp now = Timestamp.valueOf(LocalDateTime.now());
		if(getJP_PP_Start() == null)
		{
			setJP_PP_Start(now);
			setJP_PP_StartProcess("Y");
		}


		if(getJP_PP_End() == null)
		{
			setJP_PP_End(now);
			setJP_PP_EndProcess("Y");
		}

		if(!getJP_PP_Status().equals(JP_PP_STATUS_Completed))
			setJP_PP_Status(JP_PP_STATUS_Completed);

		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}

		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}

	/**
	 * 	Set the definite document number after completed
	 */
//	private void setDefiniteDocumentNo()
//	{
//		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
//		if (dt.isOverwriteDateOnComplete()) {
//			setDateInvoiced(TimeUtil.getDay(0));
//			if (getDateAcct().before(getDateInvoiced())) {
//				setDateAcct(getDateInvoiced());
//				MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
//			}
//		}
//		if (dt.isOverwriteSeqOnComplete()) {
//			String value = null;
//			int index = p_info.getColumnIndex("C_DocType_ID");
//			if (index == -1)
//				index = p_info.getColumnIndex("C_DocTypeTarget_ID");
//			if (index != -1)		//	get based on Doc Type (might return null)
//				value = DB.getDocumentNo(get_ValueAsInt(index), get_TrxName(), true);
//			if (value != null) {
//				setDocumentNo(value);
//			}
//		}
//	}


	/**
	 * 	Void Document.
	 * 	Same as Close.
	 * 	@return true if success
	 */
	public boolean voidIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
		if (m_processMsg != null)
			return false;

		MFactAcct.deleteEx(MPPDoc.Table_ID, getJP_PP_Doc_ID(), get_TrxName());
		setPosted(true);

		if(!getJP_PP_Status().equals(JP_PP_STATUS_Completed))
			setJP_PP_Status(JP_PP_STATUS_Void);

		MPPPlan[] ppPlans = getPPPlans(true, null);
		for(MPPPlan ppPlan : ppPlans )
		{
			if(!ppPlan.isProcessed())
			{
				if(ppPlan.processIt(ACTION_Void))
				{
					ppPlan.saveEx(get_TrxName());
				}else {
					m_processMsg = ppPlan.getProcessMsg();
					return false;
				}
			}
		}

		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}

	/**
	 * 	Close Document.
	 * 	Cancel not delivered Qunatities
	 * 	@return true if success
	 */
	public boolean closeIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_CLOSE);
		if (m_processMsg != null)
			return false;

		MPPPlan[] ppPlans = getPPPlans(true, null);
		boolean isOK = true;
		for(MPPPlan ppPlan : ppPlans )
		{

			if(ppPlan.isProcessed())
			{
				if(ppPlan.getDocStatus().equals(ACTION_Complete))
				{
					isOK = ppPlan.processIt(ACTION_Close);
				}

			}else {//Just in case TODO

				//You cannot be closed PP Doc because there is an unprocessed PP Plan.
				m_processMsg = Msg.getMsg(getCtx(), "JP_PP_NotClosedPPDocForUnprocessedPPPlan");
			}


			if(isOK)
			{
				ppPlan.saveEx(get_TrxName());
			}else {

				m_processMsg = ppPlan.getProcessMsg();
				return false;
			}
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_CLOSE);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}


	/**
	 * 	Reverse Correction
	 * 	@return true if success
	 */
	public boolean reverseCorrectIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		if(!getDocStatus().equals(STATUS_Completed))
		{
			//You cannot reverse, because Document status is not completed.
			m_processMsg = Msg.getMsg(getCtx(), "JP_CannotReverseForDocStatus");
			return false;
		}

		if(!reverse(ACTION_Reverse_Correct))
		{
			return false;
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		return true;
	}


	/**
	 * 	Reverse Accrual - none
	 * 	@return true if success
	 */
	public boolean reverseAccrualIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		if(!getDocStatus().equals(STATUS_Completed))
		{
			//You cannot reverse, because Document status is not completed.
			m_processMsg = Msg.getMsg(getCtx(), "JP_CannotReverseForDocStatus");
			return false;
		}

		if(!reverse(ACTION_Reverse_Accrual))
		{
			return false;
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		return true;
	}

	private boolean reverse(String docAction)
	{
		MPPPlan[] ppPlans = getPPPlans(true, null);
		boolean isOK = true;
		for(MPPPlan ppPlan : ppPlans )
		{
			if(ppPlan.getDocStatus().equals(STATUS_Closed)
					|| ppPlan.getDocStatus().equals(STATUS_Voided)
					|| ppPlan.getDocStatus().equals(STATUS_Reversed))
			{
				continue;
			}

			if(ACTION_Reverse_Accrual.equals(docAction))
			{
				if(!ppPlan.reverseCorrectIt())
					isOK = false;

			}else if(ACTION_Reverse_Correct.equals(docAction)) {

				if(!ppPlan.reverseAccrualIt())
					isOK = false;
			}

			if(isOK)
			{
				ppPlan.saveEx(get_TrxName());
			}else{
				m_processMsg = ppPlan.getProcessMsg();
				return false;
			}
		}

		if(!getJP_PP_Status().equals(JP_PP_STATUS_Completed))
			setJP_PP_Status(JP_PP_STATUS_Void);

		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}

	/**
	 * 	Re-activate
	 * 	@return true if success
	 */
	public boolean reActivateIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;

		MFactAcct.deleteEx(MPPDoc.Table_ID, getJP_PP_Doc_ID(), get_TrxName());
		setPosted(false);
		setIsApproved(false);
		setJP_PP_Status(JP_PP_STATUS_WorkInProgress);
		setJP_PP_End(null);
		setJP_PP_EndProcess("N");

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;

		setDocAction(DOCACTION_Complete);
		setProcessed(false);

		return true;
	}


	/*************************************************************************
	 * 	Get Summary
	 *	@return Summary of Document
	 */
	public String getSummary()
	{
		return getValue()+"_"+getName();
	}


	/**
	 * 	Get Process Message
	 *	@return clear text error message
	 */
	public String getProcessMsg()
	{
		return m_processMsg;
	}

	/**
	 * 	Get Document Owner (Responsible)
	 *	@return AD_User_ID
	 */
	public int getDoc_User_ID()
	{
		return getSalesRep_ID();
	}

	/**
	 * 	Get Document Approval Amount
	 *	@return amount
	 */
	public BigDecimal getApprovalAmt()
	{
		return Env.ZERO;
	}


	/**
	 * 	Get Document Currency
	 *	@return C_Currency_ID
	 */
	public int getC_Currency_ID()
	{
		return 0;
	}

	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index)
	{
		if (docStatus.equals(DocumentEngine.STATUS_Drafted) || docStatus.equals(DocumentEngine.STATUS_InProgress))
		{
			index = 0;
			options[index++] = DocumentEngine.ACTION_Void;
			options[index++] = DocumentEngine.ACTION_Prepare;
			options[index++] = DocumentEngine.ACTION_Complete;

		}else if(docStatus.equals(DocumentEngine.STATUS_Completed)) {

			index = 0;
			options[index++] = DocumentEngine.ACTION_Reverse_Accrual;
			options[index++] = DocumentEngine.ACTION_Reverse_Correct;
			options[index++] = DocumentEngine.ACTION_Close;
			options[index++] = DocumentEngine.ACTION_ReActivate;

		}


		return index;
	}

	private MPPPlan[] m_PPPlans = null;

	public MPPPlan[] getPPPlans (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPPlan.COLUMNNAME_JP_PP_Doc_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPPlan.COLUMNNAME_SeqNo;
		//
		List<MPPPlan> list = new Query(getCtx(), MPPPlan.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPPlan[list.size()]);

	}

	public MPPPlan[] getPPPlans(boolean requery, String orderBy)
	{
		if (m_PPPlans != null && !requery) {
			set_TrxName(m_PPPlans, get_TrxName());
			return m_PPPlans;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPPlan.COLUMNNAME_SeqNo;

		m_PPPlans = getPPPlans(null, orderClause);
		return m_PPPlans;
	}

	public MPPPlan[] getPPPlans()
	{
		return getPPPlans(false, null);
	}


	private MPPFact[] m_PPFacts = null;

	public MPPFact[] getPPFacts (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPFact.COLUMNNAME_JP_PP_Doc_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPFact.COLUMNNAME_JP_PP_Fact_ID;
		//
		List<MPPFact> list = new Query(getCtx(), MPPFact.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPFact[list.size()]);

	}

	public MPPFact[] getPPFacts(boolean requery, String orderBy)
	{
		if (m_PPFacts != null && !requery) {
			set_TrxName(m_PPFacts, get_TrxName());
			return m_PPFacts;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPFact.COLUMNNAME_JP_PP_Fact_ID;

		m_PPFacts = getPPFacts(null, orderClause);
		return m_PPFacts;
	}

	public MPPFact[] getPPFacts()
	{
		return getPPFacts(false, null);
	}


	public MPPPlan getPPPlan(int seqNo, int M_Product_ID, String value)
	{
		getPPPlans();
		for(MPPPlan ppPlan : m_PPPlans)
		{
			if(ppPlan.getSeqNo() == seqNo
					&& ppPlan.getM_Product_ID() == M_Product_ID
					&& ppPlan.getValue().equals(value))
			{
				return ppPlan;
			}
		}

		return null;
	}

	public static MPPDoc get (Properties ctx, String Value, String trxName)
	{
		if (Value == null || Value.length() == 0)
			return null;
		final String whereClause = "Value=? AND AD_Client_ID=?";
		MPPDoc retValue = new Query(ctx, MPPDoc.Table_Name, whereClause, trxName)
		.setParameters(Value,Env.getAD_Client_ID(ctx))
		.firstOnly();
		return retValue;
	}

}	//	MPPDoc
