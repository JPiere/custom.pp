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

/** Generated Model for JP_PP_WorkProcess
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="JP_PP_WorkProcess")
public class X_JP_PP_WorkProcess extends PO implements I_JP_PP_WorkProcess, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20231221L;

    /** Standard Constructor */
    public X_JP_PP_WorkProcess (Properties ctx, int JP_PP_WorkProcess_ID, String trxName)
    {
      super (ctx, JP_PP_WorkProcess_ID, trxName);
      /** if (JP_PP_WorkProcess_ID == 0)
        {
			setJP_PP_WorkProcessType (null);
			setJP_PP_WorkProcess_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_PP_WorkProcess (Properties ctx, int JP_PP_WorkProcess_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_PP_WorkProcess_ID, trxName, virtualColumns);
      /** if (JP_PP_WorkProcess_ID == 0)
        {
			setJP_PP_WorkProcessType (null);
			setJP_PP_WorkProcess_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_JP_PP_WorkProcess (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_PP_WorkProcess[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
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

	/** Material Movement = MMM */
	public static final String JP_PP_WORKPROCESSTYPE_MaterialMovement = "MMM";
	/** Material Production = MMP */
	public static final String JP_PP_WORKPROCESSTYPE_MaterialProduction = "MMP";
	/** Not create document = NON */
	public static final String JP_PP_WORKPROCESSTYPE_NotCreateDocument = "NON";
	/** Set Work Process Type.
		@param JP_PP_WorkProcessType JPIERE-0609:JPBP
	*/
	public void setJP_PP_WorkProcessType (String JP_PP_WorkProcessType)
	{

		set_ValueNoCheck (COLUMNNAME_JP_PP_WorkProcessType, JP_PP_WorkProcessType);
	}

	/** Get Work Process Type.
		@return JPIERE-0609:JPBP
	  */
	public String getJP_PP_WorkProcessType()
	{
		return (String)get_Value(COLUMNNAME_JP_PP_WorkProcessType);
	}

	/** Set Work Process.
		@param JP_PP_WorkProcess_ID JPIERE-0609:JPBP
	*/
	public void setJP_PP_WorkProcess_ID (int JP_PP_WorkProcess_ID)
	{
		if (JP_PP_WorkProcess_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PP_WorkProcess_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PP_WorkProcess_ID, Integer.valueOf(JP_PP_WorkProcess_ID));
	}

	/** Get Work Process.
		@return JPIERE-0609:JPBP
	  */
	public int getJP_PP_WorkProcess_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_WorkProcess_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_PP_WorkProcess_UU.
		@param JP_PP_WorkProcess_UU JP_PP_WorkProcess_UU
	*/
	public void setJP_PP_WorkProcess_UU (String JP_PP_WorkProcess_UU)
	{
		set_Value (COLUMNNAME_JP_PP_WorkProcess_UU, JP_PP_WorkProcess_UU);
	}

	/** Get JP_PP_WorkProcess_UU.
		@return JP_PP_WorkProcess_UU	  */
	public String getJP_PP_WorkProcess_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_PP_WorkProcess_UU);
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