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

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MLocator;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * JPIERE-0501: JPiere PP Doc
 * JPIERE-0502: JPiere PP Doc Template
 * JPIERE-0609: Workprocess & Create Material Movement From PP Fact Doc.
 *
 * @author Hideaki Hagiwara
 *
 */
public class MPPMMPlanLine extends X_JP_PP_MM_PlanLine {

	public MPPMMPlanLine(Properties ctx, int JP_PP_MM_PlanLine_ID, String trxName)
	{
		super(ctx, JP_PP_MM_PlanLine_ID, trxName);
	}

	public MPPMMPlanLine(Properties ctx, int JP_PP_MM_PlanLine_ID, String trxName, String... virtualColumns)
	{
		super(ctx, JP_PP_MM_PlanLine_ID, trxName, virtualColumns);
	}

	public MPPMMPlanLine(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	/** Parent							*/
	protected MPPPlan m_parent = null;
	
	/**
	 * get Parent
	 * @return Parent Movement
	 */
	public MPPPlan getParent() 
	{
		if (m_parent == null)
			m_parent = new MPPPlan (getCtx(), getJP_PP_Plan_ID(), get_TrxName());
		return m_parent;
	}	//	getParent

	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		 //either movement between locator or movement between lot
		if (getM_Locator_ID() == getM_LocatorTo_ID() && getM_AttributeSetInstance_ID() == getM_AttributeSetInstanceTo_ID())
		{
			log.saveError("Error", Msg.parseTranslation(getCtx(), "@M_Locator_ID@ == @M_LocatorTo_ID@ and @M_AttributeSetInstance_ID@ == @M_AttributeSetInstanceTo_ID@"));
			return false;
		}
		
		if (getM_AttributeSetInstanceTo_ID() == 0)
		{
			//instance id default to same for movement between locator 
			if (getM_Locator_ID() != getM_LocatorTo_ID())
			{
				if (getM_AttributeSetInstance_ID() != 0)        //set to from
					setM_AttributeSetInstanceTo_ID(getM_AttributeSetInstance_ID());
			}

		}       //      ASI
		
		if(newRecord|| is_ValueChanged("M_Locator_ID"))
		{			
			int int_WarehouseFrom_ID = getParent().getJP_WarehouseFrom_ID();
			if(int_WarehouseFrom_ID != 0)
			{
				if(MLocator.get(getM_Locator_ID()).getM_Warehouse_ID() != int_WarehouseFrom_ID)
				{
					String msg0 = Msg.getElement(Env.getCtx(), "JP_WarehouseFrom_ID");
					String msg1 = Msg.getElement(Env.getCtx(), "M_Locator_ID");
					String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
					log.saveError("Error", msg);
					return false;
				}
			}
			
			int int_PhysicalWarehouseFrom_ID =  getParent().getJP_PhysicalWarehouseFrom_ID();
			if(int_PhysicalWarehouseFrom_ID != 0)
			{
				MLocator loc = MLocator.get(getM_Locator_ID());
				int int_PhysicalWarehouse_ID = loc.get_ValueAsInt("JP_PhysicalWarehouse_ID");
				if(int_PhysicalWarehouseFrom_ID != int_PhysicalWarehouse_ID)
				{				
					String msg0 = Msg.getElement(Env.getCtx(), "JP_PhysicalWarehouseFrom_ID");
					String msg1 = Msg.getElement(Env.getCtx(), "M_Locator_ID");
					String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
					log.saveError("Error", msg);
					return false;
				}
			}
			
		}
		
		if(newRecord || is_ValueChanged("M_LocatorTo_ID"))
		{
			int int_WarehouseTo_ID = getParent().getJP_WarehouseTo_ID();
			if(int_WarehouseTo_ID != 0)
			{
				if(MLocator.get(getM_LocatorTo_ID()).getM_Warehouse_ID() != int_WarehouseTo_ID)
				{
					String msg0 = Msg.getElement(Env.getCtx(), "JP_WarehouseTo_ID");
					String msg1 = Msg.getElement(Env.getCtx(), "M_LocatorTo_ID");
					String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
					log.saveError("Error", msg);
					return false;
				}
			}
			
			int int_PhysicalWarehouseTo_ID = getParent().getJP_PhysicalWarehouseTo_ID();
			if(int_PhysicalWarehouseTo_ID != 0)
			{
				MLocator loc = MLocator.get(getM_LocatorTo_ID());
				int int_PhysicalWarehouse_ID = loc.get_ValueAsInt("JP_PhysicalWarehouse_ID");
				if(int_PhysicalWarehouseTo_ID != int_PhysicalWarehouse_ID)
				{				
					String msg0 = Msg.getElement(Env.getCtx(), "JP_PhysicalWarehouseTo_ID");
					String msg1 = Msg.getElement(Env.getCtx(), "M_LocatorTo_ID");
					String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
					log.saveError("Error", msg);
					return false;
				}
			}
		}
		
		if(newRecord || is_ValueChanged("MovementQty"))
		{
			if(getJP_MovementQtyFact().signum() > 0)
			{
				if(getMovementQty().compareTo(getJP_MovementQtyFact()) < 0)
				{
					String msg0 = Msg.getElement(Env.getCtx(), "MovementQty");
					String msg1 = Msg.getElement(Env.getCtx(), "JP_MovementQtyFact");
					log.saveError("Error", msg0 + " < "+msg1);
					return false;
				}
				
			}else if(getJP_MovementQtyFact().signum() < 0) {
				
				if(getMovementQty().compareTo(getJP_MovementQtyFact()) > 0)
				{
					String msg0 = Msg.getElement(Env.getCtx(), "MovementQty");
					String msg1 = Msg.getElement(Env.getCtx(), "JP_MovementQtyFact");
					log.saveError("Error", msg0 + " > "+msg1);
					return false;
				}
			}
		}
		
		return true;
	}

}
