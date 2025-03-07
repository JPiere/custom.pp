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
package custom.pp.jpiere.base.plugin.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MStorageOnHand;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 * JPIERE-0501: JPiere PP Doc
 * JPIERE-0502: JPiere PP Doc Template
 * JPIERE-0609: Workprocess & Create Material Movement From PP Fact Doc.
 *
 * @author Hideaki Hagiwara
 *
 */
public class MPPMMFactLineMA extends X_JP_PP_MM_FactLineMA {

	private static final long serialVersionUID = 482214406531227087L;

	public MPPMMFactLineMA(Properties ctx, int JP_PP_MM_FactLineMA_ID, String trxName) 
	{
		super(ctx, JP_PP_MM_FactLineMA_ID, trxName);
	}

	public MPPMMFactLineMA(Properties ctx, int JP_PP_MM_FactLineMA_ID, String trxName, String... virtualColumns)
	{
		super(ctx, JP_PP_MM_FactLineMA_ID, trxName, virtualColumns);
	}

	public MPPMMFactLineMA(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord new
	 *	@return save
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		MPPMMFactLine parentline = new MPPMMFactLine(getCtx(), getJP_PP_MM_FactLine_ID(), get_TrxName());
		if (newRecord && parentline.getParent().isProcessed()) {
			log.saveError("ParentComplete", Msg.translate(getCtx(), "M_Movement_ID"));
			return false;
		}
		//Set DateMaterialPolicy
		if(!newRecord && is_ValueChanged(COLUMNNAME_M_AttributeSetInstance_ID)){
			I_JP_PP_MM_FactLine line = getJP_PP_MM_FactLine();
			
			Timestamp dateMPolicy = null;
			if(getM_AttributeSetInstance_ID()>0)
			{
				dateMPolicy = MStorageOnHand.getDateMaterialPolicy(line.getM_Product_ID(), getM_AttributeSetInstance_ID(), get_TrxName());
			}
			
			if(dateMPolicy == null)
			{
				I_JP_PP_Fact  movement = line.getJP_PP_Fact();
				dateMPolicy = movement.getMovementDate();
			}
			
			setDateMaterialPolicy(dateMPolicy);
		}
		
		return true;
	} //beforeSave
	
	/**	Logger	*/
	private static CLogger	s_log	= CLogger.getCLogger (MPPMMFactLineMA.class);
	
	/**
	 * 	Get Material Allocations for Line
	 *	@param ctx context
	 *	@param JP_PP_MM_FactLineMA_ID line
	 *	@param trxName trx
	 *	@return allocations
	 */
	public static MPPMMFactLineMA[] get (Properties ctx, int JP_PP_MM_FactLine_ID, String trxName)
	{
		ArrayList<MPPMMFactLineMA> list = new ArrayList<MPPMMFactLineMA>();
		String sql = "SELECT * FROM JP_PP_MM_FactLineMA WHERE JP_PP_MM_FactLine_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, trxName);
			pstmt.setInt (1, JP_PP_MM_FactLine_ID);
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				list.add (new MPPMMFactLineMA (ctx, rs, trxName));
			}
		}
		catch (Exception e)
		{
			s_log.log (Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		MPPMMFactLineMA[] retValue = new MPPMMFactLineMA[list.size ()];
		list.toArray (retValue);
		return retValue;
	}	//	get
}
