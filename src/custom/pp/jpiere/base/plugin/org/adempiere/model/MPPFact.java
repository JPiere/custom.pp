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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.NegativeInventoryDisallowedException;
import org.compiere.model.I_M_AttributeSet;
import org.compiere.model.I_M_Production;
import org.compiere.model.I_M_ProductionLine;
import org.compiere.model.I_M_ProductionLineMA;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MPeriod;
import org.compiere.model.MProduct;
import org.compiere.model.MProductionLine;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
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
 * JPIERE-0501:JPiere PP Fact
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MPPFact extends X_JP_PP_Fact implements DocAction,DocOptions
{

	public MPPFact(Properties ctx, int JP_PP_Fact_ID, String trxName)
	{
		super(ctx, JP_PP_Fact_ID, trxName);
	}

	public MPPFact(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	private MPPPlan m_PPPlan = null;

	public MPPPlan getParent()
	{
		if(m_PPPlan == null)
			m_PPPlan = new MPPPlan(getCtx(), getJP_PP_Plan_ID(), get_TrxName());
		else
			m_PPPlan.set_TrxName(get_TrxName());

		return m_PPPlan;
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
			if(getParent().isProcessed())
			{
				log.saveError("Error", Msg.getElement(getCtx(), MPPFact.COLUMNNAME_Processed));
				return false;
			}
		}

		//Set M_Product_ID
		if(newRecord || is_ValueChanged(MPPFact.COLUMNNAME_M_Product_ID) || getM_Product_ID() == 0)
		{

			setM_Product_ID(getParent().getM_Product_ID());
		}

		//Set C_UOM_ID
		if(newRecord || is_ValueChanged(MPPFact.COLUMNNAME_C_UOM_ID) || getC_UOM_ID() == 0)
		{
			MProduct product = MProduct.get(getM_Product_ID());
			if(product.getC_UOM_ID() != getC_UOM_ID())
			{
				setC_UOM_ID(product.getC_UOM_ID());
			}
		}

		//Set JP_PP_Workload_UOM_ID
		if(newRecord || is_ValueChanged(MPPFact.COLUMNNAME_JP_PP_Workload_UOM_ID) || getJP_PP_Workload_UOM_ID() == 0)
		{
			setJP_PP_Workload_UOM_ID(getParent().getJP_PP_Workload_UOM_ID());
		}


		//Rounding Production Qty
		if(newRecord || is_ValueChanged(MPPPlan.COLUMNNAME_ProductionQty))
		{
			boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
			MUOM uom = MUOM.get(getC_UOM_ID());
			setProductionQty(getProductionQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
		}


		//Check Doc Type
		if(newRecord || is_ValueChanged(MPPDoc.COLUMNNAME_C_DocType_ID))
		{
			if(!getC_DocType().getDocBaseType().equals("JDF"))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_DifferentDocType")) ;
				return false;
			}
		}


		return true;
	}



	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		//Update Line Qty
		if(!newRecord && is_ValueChanged(MPPFact.COLUMNNAME_ProductionQty))
		{
			boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
			MUOM uom = null;
			BigDecimal newQty = getProductionQty();
			BigDecimal oldQty = (BigDecimal)get_ValueOld(MPPFact.COLUMNNAME_ProductionQty) ;
			BigDecimal rate = Env.ONE;
			if(oldQty != null && oldQty.compareTo(Env.ZERO) != 0)
				rate = newQty.divide(oldQty, 4, RoundingMode.HALF_UP);

			MPPFactLine[] lines = getPPFactLines(true, null);
			for(MPPFactLine line : lines)
			{
				if(line.isEndProduct())
				{
					line.setMovementQty(newQty);
				}else {
					uom = MUOM.get(line.getC_UOM_ID());
					oldQty = line.getQtyUsed();
					if(oldQty.compareTo(Env.ZERO) == 0)
					{
						;//Noting to do because 0 * X = 0;
					}else {
						newQty = oldQty.multiply(rate).setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP);
						line.setQtyUsed(newQty);
					}
				}
				line.saveEx(get_TrxName());
			}
		}

		//Update PP Plan JP_PP_Workload_Fact
		if(newRecord || is_ValueChanged(MPPFact.COLUMNNAME_JP_PP_Workload_Fact))
		{

			int no = updateParentWorkloadFact(get_TrxName());

			if (no != 1)
			{
					log.saveError("DBExecuteError", "MPPFact#afterSave() -> updateParentWorkloadFact()");
					return false;
			}
		}

		//Update PP Plan JP_ProductionQtyFact
		if(is_ValueChanged(COLUMNNAME_DocStatus))
		{
			int no = updateParentProductionQtyFact(get_TrxName());
			if (no != 1)
			{
					m_processMsg = Msg.getMsg(getCtx(), "DBExecuteError") + " : " +"MPPFact#afterSave() -> updateParentProductionQtyFact()";
					return false;
			}

			//Update PP Plan Line Fact Qty
			updatePlanLineQtyFact(get_TrxName());
		}
		return true;
	}



	@Override
	protected boolean afterDelete(boolean success)
	{
		int no = updateParentWorkloadFact(get_TrxName());
		if (no != 1)
		{
			log.saveError("DBExecuteError", "MPPFact#afterDelete() -> updateParentWorkloadFact()");
			return false;
		}

		return true;
	}

	private int updateParentWorkloadFact(String trxName)
	{
		String sql = "UPDATE JP_PP_Plan SET JP_PP_Workload_Fact=(SELECT COALESCE(SUM(JP_PP_Workload_Fact),0) FROM JP_PP_Fact WHERE JP_PP_Plan_ID =?) "
				+ " WHERE JP_PP_Plan_ID=?";

		int no = DB.executeUpdate(sql
						, new Object[]{ getJP_PP_Plan_ID(), getJP_PP_Plan_ID()}
						, false, trxName, 0);

		return no;
	}

	private int updateParentProductionQtyFact(String trxName)
	{
		String sql = "UPDATE JP_PP_Plan SET JP_ProductionQtyFact = (SELECT COALESCE(SUM(fl.MovementQty),0) "
				+ " FROM JP_PP_FactLine fl INNER JOIN JP_PP_Fact f ON (fl.JP_PP_Fact_ID=f.JP_PP_Fact_ID) "
				+ " WHERE f.JP_PP_Plan_ID =? AND f.DocStatus in ('CO','CL') AND fl.IsEndProduct='Y' ) "
				+ " WHERE JP_PP_Plan_ID=?" ;

		int no = DB.executeUpdate(sql
						, new Object[]{ getJP_PP_Plan_ID(), getJP_PP_Plan_ID()}
						, false, trxName, 0);

		return no;
	}

	private int updatePlanLineQtyFact(String trxName)
	{
		String sql = "UPDATE JP_PP_PlanLine pl "
				+ " SET JP_QtyUsedFact = (SELECT COALESCE(SUM(fl.QtyUsed),0) "
				+ " FROM JP_PP_FactLine fl INNER JOIN JP_PP_Fact f ON (fl.JP_PP_Fact_ID=f.JP_PP_Fact_ID) "
				+ " WHERE f.JP_PP_Plan_ID =? AND f.DocStatus in ('CO','CL') AND fl.JP_PP_PlanLine_ID=pl.JP_PP_PlanLine_ID AND fl.IsEndProduct='N') "
				+ " WHERE pl.JP_PP_Plan_ID= ? AND pl.IsEndProduct='N' " ;

		int no = DB.executeUpdate(sql
						, new Object[]{ getJP_PP_Plan_ID(), getJP_PP_Plan_ID()}
						, false, trxName, 0);

		sql = "UPDATE JP_PP_PlanLine pl "
				+ " SET JP_MovementQtyFact = (SELECT COALESCE(SUM(fl.MovementQty),0) "
				+ " FROM JP_PP_FactLine fl INNER JOIN JP_PP_Fact f ON (fl.JP_PP_Fact_ID=f.JP_PP_Fact_ID) "
				+ " WHERE f.JP_PP_Plan_ID =? AND f.DocStatus in ('CO','CL') AND fl.JP_PP_PlanLine_ID=pl.JP_PP_PlanLine_ID) "
				+ " WHERE pl.JP_PP_Plan_ID= ? " ;

		no = DB.executeUpdate(sql
						, new Object[]{ getJP_PP_Plan_ID(), getJP_PP_Plan_ID()}
						, false, trxName, 0);
		return no;
	}

	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		return getValue() + "_" + getName();
	}	//	getDocumentInfo

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

		if(!isHaveEndProduct())
		{
			//PP Lines does not contain End Product
			m_processMsg = Msg.getMsg(getCtx(), "JP_PP_NotContainEndProduct");
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
	}	//	rejectIt


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


		if(!getJP_PP_StartProcess().equals("Y"))
		{
			//Please perform PP Start Process before PP End process.
			m_processMsg = Msg.getMsg(getCtx(), "JP_PP_RunEndProcessStartCheck");
			return DocAction.STATUS_Invalid;
		}

		if(!isHaveEndProduct())
		{
			//PP Lines does not contain End Product
			m_processMsg = Msg.getMsg(getCtx(), "JP_PP_NotContainEndProduct");
			return DocAction.STATUS_Invalid;
		}


		MPPFactLine[] ppFactLines = getPPFactLines(true, null);

		//Check Mandatory Instans
		for (MPPFactLine line : ppFactLines)
		{
			MProduct product = MProduct.get(line.getM_Product_ID());
			if (line.getM_AttributeSetInstance_ID() == 0)
			{
				if (product != null && product.isASIMandatory(true))
				{
					if (product.getAttributeSet() != null && !product.getAttributeSet().excludeTableEntry(MProductionLine.Table_ID, true))
					{
						if(line.isEndProduct())
						{
							I_M_AttributeSet attributeset = product.getM_AttributeSet();
							boolean isAutoGenerateLot = false;
							if (attributeset != null)
								isAutoGenerateLot = attributeset.isAutoGenerateLot();

							if(isAutoGenerateLot)
							{
								continue;
							}
						}

						BigDecimal qtyDiff = line.getMovementQty();
						// verify if the ASIs are captured on lineMA
						MPPFactLineMA mas[] = MPPFactLineMA.get(getCtx(),line.getJP_PP_FactLine_ID(), get_TrxName());
						BigDecimal qtyma = Env.ZERO;
						for (MPPFactLineMA ma : mas)
						{
							if (!ma.isAutoGenerated())
							{
								qtyma = qtyma.add(ma.getMovementQty());
							}
						}
						if (qtyma.subtract(qtyDiff).signum() != 0)
						{
							String msg0 = Msg.getElement(Env.getCtx(), "JP_PP_FactLine_ID")+" - " + Msg.getElement(Env.getCtx(), "MovementQty");
							String msg1 = Msg.getElement(Env.getCtx(), "JP_PP_FactLineMA_ID")+" - " + Msg.getElement(Env.getCtx(), "MovementQty");
							String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});

							//m_processMsg = "@Line@ " + line.getLine() + ": @FillMandatory@ @M_AttributeSetInstance_ID@";
							m_processMsg = Msg.getElement(getCtx(), MPPFactLine.COLUMNNAME_JP_PP_FactLine_ID) + " : " + line.getLine()
											+ " - " + msg;
							return DocAction.STATUS_Invalid;
						}
					}
				}
			}
		}

		//In case of MStorageOnHand == null, MProduction create MStorageOnHand as M_AttributeSetInstance_ID = 0 ,
		//So, We should check MStorageOnHand beforehand.
		StringBuilder errors = new StringBuilder();
		for (MPPFactLine line : ppFactLines)
		{
			MProduct product = line.getProduct();
			try
			{
				if (product != null
					&& product.isStocked() )
				{

					BigDecimal qtyOnLineMA = MPPFactLineMA.getManualQty(line.getJP_PP_FactLine_ID(), get_TrxName());
					BigDecimal movementQty = line.getMovementQty();

					if(qtyOnLineMA.compareTo(Env.ZERO) != 0)
					{
						if(qtyOnLineMA.compareTo(movementQty) != 0)
						{
							//The Qty of attribute information tab and the Qty of Line tab do not match.
							m_processMsg = "@JP_PP_DontMatchQtyASI@ " + line.getLine();
							return DOCSTATUS_Invalid;
						}
					}

					if(!line.isEndProduct())
					{
						if (line.getM_AttributeSetInstance_ID() == 0)
						{
							MPPFactLineMA mas[] = line.getPPFactLineMAs();
							BigDecimal movementQtyMA = Env.ZERO;
							BigDecimal qtyOnhand = Env.ZERO;
							for (MPPFactLineMA ma : mas)
							{


								MStorageOnHand storageOnHand =MStorageOnHand.get (getCtx(), line.getM_Locator_ID(),
																			line.getM_Product_ID(),
																			ma.getM_AttributeSetInstance_ID(),
																			ma.getDateMaterialPolicy(), get_TrxName());


								if(storageOnHand == null)
								{
									MAttributeSetInstance asi =   new MAttributeSetInstance(getCtx(), ma.getM_AttributeSetInstance_ID(), get_TrxName());
									m_processMsg = Msg.getMsg(getCtx(), "InsufficientQtyAvailable") +  asi.getDescription()
											+ " - " + Msg.getElement(getCtx(), "DateMaterialPolicy") + " " + ma.getDateMaterialPolicy().toString().substring(0, 10)
											+ " - " +  Msg.getElement(getCtx(), "QtyOnHand") + " " + " 0 ";
									return DOCSTATUS_Invalid;

								}else {

									movementQtyMA = movementQtyMA.add(ma.getMovementQty());
									qtyOnhand = qtyOnhand.add(storageOnHand.getQtyOnHand());

								}
							}//for

							movementQtyMA = movementQtyMA.negate();
							if(qtyOnhand.compareTo(movementQtyMA) >=0 )
							{
								;//Noting to do;
							}else {
								m_processMsg = Msg.getMsg(getCtx(), "InsufficientQtyAvailable")
										+  Msg.getElement(getCtx(), "JP_PP_FactLine_ID")+" : "+line.getLine()
										+ " - "+  Msg.getElement(getCtx(), "QtyOnHand") + " " + qtyOnhand.toString();
								return DOCSTATUS_Invalid;
							}

						}else {

							MStorageOnHand storageOnHand = MStorageOnHand.get (getCtx(), line.getM_Locator_ID(),
									line.getM_Product_ID(),
									line.getM_AttributeSetInstance_ID(),
									null, get_TrxName());

							if(storageOnHand == null)
							{
								MAttributeSetInstance asi =   new MAttributeSetInstance(getCtx(), line.getM_AttributeSetInstance_ID(), get_TrxName());
								m_processMsg = Msg.getMsg(getCtx(), "InsufficientQtyAvailable")
										+  " - " + Msg.getElement(getCtx(), "JP_PP_FactLine_ID")+" : "+line.getLine()
										+  " - " + asi.getDescription() + " - "+  Msg.getElement(getCtx(), "QtyOnHand") + " 0 ";
								return DOCSTATUS_Invalid;
							}else {

								if(storageOnHand.getQtyOnHand().compareTo(line.getQtyUsed()) >=0 )
								{
									;//Noting to do;
								}else {
									MAttributeSetInstance asi =   new MAttributeSetInstance(getCtx(), line.getM_AttributeSetInstance_ID(), get_TrxName());
									m_processMsg = Msg.getMsg(getCtx(), "InsufficientQtyAvailable") + asi.getDescription()
											+ " - "+  Msg.getElement(getCtx(), "QtyOnHand") + " " + storageOnHand.getQtyOnHand().toString();
									return DOCSTATUS_Invalid;
								}
							}

						}
					}//if(!line.isEndProduct())

				}
			}
			catch (NegativeInventoryDisallowedException e)
			{
				log.severe(e.getMessage());
				errors.append(Msg.getElement(getCtx(), "Line")).append(" ").append(line.getLine()).append(": ");
				errors.append(e.getMessage()).append("\n");
			}

		}	//	for all lines

		if (errors.toString().length() > 0)
		{
			m_processMsg = errors.toString();
			return DocAction.STATUS_Invalid;
		}

		MPPFactLineMA[] ppFactLineMAs = null;
		BigDecimal productionQty = getEndProductMovementQty(get_TrxName());
		setProductionQty(productionQty);

		if(getM_Production_ID() == 0)
		{
			if(ppFactLines.length > 0)
			{
				MTable m_table_Production = MTable.get(getCtx(), I_M_Production.Table_Name);
				MTable m_table_ProductionLine = MTable.get(getCtx(), I_M_ProductionLine.Table_Name);
				MTable m_table_ProductionLineMA = MTable.get(getCtx(), I_M_ProductionLineMA.Table_Name);

				PO pp = m_table_Production.getPO(0, get_TrxName());
				PO.copyValues(this, pp);
				pp.setAD_Org_ID(getAD_Org_ID());
				pp.set_ValueNoCheck(MPPFact.COLUMNNAME_JP_PP_Fact_ID, getJP_PP_Fact_ID());
				pp.set_ValueNoCheck(I_M_Production.COLUMNNAME_DocumentNo,null);
				pp.set_ValueNoCheck(I_M_Production.COLUMNNAME_DatePromised, getMovementDate());
				pp.set_ValueNoCheck(I_M_Production.COLUMNNAME_MovementDate ,getMovementDate());
				pp.set_ValueNoCheck(I_M_Production.COLUMNNAME_M_Product_ID, getM_Product_ID());
				pp.set_ValueNoCheck(I_M_Production.COLUMNNAME_M_Locator_ID, getM_Locator_ID());
				pp.set_ValueNoCheck(I_M_Production.COLUMNNAME_ProductionQty, productionQty);
				pp.set_ValueNoCheck(I_M_Production.COLUMNNAME_Name, getName());
				pp.set_ValueNoCheck(I_M_Production.COLUMNNAME_Description, getDescription());
				pp.set_ValueNoCheck(I_M_Production.COLUMNNAME_IsCreated,"Y");
				pp.setIsActive(true);

				pp.set_ValueNoCheck("Processed", "N");
				pp.set_ValueNoCheck("Posted","N");
				pp.set_ValueNoCheck(I_M_Production.COLUMNNAME_DocStatus , DocAction.STATUS_Drafted);
				pp.set_ValueNoCheck(I_M_Production.COLUMNNAME_DocAction, DocAction.ACTION_Complete);

				if(!pp.save(get_TrxName()))
				{
					m_processMsg = Msg.getMsg(getCtx(), "SaveError") + " - "+ Msg.getElement(getCtx(), I_M_Production.COLUMNNAME_M_Production_ID)
											+ " - " + pp.get_Logger().getName();
					return DOCSTATUS_Invalid;
				}
				setM_Production_ID(pp.get_ID());
				setJP_PP_Status(MPPFact.JP_PP_STATUS_Completed);


				for(MPPFactLine ppFactLine : ppFactLines)
				{
					PO ppLine = m_table_ProductionLine.getPO(0, get_TrxName());
					PO.copyValues(ppFactLine, ppLine);
					ppLine.set_ValueNoCheck(I_M_ProductionLine.COLUMNNAME_M_Production_ID, pp.get_ValueAsInt(I_M_ProductionLine.COLUMNNAME_M_Production_ID));
					ppLine.setAD_Org_ID(pp.getAD_Org_ID());
					ppLine.set_ValueNoCheck(I_M_ProductionLine.COLUMNNAME_Line, ppFactLine.getLine());
					ppLine.set_ValueNoCheck(I_M_ProductionLine.COLUMNNAME_M_Product_ID, ppFactLine.getM_Product_ID());
					ppLine.set_ValueNoCheck(I_M_ProductionLine.COLUMNNAME_M_AttributeSetInstance_ID, ppFactLine.getM_AttributeSetInstance_ID());
					ppLine.set_ValueNoCheck(I_M_ProductionLine.COLUMNNAME_IsEndProduct, ppFactLine.isEndProduct()? "Y" : "N");
					ppLine.set_ValueNoCheck(I_M_ProductionLine.COLUMNNAME_PlannedQty, ppFactLine.getPlannedQty());
					ppLine.set_ValueNoCheck(I_M_ProductionLine.COLUMNNAME_QtyUsed, ppFactLine.getQtyUsed());
					ppLine.set_ValueNoCheck(I_M_ProductionLine.COLUMNNAME_MovementQty, ppFactLine.getMovementQty());
					ppLine.set_ValueNoCheck(I_M_ProductionLine.COLUMNNAME_M_Locator_ID, ppFactLine.getM_Locator_ID());
					ppLine.set_ValueNoCheck(I_M_ProductionLine.COLUMNNAME_Description, ppFactLine.getDescription());
					ppLine.set_ValueNoCheck(MPPFactLine.COLUMNNAME_JP_PP_FactLine_ID, ppFactLine.getJP_PP_FactLine_ID());
					if(!ppLine.save(get_TrxName()))
					{
						m_processMsg = Msg.getMsg(getCtx(), "SaveError") + " - "+ Msg.getElement(getCtx(), I_M_ProductionLine.COLUMNNAME_M_ProductionLine_ID)
											+ " - "+ Msg.getElement(getCtx(), I_M_ProductionLine.COLUMNNAME_Line)
											+ " : "  + ppLine.get_ValueAsInt(I_M_ProductionLine.COLUMNNAME_Line)
											+ " - "  + ppLine.get_Logger().getName();
								return DOCSTATUS_Invalid;
					}

					ppFactLineMAs = ppFactLine.getPPFactLineMAs();
					for(MPPFactLineMA ppFactLineMA : ppFactLineMAs)
					{
						PO ppLineMA = m_table_ProductionLineMA.getPO(0, get_TrxName());
						PO.copyValues(ppFactLineMA, ppLineMA);
						ppLineMA.setAD_Org_ID(ppLine.getAD_Org_ID());
						ppLineMA.set_ValueNoCheck(I_M_ProductionLineMA.COLUMNNAME_M_ProductionLine_ID, ppLine.get_ValueAsInt(I_M_ProductionLine.COLUMNNAME_M_ProductionLine_ID));
						ppLineMA.set_ValueNoCheck(I_M_ProductionLineMA.COLUMNNAME_M_AttributeSetInstance_ID, ppFactLineMA.getM_AttributeSetInstance_ID());
						ppLineMA.set_ValueNoCheck(I_M_ProductionLineMA.COLUMNNAME_DateMaterialPolicy,ppFactLineMA.getDateMaterialPolicy());
						ppLineMA.set_ValueNoCheck(I_M_ProductionLineMA.COLUMNNAME_MovementQty, ppFactLineMA.getMovementQty());
						if(!ppLineMA.save(get_TrxName()))
						{
							m_processMsg = Msg.getMsg(getCtx(), "SaveError") + " - "+ Msg.getElement(getCtx(), I_M_ProductionLineMA.COLUMNNAME_M_AttributeSetInstance_ID)
										+ " - "+ Msg.getElement(getCtx(), I_M_ProductionLine.COLUMNNAME_Line)
										+ " : "  + ppLine.get_ValueAsInt(I_M_ProductionLine.COLUMNNAME_Line)
										+ " - "  + ppLineMA.get_Logger().getName();
										return DOCSTATUS_Invalid;
						}
					}
				}

				DocAction doc = (DocAction)pp;
				try
				{
					if(!doc.processIt(DocAction.ACTION_Complete))
					{
						m_processMsg = Msg.getMsg(getCtx(), "JP_CouldNotCreate")+ " : " + Msg.getElement(getCtx(), COLUMNNAME_M_Production_ID)
														+ " - "+ Msg.getElement(getCtx(), I_M_Production.COLUMNNAME_DocAction)
														+ " : "+ DocAction.ACTION_Complete
														+ " - "+ doc.getProcessMsg();
						return DocAction.STATUS_Invalid;
					}
				} catch (Exception e) {

					m_processMsg = Msg.getMsg(getCtx(), "JP_CouldNotCreate")+ " : " + Msg.getElement(getCtx(), COLUMNNAME_M_Production_ID)
										+ " - "+ Msg.getElement(getCtx(), I_M_Production.COLUMNNAME_DocAction)
										+ " : "+ DocAction.ACTION_Complete
										+ " - "+  doc.getProcessMsg() + " - " + e.getMessage();
						return DocAction.STATUS_Invalid;
				}


				String whereClauseFinal = I_M_ProductionLine.COLUMNNAME_M_Production_ID + "=? ";
				String orderClause = I_M_ProductionLine.COLUMNNAME_Line;
				//
				List<PO> list = new Query(getCtx(), I_M_ProductionLine.Table_Name, whereClauseFinal, get_TrxName())
												.setParameters(pp.get_ID())
												.setOrderBy(orderClause)
												.list();

				for(PO productionLine : list)
				{
					for(MPPFactLine ppFactLine : ppFactLines)
					{
						if(productionLine.get_ValueAsInt(MPPFactLine.COLUMNNAME_JP_PP_FactLine_ID) == ppFactLine.getJP_PP_FactLine_ID())
						{
							ppFactLine.setM_ProductionLine_ID(productionLine.get_ValueAsInt(I_M_ProductionLine.COLUMNNAME_M_ProductionLine_ID));
							if(ppFactLine.isEndProduct())
							{
								I_M_AttributeSet attributeset = MProduct.get(ppFactLine.getM_Product_ID()).getM_AttributeSet();
								boolean isAutoGenerateLot = false;
								if (attributeset != null)
									isAutoGenerateLot = attributeset.isAutoGenerateLot();

								if(isAutoGenerateLot)
									ppFactLine.setM_AttributeSetInstance_ID(productionLine.get_ValueAsInt(I_M_ProductionLine.COLUMNNAME_M_AttributeSetInstance_ID));
							}
							if(!ppFactLine.save(get_TrxName()))
							{
								m_processMsg = Msg.getMsg(getCtx(), "SaveError") + " - "+ Msg.getElement(getCtx(), MPPFactLine.COLUMNNAME_JP_PP_FactLine_ID)
														+ " - "+ Msg.getElement(getCtx(), MPPFactLine.COLUMNNAME_Line)
														+ " : "  + ppFactLine.getLine()
														+ " - "  + ppFactLine.get_Logger().getName();
								return DOCSTATUS_Invalid;
							}
							break;
						}

					}//for
				}//for

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

		if (DOCSTATUS_Closed.equals(getDocStatus())
				|| DOCSTATUS_Reversed.equals(getDocStatus())
				|| DOCSTATUS_Voided.equals(getDocStatus())
				|| DOCSTATUS_Completed.equals(getDocStatus()))
		{
			m_processMsg = "Document Closed: " + getDocStatus();
			setDocAction(DOCACTION_None);
			return false;
		}

		MFactAcct.deleteEx(MPPFact.Table_ID, getJP_PP_Fact_ID(), get_TrxName());
		setPosted(true);

		setJP_PP_Status(JP_PP_STATUS_Void);

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

		if(getM_Production_ID() != 0)
		{
			MTable m_table_Production = MTable.get(getCtx(), I_M_Production.Table_Name);
			PO po = m_table_Production.getPO(getM_Production_ID(), get_TrxName());

			DocAction pp = (DocAction)po;
			if(pp.getDocStatus().equals(STATUS_Completed))
			{
				try
				{
					if(pp.processIt(DocAction.ACTION_Close))
					{
						po.saveEx(get_TrxName());

					}else {
						m_processMsg = pp.getProcessMsg();
						return false;
					}

				} catch (Exception e) {

					m_processMsg = pp.getProcessMsg();
					return false;
				}
			}
		}

		// After Close
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


		if(getDocStatus().equals(DocAction.STATUS_Completed))
		{

			boolean isOk = reverse(ACTION_Reverse_Correct);
		 	if(!isOk)
		 	{
		 		return false;
		 	}

		}else if(getDocStatus().equals(DocAction.STATUS_Closed)) {

			;//Noting to do;

		}else {

			return voidIt();
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}


	/**
	 * 	Reverse Accrual - none
	 * 	@return true if success
	 */
	public boolean reverseAccrualIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		if(getDocStatus().equals(STATUS_Completed))
		{

		 	boolean isOk = reverse(ACTION_Reverse_Accrual);
		 	if(!isOk)
		 	{
		 		return false;
		 	}

		}else if(getDocStatus().equals(DocAction.STATUS_Closed)) {

			;//Noting to do;

		}else {

			return voidIt();

		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}


	private boolean reverse(String DocAction)
	{
		if(getM_Production_ID() == 0)
		{
			return true;
		}

		MTable m_table_Production = MTable.get(getCtx(), I_M_Production.Table_Name);
		PO po = m_table_Production.getPO(getM_Production_ID(), get_TrxName());

		DocAction pp = (DocAction)po;
		if(pp.getDocStatus().equals(STATUS_Completed))
		{
			try
			{
				pp.processIt(DocAction);

			} catch (Exception e) {

				m_processMsg = pp.getProcessMsg();
				return false;
			}
			po.saveEx(get_TrxName());

			int reversal_ID = po.get_ValueAsInt("Reversal_ID");
			PO reversalPP = m_table_Production.getPO(reversal_ID, get_TrxName());
			reversalPP.set_ValueNoCheck(COLUMNNAME_JP_PP_Fact_ID, getJP_PP_Fact_ID());
			reversalPP.saveEx(get_TrxName());
		}

		return true;
	}

	/**
	 * 	Re-activate
	 * 	@return true if success
	 */
	public boolean reActivateIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;

		MFactAcct.deleteEx(MPPFact.Table_ID, getJP_PP_Fact_ID(), get_TrxName());
		setPosted(false);

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;

		setDocAction(DOCACTION_Complete);
		setProcessed(false);

		return false;
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
			+ "' WHERE JP_PP_Fact_ID=" + getJP_PP_Fact_ID();
		DB.executeUpdateEx("UPDATE JP_PP_FactLine " + set, get_TrxName());
		m_PPFactLines = null;
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

			return index;

		}else if(docStatus.equals(DocumentEngine.STATUS_Invalid)) {

			index = 0;
			options[index++] = DocumentEngine.ACTION_Void;
			options[index++] = DocumentEngine.ACTION_Prepare;

		}else if(docStatus.equals(DocumentEngine.STATUS_Completed)) {

			if(getM_Production_ID() == 0)
			{
				index = 0;
				options[index++] = DocumentEngine.ACTION_Void;
				options[index++] = DocumentEngine.ACTION_Close;
				options[index++] = DocumentEngine.ACTION_ReActivate;

			}else {

				index = 0;
				options[index++] = DocumentEngine.ACTION_Close;
				options[index++] = DocumentEngine.ACTION_Reverse_Accrual;
				options[index++] = DocumentEngine.ACTION_Reverse_Correct;
			}


			return index;
		}

		return index;
	}


	private MPPFactLine[] m_PPFactLines = null;

	/**
	 * Get PP Fact Lines
	 *
	 * @param whereClause
	 * @param orderClause
	 * @return
	 */
	public MPPFactLine[] getPPFactLines (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPFactLine.COLUMNNAME_JP_PP_Fact_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPFactLine.COLUMNNAME_Line;
		//
		List<MPPFactLine> list = new Query(getCtx(), MPPFactLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPFactLine[list.size()]);

	}

	/**
	 * Get PP Fact Lines
	 *
	 *
	 * @param requery
	 * @param orderBy
	 * @return
	 */
	public MPPFactLine[] getPPFactLines(boolean requery, String orderBy)
	{
		if (m_PPFactLines != null && !requery) {
			set_TrxName(m_PPFactLines, get_TrxName());
			return m_PPFactLines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPFactLine.COLUMNNAME_Line;

		m_PPFactLines = getPPFactLines(null, orderClause);
		return m_PPFactLines;
	}

	/**
	 * Get PP Fact Lines
	 *
	 *
	 * @return
	 */
	public MPPFactLine[] getPPFactLines()
	{
		return getPPFactLines(false, null);

	}


	public String createFactLineFromPlanLine(String trxName)
	{
		MPPPlan ppPlan = new MPPPlan(getCtx(), getJP_PP_Plan_ID(), trxName);
		MPPPlanLine[] ppPLines = ppPlan.getPPPlanLines();
		MPPFactLine ppFLine = null;

		BigDecimal plannedQty = Env.ZERO;
		BigDecimal qtyUsed = Env.ZERO;
		BigDecimal movementQty = Env.ZERO;
		for(MPPPlanLine ppPLine : ppPLines)
		{
			ppFLine = new MPPFactLine(getCtx(), 0 , get_TrxName());
			PO.copyValues(ppPLine, ppFLine);

			//Copy mandatory column to make sure
			ppFLine.setJP_PP_Fact_ID(getJP_PP_Fact_ID());
			ppFLine.setJP_PP_PlanLine_ID(ppPLine.getJP_PP_PlanLine_ID());
			ppFLine.setLine(ppPLine.getLine());
			ppFLine.setAD_Org_ID(getAD_Org_ID());
			ppFLine.setM_Product_ID(ppPLine.getM_Product_ID());
			ppFLine.setIsEndProduct(ppPLine.isEndProduct());
			ppFLine.setC_UOM_ID(ppPLine.getC_UOM_ID());
			if(ppPLine.isEndProduct())
			{
				plannedQty = ppPLine.getPlannedQty().subtract(ppPLine.getJP_MovementQtyFact());
				if(plannedQty.signum() == 0)
					plannedQty = Env.ZERO;
				qtyUsed = null;
				movementQty = plannedQty;
			}else {
				plannedQty = ppPLine.getPlannedQty().add(ppPLine.getJP_MovementQtyFact());
				if(plannedQty.signum() == 0)
				{
					plannedQty = Env.ZERO;
					qtyUsed = Env.ZERO;
					movementQty = Env.ZERO;
				}else {
					qtyUsed = plannedQty;
					movementQty = plannedQty.negate();
				}
			}

			ppFLine.setPlannedQty(plannedQty);
			ppFLine.setQtyUsed(qtyUsed);
			ppFLine.setMovementQty(movementQty);
			ppFLine.setJP_Processing1("N");
			ppFLine.setJP_Processing2("N");
			ppFLine.setJP_Processing3("N");
			ppFLine.setIsCreated("N");
			if(!ppFLine.save(get_TrxName()))
			{
				String msg = Msg.getMsg(getCtx(), "JP_CouldNotCreate")
								+ " " + Msg.getMsg(getCtx(), "SaveError") + " - "+ Msg.getElement(getCtx(), MPPFactLine.COLUMNNAME_JP_PP_FactLine_ID)
								+ " - "+ Msg.getElement(getCtx(), MPPFactLine.COLUMNNAME_Line)
								+ " : "  + ppPLine.getLine()
								;

				m_processMsg = msg;
				log.saveError("SaveError", msg);

				return msg;
			}

		}

		return null;
	}

	public BigDecimal getEndProductMovementQty(String trxName)
	{
		BigDecimal productionQty = Env.ZERO;

		String sql = "SELECT COALESCE(SUM(fl.MovementQty),0) FROM JP_PP_FactLine fl "
								+ " WHERE fl.IsEndProduct='Y' AND JP_PP_Fact_ID = ? ";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, getJP_PP_Fact_ID());
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				productionQty = rs.getBigDecimal(1);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return productionQty;
	}

	public boolean isHaveEndProduct()
	{
		MPPFactLine[] lines = getPPFactLines();

		for(MPPFactLine line : lines) {
			if(line.isEndProduct())
				return true;
		}

		return false;
	}


}	//	MPPDoc
