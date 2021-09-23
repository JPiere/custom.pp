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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOM;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import custom.pp.jpiere.base.plugin.org.adempiere.model.I_JP_PP_PlanLine;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlan;


/**
 * JPIERE-0501:JPiere PP Plan Line Model Validator
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class PPPlanLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(PPPlanLineModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(I_JP_PP_PlanLine.Table_Name, this);
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


	private static final String JP_PP_UOM_STDPRECISION = "JP_PP_UOM_STDPRECISION";
	private static final boolean IS_USE_MODEL_CLASS_FOR_UPDATE_PARENT = false;

	@Override
	public String modelChange(PO po, int type) throws Exception
	{
		if(type == ModelValidator.TYPE_AFTER_NEW || type == ModelValidator.TYPE_AFTER_CHANGE || type == ModelValidator.TYPE_AFTER_DELETE)
		{
			//Set Name for Tree
			if(po instanceof I_JP_PP_PlanLine)
			{
				if(type == ModelValidator.TYPE_AFTER_CHANGE && !po.is_ValueChanged(I_JP_PP_PlanLine.COLUMNNAME_PlannedQty))
				{
					;//Noting to do;
				}else {

					I_JP_PP_PlanLine i_PO = (I_JP_PP_PlanLine)po;

					if(i_PO.isEndProduct())
					{

						boolean isStdPrecision = MSysConfig.getBooleanValue(JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), i_PO.getAD_Org_ID());
						MUOM uom = MUOM.get(i_PO.getC_UOM_ID());

						BigDecimal movementQty = Env.ZERO;
						BigDecimal JP_MovementQtyFact  = Env.ZERO;

						String sql = " SELECT COALESCE(SUM(MovementQty),0) ,COALESCE(SUM(JP_MovementQtyFact),0) FROM JP_PP_PlanLine WHERE JP_PP_Plan_ID=? AND IsEndProduct='Y' AND IsActive='Y' ";
						PreparedStatement pstmt = null;
						ResultSet rs = null;
						try
						{
							pstmt = DB.prepareStatement(sql, po.get_TrxName());
							pstmt.setInt(1, i_PO.getJP_PP_Plan_ID());
							rs = pstmt.executeQuery();
							if (rs.next())
							{
								movementQty = rs.getBigDecimal(1);
								JP_MovementQtyFact = rs.getBigDecimal(2);
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

						movementQty = movementQty.setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP);
						JP_MovementQtyFact  = JP_MovementQtyFact.setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP);

						//Switch for test purpose
						if(IS_USE_MODEL_CLASS_FOR_UPDATE_PARENT)
						{

							MPPPlan ppPlan = new MPPPlan(po.getCtx(), i_PO.getJP_PP_Plan_ID(), po.get_TrxName());
							ppPlan.setName(ppPlan.getJP_Name() + " ["+ JP_MovementQtyFact + "/" + movementQty + "]");

							if(!ppPlan.save(po.get_TrxName()))
							{
								log.saveError("SaveError", "PPPlanLineModelValidator  -> modelChange() -> MPPPlan.Save()");
								return Msg.getMsg(po.getCtx(), "SaveError") + "PPPlanLineModelValidator -> modelChange(() -> MPPPlan.Save()";
							}

						}else {

							sql = "UPDATE JP_PP_Plan SET NAME = JP_NAME || ' [' || ? || '/' || ? || ']'"
									+ " WHERE JP_PP_Plan_ID=?";

							int no = DB.executeUpdate(sql
										, new Object[]{JP_MovementQtyFact, movementQty, i_PO.getJP_PP_Plan_ID()}
										, false, po.get_TrxName(), 0);

							if (no != 1)
							{
								log.saveError("DBExecuteError", "PPPlanLineModelValidator -> modelChange() -> UpdateSQL");
								return Msg.getMsg(po.getCtx(), "DBExecuteError") + "PPPlanLineModelValidator -> modelChange() -> UpdateSQL";
							}

						}
					}
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
