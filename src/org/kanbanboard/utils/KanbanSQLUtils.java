package org.kanbanboard.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;

public class KanbanSQLUtils {
	
	public static PreparedStatement getKanbanPreparedStatement(String sqlStatement, String trxName, int parameter) {
		PreparedStatement pstmt = null;
		pstmt = DB.prepareStatement(sqlStatement, trxName);
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
			}
	
		return sqlSelect.toString();
	}

}