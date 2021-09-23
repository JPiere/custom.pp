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
import java.util.logging.Level;

import org.compiere.model.MLocator;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MTree;
import org.compiere.model.MTree_Node;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPDocT;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlanLineT;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlanLineTQT;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlanT;


/**
 * JPIERE-0502: JPiere PP Doc Template
 *
 * @author Hideaki Hagiwara
 *
 */
public class PPCreateTemplateByCopy extends SvrProcess {

	private int p_JP_PP_DocT_From_ID = 0;
	private int p_JP_PP_DocT_To_ID = 0;

	private BigDecimal p_CoefficientQty = Env.ZERO;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null && para[i].getParameter_To() == null)
				;
			else if (name.equals("JP_PP_DocT_ID"))
				p_JP_PP_DocT_From_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if ("QtyEntered".equals(name))
				p_CoefficientQty = para[i].getParameterAsBigDecimal();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		p_JP_PP_DocT_To_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{
		if(p_JP_PP_DocT_From_ID == p_JP_PP_DocT_To_ID)
		{
			//Same PP Template
			throw new Exception(Msg.getMsg(getCtx(), "JP_PP_SameTemplate"));
		}

		if(p_CoefficientQty==null)
			p_CoefficientQty = Env.ZERO;

		MPPDocT m_PPDocT_From = new MPPDocT(getCtx(),p_JP_PP_DocT_From_ID, get_TrxName());
		MPPDocT m_PPDocT_To = new MPPDocT(getCtx(),p_JP_PP_DocT_To_ID, get_TrxName());

		MPPPlanT[] ppPlanT_Froms = m_PPDocT_From.getPPPlanTs();
		MPPPlanLineT[] ppPlanLineT_Froms = null;
		MPPPlanT ppPlanT_To = null;
		MPPPlanLineT ppPlanLineT_To = null;

		int M_Locator_ID = 0;
		for(MPPPlanT ppPlanT_From : ppPlanT_Froms)
		{
			ppPlanT_To = m_PPDocT_To.getPPPlanT(ppPlanT_From.getSeqNo(), ppPlanT_From.getM_Product_ID(), ppPlanT_From.getValue());
			if(ppPlanT_To != null)
			{
				//The SeqNo, Product, Search Key has already been registered
				throw new Exception(Msg.getMsg(getCtx(),"JP_Unique_Constraint_PPPlan"));
			}

			ppPlanT_To = new MPPPlanT(getCtx(), 0, get_TrxName());
			PO.copyValues(m_PPDocT_From, m_PPDocT_To);

			//Copy mandatory column to make sure
			ppPlanT_To.setJP_PP_DocT_ID(m_PPDocT_To.getJP_PP_DocT_ID());
			ppPlanT_To.setAD_Org_ID(m_PPDocT_To.getAD_Org_ID());
			ppPlanT_To.setSeqNo(ppPlanT_From.getSeqNo());
			ppPlanT_To.setIsActive(true);
			ppPlanT_To.setIsSummary(ppPlanT_From.isSummary());
			ppPlanT_To.setM_Product_ID(ppPlanT_From.getM_Product_ID());
			if(m_PPDocT_To.getAD_Org_ID() == ppPlanT_From.getAD_Org_ID())
			{
				ppPlanT_To.setM_Locator_ID(ppPlanT_From.getM_Locator_ID());
			}else {

				MOrgInfo oInfo = MOrgInfo.get(m_PPDocT_To.getAD_Org_ID());
				if(oInfo.getM_Warehouse_ID() > 0)
				{
					MWarehouse wh = MWarehouse.get(oInfo.getM_Warehouse_ID());
					MLocator[] locs = wh.getLocators(false);
					boolean isOK = false;

					for(MLocator loc : locs)
					{
						if(loc.isDefault())
						{
							M_Locator_ID =loc.getM_Locator_ID();
							ppPlanT_To.setM_Locator_ID(M_Locator_ID);
							isOK = true;
							break;
						}
					}

					if(!isOK)
					{
						//set First locator
						for(MLocator loc : locs)
						{
							M_Locator_ID =loc.getM_Locator_ID();
							ppPlanT_To.setM_Locator_ID(M_Locator_ID);
							isOK = true;
							break;
						}
					}

					if(!isOK)
					{
						//Different Organization of PP Doc Template, So could not find locator.
						throw new Exception(Msg.getMsg(getCtx(), "JP_PP_NotFoundLocatorCopyTemplate"));
					}
				}else {

					throw new Exception(Msg.getMsg(getCtx(), "JP_PP_NotFoundLocatorCopyTemplate"));
				}
			}// set Locator
			ppPlanT_To.setC_DocType_ID(ppPlanT_From.getC_DocType_ID());
			ppPlanT_To.setC_DocTypeTarget_ID(ppPlanT_From.getC_DocTypeTarget_ID());
			ppPlanT_To.setValue(ppPlanT_From.getValue());
			ppPlanT_To.setJP_Name(ppPlanT_From.getJP_Name());
			ppPlanT_To.setName(ppPlanT_From.getJP_Name());
			ppPlanT_To.setIsSplitWhenDifferenceJP(ppPlanT_From.isSplitWhenDifferenceJP());
			ppPlanT_To.setIsCompleteAutoJP(ppPlanT_From.isCompleteAutoJP());
			ppPlanT_To.setIsCreatePPFactJP(ppPlanT_From.isCreatePPFactJP());
			ppPlanT_To.setProductionQty(p_CoefficientQty.multiply(ppPlanT_From.getProductionQty()));
			ppPlanT_To.setJP_ProductionDays(ppPlanT_From.getJP_ProductionDays());
			ppPlanT_To.setJP_DayOffset(ppPlanT_From.getJP_DayOffset());
			ppPlanT_To.setJP_PP_Workload_Plan(ppPlanT_From.getJP_PP_Workload_Plan());
			ppPlanT_To.setJP_PP_Workload_UOM_ID(ppPlanT_From.getJP_PP_Workload_UOM_ID());
			ppPlanT_To.setJP_Processing1("N");
			ppPlanT_To.setJP_Processing2("N");
			ppPlanT_To.setJP_Processing3("N");
			ppPlanT_To.setJP_Processing4("N");
			ppPlanT_To.setJP_Processing5("N");
			ppPlanT_To.setJP_Processing6("N");

			ppPlanLineT_Froms = ppPlanT_From.getPPPlanLineTs();
			if(ppPlanLineT_Froms.length > 0)
				ppPlanT_To.setIsCreated("Y");
			else
				ppPlanT_To.setIsCreated("N");
			ppPlanT_To.saveEx(get_TrxName());

			for(MPPPlanLineT ppPlanLineT_From : ppPlanLineT_Froms)
			{
				ppPlanLineT_To = new MPPPlanLineT(getCtx(), 0, get_TrxName());
				PO.copyValues(ppPlanLineT_From, ppPlanLineT_To);

				//Copy mandatory column to make sure
				ppPlanLineT_To.setJP_PP_PlanT_ID(ppPlanT_To.getJP_PP_PlanT_ID()) ;
				ppPlanLineT_To.setAD_Org_ID(m_PPDocT_To.getAD_Org_ID());
				ppPlanLineT_To.setLine(ppPlanLineT_From.getLine());
				ppPlanLineT_To.setM_Product_ID(ppPlanLineT_From.getM_Product_ID());
				ppPlanLineT_To.setC_UOM_ID(ppPlanLineT_From.getC_UOM_ID());
				ppPlanLineT_To.setIsEndProduct(ppPlanLineT_From.isEndProduct());
				ppPlanLineT_To.setIsCreated("Y");
				ppPlanLineT_To.setPlannedQty(p_CoefficientQty.multiply(ppPlanLineT_From.getPlannedQty()));
				if(ppPlanLineT_To.isEndProduct())
					ppPlanLineT_To.setQtyUsed(null);
				else
					ppPlanLineT_To.setQtyUsed(p_CoefficientQty.multiply(ppPlanLineT_From.getQtyUsed()));
				ppPlanLineT_To.setMovementQty(p_CoefficientQty.multiply(ppPlanLineT_From.getMovementQty()));
				if(m_PPDocT_To.getAD_Org_ID() == ppPlanT_From.getAD_Org_ID())
				{
					ppPlanLineT_To.setM_Locator_ID(ppPlanLineT_From.getM_Locator_ID());
				}else {
					ppPlanLineT_To.setM_Locator_ID(M_Locator_ID);
				}

				ppPlanLineT_To.setJP_Processing1("N");
				ppPlanLineT_To.setJP_Processing2("N");
				ppPlanLineT_To.setJP_Processing3("N");
				ppPlanLineT_To.saveEx(get_TrxName());

				//QT
				if(ppPlanLineT_To.isEndProduct())
				{
					MPPPlanLineTQT[] qts_From = ppPlanLineT_From.getPPPlanLineTQTs(true, null);
					MPPPlanLineTQT qt_To = null;
					for(MPPPlanLineTQT qt_From :qts_From)
					{
						qt_To = new MPPPlanLineTQT(getCtx(), 0 , get_TrxName());
						PO.copyValues(qt_From, qt_To);
						qt_To.setJP_PP_PlanLineT_ID(ppPlanLineT_To.getJP_PP_PlanLineT_ID());
						qt_To.setAD_Org_ID(ppPlanLineT_To.getAD_Org_ID());
						qt_To.setSeqNo(qt_From.getSeqNo());
						qt_To.setM_QualityTest_ID(qt_From.getM_QualityTest_ID());
						qt_To.setExpectedResult(qt_From.getExpectedResult());
						qt_To.setIsActive(true);
						qt_To.saveEx(get_TrxName());
					}
				}
			}

		}

		//Update Tree
		int p_AD_TreeFrom_ID = MTree.getDefaultAD_Tree_ID(getAD_Client_ID(), "JP_PP_PlanT_ID");
		int p_AD_TreeTo_ID = MTree.getDefaultAD_Tree_ID(getAD_Client_ID(), "JP_PP_PlanT_ID");
		MTree treeFrom =  new MTree(getCtx(), p_AD_TreeFrom_ID, get_TrxName());
		MTree treeTo =  new MTree(getCtx(), p_AD_TreeTo_ID, get_TrxName());
		MTree_Node nodeTo = null;
		MTree_Node nodeFrom = null;

		MPPPlanT[] ppPlanT_Tos = m_PPDocT_To.getPPPlanTs(true, null);
		for(int i = 0;  i < ppPlanT_Tos.length ; i++)
		{
			nodeTo = MTree_Node.get(treeTo, ppPlanT_Tos[i].getJP_PP_PlanT_ID());

			for(int j = 0; j < ppPlanT_Froms.length ; j++)
			{

				if(ppPlanT_Tos[i].getSeqNo() == ppPlanT_Froms[j].getSeqNo()
						&& ppPlanT_Tos[i].getM_Product_ID() == ppPlanT_Froms[j].getM_Product_ID()
						&& ppPlanT_Tos[i].getValue().equals(ppPlanT_Froms[j].getValue())
						)
				{
					//Get NodeFrom
					nodeFrom = MTree_Node.get(treeFrom, ppPlanT_Froms[j].getJP_PP_PlanT_ID());

					if(nodeFrom.getParent_ID() == 0)
					{
						nodeTo.setParent_ID(0);
						nodeTo.setSeqNo(nodeFrom.getSeqNo());
						nodeTo.save(get_TrxName());
						break;

					}else{

						//Get Parent of NodeFrom
						boolean isOK = false;
						for(int k = 0; j < ppPlanT_Froms.length ; k++)
						{
							if(nodeFrom.getParent_ID() == ppPlanT_Froms[k].getJP_PP_PlanT_ID())
							{

								//Get Parent of NodeTo
								isOK = true;
								for(int m = 0;  m < ppPlanT_Tos.length ; m++)
								{
									if(ppPlanT_Tos[m].getSeqNo() == ppPlanT_Froms[k].getSeqNo()
											&& ppPlanT_Tos[m].getM_Product_ID() == ppPlanT_Froms[k].getM_Product_ID()
											&& ppPlanT_Tos[m].getValue().equals(ppPlanT_Froms[k].getValue())
											)
									{
										nodeTo.setParent_ID(ppPlanT_Tos[m].getJP_PP_PlanT_ID());
										nodeTo.setSeqNo(nodeFrom.getSeqNo());
										nodeTo.save(get_TrxName());
										break;
									}

								}//for m

								if(isOK)
									break;
							}
						}//for k
					}//if
				}//if
			}//for j
		}//for i


		return null;
	}

}
