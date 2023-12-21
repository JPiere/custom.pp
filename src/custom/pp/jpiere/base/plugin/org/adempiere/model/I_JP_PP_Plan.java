/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package custom.pp.jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for JP_PP_Plan
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_JP_PP_Plan 
{

    /** TableName=JP_PP_Plan */
    public static final String Table_Name = "JP_PP_Plan";

    /** AD_Table_ID=1000269 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 1 - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(1);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_OrgTrx_ID */
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";

	/** Set Trx Organization.
	  * Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID);

	/** Get Trx Organization.
	  * Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
	  */
	public int getAD_Org_ID();

    /** Column name C_Activity_ID */
    public static final String COLUMNNAME_C_Activity_ID = "C_Activity_ID";

	/** Set Activity.
	  * Business Activity
	  */
	public void setC_Activity_ID (int C_Activity_ID);

	/** Get Activity.
	  * Business Activity
	  */
	public int getC_Activity_ID();

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException;

    /** Column name C_Campaign_ID */
    public static final String COLUMNNAME_C_Campaign_ID = "C_Campaign_ID";

	/** Set Campaign.
	  * Marketing Campaign
	  */
	public void setC_Campaign_ID (int C_Campaign_ID);

	/** Get Campaign.
	  * Marketing Campaign
	  */
	public int getC_Campaign_ID();

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException;

    /** Column name C_DocType_ID */
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";

	/** Set Document Type.
	  * Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID);

	/** Get Document Type.
	  * Document type or rules
	  */
	public int getC_DocType_ID();

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException;

    /** Column name C_ProjectPhase_ID */
    public static final String COLUMNNAME_C_ProjectPhase_ID = "C_ProjectPhase_ID";

	/** Set Project Phase.
	  * Phase of a Project
	  */
	public void setC_ProjectPhase_ID (int C_ProjectPhase_ID);

	/** Get Project Phase.
	  * Phase of a Project
	  */
	public int getC_ProjectPhase_ID();

	public org.compiere.model.I_C_ProjectPhase getC_ProjectPhase() throws RuntimeException;

    /** Column name C_ProjectTask_ID */
    public static final String COLUMNNAME_C_ProjectTask_ID = "C_ProjectTask_ID";

	/** Set Project Task.
	  * Actual Project Task in a Phase
	  */
	public void setC_ProjectTask_ID (int C_ProjectTask_ID);

	/** Get Project Task.
	  * Actual Project Task in a Phase
	  */
	public int getC_ProjectTask_ID();

	public org.compiere.model.I_C_ProjectTask getC_ProjectTask() throws RuntimeException;

    /** Column name C_Project_ID */
    public static final String COLUMNNAME_C_Project_ID = "C_Project_ID";

	/** Set Project.
	  * Financial Project
	  */
	public void setC_Project_ID (int C_Project_ID);

	/** Get Project.
	  * Financial Project
	  */
	public int getC_Project_ID();

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException;

    /** Column name C_UOM_ID */
    public static final String COLUMNNAME_C_UOM_ID = "C_UOM_ID";

	/** Set UOM.
	  * Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID);

	/** Get UOM.
	  * Unit of Measure
	  */
	public int getC_UOM_ID();

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name DateAcct */
    public static final String COLUMNNAME_DateAcct = "DateAcct";

	/** Set Account Date.
	  * Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct);

	/** Get Account Date.
	  * Accounting Date
	  */
	public Timestamp getDateAcct();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name DocAction */
    public static final String COLUMNNAME_DocAction = "DocAction";

	/** Set Document Action.
	  * The targeted status of the document
	  */
	public void setDocAction (String DocAction);

	/** Get Document Action.
	  * The targeted status of the document
	  */
	public String getDocAction();

    /** Column name DocStatus */
    public static final String COLUMNNAME_DocStatus = "DocStatus";

	/** Set Document Status.
	  * The current status of the document
	  */
	public void setDocStatus (String DocStatus);

	/** Get Document Status.
	  * The current status of the document
	  */
	public String getDocStatus();

    /** Column name DocumentNo */
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";

	/** Set Document No.
	  * Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo);

	/** Get Document No.
	  * Document sequence number of the document
	  */
	public String getDocumentNo();

    /** Column name DocumentNote */
    public static final String COLUMNNAME_DocumentNote = "DocumentNote";

	/** Set Document Note.
	  * Additional information for a Document
	  */
	public void setDocumentNote (String DocumentNote);

	/** Get Document Note.
	  * Additional information for a Document
	  */
	public String getDocumentNote();

    /** Column name Help */
    public static final String COLUMNNAME_Help = "Help";

	/** Set Comment/Help.
	  * Comment or Hint
	  */
	public void setHelp (String Help);

	/** Get Comment/Help.
	  * Comment or Hint
	  */
	public String getHelp();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name IsApproved */
    public static final String COLUMNNAME_IsApproved = "IsApproved";

	/** Set Approved.
	  * Indicates if this document requires approval
	  */
	public void setIsApproved (boolean IsApproved);

	/** Get Approved.
	  * Indicates if this document requires approval
	  */
	public boolean isApproved();

    /** Column name IsCompleteAutoJP */
    public static final String COLUMNNAME_IsCompleteAutoJP = "IsCompleteAutoJP";

	/** Set Auto Complete	  */
	public void setIsCompleteAutoJP (boolean IsCompleteAutoJP);

	/** Get Auto Complete	  */
	public boolean isCompleteAutoJP();

    /** Column name IsCreated */
    public static final String COLUMNNAME_IsCreated = "IsCreated";

	/** Set Records created	  */
	public void setIsCreated (String IsCreated);

	/** Get Records created	  */
	public String getIsCreated();

    /** Column name IsRecordRouteJP */
    public static final String COLUMNNAME_IsRecordRouteJP = "IsRecordRouteJP";

	/** Set Record the Route	  */
	public void setIsRecordRouteJP (boolean IsRecordRouteJP);

	/** Get Record the Route	  */
	public boolean isRecordRouteJP();

    /** Column name IsSplitWhenDifferenceJP */
    public static final String COLUMNNAME_IsSplitWhenDifferenceJP = "IsSplitWhenDifferenceJP";

	/** Set Split when Difference.
	  * Split document when there is a difference
	  */
	public void setIsSplitWhenDifferenceJP (boolean IsSplitWhenDifferenceJP);

	/** Get Split when Difference.
	  * Split document when there is a difference
	  */
	public boolean isSplitWhenDifferenceJP();

    /** Column name IsSummary */
    public static final String COLUMNNAME_IsSummary = "IsSummary";

	/** Set Summary Level.
	  * This is a summary entity
	  */
	public void setIsSummary (boolean IsSummary);

	/** Get Summary Level.
	  * This is a summary entity
	  */
	public boolean isSummary();

    /** Column name JP_MovementDateDst */
    public static final String COLUMNNAME_JP_MovementDateDst = "JP_MovementDateDst";

	/** Set Movement Date(Destination)	  */
	public void setJP_MovementDateDst (Timestamp JP_MovementDateDst);

	/** Get Movement Date(Destination)	  */
	public Timestamp getJP_MovementDateDst();

    /** Column name JP_MovementDateNext */
    public static final String COLUMNNAME_JP_MovementDateNext = "JP_MovementDateNext";

	/** Set Movement Date(Next)	  */
	public void setJP_MovementDateNext (Timestamp JP_MovementDateNext);

	/** Get Movement Date(Next)	  */
	public Timestamp getJP_MovementDateNext();

    /** Column name JP_Name */
    public static final String COLUMNNAME_JP_Name = "JP_Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setJP_Name (String JP_Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getJP_Name();

    /** Column name JP_PP_Doc_ID */
    public static final String COLUMNNAME_JP_PP_Doc_ID = "JP_PP_Doc_ID";

	/** Set PP Doc.
	  * JPIERE-0501:JPBP
	  */
	public void setJP_PP_Doc_ID (int JP_PP_Doc_ID);

	/** Get PP Doc.
	  * JPIERE-0501:JPBP
	  */
	public int getJP_PP_Doc_ID();

	public I_JP_PP_Doc getJP_PP_Doc() throws RuntimeException;

    /** Column name JP_PP_End */
    public static final String COLUMNNAME_JP_PP_End = "JP_PP_End";

	/** Set End date and time	  */
	public void setJP_PP_End (Timestamp JP_PP_End);

	/** Get End date and time	  */
	public Timestamp getJP_PP_End();

    /** Column name JP_PP_PlanT_ID */
    public static final String COLUMNNAME_JP_PP_PlanT_ID = "JP_PP_PlanT_ID";

	/** Set PP Plan Template.
	  * JPIERE-0501:JPBP
	  */
	public void setJP_PP_PlanT_ID (int JP_PP_PlanT_ID);

	/** Get PP Plan Template.
	  * JPIERE-0501:JPBP
	  */
	public int getJP_PP_PlanT_ID();

	public I_JP_PP_PlanT getJP_PP_PlanT() throws RuntimeException;

    /** Column name JP_PP_Plan_ID */
    public static final String COLUMNNAME_JP_PP_Plan_ID = "JP_PP_Plan_ID";

	/** Set PP Plan.
	  * JPIERE-0501:JPBP
	  */
	public void setJP_PP_Plan_ID (int JP_PP_Plan_ID);

	/** Get PP Plan.
	  * JPIERE-0501:JPBP
	  */
	public int getJP_PP_Plan_ID();

    /** Column name JP_PP_Plan_UU */
    public static final String COLUMNNAME_JP_PP_Plan_UU = "JP_PP_Plan_UU";

	/** Set PP Plan(UU)	  */
	public void setJP_PP_Plan_UU (String JP_PP_Plan_UU);

	/** Get PP Plan(UU)	  */
	public String getJP_PP_Plan_UU();

    /** Column name JP_PP_ScheduledEnd */
    public static final String COLUMNNAME_JP_PP_ScheduledEnd = "JP_PP_ScheduledEnd";

	/** Set Scheduled to End	  */
	public void setJP_PP_ScheduledEnd (Timestamp JP_PP_ScheduledEnd);

	/** Get Scheduled to End	  */
	public Timestamp getJP_PP_ScheduledEnd();

    /** Column name JP_PP_ScheduledStart */
    public static final String COLUMNNAME_JP_PP_ScheduledStart = "JP_PP_ScheduledStart";

	/** Set Scheduled to Start	  */
	public void setJP_PP_ScheduledStart (Timestamp JP_PP_ScheduledStart);

	/** Get Scheduled to Start	  */
	public Timestamp getJP_PP_ScheduledStart();

    /** Column name JP_PP_Start */
    public static final String COLUMNNAME_JP_PP_Start = "JP_PP_Start";

	/** Set Start date and time	  */
	public void setJP_PP_Start (Timestamp JP_PP_Start);

	/** Get Start date and time	  */
	public Timestamp getJP_PP_Start();

    /** Column name JP_PP_Status */
    public static final String COLUMNNAME_JP_PP_Status = "JP_PP_Status";

	/** Set Production Status	  */
	public void setJP_PP_Status (String JP_PP_Status);

	/** Get Production Status	  */
	public String getJP_PP_Status();

    /** Column name JP_PP_WorkProcessType */
    public static final String COLUMNNAME_JP_PP_WorkProcessType = "JP_PP_WorkProcessType";

	/** Set Work Process Type.
	  * JPIERE-0609:JPBP
	  */
	public void setJP_PP_WorkProcessType (String JP_PP_WorkProcessType);

	/** Get Work Process Type.
	  * JPIERE-0609:JPBP
	  */
	public String getJP_PP_WorkProcessType();

    /** Column name JP_PP_WorkProcess_ID */
    public static final String COLUMNNAME_JP_PP_WorkProcess_ID = "JP_PP_WorkProcess_ID";

	/** Set Work Process.
	  * JPIERE-0609:JPBP
	  */
	public void setJP_PP_WorkProcess_ID (int JP_PP_WorkProcess_ID);

	/** Get Work Process.
	  * JPIERE-0609:JPBP
	  */
	public int getJP_PP_WorkProcess_ID();

	public I_JP_PP_WorkProcess getJP_PP_WorkProcess() throws RuntimeException;

    /** Column name JP_PP_Workload_Fact */
    public static final String COLUMNNAME_JP_PP_Workload_Fact = "JP_PP_Workload_Fact";

	/** Set Workload(Fact)	  */
	public void setJP_PP_Workload_Fact (BigDecimal JP_PP_Workload_Fact);

	/** Get Workload(Fact)	  */
	public BigDecimal getJP_PP_Workload_Fact();

    /** Column name JP_PP_Workload_Plan */
    public static final String COLUMNNAME_JP_PP_Workload_Plan = "JP_PP_Workload_Plan";

	/** Set Workload(Plan)	  */
	public void setJP_PP_Workload_Plan (BigDecimal JP_PP_Workload_Plan);

	/** Get Workload(Plan)	  */
	public BigDecimal getJP_PP_Workload_Plan();

    /** Column name JP_PP_Workload_UOM_ID */
    public static final String COLUMNNAME_JP_PP_Workload_UOM_ID = "JP_PP_Workload_UOM_ID";

	/** Set Workload UOM	  */
	public void setJP_PP_Workload_UOM_ID (int JP_PP_Workload_UOM_ID);

	/** Get Workload UOM	  */
	public int getJP_PP_Workload_UOM_ID();

	public org.compiere.model.I_C_UOM getJP_PP_Workload_UOM() throws RuntimeException;

    /** Column name JP_PhysicalWarehouseDst_ID */
    public static final String COLUMNNAME_JP_PhysicalWarehouseDst_ID = "JP_PhysicalWarehouseDst_ID";

	/** Set Physical Warehouse(Destination)	  */
	public void setJP_PhysicalWarehouseDst_ID (int JP_PhysicalWarehouseDst_ID);

	/** Get Physical Warehouse(Destination)	  */
	public int getJP_PhysicalWarehouseDst_ID();

	public I_JP_PhysicalWarehouse getJP_PhysicalWarehouseDst() throws RuntimeException;

    /** Column name JP_PhysicalWarehouseFrom_ID */
    public static final String COLUMNNAME_JP_PhysicalWarehouseFrom_ID = "JP_PhysicalWarehouseFrom_ID";

	/** Set Physical Warehouse(From)	  */
	public void setJP_PhysicalWarehouseFrom_ID (int JP_PhysicalWarehouseFrom_ID);

	/** Get Physical Warehouse(From)	  */
	public int getJP_PhysicalWarehouseFrom_ID();

	public I_JP_PhysicalWarehouse getJP_PhysicalWarehouseFrom() throws RuntimeException;

    /** Column name JP_PhysicalWarehouseNext_ID */
    public static final String COLUMNNAME_JP_PhysicalWarehouseNext_ID = "JP_PhysicalWarehouseNext_ID";

	/** Set Physical Warehouse(Next)	  */
	public void setJP_PhysicalWarehouseNext_ID (int JP_PhysicalWarehouseNext_ID);

	/** Get Physical Warehouse(Next)	  */
	public int getJP_PhysicalWarehouseNext_ID();

	public I_JP_PhysicalWarehouse getJP_PhysicalWarehouseNext() throws RuntimeException;

    /** Column name JP_PhysicalWarehouseTo_ID */
    public static final String COLUMNNAME_JP_PhysicalWarehouseTo_ID = "JP_PhysicalWarehouseTo_ID";

	/** Set Physical Warehouse(To)	  */
	public void setJP_PhysicalWarehouseTo_ID (int JP_PhysicalWarehouseTo_ID);

	/** Get Physical Warehouse(To)	  */
	public int getJP_PhysicalWarehouseTo_ID();

	public I_JP_PhysicalWarehouse getJP_PhysicalWarehouseTo() throws RuntimeException;

    /** Column name JP_Processing1 */
    public static final String COLUMNNAME_JP_Processing1 = "JP_Processing1";

	/** Set Process Now	  */
	public void setJP_Processing1 (String JP_Processing1);

	/** Get Process Now	  */
	public String getJP_Processing1();

    /** Column name JP_Processing2 */
    public static final String COLUMNNAME_JP_Processing2 = "JP_Processing2";

	/** Set Process Now	  */
	public void setJP_Processing2 (String JP_Processing2);

	/** Get Process Now	  */
	public String getJP_Processing2();

    /** Column name JP_Processing3 */
    public static final String COLUMNNAME_JP_Processing3 = "JP_Processing3";

	/** Set Process Now	  */
	public void setJP_Processing3 (String JP_Processing3);

	/** Get Process Now	  */
	public String getJP_Processing3();

    /** Column name JP_Processing4 */
    public static final String COLUMNNAME_JP_Processing4 = "JP_Processing4";

	/** Set Process Now	  */
	public void setJP_Processing4 (String JP_Processing4);

	/** Get Process Now	  */
	public String getJP_Processing4();

    /** Column name JP_Processing5 */
    public static final String COLUMNNAME_JP_Processing5 = "JP_Processing5";

	/** Set Process Now	  */
	public void setJP_Processing5 (String JP_Processing5);

	/** Get Process Now	  */
	public String getJP_Processing5();

    /** Column name JP_Processing6 */
    public static final String COLUMNNAME_JP_Processing6 = "JP_Processing6";

	/** Set Process Now	  */
	public void setJP_Processing6 (String JP_Processing6);

	/** Get Process Now	  */
	public String getJP_Processing6();

    /** Column name JP_ProductionQtyFact */
    public static final String COLUMNNAME_JP_ProductionQtyFact = "JP_ProductionQtyFact";

	/** Set Production Qty(Fact)	  */
	public void setJP_ProductionQtyFact (BigDecimal JP_ProductionQtyFact);

	/** Get Production Qty(Fact)	  */
	public BigDecimal getJP_ProductionQtyFact();

    /** Column name JP_Remarks */
    public static final String COLUMNNAME_JP_Remarks = "JP_Remarks";

	/** Set Remarks.
	  * JPIERE-0490:JPBP
	  */
	public void setJP_Remarks (String JP_Remarks);

	/** Get Remarks.
	  * JPIERE-0490:JPBP
	  */
	public String getJP_Remarks();

    /** Column name JP_Subject */
    public static final String COLUMNNAME_JP_Subject = "JP_Subject";

	/** Set Subject.
	  * JPIERE-0490:JPBP
	  */
	public void setJP_Subject (String JP_Subject);

	/** Get Subject.
	  * JPIERE-0490:JPBP
	  */
	public String getJP_Subject();

    /** Column name JP_WarehouseDst_ID */
    public static final String COLUMNNAME_JP_WarehouseDst_ID = "JP_WarehouseDst_ID";

	/** Set Org Warehouse(Destination)	  */
	public void setJP_WarehouseDst_ID (int JP_WarehouseDst_ID);

	/** Get Org Warehouse(Destination)	  */
	public int getJP_WarehouseDst_ID();

	public org.compiere.model.I_M_Warehouse getJP_WarehouseDst() throws RuntimeException;

    /** Column name JP_WarehouseFrom_ID */
    public static final String COLUMNNAME_JP_WarehouseFrom_ID = "JP_WarehouseFrom_ID";

	/** Set Org Warehouse(From).
	  * Storage Warehouse and Service Point
	  */
	public void setJP_WarehouseFrom_ID (int JP_WarehouseFrom_ID);

	/** Get Org Warehouse(From).
	  * Storage Warehouse and Service Point
	  */
	public int getJP_WarehouseFrom_ID();

	public org.compiere.model.I_M_Warehouse getJP_WarehouseFrom() throws RuntimeException;

    /** Column name JP_WarehouseNext_ID */
    public static final String COLUMNNAME_JP_WarehouseNext_ID = "JP_WarehouseNext_ID";

	/** Set Org Warehouse(Next)	  */
	public void setJP_WarehouseNext_ID (int JP_WarehouseNext_ID);

	/** Get Org Warehouse(Next)	  */
	public int getJP_WarehouseNext_ID();

	public org.compiere.model.I_M_Warehouse getJP_WarehouseNext() throws RuntimeException;

    /** Column name JP_WarehouseTo_ID */
    public static final String COLUMNNAME_JP_WarehouseTo_ID = "JP_WarehouseTo_ID";

	/** Set Org Warehouse(To).
	  * Storage Warehouse and Service Point
	  */
	public void setJP_WarehouseTo_ID (int JP_WarehouseTo_ID);

	/** Get Org Warehouse(To).
	  * Storage Warehouse and Service Point
	  */
	public int getJP_WarehouseTo_ID();

	public org.compiere.model.I_M_Warehouse getJP_WarehouseTo() throws RuntimeException;

    /** Column name M_Locator_ID */
    public static final String COLUMNNAME_M_Locator_ID = "M_Locator_ID";

	/** Set Locator.
	  * Warehouse Locator
	  */
	public void setM_Locator_ID (int M_Locator_ID);

	/** Get Locator.
	  * Warehouse Locator
	  */
	public int getM_Locator_ID();

	public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException;

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException;

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name Posted */
    public static final String COLUMNNAME_Posted = "Posted";

	/** Set Posted.
	  * Posting status
	  */
	public void setPosted (boolean Posted);

	/** Get Posted.
	  * Posting status
	  */
	public boolean isPosted();

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name ProcessedOn */
    public static final String COLUMNNAME_ProcessedOn = "ProcessedOn";

	/** Set Processed On.
	  * The date+time (expressed in decimal format) when the document has been processed
	  */
	public void setProcessedOn (BigDecimal ProcessedOn);

	/** Get Processed On.
	  * The date+time (expressed in decimal format) when the document has been processed
	  */
	public BigDecimal getProcessedOn();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

    /** Column name ProductionQty */
    public static final String COLUMNNAME_ProductionQty = "ProductionQty";

	/** Set Production Quantity.
	  * Quantity of products to produce
	  */
	public void setProductionQty (BigDecimal ProductionQty);

	/** Get Production Quantity.
	  * Quantity of products to produce
	  */
	public BigDecimal getProductionQty();

    /** Column name SalesRep_ID */
    public static final String COLUMNNAME_SalesRep_ID = "SalesRep_ID";

	/** Set Sales Rep.
	  * Sales Representative or Company Agent
	  */
	public void setSalesRep_ID (int SalesRep_ID);

	/** Get Sales Rep.
	  * Sales Representative or Company Agent
	  */
	public int getSalesRep_ID();

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException;

    /** Column name SeqNo */
    public static final String COLUMNNAME_SeqNo = "SeqNo";

	/** Set Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public void setSeqNo (int SeqNo);

	/** Get Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public int getSeqNo();

    /** Column name UPC */
    public static final String COLUMNNAME_UPC = "UPC";

	/** Set UPC/EAN.
	  * Bar Code (Universal Product Code or its superset European Article Number)
	  */
	public void setUPC (String UPC);

	/** Get UPC/EAN.
	  * Bar Code (Universal Product Code or its superset European Article Number)
	  */
	public String getUPC();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name User1_ID */
    public static final String COLUMNNAME_User1_ID = "User1_ID";

	/** Set User Element List 1.
	  * User defined list element #1
	  */
	public void setUser1_ID (int User1_ID);

	/** Get User Element List 1.
	  * User defined list element #1
	  */
	public int getUser1_ID();

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException;

    /** Column name User2_ID */
    public static final String COLUMNNAME_User2_ID = "User2_ID";

	/** Set User Element List 2.
	  * User defined list element #2
	  */
	public void setUser2_ID (int User2_ID);

	/** Get User Element List 2.
	  * User defined list element #2
	  */
	public int getUser2_ID();

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException;

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();
}
