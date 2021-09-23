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
package custom.pp.jpiere.base.plugin.org.adempiere.base;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.compiere.model.MClient;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.model.MUOM;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Msg;

import custom.pp.jpiere.base.plugin.org.adempiere.model.I_JP_PP_Fact;
import custom.pp.jpiere.base.plugin.org.adempiere.model.I_JP_PP_Plan;


/**
 * JPIERE-0501:JPiere PP Fact Model Validator
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class PPFactModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(PPFactModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(I_JP_PP_Fact.Table_Name, this);
		;
	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{
		//Set Name for Tree
		if(type == ModelValidator.TYPE_AFTER_CHANGE && po.is_ValueChanged(I_JP_PP_Fact.COLUMNNAME_DocStatus))
		{
			if(po instanceof I_JP_PP_Fact)
			{
				I_JP_PP_Fact i_PO = (I_JP_PP_Fact)po;
				String sql = "UPDATE JP_PP_Plan p SET NAME = p.JP_NAME || ' [' ||"
						+ " (SELECT COALESCE(SUM(fl.MovementQty),0) FROM JP_PP_FactLine fl "
											+ " INNER JOIN JP_PP_Fact f ON (fl.JP_PP_Fact_ID = f.JP_PP_Fact_ID) "
											+ "  WHERE f.JP_PP_Plan_ID=? AND f.DocStatus in ('CO','CL') AND fl.IsEndProduct='Y')  || '/' || ? || ']' "	//param1,2
						+ " WHERE p.JP_PP_Plan_ID=?";	//param3

				MTable m_table_PPPlan= MTable.get(po.getCtx(), I_JP_PP_Plan.Table_Name);
				I_JP_PP_Plan pp = (I_JP_PP_Plan)m_table_PPPlan.getPO(i_PO.getJP_PP_Plan_ID(), po.get_TrxName());
				BigDecimal productionQtyPlan = pp.getProductionQty();

				boolean isStdPrecision = MSysConfig.getBooleanValue("JP_PP_UOM_STDPRECISION", true, i_PO.getAD_Client_ID(), i_PO.getAD_Org_ID());
				MUOM uom = MUOM.get(i_PO.getC_UOM_ID());
				productionQtyPlan = productionQtyPlan.setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP);

				int no = DB.executeUpdate(sql
							, new Object[]{i_PO.getJP_PP_Plan_ID(), productionQtyPlan, i_PO.getJP_PP_Plan_ID()}
							, false, po.get_TrxName(), 0);

				if (no != 1)
				{
					log.saveError("DBExecuteError", "PPFactModelValidator -> modelChange()");
					return Msg.getMsg(po.getCtx(), "DBExecuteError") + "PPFactModelValidator -> modelChange()";
				}
			}
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}

}
