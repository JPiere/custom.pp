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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOM;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0502: JPiere PP Doc Template
 *
 * @author Hideaki Hagiwara
 *
 */
public class MPPPlanT extends X_JP_PP_PlanT {

	public MPPPlanT(Properties ctx, int JP_PP_PlanT_ID, String trxName)
	{
		super(ctx, JP_PP_PlanT_ID, trxName);
	}

	public MPPPlanT(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	private MPPDocT m_PPDocT = null;

	public MPPDocT getParent()
	{
		if(m_PPDocT == null)
			m_PPDocT = new MPPDocT(getCtx(), getJP_PP_DocT_ID(), get_TrxName());
		else
			m_PPDocT.set_TrxName(get_TrxName());

		return m_PPDocT;
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//SetAD_Org_ID
		if(newRecord)
		{
			setAD_Org_ID(getParent().getAD_Org_ID());
		}

		//Set C_UOM_ID
		if(newRecord || is_ValueChanged(MPPPlanT.COLUMNNAME_C_UOM_ID) || getC_UOM_ID() == 0)
		{
			MProduct product = MProduct.get(getM_Product_ID());
			if(product.getC_UOM_ID() != getC_UOM_ID())
			{
				setC_UOM_ID(product.getC_UOM_ID());
			}
		}

		//Rounding Production Qty
		if(newRecord || is_ValueChanged(MPPPlanT.COLUMNNAME_ProductionQty))
		{
			boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
			MUOM uom = MUOM.get(getC_UOM_ID());
			setProductionQty(getProductionQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
		}

		if(newRecord || is_ValueChanged(MPPPlanT.COLUMNNAME_JP_ProductionDays))
		{
			if(getJP_ProductionDays() < 0)
			{
				log.saveError("Error", Msg.getElement(getCtx(), MPPPlanT.COLUMNNAME_JP_ProductionDays) + " - "  + Msg.getMsg(getCtx(), "Minus"));
				return false;
			}
		}

		if(newRecord || is_ValueChanged(MPPPlanT.COLUMNNAME_JP_DayOffset))
		{
			if(getJP_DayOffset() < 0)
			{
				log.saveError("Error", Msg.getElement(getCtx(), MPPPlanT.COLUMNNAME_JP_DayOffset) + " - "  + Msg.getMsg(getCtx(), "Minus"));
				return false;
			}
		}

		//Check Doc Type
		if(newRecord || is_ValueChanged(MPPPlanT.COLUMNNAME_C_DocType_ID))
		{
			if(!getC_DocType().getDocBaseType().equals("JDP"))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_DifferentDocType") +" : JDP") ;
				return false;
			}
		}

		//Check Doc Type
		if(newRecord || is_ValueChanged(MPPPlanT.COLUMNNAME_C_DocTypeTarget_ID))
		{
			if(!getC_DocTypeTarget().getDocBaseType().equals("JDF"))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_DifferentDocType") +" : JDF") ;
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
		if(!success)
			return false;

		//Update Line Qty
		if(!newRecord && is_ValueChanged(MPPPlanT.COLUMNNAME_ProductionQty))
		{
			boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
			MUOM uom = null;

			BigDecimal newQty = getProductionQty();
			BigDecimal oldQty = (BigDecimal)get_ValueOld(MPPPlanT.COLUMNNAME_ProductionQty) ;
			BigDecimal rate = Env.ONE;
			if(oldQty != null && oldQty.compareTo(Env.ZERO) != 0)
				rate = newQty.divide(oldQty, 4, RoundingMode.HALF_UP);

			MPPPlanLineT[] lines = getPPPlanLineTs(true, null);
			for(MPPPlanLineT line : lines)
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
					String msg =  Msg.getElement(getCtx(), MPPPlanLineT.COLUMNNAME_JP_PP_PlanLineT_ID)
				 			+ " - " + Msg.getElement(getCtx(), MPPPlanLineT.COLUMNNAME_Line) + " : " + line.getLine();
					log.saveError("SaveError", msg);
					return false;
				}
			}
		}

		return true;
	}

	private MPPPlanLineT[] m_PPPlanLineTs = null;

	public MPPPlanLineT[] getPPPlanLineTs (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPPlanLineT.COLUMNNAME_JP_PP_PlanT_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPPlanLineT.COLUMNNAME_Line;
		//
		List<MPPPlanLineT> list = new Query(getCtx(), MPPPlanLineT.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPPlanLineT[list.size()]);

	}

	public MPPPlanLineT[] getPPPlanLineTs(boolean requery, String orderBy)
	{
		if (m_PPPlanLineTs != null && !requery) {
			set_TrxName(m_PPPlanLineTs, get_TrxName());
			return m_PPPlanLineTs;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPPlanLineT.COLUMNNAME_Line;

		m_PPPlanLineTs = getPPPlanLineTs(" AND IsActive='Y' ", orderClause);
		return m_PPPlanLineTs;
	}

	public MPPPlanLineT[] getPPPlanLineTs()
	{
		return getPPPlanLineTs(false, null);
	}
}
