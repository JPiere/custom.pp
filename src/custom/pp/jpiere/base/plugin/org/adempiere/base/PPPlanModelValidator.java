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

import org.compiere.model.MClient;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.Util;

import custom.pp.jpiere.base.plugin.org.adempiere.model.I_JP_PP_Plan;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPWorkProcess;


/**
 * JPIERE-0501:JPiere PP Plan Model Validator
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class PPPlanModelValidator implements ModelValidator {

	//private static CLogger log = CLogger.getCLogger(PPPlanModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(I_JP_PP_Plan.Table_Name, this);
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
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			//Set Name for Tree
			if(po instanceof I_JP_PP_Plan)
			{
				String JP_PP_WorkProcessType = po.get_ValueAsString("JP_PP_WorkProcessType");
				
				if(Util.isEmpty(JP_PP_WorkProcessType) || JP_PP_WorkProcessType.equals(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialProduction))
				{
					I_JP_PP_Plan i_PO = (I_JP_PP_Plan)po;
					i_PO.setName(i_PO.getJP_Name() + " [" +i_PO.getJP_ProductionQtyFact() + "/" + i_PO.getProductionQty()+"]");
					
				}else if(JP_PP_WorkProcessType.equals(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialMovement)) {
					
					I_JP_PP_Plan i_PO = (I_JP_PP_Plan)po;
					i_PO.setName(i_PO.getJP_Name());
					
				}else if(JP_PP_WorkProcessType.equals(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_NotCreateDocument)) {
					
					I_JP_PP_Plan i_PO = (I_JP_PP_Plan)po;
					i_PO.setName(i_PO.getJP_Name());
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
