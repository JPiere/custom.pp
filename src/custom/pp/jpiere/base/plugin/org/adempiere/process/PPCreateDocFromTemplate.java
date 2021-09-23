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
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;

import org.compiere.model.I_C_NonBusinessDay;
import org.compiere.model.MLocator;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MTable;
import org.compiere.model.MTree;
import org.compiere.model.MTree_Node;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_C_NonBusinessDay;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPDoc;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPDocT;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlan;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlanLine;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlanLineT;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlanT;


/**
 * JPIERE-0502: Create JPiere PP Doc from Template
 *
 * @author Hideaki Hagiwara
 *
 */
public class PPCreateDocFromTemplate extends SvrProcess {

	private int p_Record_ID = 0;
	private int p_JP_PP_DocT_ID = 0;
	private BigDecimal p_CoefficientQty = Env.ZERO;
	private Timestamp p_JP_PP_ScheduledStart = null;
	private String p_Value = null;
	private String p_Name = null;
	private MTable m_Table = null;

	private int default_M_Locator_ID = 0;
	private MPPDoc m_PPDoc = null;
	private MPPDocT m_PPDocT = null;

	private LocalDateTime local_ScheduledStart = null;


	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if ("JP_PP_DocT_ID".equals(name))
				p_JP_PP_DocT_ID = para[i].getParameterAsInt();
			else if ("QtyEntered".equals(name))
				p_CoefficientQty = para[i].getParameterAsBigDecimal();
			else if ("JP_PP_ScheduledStart".equals(name))
				p_JP_PP_ScheduledStart  =  para[i].getParameterAsTimestamp();
			else if ("Value".equals(name))
				p_Value  =  para[i].getParameterAsString();
			else if ("Name".equals(name))
				p_Name  =  para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		p_Record_ID = getRecord_ID();

