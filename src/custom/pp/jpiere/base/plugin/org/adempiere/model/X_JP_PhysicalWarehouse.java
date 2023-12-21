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

/** Generated Model for JP_PhysicalWarehouse
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="JP_PhysicalWarehouse")
public class X_JP_PhysicalWarehouse extends PO implements I_JP_PhysicalWarehouse, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20231221L;

    /** Standard Constructor */
    public X_JP_PhysicalWarehouse (Properties ctx, int JP_PhysicalWarehouse_ID, String trxName)
    {
      super (ctx, JP_PhysicalWarehouse_ID, trxName);
      /** if (JP_PhysicalWarehouse_ID == 0)
        {
			setC_Location_ID (0);
			setJP_PhysicalWarehouse_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_PhysicalWarehouse (Properties ctx, int JP_PhysicalWarehouse_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_PhysicalWarehouse_ID, trxName, virtualColumns);
      /** if (JP_PhysicalWarehouse_ID == 0)
        {
			setC_Location_ID (0);
			setJP_PhysicalWarehouse_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_PhysicalWarehouse (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
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
      StringBuilder sb = new StringBuilder ("X_JP_PhysicalWarehouse[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_Location getC_Location() throws RuntimeException
	{
		return (org.compiere.model.I_C_Location)MTable.get(getCtx(), org.compiere.model.I_C_Location.Table_ID)
			.getPO(getC_Location_ID(), get_TrxName());
	}

	/** Set Address.
		@param C_Location_ID Location or Address
	*/
	public void setC_Location_ID (int C_Location_ID)
	{
		if (C_Location_ID < 1)
			set_Value (COLUMNNAME_C_Location_ID, null);
		else
			set_Value (COLUMNNAME_C_Location_ID, Integer.valueOf(C_Location_ID));
	}

	/** Get Address.
		@return Location or Address
	  */
	public int getC_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Physical Warehouse.
		@param JP_PhysicalWarehouse_ID Physical Warehouse
	*/
	public void setJP_PhysicalWarehouse_ID (int JP_PhysicalWarehouse_ID)
	{
		if (JP_PhysicalWarehouse_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PhysicalWarehouse_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PhysicalWarehouse_ID, Integer.valueOf(JP_PhysicalWarehouse_ID));
	}

	/** Get Physical Warehouse.
		@return Physical Warehouse	  */
	public int getJP_PhysicalWarehouse_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PhysicalWarehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Physical Warehouse.
		@param JP_PhysicalWarehouse_UU Physical Warehouse
	*/
	public void setJP_PhysicalWarehouse_UU (String JP_PhysicalWarehouse_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_PhysicalWarehouse_UU, JP_PhysicalWarehouse_UU);
	}

	/** Get Physical Warehouse.
		@return Physical Warehouse	  */
	public String getJP_PhysicalWarehouse_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_PhysicalWarehouse_UU);
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getName());
    }

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}