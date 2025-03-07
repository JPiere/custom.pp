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
public class MPPDocT extends X_JP_PP_DocT {

	private static final long serialVersionUID = 2200188672033815464L;

	public MPPDocT(Properties ctx, int JP_PP_DocT_ID, String trxName)
	{
		super(ctx, JP_PP_DocT_ID, trxName);
	}

	public MPPDocT(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//Rounding Production Qty
		if(newRecord || is_ValueChanged(MPPDocT.COLUMNNAME_QtyEntered))
		{
			if(Env.ONE.compareTo(getQtyEntered()) != 0)
			{
				log.saveError("Error", Msg.getElement(getCtx(), MPPDocT.COLUMNNAME_QtyEntered) + " = 1 ") ;
				return false;
			}
		}

		//Rounding Production Qty
		if(newRecord || is_ValueChanged(MPPDocT.COLUMNNAME_ProductionQty))
		{
			boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
			MUOM uom = MUOM.get(getC_UOM_ID());
			setProductionQty(getProductionQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
		}

		if(newRecord || is_ValueChanged(MPPDocT.COLUMNNAME_JP_ProductionDays))
		{
			if(getJP_ProductionDays() < 0)
			{
				log.saveError("Error", Msg.getElement(getCtx(), MPPDocT.COLUMNNAME_JP_ProductionDays) + " - "  + Msg.getMsg(getCtx(), "Minus"));
				return false;
			}
		}

		return true;
	}

	private MPPPlanT[] m_PPPlanTs = null;

	public MPPPlanT[] getPPPlanTs (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPPlanT.COLUMNNAME_JP_PP_DocT_ID+"=?");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPPlanT.COLUMNNAME_SeqNo;
		//
		List<MPPPlanT> list = new Query(getCtx(), MPPPlanT.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPPlanT[list.size()]);

	}

	public MPPPlanT[] getPPPlanTs(boolean requery, String orderBy)
	{
		if (m_PPPlanTs != null && !requery) {
			set_TrxName(m_PPPlanTs, get_TrxName());
			return m_PPPlanTs;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPPlanT.COLUMNNAME_SeqNo;

		m_PPPlanTs = getPPPlanTs(" AND IsActive='Y' ", orderClause);
		return m_PPPlanTs;
	}

	public MPPPlanT[] getPPPlanTs()
	{
		return getPPPlanTs(false, null);
	}

	public MPPPlanT getPPPlanT(int seqNo, int M_Product_ID, String value)
	{
		getPPPlanTs();
		for(MPPPlanT ppPlanT : m_PPPlanTs)
		{
			if(ppPlanT.getSeqNo() == seqNo
					&& ppPlanT.getM_Product_ID() == M_Product_ID
					&& ppPlanT.getValue().equals(value))
			{
				return ppPlanT;
			}
		}

		return null;
	}

	public static MPPDocT get (Properties ctx, String Value, String trxName)
	{
		if (Value == null || Value.length() == 0)
			return null;
		final String whereClause = "Value=? AND AD_Client_ID=?";
		MPPDocT retValue = new Query(ctx, MPPDocT.Table_Name, whereClause, trxName)
		.setParameters(Value,Env.getAD_Client_ID(ctx))
		.firstOnly();
		return retValue;
	}
}
