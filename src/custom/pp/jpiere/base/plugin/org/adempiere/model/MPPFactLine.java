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

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOM;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;


/**
 * JPIERE-0501:JPiere PP Fact Line
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MPPFactLine extends X_JP_PP_FactLine {

	public MPPFactLine(Properties ctx, int JP_PP_FactLine_ID, String trxName)
	{
		super(ctx, JP_PP_FactLine_ID, trxName);
	}

	public MPPFactLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}


	protected MPPFact parent = null;

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

		//Set C_UOM_ID
		if(newRecord || is_ValueChanged(MPPFact.COLUMNNAME_C_UOM_ID) || getC_UOM_ID() == 0 )
		{
			MProduct product = MProduct.get(getM_Product_ID());
			if(product.getC_UOM_ID() != getC_UOM_ID())
			{
				setC_UOM_ID(product.getC_UOM_ID());
			}
		}


		//Check IsEndProduct
		if (getParent().getM_Product_ID() == getM_Product_ID() &&
				(getParent().getProductionQty().signum() == getMovementQty().signum()
				|| getParent().getProductionQty().compareTo(Env.ZERO)==0
				|| getMovementQty().compareTo(Env.ZERO)==0 ))
		{
			setIsEndProduct(true);
		}else {
			setIsEndProduct(false);
		}


		//Convert Qty & Rounding Qty
		if (isEndProduct())
		{
			if(newRecord || is_ValueChanged(COLUMNNAME_MovementQty))
			{
				boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
				MUOM uom = MUOM.get(getC_UOM_ID());
				setPlannedQty(getPlannedQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
				setQtyUsed(null);
				setMovementQty(getMovementQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
			}
		}else {

			if(newRecord || is_ValueChanged(COLUMNNAME_QtyUsed))
			{
				boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
				MUOM uom = MUOM.get(getC_UOM_ID());
				setPlannedQty(getPlannedQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
				setQtyUsed(getQtyUsed().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
				setMovementQty(getQtyUsed().negate());
			}
		}

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		//Update Parent ProductionQty
		if (isEndProduct() && (newRecord || is_ValueChanged(COLUMNNAME_MovementQty)) )
		{

			int no = updateParentProductionQty(get_TrxName());
			if (no != 1)
			{
				log.saveError("DBExecuteError", "MPPFactLine#afterSave() -> updateParentProductionQty()");
				return false;
			}
		}

		if (newRecord && isEndProduct() && getJP_PP_PlanLine_ID() != 0 && !getIsCreated().equals("Y"))
		{
			MPPPlanLine ppPlanLine = new MPPPlanLine(getCtx(), getJP_PP_PlanLine_ID(), get_TrxName());
			MPPPlanLineQT[] qts_From = ppPlanLine.getPPPlanLineQTs();
			MPPFactLineQT qt_To = null;
			for(MPPPlanLineQT qt_From: qts_From)
			{
				qt_To = new MPPFactLineQT(getCtx(), 0, get_TrxName());
				PO.copyValues(qt_From, qt_To);
				qt_To.setJP_PP_FactLine_ID(getJP_PP_FactLine_ID());
				qt_To.setJP_PP_PlanLineQT_ID(qt_From.getJP_PP_PlanLineQT_ID());
				qt_To.setAD_Org_ID(getAD_Org_ID());
				qt_To.setSeqNo(qt_From.getSeqNo());
				qt_To.setM_QualityTest_ID(qt_From.getM_QualityTest_ID());
				qt_To.setExpectedResult(qt_From.getExpectedResult());
				qt_To.setIsActive(true);
				qt_To.setProcessed(false);
				qt_To.save(get_TrxName());
			}
		}

		return true;
	}


	@Override
	protected boolean afterDelete(boolean success)
	{
		//Update Parent ProductionQty
		if(isEndProduct())
		{
			int no = updateParentProductionQty(get_TrxName());
			if (no != 1)
			{
				log.saveError("DBExecuteError", "MPPFactLine#afterDelete() -> updateParentProductionQty()");
				return false;
			}
		}

		MPPFactLine[] lines = getParent().getPPFactLines(true, null);
		if(lines.length == 0)
		{
			String sql = "UPDATE JP_PP_Fact SET IsCreated='N' WHERE JP_PP_Fact_ID=? ";

			int no = DB.executeUpdate(sql
						, new Object[]{getJP_PP_Fact_ID()}
						, false, get_TrxName(), 0);

			if (no != 1)
			{
				log.saveError("DBExecuteError", "MPPFactLine#afterDelete()");
				return false;
			}
		}

		return true;
	}


	private int updateParentProductionQty(String trxName)
	{
		String sql = "UPDATE JP_PP_Fact SET ProductionQty=(SELECT COALESCE(SUM(MovementQty),0) FROM JP_PP_FactLine WHERE JP_PP_Fact_ID=? AND IsEndProduct='Y') "
				+ " WHERE JP_PP_Fact_ID=?";

		int no = DB.executeUpdate(sql
					, new Object[]{getJP_PP_Fact_ID(), getJP_PP_Fact_ID()}
					, false, trxName, 0);
		return no;
	}

	public MPPFact getParent()
	{
		if(parent==null)
			parent = new MPPFact(getCtx(), getJP_PP_Fact_ID(), get_TrxName());
		else
			parent.set_TrxName(get_TrxName());

		return parent;
	}

	public MPPFactLineMA[] getPPFactLineMAs ()
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPFactLineMA.COLUMNNAME_JP_PP_FactLine_ID+"=? ");
		StringBuilder orderClause = new StringBuilder(MPPFactLineMA.COLUMNNAME_JP_PP_FactLineMA_ID);
		//
		List<MPPFactLineMA> list = new Query(getCtx(), MPPFactLineMA.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause.toString())
										.list();

		return list.toArray(new MPPFactLineMA[list.size()]);

	}

	private MPPFactLineQT[] m_PPFactLineQTs = null;

	public MPPFactLineQT[] getPPFactLineQTs (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPFactLineQT.COLUMNNAME_JP_PP_FactLine_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPFactLineQT.COLUMNNAME_SeqNo;
		//
		List<MPPFactLineQT> list = new Query(getCtx(), MPPPlanLineQT.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPFactLineQT[list.size()]);

	}

	public MPPFactLineQT[] getPPFactLineQTs(boolean requery, String orderBy)
	{
		if (m_PPFactLineQTs != null && !requery) {
			set_TrxName(m_PPFactLineQTs, get_TrxName());
			return m_PPFactLineQTs;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPFactLineQT.COLUMNNAME_SeqNo;

		m_PPFactLineQTs = getPPFactLineQTs(" AND IsActive='Y' ", orderClause);
		return m_PPFactLineQTs;
	}

	public MPPFactLineQT[] getPPFactLineQTs()
	{
		return getPPFactLineQTs(false, null);
	}

	public MProduct getProduct()
	{
		if (getM_Product_ID() != 0)
			return MProduct.getCopy(getCtx(), getM_Product_ID(), get_TrxName());
		return null;
	}
}
