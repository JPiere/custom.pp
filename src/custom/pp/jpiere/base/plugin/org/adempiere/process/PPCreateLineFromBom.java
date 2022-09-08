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

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import org.compiere.model.MProduct;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductBOMLine;

import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPFact;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPFactLine;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlan;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlanLine;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlanLineT;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlanT;

/**
 * JPIERE-0501: JPiere PP Doc - Create Line From Bom
 *
 * This Class is called from JP_PP_PlanT, JP_PP_Plan, JP_PP_Fact tables.
 *
 *
 * @author Hideaki Hagiwara
 *
 */
public class PPCreateLineFromBom extends SvrProcess {

	private int p_Record_ID = 0;
	private MTable m_Table = null;
	private boolean p_Recreate = true;
	private BigDecimal p_ProductionQty = Env.ZERO;
	private int p_PP_Product_BOM_ID = 0;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if ("Recreate".equals(name))
				p_Recreate = para[i].getParameterAsBoolean();
			else if ("ProductionQty".equals(name))
				p_ProductionQty  = (BigDecimal) para[i].getParameter();
			else if ("PP_Product_BOM_ID".equals(name))
				p_PP_Product_BOM_ID  =  para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		p_Record_ID = getRecord_ID();
		m_Table = MTable.get(getTable_ID());
	}

	@Override
	protected String doIt() throws Exception
	{
		String msg = null;
		PO po = m_Table.getPO(p_Record_ID, get_TrxName());

		if (!p_Recreate && "Y".equalsIgnoreCase(po.get_ValueAsString("IsCreated")))
			throw new AdempiereUserError(Msg.getMsg(getCtx(), "JP_DocLineCreated"));

		if(p_Recreate)
		{
			msg = deleteLine(po);
			if(!Util.isEmpty(msg))
				return msg;
		}

		if(po instanceof MPPPlan)
		{
			MPPPlan doc = (MPPPlan)po;
			if(doc.getPPPlanLines().length > 0)
			{
				// There are PP Lines already.
				throw new Exception(Msg.getMsg(getCtx(), "JP_PP_LinesThere"));
			}

		}else if(po instanceof MPPPlanT) {

			MPPPlanT doc = (MPPPlanT)po;
			if(doc.getPPPlanLineTs().length > 0)
			{
				// There are PP Lines already.
				throw new Exception(Msg.getMsg(getCtx(), "JP_PP_LinesThere"));
			}

		}else if(po instanceof MPPFact) {

			MPPFact doc = (MPPFact)po;
			if(doc.getPPFactLines().length > 0)
			{
				// There are PP Lines already.
				throw new Exception(Msg.getMsg(getCtx(), "JP_PP_LinesThere"));
			}

		}

		//Update ProductionQty & IsCreated
		String sql = "UPDATE "+ m_Table.getTableName() + " SET ProductionQty=?, IsCreated=? "
							+ " WHERE "+ m_Table.getTableName()+"_ID=?";

		int no = DB.executeUpdate(sql
					, new Object[]{p_ProductionQty, "Y", po.get_ID()}
					, false, get_TrxName(), 0);
		if (no != 1)
		{
			throw new Exception(Msg.getMsg(getCtx(), "DBExecuteError") + " : " + sql);
		}


		//Create Line
		PO poLine = createLine(po);

		//line of End Product
		int line = 10;
		MProduct product = MProduct.get(po.get_ValueAsInt("M_Product_ID"));
		poLine.setAD_Org_ID(po.getAD_Org_ID());
		poLine.set_ValueNoCheck("Line", line);
		poLine.set_ValueNoCheck("M_Product_ID", product.getM_Product_ID());
		poLine.set_ValueNoCheck("M_Locator_ID", po.get_Value("M_Locator_ID"));
		poLine.set_ValueNoCheck("IsEndProduct",true);
		poLine.set_ValueNoCheck("PlannedQty", p_ProductionQty);
		//poLine.set_ValueNoCheck("QtyUsed", Env.ZERO);
		poLine.set_ValueNoCheck("MovementQty",p_ProductionQty);
		poLine.saveEx(get_TrxName());

		//lines of Bom
		MPPProductBOM bom = null;
		if(p_PP_Product_BOM_ID == 0)
			bom = MPPProductBOM.getDefault(product, get_TrxName());
		else
			bom = new MPPProductBOM(getCtx(),p_PP_Product_BOM_ID,get_TrxName());
					
		if(bom != null)
		{
			for(MPPProductBOMLine bomLine : bom.getLines())
			{
				line = line + 10;
				poLine = createLine(po);
				poLine.setAD_Org_ID(po.getAD_Org_ID());
				poLine.set_ValueNoCheck("Line", line);
					poLine.set_ValueNoCheck("M_Product_ID", bomLine.getM_Product_ID());
				poLine.set_ValueNoCheck("M_Locator_ID", po.get_Value("M_Locator_ID"));
				poLine.set_ValueNoCheck("IsEndProduct",false);
					poLine.set_ValueNoCheck("PlannedQty", p_ProductionQty.multiply(bomLine.getQtyBOM()));
					poLine.set_ValueNoCheck("QtyUsed", p_ProductionQty.multiply(bomLine.getQtyBOM()));
					poLine.set_ValueNoCheck("MovementQty",p_ProductionQty.multiply(bomLine.getQtyBOM()).negate());
				poLine.set_ValueNoCheck("IsCreated", "N");
				poLine.saveEx(get_TrxName());
		}
		}

		return "@Success@";
	}

	/**
	 * Delete Line
	 *
	 * @param po
	 * @return
	 */
	private String deleteLine(PO po)
	{

		String tableName = null;

		if(m_Table.getTableName().equals(MPPPlanT.Table_Name))
			tableName = MPPPlanLineT.Table_Name;
		else if(m_Table.getTableName().equals(MPPPlan.Table_Name))
			tableName = MPPPlanLine.Table_Name;
		else if(m_Table.getTableName().equals(MPPFact.Table_Name))
			tableName = MPPFactLine.Table_Name;

		List<PO> list = new Query(Env.getCtx(), tableName, m_Table.getTableName()+"_ID = ?", get_TrxName())
								.setOnlyActiveRecords(false)
								.setApplyAccessFilter(false)
								.setClient_ID()
								.setParameters(po.get_ID())
								.list();

		for(PO poLine : list)
		{
			poLine.deleteEx(false);
		}

		return "";
	}


	/**
	 * Create Line
	 *
	 * @param po
	 * @return
	 */
	private PO createLine(PO po)
	{
		PO poLine = null;
		if(m_Table.getTableName().equals(MPPPlanT.Table_Name))
		{
			poLine = new MPPPlanLineT(getCtx(), 0 , get_TrxName());
			poLine.set_ValueNoCheck(MPPPlanT.COLUMNNAME_JP_PP_PlanT_ID, po.get_ValueAsInt(MPPPlanT.COLUMNNAME_JP_PP_PlanT_ID));

		}else if(m_Table.getTableName().equals(MPPPlan.Table_Name)) {

			poLine = new MPPPlanLine(getCtx(), 0 , get_TrxName());
			poLine.set_ValueNoCheck(MPPPlan.COLUMNNAME_JP_PP_Plan_ID, po.get_ValueAsInt(MPPPlan.COLUMNNAME_JP_PP_Plan_ID));

		}else if(m_Table.getTableName().equals(MPPFact.Table_Name)) {

			poLine = new MPPFactLine(getCtx(), 0 , get_TrxName());
			poLine.set_ValueNoCheck(MPPFact.COLUMNNAME_JP_PP_Fact_ID, po.get_ValueAsInt(MPPFact.COLUMNNAME_JP_PP_Fact_ID));

		}

		return poLine;
	}
}
