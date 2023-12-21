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
/** Generated Model - DO NOT CHANGE */
package custom.pp.jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Model for JP_PP_FactLineQT
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="JP_PP_FactLineQT")
public class X_JP_PP_FactLineQT extends PO implements I_JP_PP_FactLineQT, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20231221L;

    /** Standard Constructor */
    public X_JP_PP_FactLineQT (Properties ctx, int JP_PP_FactLineQT_ID, String trxName)
    {
      super (ctx, JP_PP_FactLineQT_ID, trxName);
      /** if (JP_PP_FactLineQT_ID == 0)
        {
			setExpectedResult (null);
			setIsQCPass (false);
// N
			setJP_PP_FactLineQT_ID (0);
			setJP_PP_FactLine_ID (0);
			setM_QualityTest_ID (0);
			setProcessed (false);
// N
			setSeqNo (0);
// @SQL=SELECT COALESCE(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_PP_FactLineQT WHERE JP_PP_FactLine_ID=@JP_PP_FactLine_ID@
        } */
    }

    /** Standard Constructor */
    public X_JP_PP_FactLineQT (Properties ctx, int JP_PP_FactLineQT_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_PP_FactLineQT_ID, trxName, virtualColumns);
      /** if (JP_PP_FactLineQT_ID == 0)
        {
			setExpectedResult (null);
			setIsQCPass (false);
// N
			setJP_PP_FactLineQT_ID (0);
			setJP_PP_FactLine_ID (0);
			setM_QualityTest_ID (0);
			setProcessed (false);
// N
			setSeqNo (0);
// @SQL=SELECT COALESCE(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_PP_FactLineQT WHERE JP_PP_FactLine_ID=@JP_PP_FactLine_ID@
        } */
    }

    /** Load Constructor */
    public X_JP_PP_FactLineQT (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_JP_PP_FactLineQT[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Expected Result.
		@param ExpectedResult Expected Result
	*/
	public void setExpectedResult (String ExpectedResult)
	{
		set_Value (COLUMNNAME_ExpectedResult, ExpectedResult);
	}

	/** Get Expected Result.
		@return Expected Result	  */
	public String getExpectedResult()
	{
		return (String)get_Value(COLUMNNAME_ExpectedResult);
	}

	/** Set QC Pass.
		@param IsQCPass QC Pass
	*/
	public void setIsQCPass (boolean IsQCPass)
	{
		set_Value (COLUMNNAME_IsQCPass, Boolean.valueOf(IsQCPass));
	}

	/** Get QC Pass.
		@return QC Pass	  */
	public boolean isQCPass()
	{
		Object oo = get_Value(COLUMNNAME_IsQCPass);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Quality Test Result.
		@param JP_PP_FactLineQT_ID Quality Test Result
	*/
	public void setJP_PP_FactLineQT_ID (int JP_PP_FactLineQT_ID)
	{
		if (JP_PP_FactLineQT_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PP_FactLineQT_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PP_FactLineQT_ID, Integer.valueOf(JP_PP_FactLineQT_ID));
	}

	/** Get Quality Test Result.
		@return Quality Test Result	  */
	public int getJP_PP_FactLineQT_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_FactLineQT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Quality Test Result(UU).
		@param JP_PP_FactLineQT_UU Quality Test Result(UU)
	*/
	public void setJP_PP_FactLineQT_UU (String JP_PP_FactLineQT_UU)
	{
		set_Value (COLUMNNAME_JP_PP_FactLineQT_UU, JP_PP_FactLineQT_UU);
	}

	/** Get Quality Test Result(UU).
		@return Quality Test Result(UU)	  */
	public String getJP_PP_FactLineQT_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_PP_FactLineQT_UU);
	}

	public I_JP_PP_FactLine getJP_PP_FactLine() throws RuntimeException
	{
		return (I_JP_PP_FactLine)MTable.get(getCtx(), I_JP_PP_FactLine.Table_ID)
			.getPO(getJP_PP_FactLine_ID(), get_TrxName());
	}

	/** Set PP Fact Line.
		@param JP_PP_FactLine_ID PP Fact Line
	*/
	public void setJP_PP_FactLine_ID (int JP_PP_FactLine_ID)
	{
		if (JP_PP_FactLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PP_FactLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PP_FactLine_ID, Integer.valueOf(JP_PP_FactLine_ID));
	}

	/** Get PP Fact Line.
		@return PP Fact Line	  */
	public int getJP_PP_FactLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_FactLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_PP_PlanLineQT getJP_PP_PlanLineQT() throws RuntimeException
	{
		return (I_JP_PP_PlanLineQT)MTable.get(getCtx(), I_JP_PP_PlanLineQT.Table_ID)
			.getPO(getJP_PP_PlanLineQT_ID(), get_TrxName());
	}

	/** Set Quality Test Item.
		@param JP_PP_PlanLineQT_ID Quality Test Item
	*/
	public void setJP_PP_PlanLineQT_ID (int JP_PP_PlanLineQT_ID)
	{
		if (JP_PP_PlanLineQT_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PP_PlanLineQT_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PP_PlanLineQT_ID, Integer.valueOf(JP_PP_PlanLineQT_ID));
	}

	/** Get Quality Test Item.
		@return Quality Test Item	  */
	public int getJP_PP_PlanLineQT_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_PlanLineQT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException
	{
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_ID)
			.getPO(getM_AttributeSetInstance_ID(), get_TrxName());
	}

	/** Set Attribute Info.
		@param M_AttributeSetInstance_ID Product Attribute Set Instance
	*/
	public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
	{
		if (M_AttributeSetInstance_ID < 0)
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, null);
		else
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
	}

	/** Get Attribute Info.
		@return Product Attribute Set Instance
	  */
	public int getM_AttributeSetInstance_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_AttributeSetInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_QualityTest getM_QualityTest() throws RuntimeException
	{
		return (org.compiere.model.I_M_QualityTest)MTable.get(getCtx(), org.compiere.model.I_M_QualityTest.Table_ID)
			.getPO(getM_QualityTest_ID(), get_TrxName());
	}

	/** Set Quality Test.
		@param M_QualityTest_ID Quality Test
	*/
	public void setM_QualityTest_ID (int M_QualityTest_ID)
	{
		if (M_QualityTest_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_QualityTest_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_QualityTest_ID, Integer.valueOf(M_QualityTest_ID));
	}

	/** Get Quality Test.
		@return Quality Test	  */
	public int getM_QualityTest_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_QualityTest_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getM_QualityTest_ID()));
    }

	/** Set Processed.
		@param Processed The document has been processed
	*/
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed()
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Result.
		@param Result Result of the action taken
	*/
	public void setResult (String Result)
	{
		set_Value (COLUMNNAME_Result, Result);
	}

	/** Get Result.
		@return Result of the action taken
	  */
	public String getResult()
	{
		return (String)get_Value(COLUMNNAME_Result);
	}

	/** Set Sequence.
		@param SeqNo Method of ordering records; lowest number comes first
	*/
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	public int getSeqNo()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}