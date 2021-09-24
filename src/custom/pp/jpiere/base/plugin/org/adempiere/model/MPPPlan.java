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
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOM;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0501:JPiere PP Plan
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MPPPlan extends X_JP_PP_Plan implements DocAction,DocOptions
{

	public MPPPlan(Properties ctx, int JP_PP_Plan_ID, String trxName)
	{
		super(ctx, JP_PP_Plan_ID, trxName);
	}

	public MPPPlan(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	private MPPDoc m_PPDoc = null;

	public MPPDoc getParent()
	{
		if(m_PPDoc == null)
			m_PPDoc = new MPPDoc(getCtx(), getJP_PP_Doc_ID(), get_TrxName());
		else
			m_PPDoc.set_TrxName(get_TrxName());

		return m_PPDoc;
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//SetAD_Org_ID
		if(newRecord)
		{
			setAD_Org_ID(getParent().getAD_Org_ID());
		}

		//Check Parent processed
		if(newRecord)
		{
			MPPDoc ppDoc = getParent();
			if(ppDoc.isProcessed())
			{
				log.saveError("Error", Msg.getElement(getCtx(), MPPDoc.COLUMNNAME_Processed));
				return false;
			}
		}

		//Set C_UOM_ID
		if(newRecord || is_ValueChanged(MPPPlan.COLUMNNAME_C_UOM_ID) || getC_UOM_ID() == 0)
		{
			MProduct product = MProduct.get(getM_Product_ID());
			if(product.getC_UOM_ID() != getC_UOM_ID())
			{
				setC_UOM_ID(product.getC_UOM_ID());
			}
		}

		//Check JP_PP_PlanT_ID
		if( (newRecord || is_ValueChanged(MPPPlan.COLUMNNAME_JP_PP_PlanT_ID))
					&& getJP_PP_PlanT_ID() != 0)
		{
			MPPPlanT ppPlanT = new MPPPlanT(getCtx(), getJP_PP_PlanT_ID(), get_TrxName());
			if(getM_Product_ID() != ppPlanT.getM_Product_ID())
			{
				//Different between {0} and {1}
				String msg0 = Msg.getElement(Env.getCtx(), MPPPlan.COLUMNNAME_JP_PP_Plan_ID) + " - " + Msg.getElement(Env.getCtx(), MPPPlan.COLUMNNAME_M_Product_ID);
				String msg1 = Msg.getElement(Env.getCtx(), MPPPlanT.COLUMNNAME_JP_PP_PlanT_ID) + " - " + Msg.getElement(Env.getCtx(),  MPPPlan.COLUMNNAME_M_Product_ID);
				String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});

				log.saveError("Error", msg);
				return false;
			}
		}

		//Rounding Production Qty
		if(newRecord || is_ValueChanged(MPPPlan.COLUMNNAME_ProductionQty))
		{
			boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
			MUOM uom = MUOM.get(getC_UOM_ID());
			setProductionQty(getProductionQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
		}

		if(newRecord || is_ValueChanged(MPPPlan.COLUMNNAME_JP_ProductionQtyFact))
		{
			boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
			MUOM uom = MUOM.get(getC_UOM_ID());
			setJP_ProductionQtyFact(getJP_ProductionQtyFact().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
		}

		//Check Doc Type
		if(newRecord || is_ValueChanged(MPPDoc.COLUMNNAME_C_DocType_ID))
		{
			if(!getC_DocType().getDocBaseType().equals("JDP"))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_DifferentDocType")) ;
				return false;
			}
		}


		//For Tree
		setName(getJP_Name());

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		//Update Line Qty
		if(!newRecord && is_ValueChanged(MPPPlan.COLUMNNAME_ProductionQty))
		{
			boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
			MUOM uom = null;

			BigDecimal newQty = getProductionQty();
			BigDecimal oldQty = (BigDecimal)get_ValueOld(MPPPlan.COLUMNNAME_ProductionQty) ;
			BigDecimal rate = Env.ONE;
			if(oldQty != null && oldQty.compareTo(Env.ZERO) != 0)
				rate = newQty.divide(oldQty, 4, RoundingMode.HALF_UP);

			MPPPlanLine[] lines = getPPPlanLines(true, null);
			for(MPPPlanLine line : lines)
			{
				if(line.isEndProduct())
				{
					line.setPlannedQty(getProductionQty());
					line.setQtyUsed(null);
					line.setMovementQty(getProductionQty());

				}else {
					uom = MUOM.get(line.getC_UOM_ID());
					oldQty = line.getPlannedQty();
					newQty = oldQty.multiply(rate).setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP);
					line.setPlannedQty(newQty);
					line.setQtyUsed(newQty);
					line.setMovementQty(newQty.negate());
				}
				if(!line.save(get_TrxName()))
				{
					String msg =  Msg.getElement(getCtx(), MPPPlanLine.COLUMNNAME_JP_PP_PlanLine_ID)
				 			+ " - " + Msg.getElement(getCtx(), MPPPlanLine.COLUMNNAME_Line) + " : " + line.getLine();
					log.saveError("SaveError", msg);
					m_processMsg = msg;
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		return getValue() + "_" + getName();
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


		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}	//	prepareIt

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


		MPPFact[] ppFacts = getPPFacts(true, null);
		for(MPPFact ppFact : ppFacts)
		{
			if(!ppFact.isProcessed())
			{
				//You cannot be completed PP Plan because there is an unprocessed PP Fact.
				m_processMsg = Msg.getMsg(getCtx(), "JP_PP_NotCompletePPPlanForUnprocessedPPFact");
				return DocAction.STATUS_Invalid;
			}
		}

		setJP_PP_Status(JP_PP_STATUS_Completed);
		Timestamp now = Timestamp.valueOf(LocalDateTime.now());
		if(getJP_PP_Start() == null)
		{
			setJP_PP_Start(now);
		}
		setJP_PP_End(now);

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
	}	//	completeIt

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

		MFactAcct.deleteEx(MPPPlan.Table_ID, getJP_PP_Plan_ID(), get_TrxName());
		setPosted(true);

		if(!getJP_PP_Status().equals(JP_PP_STATUS_Completed))
			setJP_PP_Status(JP_PP_STATUS_Void);

		MPPFact[] ppFacts = getPPFacts(true, null);
		for(MPPFact ppFact : ppFacts)
		{
			if(!ppFact.isProcessed())
			{
				if(ppFact.processIt(ACTION_Void))
				{
					ppFact.saveEx(get_TrxName());
				}else {
					m_processMsg = ppFact.getProcessMsg();
					return false;
				}
			}
		}

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

		MPPFact[] ppFacts = getPPFacts(true, null);
		boolean isOK = true;
		for(MPPFact ppFact : ppFacts)
		{
			if(ppFact.isProcessed())
			{
				if(ppFact.getDocStatus().equals(STATUS_Completed))
				{
					isOK = ppFact.processIt(ACTION_Close);
				}

			}else{//Just in case

				//You cannot be closed PP Plan because there is an unprocessed PP Fact.
				m_processMsg = Msg.getMsg(getCtx(), "JP_PP_NotClosedPPPlanForUnprocessedPPFact");
				return false;
			}

			if(isOK)
			{
				ppFact.saveEx(get_TrxName());
			}else {
				m_processMsg = ppFact.getProcessMsg();
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

		if(!reverse(DocAction.ACTION_Reverse_Correct))
			return false;


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

		if(!reverse(DocAction.ACTION_Reverse_Accrual))
			return false;


		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		return true;
	}


	private boolean reverse(String docAction)
	{
		MPPFact[] ppFacts = getPPFacts(true, null);
		boolean isOK = true;
		for(MPPFact ppFact : ppFacts)
		{
			if(ppFact.isProcessed())
			{
				if(ppFact.getDocStatus().equals(ACTION_Complete))
					isOK = ppFact.processIt(docAction);

			}else {

				isOK = ppFact.processIt(ACTION_Void);
			}

			if(isOK)
			{
				ppFact.saveEx(get_TrxName());
			}else {
				m_processMsg = ppFact.getProcessMsg();
				return false;
			}
		}

		if(!getJP_PP_Status().equals(JP_PP_STATUS_Completed))
			setJP_PP_Status(JP_PP_STATUS_Void);

		setProcessed(true);
		setPosted(true);
		setDocStatus(STATUS_Reversed);
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

		MFactAcct.deleteEx(MPPPlan.Table_ID, getJP_PP_Plan_ID(), get_TrxName());
		setPosted(false);
		setIsApproved(false);
		setJP_PP_Status(JP_PP_STATUS_WorkInProgress);
		setJP_PP_End(null);

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;

		setDocAction(DOCACTION_Complete);
		setProcessed(false);

		return true;
	}	//	reActivateIt


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

	/**
	 * 	Set Processed.
	 *
	 *	@param processed processed
	 */
	public void setProcessed (boolean processed)
	{
		super.setProcessed (processed);
		if (get_ID() == 0)
			return;
		String set = "SET Processed='"
			+ (processed ? "Y" : "N")
			+ "' WHERE JP_PP_Plan_ID=" + getJP_PP_Plan_ID();
		DB.executeUpdateEx("UPDATE JP_PP_PlanLine " + set, get_TrxName());
		m_PPPlanLines = null;
	}

	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index)
	{
		if (docStatus.equals(DocumentEngine.STATUS_Drafted) || docStatus.equals(DocumentEngine.STATUS_InProgress))
		{
			index = 0;
			options[index++] = DocumentEngine.ACTION_Void;
			options[index++] = DocumentEngine.ACTION_Reverse_Accrual;
			options[index++] = DocumentEngine.ACTION_Reverse_Correct;
			options[index++] = DocumentEngine.ACTION_Prepare;
			options[index++] = DocumentEngine.ACTION_Complete;

			return index;

		}else if(docStatus.equals(DocumentEngine.STATUS_Completed)) {

			index = 0;
			options[index++] = DocumentEngine.ACTION_Void;
			options[index++] = DocumentEngine.ACTION_Reverse_Accrual;
			options[index++] = DocumentEngine.ACTION_Reverse_Correct;
			options[index++] = DocumentEngine.ACTION_Close;
			options[index++] = DocumentEngine.ACTION_ReActivate;

			return index;
		}


		return index;
	}


	private MPPPlanLine[] m_PPPlanLines = null;

	/**
	 * Get PP Plan Lines
	 *
	 * @param whereClause
	 * @param orderClause
	 * @return
	 */
	public MPPPlanLine[] getPPPlanLines (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPPlanLine.COLUMNNAME_JP_PP_Plan_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPPlanLine.COLUMNNAME_Line;
		//
		List<MPPPlanLine> list = new Query(getCtx(), MPPPlanLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPPlanLine[list.size()]);

	}

	/**
	 * Get PP Plan Lines
	 *
	 *
	 * @param requery
	 * @param orderBy
	 * @return
	 */
	public MPPPlanLine[] getPPPlanLines(boolean requery, String orderBy)
	{
		if (m_PPPlanLines != null && !requery) {
			set_TrxName(m_PPPlanLines, get_TrxName());
			return m_PPPlanLines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPPlanLine.COLUMNNAME_Line;

		m_PPPlanLines = getPPPlanLines(" AND IsActive='Y' ", orderClause);
		return m_PPPlanLines;
	}

	/**
	 * Get PP Plan Lines
	 *
	 *
	 * @return
	 */
	public MPPPlanLine[] getPPPlanLines()
	{
		return getPPPlanLines(false, null);

	}



	private MPPFact[] m_PPFacts = null;

	/**
	 * Get PP Facts
	 *
	 * @param whereClause
	 * @param orderClause
	 * @return
	 */
	public MPPFact[] getPPFacts (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPFact.COLUMNNAME_JP_PP_Plan_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPFact.COLUMNNAME_DocumentNo;
		//
		List<MPPFact> list = new Query(getCtx(), MPPFact.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPFact[list.size()]);

	}

	/**
	 * Get PP Facts
	 *
	 *
	 * @param requery
	 * @param orderBy
	 * @return
	 */
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
			orderClause += MPPFact.COLUMNNAME_DocumentNo;

		m_PPFacts = getPPFacts(null, orderClause);
		return m_PPFacts;
	}

	/**
	 * Get PP Facts
	 *
	 *
	 * @return
	 */
	public MPPFact[] getPPFacts()
	{
		return getPPFacts(false, null);

	}

	/**
	 *
	 * Create Fact from Plan
	 *
	 * @return
	 */
	public String createFact(String trxName)
	{
		String msg = null;

		if(!isHaveEndProduct())
		{
			//PP Lines does not contain End Product
			return Msg.getMsg(getCtx(), "JP_PP_NotContainEndProduct");
		}

		MPPFact[] ppFacts = getPPFacts(true, null);
		for(MPPFact ppFact : ppFacts)
		{
			if( ppFact.getJP_PP_Status().equals(MPPFact.JP_PP_STATUS_WorkInProgress)
				|| ppFact.getJP_PP_Status().equals(MPPFact.JP_PP_STATUS_NotYetStarted))
			{
				//You cannot create a new PP Fact if you have a PP Fact that has not been complete.
				return Msg.getMsg(getCtx(), "JP_PP_CannotCreateFact");
			}
		}

		MPPPlanLine[] ppPLines = getPPPlanLines(true, null);
		MPPFact ppFact = new MPPFact(getCtx(), 0, get_TrxName());
		PO.copyValues(this, ppFact);

		//Copy mandatory column to make sure
		ppFact.setDocumentNo(null);
		ppFact.setJP_PP_Doc_ID(getJP_PP_Doc_ID());
		ppFact.setJP_PP_Plan_ID(getJP_PP_Plan_ID());
		ppFact.setValue(getValue());
		ppFact.setAD_Org_ID(getAD_Org_ID());
		if(getJP_PP_PlanT_ID() == 0)
		{
			ppFact.setC_DocType_ID(MDocType.getDocType("JDF"));
		}else {
			ppFact.setC_DocType_ID(getJP_PP_PlanT().getC_DocTypeTarget_ID());
		}
		ppFact.setDocumentNo(null);
		ppFact.setName(getJP_Name());
		ppFact.setMovementDate(getDateAcct());
		if(ppPLines.length > 0)
			ppFact.setIsCreated("Y");
		ppFact.setProductionQty(getProductionQty().subtract(getJP_ProductionQtyFact()));
		ppFact.setM_Product_ID(getM_Product_ID());
		ppFact.setC_UOM_ID(getC_UOM_ID());
		ppFact.setJP_PP_Workload_UOM_ID(getJP_PP_Workload_UOM_ID());
		ppFact.setJP_PP_Workload_Fact(Env.ZERO);
		ppFact.setJP_PP_Start(null);
		ppFact.setJP_PP_End(null);
		ppFact.setDocStatus(STATUS_Drafted);
		ppFact.setDocAction(ACTION_Complete);
		ppFact.setJP_PP_Status(JP_PP_STATUS_NotYetStarted);
		ppFact.setJP_Processing1("N");
		ppFact.setJP_Processing2("N");
		ppFact.setJP_Processing3("N");
		ppFact.setJP_Processing4("N");
		ppFact.setJP_Processing5("N");
		ppFact.setJP_Processing6("N");
		if(!ppFact.save(get_TrxName()))
		{
			msg = Msg.getMsg(getCtx(), "JP_CouldNotCreate")
					+ " " + Msg.getMsg(getCtx(), "SaveError") + " - "+ Msg.getElement(getCtx(), MPPFact.COLUMNNAME_JP_PP_Fact_ID);

			m_processMsg = msg;
			log.saveError("SaveError", msg);

			return msg;
		}

		msg = ppFact.createFactLineFromPlanLine(trxName);
		if(!Util.isEmpty(msg))
		{
			return msg;
		}

		return msg;
	}

	public boolean isHaveEndProduct()
	{
		MPPPlanLine[] lines = getPPPlanLines();

		for(MPPPlanLine line : lines) {
			if(line.isEndProduct())
				return true;
		}

		return false;
	}

}
