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

/** Generated Interface for JP_PP_PlanT
 *  @author iDempiere (generated) 
 *  @version Release 8.2
 */
@SuppressWarnings("all")
public interface I_JP_PP_PlanT 
{

    /** TableName=JP_PP_PlanT */
    public static final String Table_Name = "JP_PP_PlanT";

    /** AD_Table_ID=1000266 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 1 - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(1);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
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
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
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

    /** Column name C_DocTypeTarget_ID */
    public static final String COLUMNNAME_C_DocTypeTarget_ID = "C_DocTypeTarget_ID";

	/** Set Target Doc Type.
	  * Target document type for conversing documents
	  */
	public void setC_DocTypeTarget_ID (int C_DocTypeTarget_ID);

	/** Get Target Doc Type.
	  * Target document type for conversing documents
	  */
	public int getC_DocTypeTarget_ID();

	public org.compiere.model.I_C_DocType getC_DocTypeTarget() throws RuntimeException;

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

    /** Column name IsCompleteAutoJP */
    public static final String COLUMNNAME_IsCompleteAutoJP = "IsCompleteAutoJP";

	/** Set Auto Complete	  */
	public void setIsCompleteAutoJP (boolean IsCompleteAutoJP);

	/** Get Auto Complete	  */
	public boolean isCompleteAutoJP();

    /** Column name IsCreatePPFactJP */
    public static final String COLUMNNAME_IsCreatePPFactJP = "IsCreatePPFactJP";

	/** Set Create PP Fact	  */
	public void setIsCreatePPFactJP (boolean IsCreatePPFactJP);

	/** Get Create PP Fact	  */
	public boolean isCreatePPFactJP();

    /** Column name IsCreated */
    public static final String COLUMNNAME_IsCreated = "IsCreated";

	/** Set Records created	  */
	public void setIsCreated (String IsCreated);

	/** Get Records created	  */
	public String getIsCreated();

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

    /** Column name JP_DayOffset */
    public static final String COLUMNNAME_JP_DayOffset = "JP_DayOffset";

	/** Set Day Offset	  */
	public void setJP_DayOffset (int JP_DayOffset);

	/** Get Day Offset	  */
	public int getJP_DayOffset();

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

    /** Column name JP_PP_DocT_ID */
    public static final String COLUMNNAME_JP_PP_DocT_ID = "JP_PP_DocT_ID";

	/** Set PP Doc Template.
	  * JPIERE-0501:JPBP
	  */
	public void setJP_PP_DocT_ID (int JP_PP_DocT_ID);

	/** Get PP Doc Template.
	  * JPIERE-0501:JPBP
	  */
	public int getJP_PP_DocT_ID();

	public I_JP_PP_DocT getJP_PP_DocT() throws RuntimeException;

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

    /** Column name JP_PP_PlanT_UU */
    public static final String COLUMNNAME_JP_PP_PlanT_UU = "JP_PP_PlanT_UU";

	/** Set PP Plan Template(UU)	  */
	public void setJP_PP_PlanT_UU (String JP_PP_PlanT_UU);

	/** Get PP Plan Template(UU)	  */
	public String getJP_PP_PlanT_UU();

    /** Column name JP_PP_ScheduledEndTime */
    public static final String COLUMNNAME_JP_PP_ScheduledEndTime = "JP_PP_ScheduledEndTime";

	/** Set Scheduled End Time	  */
	public void setJP_PP_ScheduledEndTime (Timestamp JP_PP_ScheduledEndTime);

	/** Get Scheduled End Time	  */
	public Timestamp getJP_PP_ScheduledEndTime();

    /** Column name JP_PP_ScheduledStartTime */
    public static final String COLUMNNAME_JP_PP_ScheduledStartTime = "JP_PP_ScheduledStartTime";

	/** Set Scheduled Start Time	  */
	public void setJP_PP_ScheduledStartTime (Timestamp JP_PP_ScheduledStartTime);

	/** Get Scheduled Start Time	  */
	public Timestamp getJP_PP_ScheduledStartTime();

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

    /** Column name JP_ProductionDays */
    public static final String COLUMNNAME_JP_ProductionDays = "JP_ProductionDays";

	/** Set Production Days 	  */
	public void setJP_ProductionDays (int JP_ProductionDays);

	/** Get Production Days 	  */
	public int getJP_ProductionDays();

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
