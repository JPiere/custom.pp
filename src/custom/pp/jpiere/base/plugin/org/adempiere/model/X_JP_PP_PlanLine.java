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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for JP_PP_PlanLine
 *  @author iDempiere (generated) 
 *  @version Release 8.2 - $Id$ */
public class X_JP_PP_PlanLine extends PO implements I_JP_PP_PlanLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20210923L;

    /** Standard Constructor */
    public X_JP_PP_PlanLine (Properties ctx, int JP_PP_PlanLine_ID, String trxName)
    {
      super (ctx, JP_PP_PlanLine_ID, trxName);
      /** if (JP_PP_PlanLine_ID == 0)
        {
			setIsCreated (null);
// N
			setIsEndProduct (false);
// N
			setJP_MovementQtyFact (Env.ZERO);
// 0
			setJP_PP_PlanLine_ID (0);
			setJP_PP_Plan_ID (0);
			setJP_Processing1 (null);
// N
			setJP_Processing2 (null);
// N
			setJP_Processing3 (null);
// N
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM JP_PP_PlanLine WHERE JP_PP_Plan_ID=@JP_PP_Plan_ID@
			setM_Locator_ID (0);
// @M_Locator_ID@
			setM_Product_ID (0);
			setMovementQty (Env.ZERO);
			setProcessed (false);
// N
        } */
    }

    /** Load Constructor */
    public X_JP_PP_PlanLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_PP_PlanLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException
    {
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_Name)
			.getPO(getC_UOM_ID(), get_TrxName());	}

	/** Set UOM.
		@param C_UOM_ID 
		Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_UOM_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	  */
	public int getC_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Document Note.
		@param DocumentNote 
		Additional information for a Document
	  */
	public void setDocumentNote (String DocumentNote)
	{
		set_Value (COLUMNNAME_DocumentNote, DocumentNote);
	}

	/** Get Document Note.
		@return Additional information for a Document
	  */
	public String getDocumentNote () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNote);
	}

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** IsCreated AD_Reference_ID=319 */
	public static final int ISCREATED_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISCREATED_Yes = "Y";
	/** No = N */
	public static final String ISCREATED_No = "N";
	/** Set Records created.
		@param IsCreated Records created	  */
	public void setIsCreated (String IsCreated)
	{

		set_Value (COLUMNNAME_IsCreated, IsCreated);
	}

	/** Get Records created.
		@return Records created	  */
	public String getIsCreated () 
	{
		return (String)get_Value(COLUMNNAME_IsCreated);
	}

	/** Set End Product.
		@param IsEndProduct 
		End Product of production
	  */
	public void setIsEndProduct (boolean IsEndProduct)
	{
		set_Value (COLUMNNAME_IsEndProduct, Boolean.valueOf(IsEndProduct));
	}

	/** Get End Product.
		@return End Product of production
	  */
	public boolean isEndProduct () 
	{
		Object oo = get_Value(COLUMNNAME_IsEndProduct);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Movement Quantity(Fact).
		@param JP_MovementQtyFact 
		Quantity of a product moved.
	  */
	public void setJP_MovementQtyFact (BigDecimal JP_MovementQtyFact)
	{
		set_ValueNoCheck (COLUMNNAME_JP_MovementQtyFact, JP_MovementQtyFact);
	}

	/** Get Movement Quantity(Fact).
		@return Quantity of a product moved.
	  */
	public BigDecimal getJP_MovementQtyFact () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_MovementQtyFact);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_JP_PP_PlanLineT getJP_PP_PlanLineT() throws RuntimeException
    {
		return (I_JP_PP_PlanLineT)MTable.get(getCtx(), I_JP_PP_PlanLineT.Table_Name)
			.getPO(getJP_PP_PlanLineT_ID(), get_TrxName());	}

	/** Set PP Plan Line Template.
		@param JP_PP_PlanLineT_ID 
		JPIERE-0501:JPBP
	  */
	public void setJP_PP_PlanLineT_ID (int JP_PP_PlanLineT_ID)
	{
		if (JP_PP_PlanLineT_ID < 1) 
			set_Value (COLUMNNAME_JP_PP_PlanLineT_ID, null);
		else 
			set_Value (COLUMNNAME_JP_PP_PlanLineT_ID, Integer.valueOf(JP_PP_PlanLineT_ID));
	}

	/** Get PP Plan Line Template.
		@return JPIERE-0501:JPBP
	  */
	public int getJP_PP_PlanLineT_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_PlanLineT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set PP Plan Line.
		@param JP_PP_PlanLine_ID PP Plan Line	  */
	public void setJP_PP_PlanLine_ID (int JP_PP_PlanLine_ID)
	{
		if (JP_PP_PlanLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_PP_PlanLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_PP_PlanLine_ID, Integer.valueOf(JP_PP_PlanLine_ID));
	}

	/** Get PP Plan Line.
		@return PP Plan Line	  */
	public int getJP_PP_PlanLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_PlanLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set PP Plan Line(UU).
		@param JP_PP_PlanLine_UU PP Plan Line(UU)	  */
	public void setJP_PP_PlanLine_UU (String JP_PP_PlanLine_UU)
	{
		set_Value (COLUMNNAME_JP_PP_PlanLine_UU, JP_PP_PlanLine_UU);
	}

	/** Get PP Plan Line(UU).
		@return PP Plan Line(UU)	  */
	public String getJP_PP_PlanLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_PP_PlanLine_UU);
	}

	public I_JP_PP_Plan getJP_PP_Plan() throws RuntimeException
    {
		return (I_JP_PP_Plan)MTable.get(getCtx(), I_JP_PP_Plan.Table_Name)
			.getPO(getJP_PP_Plan_ID(), get_TrxName());	}

	/** Set PP Plan.
		@param JP_PP_Plan_ID 
		JPIERE-0501:JPBP
	  */
	public void setJP_PP_Plan_ID (int JP_PP_Plan_ID)
	{
		if (JP_PP_Plan_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_PP_Plan_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_PP_Plan_ID, Integer.valueOf(JP_PP_Plan_ID));
	}

	/** Get PP Plan.
		@return JPIERE-0501:JPBP
	  */
	public int getJP_PP_Plan_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_Plan_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Process Now.
		@param JP_Processing1 Process Now	  */
	public void setJP_Processing1 (String JP_Processing1)
	{
		set_Value (COLUMNNAME_JP_Processing1, JP_Processing1);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing1 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing1);
	}

	/** Set Process Now.
		@param JP_Processing2 Process Now	  */
	public void setJP_Processing2 (String JP_Processing2)
	{
		set_Value (COLUMNNAME_JP_Processing2, JP_Processing2);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing2 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing2);
	}

	/** Set Process Now.
		@param JP_Processing3 Process Now	  */
	public void setJP_Processing3 (String JP_Processing3)
	{
		set_Value (COLUMNNAME_JP_Processing3, JP_Processing3);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing3 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing3);
	}

	/** Set Quantity Used(Fact).
		@param JP_QtyUsedFact Quantity Used(Fact)	  */
	public void setJP_QtyUsedFact (BigDecimal JP_QtyUsedFact)
	{
		set_Value (COLUMNNAME_JP_QtyUsedFact, JP_QtyUsedFact);
	}

	/** Get Quantity Used(Fact).
		@return Quantity Used(Fact)	  */
	public BigDecimal getJP_QtyUsedFact () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_QtyUsedFact);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Line No.
		@param Line 
		Unique line for this document
	  */
	public void setLine (int Line)
	{
		set_ValueNoCheck (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException
    {
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_Name)
			.getPO(getM_Locator_ID(), get_TrxName());	}

	/** Set Locator.
		@param M_Locator_ID 
		Warehouse Locator
	  */
	public void setM_Locator_ID (int M_Locator_ID)
	{
		if (M_Locator_ID < 1) 
			set_Value (COLUMNNAME_M_Locator_ID, null);
		else 
			set_Value (COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
	}

	/** Get Locator.
		@return Warehouse Locator
	  */
	public int getM_Locator_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Movement Quantity.
		@param MovementQty 
		Quantity of a product moved.
	  */
	public void setMovementQty (BigDecimal MovementQty)
	{
		set_Value (COLUMNNAME_MovementQty, MovementQty);
	}

	/** Get Movement Quantity.
		@return Quantity of a product moved.
	  */
	public BigDecimal getMovementQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MovementQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Planned Quantity.
		@param PlannedQty 
		Planned quantity for this project
	  */
	public void setPlannedQty (BigDecimal PlannedQty)
	{
		set_Value (COLUMNNAME_PlannedQty, PlannedQty);
	}

	/** Get Planned Quantity.
		@return Planned quantity for this project
	  */
	public BigDecimal getPlannedQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PlannedQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
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

	/** Set Available Quantity.
		@param QtyAvailable 
		Available Quantity (On Hand - Reserved)
	  */
	public void setQtyAvailable (BigDecimal QtyAvailable)
	{
		throw new IllegalArgumentException ("QtyAvailable is virtual column");	}

	/** Get Available Quantity.
		@return Available Quantity (On Hand - Reserved)
	  */
	public BigDecimal getQtyAvailable () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyAvailable);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity Used.
		@param QtyUsed Quantity Used	  */
	public void setQtyUsed (BigDecimal QtyUsed)
	{
		set_Value (COLUMNNAME_QtyUsed, QtyUsed);
	}

	/** Get Quantity Used.
		@return Quantity Used	  */
	public BigDecimal getQtyUsed () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyUsed);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}