		m_Table = MTable.get(getTable_ID());
	}



	@Override
	protected String doIt() throws Exception
	{
		if(m_Table.getTableName().equals(MOrderLine.Table_Name))
		{
			int C_OrderLine_ID = getRecord_ID();

			if(Util.isEmpty(p_Value))
			{
				throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "Value"));
			}else {

				MPPDoc ppd = MPPDoc.get(getCtx(), p_Value, get_TrxName());
				if(ppd != null)
				{
					throw new Exception(Msg.getMsg(getCtx(), "JP_Unique_Constraint_Value"));
				}
			}

			createPPDoc();
			createPlan();
			updateTree();

			MOrderLine oLine = new MOrderLine(getCtx(), C_OrderLine_ID, get_TrxName());
			m_PPDoc.setC_BPartner_ID(oLine.getParent().getC_BPartner_ID());
			m_PPDoc.setC_Order_ID(oLine.getC_Order_ID());
			m_PPDoc.setPOReference(oLine.getParent().getPOReference());
			m_PPDoc.save(get_TrxName());

			addBufferLog(0, null, null, m_PPDoc.getDocumentInfo(), MPPDoc.Table_ID, m_PPDoc.getJP_PP_Doc_ID());


		}else if(m_Table.getTableName().equals(MPPDocT.Table_Name)){

			p_JP_PP_DocT_ID = p_Record_ID;

			if(Util.isEmpty(p_Value))
			{
				throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "Value"));
			}else {

				MPPDoc ppd = MPPDoc.get(getCtx(), p_Value, get_TrxName());
				if(ppd != null)
				{
					throw new Exception(Msg.getMsg(getCtx(), "JP_Unique_Constraint_Value"));
				}
			}

			createPPDoc();
			createPlan();
			updateTree();
			addBufferLog(0, null, null, m_PPDoc.getDocumentInfo(), MPPDoc.Table_ID, m_PPDoc.getJP_PP_Doc_ID());

		}else if(m_Table.getTableName().equals(MPPDoc.Table_Name)) {

			m_PPDoc = new MPPDoc(getCtx(), p_Record_ID, get_TrxName());

			if(m_PPDoc.getProductionQty().compareTo(Env.ZERO)==0)
			{
				throw new Exception(Msg.getElement(getCtx(), MPPDoc.COLUMNNAME_ProductionQty) + " = 0" );
			}

			p_CoefficientQty = m_PPDoc.getProductionQty();
			p_JP_PP_ScheduledStart = m_PPDoc.getJP_PP_ScheduledStart();
			if(p_JP_PP_DocT_ID == 0)
			{
				p_JP_PP_DocT_ID = m_PPDoc.getJP_PP_DocT_ID();
			}

			if(p_JP_PP_DocT_ID == 0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "NotFound")+ " " + Msg.getElement(getCtx(), MPPDoc.COLUMNNAME_JP_PP_DocT_ID) );

			}else {

				if(p_JP_PP_DocT_ID !=  m_PPDoc.getJP_PP_DocT_ID())
				{
					m_PPDoc.setJP_PP_DocT_ID(p_JP_PP_DocT_ID);
					m_PPDoc.saveEx(get_TrxName());
				}
			}

			m_PPDocT = new MPPDocT(getCtx(), p_JP_PP_DocT_ID, get_TrxName());

			MPPPlanT[] ppPlanTs = m_PPDocT.getPPPlanTs();
			MPPPlan[] ppPlans = m_PPDoc.getPPPlans();
			for(MPPPlanT ppPlanT :  ppPlanTs )
			{
				for(MPPPlan ppPlan :  ppPlans )
				{
					if(ppPlanT.getJP_PP_PlanT_ID() == ppPlan.getJP_PP_PlanT_ID() )
					{
						//The PP Template is already in use.
						throw new Exception(Msg.getMsg(getCtx(), "JP_PP_TemplateInUse"));
					}
				}
			}

			createPlan();
			updateTree();

		}else if(m_Table.getTableName().equals(MPPPlan.Table_Name)) {

			MPPPlan ppPlan = new MPPPlan(getCtx(), p_Record_ID, get_TrxName());
			if(ppPlan.getJP_PP_PlanT_ID() == 0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "NotFound")+ " " + Msg.getElement(getCtx(), MPPPlan.COLUMNNAME_JP_PP_PlanT_ID) );
			}

			if(ppPlan.getProductionQty().compareTo(Env.ZERO)==0)
			{
				throw new Exception(Msg.getElement(getCtx(), MPPDoc.COLUMNNAME_ProductionQty) + " = 0" );
			}

			if(ppPlan.getPPPlanLines().length > 0 )
			{
				// There are PP Lines already.
				throw new Exception(Msg.getMsg(getCtx(), "JP_PP_LinesThere"));
			}

			MPPPlanT ppPlanT = new MPPPlanT(getCtx(), ppPlan.getJP_PP_PlanT_ID(), get_TrxName());

			if(ppPlan.getM_Product_ID() != ppPlanT.getM_Product_ID())
			{
				//Different between {0} and {1}
				String msg0 = Msg.getElement(Env.getCtx(), MPPPlan.COLUMNNAME_JP_PP_Plan_ID) + " - " + Msg.getElement(Env.getCtx(), MPPPlan.COLUMNNAME_M_Product_ID);
				String msg1 = Msg.getElement(Env.getCtx(), MPPPlanT.COLUMNNAME_JP_PP_PlanT_ID) + " - " + Msg.getElement(Env.getCtx(),  MPPPlan.COLUMNNAME_M_Product_ID);
				throw new Exception(Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));
			}

			if(ppPlanT.getPPPlanLineTs().length == 0)
			{
				//There are not PP Plan Line Templates at PP Plan Template.
				throw new Exception(Msg.getMsg(getCtx(), "JP_PP_LineT_NotThere"));
			}

			BigDecimal planQty = ppPlan.getProductionQty();
			BigDecimal templateQty = ppPlanT.getProductionQty();;
			BigDecimal rate = Env.ONE;
			if(templateQty != null && templateQty.compareTo(Env.ZERO) != 0)
				rate = planQty.divide(templateQty, 4, RoundingMode.HALF_UP);

			p_CoefficientQty = rate;
			createPlanLine(ppPlan, ppPlanT);

			ppPlan.setIsCreated("Y");
			ppPlan.saveEx(get_TrxName());
		}

		return "@OK@";
	}

	private MPPDoc createPPDoc()
	{
		m_PPDoc = new  MPPDoc(getCtx(), 0, get_TrxName());
		m_PPDocT = new MPPDocT(getCtx(), p_JP_PP_DocT_ID, get_TrxName());

		PO.copyValues(m_PPDocT, m_PPDoc);

		//Copy mandatory column to make sure
		m_PPDoc.setJP_PP_DocT_ID(p_JP_PP_DocT_ID);
		m_PPDoc.setAD_Org_ID(m_PPDocT.getAD_Org_ID());
		m_PPDoc.setM_Product_ID(m_PPDocT.getM_Product_ID());
		m_PPDoc.setQtyEntered(p_CoefficientQty);
		m_PPDoc.setC_UOM_ID(m_PPDocT.getC_UOM_ID());
		m_PPDoc.setProductionQty(p_CoefficientQty.multiply(m_PPDocT.getProductionQty()));
		m_PPDoc.setC_DocType_ID(m_PPDocT.getC_DocType_ID());
		m_PPDoc.setValue(p_Value);
		m_PPDoc.setName(p_Name);
		m_PPDoc.setDocStatus(DocAction.STATUS_Drafted);
		m_PPDoc.setDocAction(DocAction.ACTION_Complete);
		m_PPDoc.setJP_PP_Status(MPPDoc.JP_PP_STATUS_NotYetStarted);
		m_PPDoc.setJP_Processing1("N");
		m_PPDoc.setJP_Processing2("N");
		m_PPDoc.setJP_Processing3("N");
		m_PPDoc.setJP_Processing4("N");
		m_PPDoc.setJP_Processing5("N");
		m_PPDoc.setJP_Processing6("N");

		//Set JP_PP_ScheduledStart
		LocalDateTime toDay = p_JP_PP_ScheduledStart.toLocalDateTime();
		while (isNonBusinessDay(toDay))
		{
			toDay = toDay.plusDays(1);
		}
		local_ScheduledStart = toDay;
		m_PPDoc.setJP_PP_ScheduledStart(Timestamp.valueOf(local_ScheduledStart));

		//Set JP_PP_ScheduledEnd
		int JP_ProductionDays = m_PPDocT.getJP_ProductionDays();
		while (JP_ProductionDays > 1 )
		{
			toDay = toDay.plusDays(1);
			if(isBusinessDay(toDay))
				JP_ProductionDays--;
		}
		m_PPDoc.setJP_PP_ScheduledEnd(Timestamp.valueOf(toDay));
		m_PPDoc.setDateAcct(Timestamp.valueOf(toDay));

		m_PPDoc.saveEx(get_TrxName());

		return m_PPDoc;
	}

	private MPPPlan createPlan() throws Exception
	{
		MPPPlanT[] ppPlanTs = m_PPDocT.getPPPlanTs(true, null);
		MPPPlan ppPlan = null;
		for(MPPPlanT ppPlanT : ppPlanTs)
		{
			ppPlan = m_PPDoc.getPPPlan(ppPlanT.getSeqNo(), ppPlanT.getM_Product_ID(), ppPlanT.getValue());
			if(ppPlan != null)
			{
				//The SeqNo, Product, Search Key has already been registered
				throw new Exception(Msg.getMsg(getCtx(),"JP_Unique_Constraint_PPPlan"));
			}

			ppPlan = new  MPPPlan(getCtx(), 0, get_TrxName());
			PO.copyValues(ppPlanT, ppPlan);

			//Copy mandatory column to make sure
			ppPlan.setJP_PP_Doc_ID(m_PPDoc.getJP_PP_Doc_ID());
			ppPlan.setAD_Org_ID(m_PPDoc.getAD_Org_ID());
			ppPlan.setDocumentNo(null);
			ppPlan.setJP_PP_PlanT_ID(ppPlanT.getJP_PP_PlanT_ID());
			ppPlan.setSeqNo(ppPlanT.getSeqNo());
			ppPlan.setIsSummary(ppPlanT.isSummary());
			ppPlan.setM_Product_ID(ppPlanT.getM_Product_ID());
			ppPlan.setC_DocType_ID(ppPlanT.getC_DocType_ID());
			ppPlan.setValue(ppPlanT.getValue());
			ppPlan.setJP_Name(ppPlanT.getJP_Name());
			ppPlan.setName(ppPlanT.getJP_Name());
			ppPlan.setProductionQty(p_CoefficientQty.multiply(ppPlanT.getProductionQty()));
			ppPlan.setJP_ProductionQtyFact(Env.ZERO);
			ppPlan.setC_UOM_ID(ppPlanT.getC_UOM_ID());
			ppPlan.setJP_PP_Workload_Plan(ppPlanT.getJP_PP_Workload_Plan());
			ppPlan.setJP_PP_Workload_UOM_ID(ppPlanT.getJP_PP_Workload_UOM_ID());
			ppPlan.setDocStatus(DocAction.STATUS_Drafted);
			ppPlan.setDocAction(DocAction.ACTION_Complete);
			ppPlan.setJP_PP_Status(MPPDoc.JP_PP_STATUS_NotYetStarted);
			ppPlan.setIsCreated("Y");
			ppPlan.setJP_Processing1("N");
			ppPlan.setJP_Processing2("N");
			ppPlan.setJP_Processing3("N");
			ppPlan.setJP_Processing4("N");
			ppPlan.setJP_Processing5("N");
			ppPlan.setJP_Processing6("N");

			//Set Locator
			if(ppPlan.getAD_Org_ID() == ppPlanT.getAD_Org_ID())
			{
				ppPlan.setM_Locator_ID(ppPlanT.getM_Locator_ID());

			}else if (default_M_Locator_ID != 0){

				ppPlan.setM_Locator_ID(default_M_Locator_ID);

			}else {

				default_M_Locator_ID = searchLocator(ppPlan.getAD_Org_ID());
				if(default_M_Locator_ID > 0)
				{
					ppPlan.setM_Locator_ID(default_M_Locator_ID);
				}else {
					throw new Exception(Msg.getMsg(getCtx(), "JP_PP_NotFoundLocatorCopyTemplate"));
				}
			}

			//Set JP_PP_ScheduledStart
			int offset = ppPlanT.getJP_DayOffset();

			if(local_ScheduledStart == null)
				local_ScheduledStart = m_PPDoc.getJP_PP_ScheduledStart().toLocalDateTime();
			LocalDateTime startDay = local_ScheduledStart;
			while (offset >= 0 )
			{
				if(isBusinessDay(startDay))
				{
					if(offset == 0)
					{
						;//Noting to do;
					}else {
						startDay = startDay.plusDays(1);
					}
					offset--;
				}else {
					startDay = startDay.plusDays(1);
				}
			}

			if(ppPlanT.getJP_PP_ScheduledStartTime() == null)
			{
				ppPlan.setJP_PP_ScheduledStart(Timestamp.valueOf(startDay));
			}else {

				LocalTime localTime = ppPlanT.getJP_PP_ScheduledStartTime().toLocalDateTime().toLocalTime();
				LocalDateTime localDateTime = LocalDateTime.of(startDay.toLocalDate(), localTime);
				ppPlan.setJP_PP_ScheduledStart(Timestamp.valueOf(localDateTime));
			}

			//Set JP_PP_ScheduledEnd
			int JP_ProductionDays = ppPlanT.getJP_ProductionDays();
			while (JP_ProductionDays > 1 )
			{
				startDay = startDay.plusDays(1);
				if(isBusinessDay(startDay))
					JP_ProductionDays--;
			}
			if(ppPlanT.getJP_PP_ScheduledEndTime() == null)
			{
				ppPlan.setJP_PP_ScheduledEnd(Timestamp.valueOf(startDay));
				ppPlan.setDateAcct(Timestamp.valueOf(startDay));
			}else {
				ppPlan.setDateAcct(Timestamp.valueOf(startDay));
				LocalTime localTime = ppPlanT.getJP_PP_ScheduledEndTime().toLocalDateTime().toLocalTime();
				LocalDateTime localDateTime = LocalDateTime.of(startDay.toLocalDate(), localTime);
				ppPlan.setJP_PP_ScheduledEnd(Timestamp.valueOf(localDateTime));
			}


			ppPlan.saveEx(get_TrxName());

			createPlanLine(ppPlan, ppPlanT);

		}

		return ppPlan;
	}

	private int searchLocator(int AD_Org_ID)
	{
		MOrgInfo oInfo = MOrgInfo.get(AD_Org_ID);
		if(oInfo.getM_Warehouse_ID() > 0)
		{
			MWarehouse wh = MWarehouse.get(oInfo.getM_Warehouse_ID());
			MLocator[] locs = wh.getLocators(false);
			boolean isOK = false;

			for(MLocator loc : locs)
			{
				if(loc.isDefault())
				{
					default_M_Locator_ID =loc.getM_Locator_ID();
					return default_M_Locator_ID;
				}
			}

			if(!isOK)
			{
				//set First locator
				for(MLocator loc : locs)
				{
					default_M_Locator_ID =loc.getM_Locator_ID();
					return default_M_Locator_ID;
				}
			}

		}

		return 0;
	}

	private boolean createPlanLine(MPPPlan ppPlan, MPPPlanT ppPlanT) throws Exception
	{
		MPPPlanLineT[] ppPlanLineTs = ppPlanT.getPPPlanLineTs(true, null);
		MPPPlanLine ppPlanLine = null;
		for(MPPPlanLineT ppPlanLineT : ppPlanLineTs)
		{
			ppPlanLine = new  MPPPlanLine(getCtx(), 0, get_TrxName());
			PO.copyValues(ppPlanLineT, ppPlanLine);

			//Copy mandatory column to make sure
			ppPlanLine.setAD_Org_ID(ppPlan.getAD_Org_ID());
			ppPlanLine.setJP_PP_Plan_ID(ppPlan.getJP_PP_Plan_ID());
			ppPlanLine.setJP_PP_PlanLineT_ID(ppPlanLineT.getJP_PP_PlanLineT_ID());
			ppPlanLine.setLine(ppPlanLineT.getLine());
			ppPlanLine.setM_Product_ID(ppPlanLineT.getM_Product_ID());
			ppPlanLine.setIsEndProduct(ppPlanLineT.isEndProduct());
			ppPlanLine.setPlannedQty(p_CoefficientQty.multiply(ppPlanLineT.getPlannedQty()));
			ppPlanLine.setC_UOM_ID(ppPlanLineT.getC_UOM_ID());
			if(ppPlanLineT.isEndProduct())
			{
				ppPlanLine.setQtyUsed(null);
				ppPlanLine.setJP_QtyUsedFact(null);
			}else {
				ppPlanLine.setQtyUsed(p_CoefficientQty.multiply(ppPlanLineT.getQtyUsed()));
				ppPlanLine.setJP_QtyUsedFact(Env.ZERO);
			}
			ppPlanLine.setMovementQty(p_CoefficientQty.multiply(ppPlanLineT.getMovementQty()));
			ppPlanLine.setJP_Processing1("N");
			ppPlanLine.setJP_Processing2("N");
			ppPlanLine.setJP_Processing3("N");
			ppPlanLine.setIsCreated("N");

			//Set Locator
			if(ppPlanLine.getAD_Org_ID() == ppPlanLineT.getAD_Org_ID())
			{
				ppPlanLine.setM_Locator_ID(ppPlanLineT.getM_Locator_ID());
			}else if (default_M_Locator_ID != 0){

				ppPlanLine.setM_Locator_ID(default_M_Locator_ID);

			}else {

				default_M_Locator_ID = searchLocator(ppPlanLine.getAD_Org_ID());
				if(default_M_Locator_ID > 0)
				{
					ppPlanLine.setM_Locator_ID(default_M_Locator_ID);
				}else {
					throw new Exception(Msg.getMsg(getCtx(), "JP_PP_NotFoundLocatorCopyTemplate"));
				}
			}

			ppPlanLine.saveEx(get_TrxName());

		}


		if(ppPlanT.isCreatePPFactJP())
		{
			String msg = ppPlan.createFact(get_TrxName());
			if(!Util.isEmpty(msg))
				throw new Exception(msg);
		}

		return true;
	}

	private boolean updateTree()
	{
		int p_AD_TreeFrom_ID = MTree.getDefaultAD_Tree_ID(getAD_Client_ID(), "JP_PP_PlanT_ID");
		int p_AD_TreeTo_ID = MTree.getDefaultAD_Tree_ID(getAD_Client_ID(), "JP_PP_Plan_ID");
		MTree treeFrom =  new MTree(getCtx(), p_AD_TreeFrom_ID, get_TrxName());
		MTree treeTo =  new MTree(getCtx(), p_AD_TreeTo_ID, get_TrxName());

		MPPPlan[] ppPlans = m_PPDoc.getPPPlans(true, null);
		for(int i = 0;  i < ppPlans.length ; i++)
		{
			MTree_Node nodeTo = MTree_Node.get(treeTo, ppPlans[i].getJP_PP_Plan_ID());
			MTree_Node nodeFrom = MTree_Node.get(treeFrom, ppPlans[i].getJP_PP_PlanT_ID());

			for(int j = 0; j < ppPlans.length ; j++)
			{
				if(nodeFrom.getParent_ID() == 0)
				{
					nodeTo.setParent_ID(0);
					nodeTo.setSeqNo(nodeFrom.getSeqNo());
					nodeTo.save(get_TrxName());
					break;

				}else if(nodeFrom.getParent_ID() == ppPlans[j].getJP_PP_PlanT_ID()){

						nodeTo.setParent_ID(ppPlans[j].getJP_PP_Plan_ID());
						nodeTo.setSeqNo(nodeFrom.getSeqNo());
						nodeTo.save(get_TrxName());
						break;
				}

			}//for j
		}//for i

		return true;
	}

	private TreeSet<Timestamp> nonBusinessDaysSet = null;
	private boolean isNonBusinessDay(LocalDateTime toDay)
	{
		getNonBusinessDays(toDay);
		return nonBusinessDaysSet.contains(Timestamp.valueOf(toDay));

	}

	private boolean isBusinessDay(LocalDateTime toDay)
	{
		getNonBusinessDays(toDay);
		return !nonBusinessDaysSet.contains(Timestamp.valueOf(toDay));
	}

	private TreeSet<Timestamp> getNonBusinessDays(LocalDateTime toDay)
	{
		if(nonBusinessDaysSet == null)
		{
			nonBusinessDaysSet = new TreeSet<Timestamp>();

			if(m_PPDocT.getJP_NonBusinessDayCalendar_ID() > 0)
			{
				List<X_C_NonBusinessDay> list_NonBusinessDays = null;
				StringBuilder whereClause = null;
				StringBuilder orderClause = null;
				ArrayList<Object> list_parameters  = new ArrayList<Object>();
				Object[] parameters = null;

				LocalDateTime toDayMin = LocalDateTime.of(toDay.toLocalDate(), LocalTime.MIN);

				whereClause = new StringBuilder(" AD_Client_ID=? ");
				list_parameters.add(Env.getAD_Client_ID(getCtx()));

				//C_Calendar_ID
				whereClause = whereClause.append(" AND C_Calendar_ID = ? ");
				list_parameters.add(m_PPDocT.getJP_NonBusinessDayCalendar_ID());

				//Date1
				whereClause = whereClause.append(" AND Date1 >= ? AND IsActive='Y' ");
				list_parameters.add(Timestamp.valueOf(toDayMin));

				//C_Country_ID
				if(m_PPDocT.getC_Country_ID() == 0)
				{
					whereClause = whereClause.append(" AND C_Country_ID IS NULL ");

				}else {
					whereClause = whereClause.append(" AND ( C_Country_ID IS NULL OR C_Country_ID = ? ) ");
					list_parameters.add(m_PPDocT.getC_Country_ID());
				}

				parameters = list_parameters.toArray(new Object[list_parameters.size()]);
				orderClause = new StringBuilder("Date1");


				list_NonBusinessDays = new Query(Env.getCtx(), I_C_NonBusinessDay.Table_Name, whereClause.toString(), null)
													.setParameters(parameters)
													.setOrderBy(orderClause.toString())
													.list();

				LocalDateTime nonBusinessDayMin = null;
				for(X_C_NonBusinessDay m_NonBusinessDays : list_NonBusinessDays )
				{
					nonBusinessDayMin = LocalDateTime.of(m_NonBusinessDays.getDate1().toLocalDateTime().toLocalDate(), LocalTime.MIN);
					nonBusinessDaysSet.add(Timestamp.valueOf(nonBusinessDayMin));
				}
			}
		}

		return nonBusinessDaysSet;
	}
}
