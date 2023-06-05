package org.kanbanboard.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ChoiceFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Util;

public class KanbanSQLUtils {
	
	/**	Logger							*/
	protected static transient CLogger	log = CLogger.getCLogger (KanbanSQLUtils.class);
	
	public static PreparedStatement getKanbanPreparedStatement(String sqlStatement, String trxName, Integer parameter) {
		PreparedStatement pstmt = null;
		pstmt = DB.prepareStatement(sqlStatement, trxName);
		if(parameter != null)
			setParameters(pstmt, parameter);

		return pstmt;
	}

	private static void setParameters(PreparedStatement pstmt, int parameter) {
		try {
			pstmt.setInt(1, parameter);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AdempiereException(e);
		}
	}

	public static String getColumnSQLStatement(MColumn column) {
		return getColumnSQLStatement(column, null, null);
	}

	public static String getColumnSQLStatement(MColumn column, String whereClause, String orderByClause) {
		return getColumnSQLStatement(column, whereClause, orderByClause, null, null);
	}

	public static String getColumnSQLStatement(MColumn column, String whereClause, String orderByClause, Object paraFrom, Object paraTo) {
		StringBuilder sqlSelect = new StringBuilder();

		//Reference List
		if (column.getAD_Reference_ID() == DisplayType.List) {
			if (column.getAD_Reference_Value_ID() != 0) {
				sqlSelect.append("SELECT DISTINCT Name, Value FROM AD_Ref_List ")
				.append("WHERE AD_Reference_ID = ? AND IsActive = 'Y'");
			}
		} else if (column.getAD_Reference_ID() == DisplayType.Table ||
				column.getAD_Reference_ID() == DisplayType.Search ||
				column.getAD_Reference_ID() == DisplayType.TableDir) {

			MTable table =  MTable.get(Env.getCtx(), column.getReferenceTableName());
			String keyColumns[] = table.getKeyColumns();
			String identifiers[] = table.getIdentifierColumns();

			sqlSelect.append("SELECT DISTINCT ").append(identifiers[0]).append(", ").append(keyColumns[0])
			.append(" FROM ").append(table.getTableName())
			.append(" WHERE ")
			.append(" AD_Client_ID IN (0, ?) AND")
			.append(" IsActive = 'Y'");
		} else if ((column.getAD_Reference_ID() == DisplayType.Date || 
				column.getAD_Reference_ID() == DisplayType.DateTime)
				&&
				(paraFrom == null || paraFrom instanceof Timestamp) && (paraTo == null || paraTo instanceof Timestamp)) {
			
			Calendar cal = Calendar.getInstance(); // locale-specific
			cal.setTime(new Timestamp(System.currentTimeMillis()));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Timestamp now = new Timestamp(cal.getTimeInMillis());
			
			if(paraFrom == null) {
				if(paraTo == null || ((Timestamp)paraTo).compareTo(now) >= 0)
					paraFrom = now;
				else
					paraFrom = paraTo;
			}
			if(paraTo == null) {
				if(((Timestamp)paraFrom).compareTo(now) <= 0)
					paraTo = now;
				else
					paraTo = paraFrom;
			}
			
			sqlSelect.append(
				"SELECT to_char(series,'DD-MM-YYYY') as name, trunc(series,'dd')::timestamp as " + column.getColumnName()
				+ " FROM generate_series('"+paraFrom+"'::timestamp, '"+paraTo+"'::timestamp, '1 day') series  "
			);
		}

		if (!Util.isEmpty(whereClause))
			sqlSelect.append(" AND ").append(whereClause);

		if (!Util.isEmpty(orderByClause))
			sqlSelect.append(" ORDER BY ").append(orderByClause);

		return sqlSelect.toString();
	}
	
	public static String getSummary(String summarySQL, String msgValue) {
		if (summarySQL != null) {
			parseContextVariables(summarySQL);
			if (summarySQL.length() == 0) {
				return null;
			}
			
			MessageFormat mf = getMessageFormat(msgValue);
			if (mf == null)
				return null;

			Format[] fmts = mf.getFormatsByArgumentIndex();
			Object[] arguments = new Object[fmts.length];
			boolean filled = false;

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(summarySQL, null);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					for (int idx = 0; idx < fmts.length; idx++) {
						Format fmt = fmts[idx];
						Object obj;
						if (fmt instanceof DecimalFormat || fmt instanceof ChoiceFormat) {
							obj = rs.getDouble(idx+1);
						} else if (fmt instanceof SimpleDateFormat) {
							obj = rs.getTimestamp(idx+1);
						} else {
							obj = rs.getString(idx+1);
						}
						arguments[idx] = obj;
					}
					filled = true;
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, summarySQL, e);
			} finally{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
			if (filled)
				return mf.format(arguments);
		}
		return null;
	} //getSummary
	
	private static MessageFormat getMessageFormat(String msgValue) {
		MessageFormat mf = null;
		try {
			mf = new MessageFormat(msgValue, Env.getLanguage(Env.getCtx()).getLocale());
		} catch (Exception e) {
			log.log(Level.SEVERE, msgValue, e);
		}
		return mf;
	}
	
	private static void parseContextVariables(String sql) {
		if (sql.indexOf("@") >= 0) {
			sql = Env.parseContext(Env.getCtx(), 0, sql, false, false);
		}
	}
	
	public static String replaceTokenWithValue(String originalString, String tokenName, String tokenValue) {
		int j = originalString.indexOf(tokenName);
		if (j > -1) {
			return originalString.replaceAll(tokenName, tokenValue);
		}
		return originalString;
	}

}