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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MMovementLine;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOM;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0501: JPiere PP Doc
 * JPIERE-0502: JPiere PP Doc Template
 * JPIERE-0609: Workprocess & Create Material Movement From PP Fact Doc.
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
		/** Common check */ 
		//SetAD_Org_ID
		if(newRecord)
		{
			setAD_Org_ID(getParent().getAD_Org_ID());
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
		
		//JPIERE-0582
		if(!isRecordRouteJP())
		{
			setJP_WarehouseDst_ID(0);
			setJP_PhysicalWarehouseDst_ID(0);
		}
		
		
		//Check Work Process Type
		String JP_PP_WorkProcessType = getJP_PP_WorkProcessType();
		if(!newRecord)
		{
			Object obj_Old_JP_PP_WorkProcessType = get_ValueOld(MPPWorkProcess.COLUMNNAME_JP_PP_WorkProcessType);
			String old_JP_PP_WorkProcessType = null;
					
			if(obj_Old_JP_PP_WorkProcessType == null)
				old_JP_PP_WorkProcessType = MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialProduction;
			else	
				old_JP_PP_WorkProcessType = (String)obj_Old_JP_PP_WorkProcessType;
			
			if(JP_PP_WorkProcessType.equals(old_JP_PP_WorkProcessType))
			{
				;//Nothing to do
				
			}else{
				
				if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_NotCreateDocument.equals(old_JP_PP_WorkProcessType))
				{
					MPPPlanLineT[] m_PPPlanLineTs = getPPPlanLineTs();
					if(m_PPPlanLineTs.length > 0)
					{
						//This PP Plan have some PP Plan lines, you can not change to different Work process type.
						String msg = Msg.getMsg(getCtx(), "JP_PP_DiffWorkProcessType");
						log.saveError("Error", msg);
						return false;
					}
					
					MPPMMPlanLineT[] m_PPMMPlanLineTs = getPPMMPlanLineTs();
					if(m_PPMMPlanLineTs.length > 0)
					{
						String msg = Msg.getMsg(getCtx(), "JP_PP_DiffWorkProcessType");
						log.saveError("Error", msg);
						return false;
					}
					
				}else if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialProduction.equals(old_JP_PP_WorkProcessType)) {
					
					MPPPlanLineT[] m_PPPlanLineTs = getPPPlanLineTs();
					if(m_PPPlanLineTs.length > 0)
					{
						String msg = Msg.getMsg(getCtx(), "JP_PP_DiffWorkProcessType");
						log.saveError("Error", msg);
						return false;
					}
					
				}else if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialMovement.equals(old_JP_PP_WorkProcessType)) {
					
					MPPMMPlanLineT[] m_PPMMPlanLineTs = getPPMMPlanLineTs();
					if(m_PPMMPlanLineTs.length > 0)
					{
						String msg = Msg.getMsg(getCtx(), "JP_PP_DiffWorkProcessType");
						log.saveError("Error", msg);
						return false;
					}
				}
			}
		}
		
		
		/** Work Process Type individual Check */
		if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_NotCreateDocument.equals(JP_PP_WorkProcessType))
		{
			//Initialize
			setJP_WarehouseFrom_ID(0);
			setJP_WarehouseTo_ID(0);
			setJP_PhysicalWarehouseFrom_ID(0);
			setJP_PhysicalWarehouseTo_ID(0);
			setIsRecordRouteJP(false);
			setJP_WarehouseNext_ID(0);
			setJP_WarehouseDst_ID(0);
			setJP_PhysicalWarehouseNext_ID(0);
			setJP_PhysicalWarehouseDst_ID(0);
			
			setM_Product_ID(0);
			setM_Locator_ID(0);
			setUPC(null);
			setProductionQty(Env.ZERO);
			setC_UOM_ID(0);
			
			setIsSplitWhenDifferenceJP(false);
			
		}else if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialProduction.equals(JP_PP_WorkProcessType)) {
			
			//Initialize
			setJP_WarehouseFrom_ID(0);
			setJP_WarehouseTo_ID(0);
			setJP_PhysicalWarehouseFrom_ID(0);
			setJP_PhysicalWarehouseTo_ID(0);
			setIsRecordRouteJP(false);
			setJP_WarehouseNext_ID(0);
			setJP_WarehouseDst_ID(0);
			setJP_PhysicalWarehouseNext_ID(0);
			setJP_PhysicalWarehouseDst_ID(0);
			
			
			//Mandatory check
			if(getM_Product_ID() == 0)
			{
				String msg = Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "M_Product_ID");
				log.saveError("Error", msg);
				return false;
			}
			
			if(getM_Locator_ID() == 0)
			{
				String msg = Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "M_Locator_ID");
				log.saveError("Error", msg);
				return false;
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
			
		}else if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialMovement.equals(JP_PP_WorkProcessType)) {
			
			//Initialize
			setM_Product_ID(0);
			setM_Locator_ID(0);
			setUPC(null);
			setProductionQty(Env.ZERO);
			setC_UOM_ID(0);
			
			if(!newRecord && isRecordRouteJP())
			{				
				int int_WarehouseFrom_ID = getJP_WarehouseFrom_ID();
				int int_PhysicalWarehouseFrom_ID = getJP_PhysicalWarehouseFrom_ID();
				int int_WarehouseTo_ID = getJP_WarehouseTo_ID();
				int int_PhysicalWarehouseTo_ID = getJP_PhysicalWarehouseTo_ID();
				StringBuffer sb = new StringBuffer();
				
				if(int_WarehouseFrom_ID != 0 && is_ValueChanged(MPPPlanT.COLUMNNAME_JP_WarehouseFrom_ID ))
				{
					String sql = "SELECT * FROM JP_PP_MM_PlanLineT ml INNER JOIN M_Locator loc ON (ml.M_Locator_ID = loc.M_Locator_ID) "
									+ " WHERE JP_PP_PlanT_ID = ? AND loc.M_Warehouse_ID <> ? ORDER BY ml.line";
					
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try
					{
						pstmt = DB.prepareStatement(sql, get_TrxName());
						pstmt.setInt(1, getJP_PP_PlanT_ID());
						pstmt.setInt(2, int_WarehouseFrom_ID);
						rs = pstmt.executeQuery();
						
						while (rs.next())
						{
							MPPMMPlanLineT line = new MPPMMPlanLineT(getCtx(), rs, get_TrxName());
							sb.append(line.getLine()).append(" / ");
						}
						
						if(!Util.isEmpty(sb.toString()))
						{
							String msg0 = Msg.getElement(Env.getCtx(), MPPPlanT.COLUMNNAME_JP_WarehouseFrom_ID);
							String msg1 = Msg.getElement(Env.getCtx(),  MPPMMPlanLineT.COLUMNNAME_M_Locator_ID);
							String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
							log.saveError("Error", msg + Msg.getElement(getCtx(), MPPMMPlanLineT.COLUMNNAME_Line) + " : " + sb.toString());
							return false;
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
				}
				
				if(int_PhysicalWarehouseFrom_ID != 0 && is_ValueChanged(MPPPlanT.COLUMNNAME_JP_PhysicalWarehouseFrom_ID ))
				{
					String sql = "SELECT * FROM JP_PP_MM_PlanLineT ml INNER JOIN M_Locator loc ON (ml.M_Locator_ID = loc.M_Locator_ID) "
							+ " WHERE JP_PP_PlanT_ID = ? AND loc.JP_PhysicalWarehouse_ID <> ? ORDER BY ml.line";
					
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try
					{
						pstmt = DB.prepareStatement(sql, get_TrxName());
						pstmt.setInt(1, getJP_PP_PlanT_ID());
						pstmt.setInt(2, int_PhysicalWarehouseFrom_ID);
						rs = pstmt.executeQuery();
						while (rs.next())
						{
							MPPMMPlanLineT line = new MPPMMPlanLineT(getCtx(), rs, get_TrxName());
							sb.append(line.getLine()).append(" / ");
						}
						
						if(!Util.isEmpty(sb.toString()))
						{
							String msg0 = Msg.getElement(Env.getCtx(), MPPPlanT.COLUMNNAME_JP_PhysicalWarehouseFrom_ID);
							String msg1 = Msg.getElement(Env.getCtx(),  MPPMMPlanLineT.COLUMNNAME_M_Locator_ID);
							String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
							log.saveError("Error", msg + Msg.getElement(getCtx(), MPPMMPlanLineT.COLUMNNAME_Line) + " : " + sb.toString());
							return false;
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
				}
				
				if(int_WarehouseTo_ID != 0 && is_ValueChanged(MPPPlanT.COLUMNNAME_JP_WarehouseTo_ID))
				{
					String sql = "SELECT * FROM JP_PP_MM_PlanLineT ml INNER JOIN M_Locator loc ON (ml.M_LocatorTo_ID = loc.M_Locator_ID) "
							+ " WHERE JP_PP_PlanT_ID = ? AND loc.M_Warehouse_ID <> ? ORDER BY ml.line";		
					
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try
					{
						pstmt = DB.prepareStatement(sql, get_TrxName());
						pstmt.setInt(1, getJP_PP_PlanT_ID());
						pstmt.setInt(2, int_WarehouseTo_ID);
						rs = pstmt.executeQuery();
						while (rs.next())
						{
							MPPMMPlanLineT line = new MPPMMPlanLineT(getCtx(), rs, get_TrxName());
							sb.append(line.getLine()).append(" / ");
						}
						
						if(!Util.isEmpty(sb.toString()))
						{
							String msg0 = Msg.getElement(Env.getCtx(), MPPPlanT.COLUMNNAME_JP_WarehouseTo_ID);
							String msg1 = Msg.getElement(Env.getCtx(), MPPMMPlanLineT.COLUMNNAME_M_LocatorTo_ID);
							String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
							log.saveError("Error",  msg + Msg.getElement(getCtx(), MPPMMPlanLineT.COLUMNNAME_Line) + " : " + sb.toString());
							return false;
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
				}
				
				
				if(int_PhysicalWarehouseTo_ID != 0 && is_ValueChanged(MPPPlanT.COLUMNNAME_JP_PhysicalWarehouseTo_ID))
				{
					String sql = "SELECT * FROM JP_PP_MM_PlanLineT ml INNER JOIN M_Locator loc ON (ml.M_LocatorTo_ID = loc.M_Locator_ID) "
							+ " WHERE JP_PP_PlanT_ID = ? AND loc.JP_PhysicalWarehouse_ID <> ? ORDER BY ml.line";	
					
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try
					{
						pstmt = DB.prepareStatement(sql, get_TrxName());
						pstmt.setInt(1, getJP_PP_PlanT_ID());
						pstmt.setInt(2, int_PhysicalWarehouseTo_ID);
						rs = pstmt.executeQuery();
						while (rs.next())
						{
							MMovementLine line = new MMovementLine(getCtx(), rs, get_TrxName());
							sb.append(line.getLine()).append(" / ");
						}
						
						if(!Util.isEmpty(sb.toString()))
						{
							String msg0 = Msg.getElement(Env.getCtx(), MPPPlanT.COLUMNNAME_JP_PhysicalWarehouseTo_ID);
							String msg1 = Msg.getElement(Env.getCtx(),  MPPMMPlanLineT.COLUMNNAME_M_LocatorTo_ID);
							String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
							log.saveError("Error",  msg + Msg.getElement(getCtx(), MPPMMPlanLineT.COLUMNNAME_Line) + " : " + sb.toString());
							return false;
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
				}
				
			}//if(!newRecord && get_ValueAsBoolean(MPPPlanT.COLUMNNAME_IsRecordRouteJP))
			
		}//Work Process Type individual Check 
		

		//For Tree
		setName(getJP_Name());

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		if(!success)
			return false;

		
		/** Work Process Type individual Check */
		String JP_PP_WorkProcessType = getJP_PP_WorkProcessType();
		if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_NotCreateDocument.equals(JP_PP_WorkProcessType))
		{
			;
			
		}else if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialProduction.equals(JP_PP_WorkProcessType)) {
			
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
		
		}else if(MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialMovement.equals(JP_PP_WorkProcessType)) {
			
			;
		}
		

		return true;
	}

	@Override
	public String getJP_PP_WorkProcessType() 
	{
		String JP_PP_WorkProcessType = super.getJP_PP_WorkProcessType();
		if(Util.isEmpty(JP_PP_WorkProcessType))
			return MPPWorkProcess.JP_PP_WORKPROCESSTYPE_MaterialProduction;
		else
			return JP_PP_WorkProcessType;
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
	
	private MPPMMPlanLineT[] m_PPMMPlanLineTs = null;

	public MPPMMPlanLineT[] getPPMMPlanLineTs (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPMMPlanLineT.COLUMNNAME_JP_PP_PlanT_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPMMPlanLineT.COLUMNNAME_Line;
		//
		List<MPPMMPlanLineT> list = new Query(getCtx(), MPPMMPlanLineT.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPMMPlanLineT[list.size()]);

	}

	public MPPMMPlanLineT[] getPPMMPlanLineTs(boolean requery, String orderBy)
	{
		if (m_PPMMPlanLineTs != null && !requery) {
			set_TrxName(m_PPMMPlanLineTs, get_TrxName());
			return m_PPMMPlanLineTs;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPMMPlanLineT.COLUMNNAME_Line;

		m_PPMMPlanLineTs = getPPMMPlanLineTs(" AND IsActive='Y' ", orderClause);
		return m_PPMMPlanLineTs;
	}

	public MPPMMPlanLineT[] getPPMMPlanLineTs()
	{
		return getPPMMPlanLineTs(false, null);
	}
}
