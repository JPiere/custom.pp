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
import java.util.List;
import java.util.Properties;

import org.compiere.model.MLocator;
import org.compiere.model.MMovement;
import org.compiere.model.MProduct;
import org.compiere.model.Query;
import org.compiere.util.DB;
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
public class MPPMMFactLine extends X_JP_PP_MM_FactLine {

	public MPPMMFactLine(Properties ctx, int JP_PP_MM_FactLine_ID, String trxName) 
	{
		super(ctx, JP_PP_MM_FactLine_ID, trxName);
	}

	public MPPMMFactLine(Properties ctx, int JP_PP_MM_FactLine_ID, String trxName, String... virtualColumns)
	{
		super(ctx, JP_PP_MM_FactLine_ID, trxName, virtualColumns);
	}

	public MPPMMFactLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}
	
	
	
	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		if (newRecord && getParent().isProcessed()) {
			log.saveError("ParentComplete", Msg.translate(getCtx(), "JP_PP_MM_Fact_ID"));
			return false;
		}

		//	Set Line No
		if (getLine() == 0)
		{
			String sql = "SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_PP_MM_FactLine WHERE JP_PP_Fact_ID=?";
			int ii = DB.getSQLValue (get_TrxName(), sql, getJP_PP_Fact_ID());
			setLine (ii);
		}
		
		 //either movement between locator or movement between lot
		if (getM_Locator_ID() == getM_LocatorTo_ID() && getM_AttributeSetInstance_ID() == getM_AttributeSetInstanceTo_ID())
		{
			log.saveError("Error", Msg.parseTranslation(getCtx(), "@M_Locator_ID@ == @M_LocatorTo_ID@ and @M_AttributeSetInstance_ID@ == @M_AttributeSetInstanceTo_ID@"));
			return false;
		}

		if (getMovementQty().signum() == 0)
		{
			String docAction = getParent().getDocAction();
			String docStatus = getParent().getDocStatus();
			if (   MMovement.DOCACTION_Void.equals(docAction)
				&& (   MMovement.DOCSTATUS_Drafted.equals(docStatus)
					|| MMovement.DOCSTATUS_Invalid.equals(docStatus)
					|| MMovement.DOCSTATUS_InProgress.equals(docStatus)
					|| MMovement.DOCSTATUS_Approved.equals(docStatus)
					|| MMovement.DOCSTATUS_NotApproved.equals(docStatus)
				   )
				)
			{
				// [ 2092198 ] Error voiding an Inventory Move - globalqss
				// zero allowed in this case (action Void and status Draft)
			} else if (   MMovement.DOCACTION_Complete.equals(docAction)
					   && MMovement.DOCSTATUS_InProgress.equals(docStatus))
			{
				// IDEMPIERE-2624 Cant confirm 0 qty on Movement Confirmation
				// zero allowed in this case (action Complete and status In Progress)
			} else {
				log.saveError("FillMandatory", Msg.getElement(getCtx(), "MovementQty"));
				return false;
			}
		}

		//	Qty Precision
		if (newRecord || is_ValueChanged(COLUMNNAME_MovementQty))
			setMovementQty(getMovementQty());

		if (getM_AttributeSetInstanceTo_ID() == 0)
		{
			//instance id default to same for movement between locator 
			if (getM_Locator_ID() != getM_LocatorTo_ID())
			{
				if (getM_AttributeSetInstance_ID() != 0)        //set to from
					setM_AttributeSetInstanceTo_ID(getM_AttributeSetInstance_ID());
			}

		}       //      ASI
		
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
			int int_WarehouseFrom_ID = getParent().getParent().getJP_WarehouseFrom_ID();
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
			
			int int_PhysicalWarehouseFrom_ID =  getParent().getParent().getJP_PhysicalWarehouseFrom_ID();
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
			int int_WarehouseTo_ID = getParent().getParent().getJP_WarehouseTo_ID();
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
			
			int int_PhysicalWarehouseTo_ID = getParent().getParent().getJP_PhysicalWarehouseTo_ID();
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

		return true;
	}

	/**
	 * 	Before Delete
	 *	@return true if it can be deleted
	 */
	@Override
	protected boolean beforeDelete() {

		return super.beforeDelete();
	}
	
	@Override
	protected boolean afterSave(boolean newRecord, boolean success) 
	{
		return super.afterSave(newRecord, success);
	}

	@Override
	protected boolean afterDelete(boolean success)
	{
		return super.afterDelete(success);
	}


	protected MPPFact parent = null;
	
	public MPPFact getParent()
	{
		if(parent==null)
			parent = new MPPFact(getCtx(), getJP_PP_Fact_ID(), get_TrxName());
		else
			parent.set_TrxName(get_TrxName());

		return parent;
	}
	
	public MPPMMFactLineMA[] getPPMMFactLineMAs ()
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPMMFactLineMA.COLUMNNAME_JP_PP_MM_FactLine_ID+"=? ");
		StringBuilder orderClause = new StringBuilder(MPPMMFactLineMA.COLUMNNAME_JP_PP_MM_FactLine_ID);//TODO  JP_PP_MM_FactLineMA_IDのカラムは無くて良いの？
		//
		List<MPPMMFactLineMA> list = new Query(getCtx(), MPPMMFactLineMA.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause.toString())
										.list();

		return list.toArray(new MPPMMFactLineMA[list.size()]);

	}

	/**
	 * 	Get Product
	 *	@return product or null if not defined
	 */
	public MProduct getProduct()
	{
		if (getM_Product_ID() != 0)
			return MProduct.getCopy(getCtx(), getM_Product_ID(), get_TrxName());
		return null;
	}	//	getProduct
}
