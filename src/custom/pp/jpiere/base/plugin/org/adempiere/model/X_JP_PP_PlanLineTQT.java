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

/** Generated Model for JP_PP_PlanLineTQT
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="JP_PP_PlanLineTQT")
public class X_JP_PP_PlanLineTQT extends PO implements I_JP_PP_PlanLineTQT, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20231221L;

    /** Standard Constructor */
    public X_JP_PP_PlanLineTQT (Properties ctx, int JP_PP_PlanLineTQT_ID, String trxName)
    {
      super (ctx, JP_PP_PlanLineTQT_ID, trxName);
      /** if (JP_PP_PlanLineTQT_ID == 0)
        {
			setExpectedResult (null);
			setJP_PP_PlanLineTQT_ID (0);
			setJP_PP_PlanLineT_ID (0);
			setM_QualityTest_ID (0);
			setSeqNo (0);
// @SQL=SELECT COALESCE(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_PP_PlanLineTQT WHERE JP_PP_PlanLineT_ID=@JP_PP_PlanLineT_ID@
        } */
    }

    /** Standard Constructor */
    public X_JP_PP_PlanLineTQT (Properties ctx, int JP_PP_PlanLineTQT_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_PP_PlanLineTQT_ID, trxName, virtualColumns);
      /** if (JP_PP_PlanLineTQT_ID == 0)
        {
			setExpectedResult (null);
			setJP_PP_PlanLineTQT_ID (0);
			setJP_PP_PlanLineT_ID (0);
			setM_QualityTest_ID (0);
			setSeqNo (0);
// @SQL=SELECT COALESCE(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_PP_PlanLineTQT WHERE JP_PP_PlanLineT_ID=@JP_PP_PlanLineT_ID@
        } */
    }

    /** Load Constructor */
    public X_JP_PP_PlanLineTQT (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_PP_PlanLineTQT[")
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

	/** Set PP Quality Test Template.
		@param JP_PP_PlanLineTQT_ID PP Quality Test Template
	*/
	public void setJP_PP_PlanLineTQT_ID (int JP_PP_PlanLineTQT_ID)
	{
		if (JP_PP_PlanLineTQT_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PP_PlanLineTQT_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PP_PlanLineTQT_ID, Integer.valueOf(JP_PP_PlanLineTQT_ID));
	}

	/** Get PP Quality Test Template.
		@return PP Quality Test Template	  */
	public int getJP_PP_PlanLineTQT_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_PlanLineTQT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set PP Quality Test Template (UU).
		@param JP_PP_PlanLineTQT_UU PP Quality Test Template (UU)
	*/
	public void setJP_PP_PlanLineTQT_UU (String JP_PP_PlanLineTQT_UU)
	{
		set_Value (COLUMNNAME_JP_PP_PlanLineTQT_UU, JP_PP_PlanLineTQT_UU);
	}

	/** Get PP Quality Test Template (UU).
		@return PP Quality Test Template (UU)	  */
	public String getJP_PP_PlanLineTQT_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_PP_PlanLineTQT_UU);
	}

	public I_JP_PP_PlanLineT getJP_PP_PlanLineT() throws RuntimeException
	{
		return (I_JP_PP_PlanLineT)MTable.get(getCtx(), I_JP_PP_PlanLineT.Table_ID)
			.getPO(getJP_PP_PlanLineT_ID(), get_TrxName());
	}

	/** Set PP Plan Line Template.
		@param JP_PP_PlanLineT_ID JPIERE-0501:JPBP
	*/
	public void setJP_PP_PlanLineT_ID (int JP_PP_PlanLineT_ID)
	{
		if (JP_PP_PlanLineT_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PP_PlanLineT_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PP_PlanLineT_ID, Integer.valueOf(JP_PP_PlanLineT_ID));
	}

	/** Get PP Plan Line Template.
		@return JPIERE-0501:JPBP
	  */
	public int getJP_PP_PlanLineT_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_PlanLineT_ID);
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