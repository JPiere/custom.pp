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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for JP_PP_MM_FactLineMA
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="JP_PP_MM_FactLineMA")
public class X_JP_PP_MM_FactLineMA extends PO implements I_JP_PP_MM_FactLineMA, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20231221L;

    /** Standard Constructor */
    public X_JP_PP_MM_FactLineMA (Properties ctx, int JP_PP_MM_FactLineMA_ID, String trxName)
    {
      super (ctx, JP_PP_MM_FactLineMA_ID, trxName);
      /** if (JP_PP_MM_FactLineMA_ID == 0)
        {
			setIsAutoGenerated (false);
// N
			setJP_PP_MM_FactLine_ID (0);
			setM_AttributeSetInstance_ID (0);
			setMovementQty (Env.ZERO);
// 1
        } */
    }

    /** Standard Constructor */
    public X_JP_PP_MM_FactLineMA (Properties ctx, int JP_PP_MM_FactLineMA_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_PP_MM_FactLineMA_ID, trxName, virtualColumns);
      /** if (JP_PP_MM_FactLineMA_ID == 0)
        {
			setIsAutoGenerated (false);
// N
			setJP_PP_MM_FactLine_ID (0);
			setM_AttributeSetInstance_ID (0);
			setMovementQty (Env.ZERO);
// 1
        } */
    }

    /** Load Constructor */
    public X_JP_PP_MM_FactLineMA (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_PP_MM_FactLineMA[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Date  Material Policy.
		@param DateMaterialPolicy Time used for LIFO and FIFO Material Policy
	*/
	public void setDateMaterialPolicy (Timestamp DateMaterialPolicy)
	{
		set_ValueNoCheck (COLUMNNAME_DateMaterialPolicy, DateMaterialPolicy);
	}

	/** Get Date  Material Policy.
		@return Time used for LIFO and FIFO Material Policy
	  */
	public Timestamp getDateMaterialPolicy()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateMaterialPolicy);
	}

	/** Set Auto Generated.
		@param IsAutoGenerated Auto Generated
	*/
	public void setIsAutoGenerated (boolean IsAutoGenerated)
	{
		set_ValueNoCheck (COLUMNNAME_IsAutoGenerated, Boolean.valueOf(IsAutoGenerated));
	}

	/** Get Auto Generated.
		@return Auto Generated	  */
	public boolean isAutoGenerated()
	{
		Object oo = get_Value(COLUMNNAME_IsAutoGenerated);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set JP_PP_MM_FactLineMA_UU.
		@param JP_PP_MM_FactLineMA_UU JP_PP_MM_FactLineMA_UU
	*/
	public void setJP_PP_MM_FactLineMA_UU (String JP_PP_MM_FactLineMA_UU)
	{
		set_Value (COLUMNNAME_JP_PP_MM_FactLineMA_UU, JP_PP_MM_FactLineMA_UU);
	}

	/** Get JP_PP_MM_FactLineMA_UU.
		@return JP_PP_MM_FactLineMA_UU	  */
	public String getJP_PP_MM_FactLineMA_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_PP_MM_FactLineMA_UU);
	}

	public I_JP_PP_MM_FactLine getJP_PP_MM_FactLine() throws RuntimeException
	{
		return (I_JP_PP_MM_FactLine)MTable.get(getCtx(), I_JP_PP_MM_FactLine.Table_ID)
			.getPO(getJP_PP_MM_FactLine_ID(), get_TrxName());
	}

	/** Set JP_PP_MM_FactLine.
		@param JP_PP_MM_FactLine_ID JP_PP_MM_FactLine
	*/
	public void setJP_PP_MM_FactLine_ID (int JP_PP_MM_FactLine_ID)
	{
		if (JP_PP_MM_FactLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PP_MM_FactLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PP_MM_FactLine_ID, Integer.valueOf(JP_PP_MM_FactLine_ID));
	}

	/** Get JP_PP_MM_FactLine.
		@return JP_PP_MM_FactLine	  */
	public int getJP_PP_MM_FactLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_MM_FactLine_ID);
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
			set_ValueNoCheck (COLUMNNAME_M_AttributeSetInstance_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
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
}