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

/** Generated Model for JP_PP_MM_PlanLine
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="JP_PP_MM_PlanLine")
public class X_JP_PP_MM_PlanLine extends PO implements I_JP_PP_MM_PlanLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20231221L;

    /** Standard Constructor */
    public X_JP_PP_MM_PlanLine (Properties ctx, int JP_PP_MM_PlanLine_ID, String trxName)
    {
      super (ctx, JP_PP_MM_PlanLine_ID, trxName);
      /** if (JP_PP_MM_PlanLine_ID == 0)
        {
			setJP_MovementQtyFact (Env.ZERO);
// 0
			setJP_PP_MM_PlanLine_ID (0);
			setJP_PP_Plan_ID (0);
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM JP_PP_MM_PlanLine WHERE JP_PP_Plan_ID=@JP_PP_Plan_ID@
			setM_LocatorTo_ID (0);
// @M_LocatorTo_ID@
			setM_Locator_ID (0);
			setM_Product_ID (0);
			setMovementQty (Env.ZERO);
// 1
			setProcessed (false);
        } */
    }

    /** Standard Constructor */
    public X_JP_PP_MM_PlanLine (Properties ctx, int JP_PP_MM_PlanLine_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_PP_MM_PlanLine_ID, trxName, virtualColumns);
      /** if (JP_PP_MM_PlanLine_ID == 0)
        {
			setJP_MovementQtyFact (Env.ZERO);
// 0
			setJP_PP_MM_PlanLine_ID (0);
			setJP_PP_Plan_ID (0);
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM JP_PP_MM_PlanLine WHERE JP_PP_Plan_ID=@JP_PP_Plan_ID@
			setM_LocatorTo_ID (0);
// @M_LocatorTo_ID@
			setM_Locator_ID (0);
			setM_Product_ID (0);
			setMovementQty (Env.ZERO);
// 1
			setProcessed (false);
        } */
    }

    /** Load Constructor */
    public X_JP_PP_MM_PlanLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_PP_MM_PlanLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Communication Column.
		@param JP_CommunicationColumn Communication Column
	*/
	public void setJP_CommunicationColumn (String JP_CommunicationColumn)
	{
		set_Value (COLUMNNAME_JP_CommunicationColumn, JP_CommunicationColumn);
	}

	/** Get Communication Column.
		@return Communication Column	  */
	public String getJP_CommunicationColumn()
	{
		return (String)get_Value(COLUMNNAME_JP_CommunicationColumn);
	}

	/** Set Movement Quantity(Fact).
		@param JP_MovementQtyFact Quantity of a product moved.
	*/
	public void setJP_MovementQtyFact (BigDecimal JP_MovementQtyFact)
	{
		set_ValueNoCheck (COLUMNNAME_JP_MovementQtyFact, JP_MovementQtyFact);
	}

	/** Get Movement Quantity(Fact).
		@return Quantity of a product moved.
	  */
	public BigDecimal getJP_MovementQtyFact()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_MovementQtyFact);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_JP_PP_MM_PlanLineT getJP_PP_MM_PlanLineT() throws RuntimeException
	{
		return (I_JP_PP_MM_PlanLineT)MTable.get(getCtx(), I_JP_PP_MM_PlanLineT.Table_ID)
			.getPO(getJP_PP_MM_PlanLineT_ID(), get_TrxName());
	}

	/** Set JP_PP_MM_PlanLineT.
		@param JP_PP_MM_PlanLineT_ID JP_PP_MM_PlanLineT
	*/
	public void setJP_PP_MM_PlanLineT_ID (int JP_PP_MM_PlanLineT_ID)
	{
		if (JP_PP_MM_PlanLineT_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PP_MM_PlanLineT_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PP_MM_PlanLineT_ID, Integer.valueOf(JP_PP_MM_PlanLineT_ID));
	}

	/** Get JP_PP_MM_PlanLineT.
		@return JP_PP_MM_PlanLineT	  */
	public int getJP_PP_MM_PlanLineT_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_MM_PlanLineT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_PP_MM_PlanLine.
		@param JP_PP_MM_PlanLine_ID JP_PP_MM_PlanLine
	*/
	public void setJP_PP_MM_PlanLine_ID (int JP_PP_MM_PlanLine_ID)
	{
		if (JP_PP_MM_PlanLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PP_MM_PlanLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PP_MM_PlanLine_ID, Integer.valueOf(JP_PP_MM_PlanLine_ID));
	}

	/** Get JP_PP_MM_PlanLine.
		@return JP_PP_MM_PlanLine	  */
	public int getJP_PP_MM_PlanLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_MM_PlanLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_PP_MM_PlanLine_UU.
		@param JP_PP_MM_PlanLine_UU JP_PP_MM_PlanLine_UU
	*/
	public void setJP_PP_MM_PlanLine_UU (String JP_PP_MM_PlanLine_UU)
	{
		set_Value (COLUMNNAME_JP_PP_MM_PlanLine_UU, JP_PP_MM_PlanLine_UU);
	}

	/** Get JP_PP_MM_PlanLine_UU.
		@return JP_PP_MM_PlanLine_UU	  */
	public String getJP_PP_MM_PlanLine_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_PP_MM_PlanLine_UU);
	}

	public I_JP_PP_Plan getJP_PP_Plan() throws RuntimeException
	{
		return (I_JP_PP_Plan)MTable.get(getCtx(), I_JP_PP_Plan.Table_ID)
			.getPO(getJP_PP_Plan_ID(), get_TrxName());
	}

	/** Set PP Plan.
		@param JP_PP_Plan_ID JPIERE-0501:JPBP
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
	public int getJP_PP_Plan_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_Plan_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Line No.
		@param Line Unique line for this document
	*/
	public void setLine (int Line)
	{
		set_Value (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_AttributeSetInstance getM_AttributeSetInstanceTo() throws RuntimeException
	{
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_ID)
			.getPO(getM_AttributeSetInstanceTo_ID(), get_TrxName());
	}

	/** Set Attribute Info To.
		@param M_AttributeSetInstanceTo_ID Target Product Attribute Set Instance
	*/
	public void setM_AttributeSetInstanceTo_ID (int M_AttributeSetInstanceTo_ID)
	{
		if (M_AttributeSetInstanceTo_ID < 1)
			set_Value (COLUMNNAME_M_AttributeSetInstanceTo_ID, null);
		else
			set_Value (COLUMNNAME_M_AttributeSetInstanceTo_ID, Integer.valueOf(M_AttributeSetInstanceTo_ID));
	}

	/** Get Attribute Info To.
		@return Target Product Attribute Set Instance
	  */
	public int getM_AttributeSetInstanceTo_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_AttributeSetInstanceTo_ID);
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

	public org.compiere.model.I_M_Locator getM_LocatorTo() throws RuntimeException
	{
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_ID)
			.getPO(getM_LocatorTo_ID(), get_TrxName());
	}

	/** Set Locator To.
		@param M_LocatorTo_ID Location inventory is moved to
	*/
	public void setM_LocatorTo_ID (int M_LocatorTo_ID)
	{
		if (M_LocatorTo_ID < 1)
			set_Value (COLUMNNAME_M_LocatorTo_ID, null);
		else
			set_Value (COLUMNNAME_M_LocatorTo_ID, Integer.valueOf(M_LocatorTo_ID));
	}

	/** Get Locator To.
		@return Location inventory is moved to
	  */
	public int getM_LocatorTo_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_LocatorTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException
	{
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_ID)
			.getPO(getM_Locator_ID(), get_TrxName());
	}

	/** Set Locator.
		@param M_Locator_ID Warehouse Locator
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
	public int getM_Locator_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
	{
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_ID)
			.getPO(getM_Product_ID(), get_TrxName());
	}

	/** Set Product.
		@param M_Product_ID Product, Service, Item
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
	public int getM_Product_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Movement Quantity.
		@param MovementQty Quantity of a product moved.
	*/
	public void setMovementQty (BigDecimal MovementQty)
	{
		set_Value (COLUMNNAME_MovementQty, MovementQty);
	}

	/** Get Movement Quantity.
		@return Quantity of a product moved.
	  */
	public BigDecimal getMovementQty()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MovementQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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
}