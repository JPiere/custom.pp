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
package custom.pp.jpiere.base.plugin.factory;

import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.adempiere.base.IDocFactory;
import org.compiere.acct.Doc;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MTable;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPDoc;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPFact;
import custom.pp.jpiere.base.plugin.org.adempiere.model.MPPPlan;


/**
 *  JPiere Doc Factory
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class CustomPPDocFactory implements IDocFactory {

	private final static CLogger s_log = CLogger.getCLogger(CustomPPDocFactory.class);

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, int Record_ID,
			String trxName) {

		Doc doc = null;
		if(	AD_Table_ID==MPPDoc.Table_ID//1000268
			|| AD_Table_ID==MPPPlan.Table_ID//1000269
			|| AD_Table_ID==MPPFact.Table_ID//1000271
			)
		{

			String tableName = MTable.get(Env.getCtx(), AD_Table_ID).get_TableName();
			StringBuffer sql = new StringBuffer("SELECT * FROM ")
				.append(tableName)
				.append(" WHERE ").append(tableName).append("_ID=? AND Processed='Y'");
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql.toString(), trxName);
				pstmt.setInt (1, Record_ID);
				rs = pstmt.executeQuery ();
				if (rs.next ())
				{
					doc = getDocument(as, AD_Table_ID, rs, trxName);
				}
				else
					s_log.severe("Not Found: " + tableName + "_ID=" + Record_ID);
			}
			catch (Exception e)
			{
				s_log.log (Level.SEVERE, sql.toString(), e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}


		}

		return doc;
	}

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, ResultSet rs, String trxName) {
		Doc doc = null;

		String className = null;

		if(AD_Table_ID == MPPDoc.Table_ID){
			className = "custom.pp.jpiere.base.plugin.org.compiere.acct.Doc_JPPPDoc";
		}else if(AD_Table_ID == MPPPlan.Table_ID){
			className = "custom.pp.jpiere.base.plugin.org.compiere.acct.Doc_JPPPPlan";
		}else if(AD_Table_ID == MPPFact.Table_ID){
			className = "custom.pp.jpiere.base.plugin.org.compiere.acct.Doc_JPPPFact";
		}else {
			return null;
		}


		try
		{
			Class<?> cClass = Class.forName(className);
			Constructor<?> cnstr = cClass.getConstructor(new Class[] {MAcctSchema.class, ResultSet.class, String.class});
			doc = (Doc) cnstr.newInstance(as, rs, trxName);
		}
		catch (Exception e)
		{
			doc = null;
		}

		return doc;
	}

}
