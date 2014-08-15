package org.idempiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.kanbanboard.model.MKanbanBoard;
import org.kanbanboard.model.MKanbanStatus;

public class CreateStatusProcess extends SvrProcess{

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String doIt() throws Exception {

		int m_kanbanBoard_id = getRecord_ID();
		boolean isRefList = true;

		if (m_kanbanBoard_id==0)
			throw new IllegalArgumentException("KanbanBoard_ID == 0");

		MKanbanBoard kanbanBoard = new MKanbanBoard(getCtx(),m_kanbanBoard_id,get_TrxName());

		int columnId=0;
		String sqlSelect = null;

		//int p_source_AD_Table_ID = kanbanBoard.getAD_Table_ID();

		if (kanbanBoard.getKDB_ColumnList_ID()!=0){
			columnId = kanbanBoard.getKDB_ColumnList_ID();
			MColumn column = new MColumn(getCtx(), columnId, get_TrxName());

			//Reference List
			if(column.getAD_Reference_ID()==DisplayType.List){
				if(column.getAD_Reference_Value_ID()!=0){
					// Reference Key is not a table but a RefList
					//if(column.getAD_Reference_Value_ID()==131){

					sqlSelect = "SELECT DISTINCT name,value from AD_ref_list WHERE " +
							"ad_reference_id = "+ column.getAD_Reference_Value_ID()+ 
							//" WHERE AD_Client_ID = ? AND IsActive = 'Y'";
							" AND IsActive = 'Y'";
					//}
				}

			}
			isRefList= true;
		}
		else if (kanbanBoard.getKDB_ColumnTable_ID()!=0){
			columnId = kanbanBoard.getKDB_ColumnTable_ID();
			MColumn column = new MColumn(getCtx(), columnId, get_TrxName());
			//Table, Table direct or Search Reference
			if(column.getAD_Reference_ID()==DisplayType.Table
					||column.getAD_Reference_ID()==DisplayType.Search
					||column.getAD_Reference_ID()==DisplayType.TableDir){					

				MTable table =  MTable.get(getCtx(),column.getReferenceTableName());
				String llaves[] = table.getKeyColumns();
				String iden[]=table.getIdentifierColumns();

				sqlSelect = "SELECT DISTINCT "+ iden[0]+", "+ llaves[0]+" FROM "+ table.getTableName()+" WHERE " +
						//" WHERE AD_Client_ID = ? AND IsActive = 'Y'";
						"IsActive = 'Y'";
				System.out.println();
			}
			/*System.out.println("No referencia");
			String columnName = column.getColumnName();
			String tableName = MTable.getTableName(getCtx(), p_source_AD_Table_ID);
			MTable table =  MTable.get(getCtx(),tableName);
			String llaves[] = table.getKeyColumns();
			sqlSelect = "SELECT DISTINCT " + columnName+", "+ llaves[0]+ " FROM " + tableName+
					//" WHERE AD_Client_ID = ? AND IsActive = 'Y'";
					" WHERE IsActive = 'Y'";*/
			isRefList=false;
		}

		/*StringBuilder whereClause = new StringBuilder();
		whereClause.append("AD_Table_ID=?");
		whereClause.append(" AND AD_Column_ID=?");*/

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		System.out.println(sqlSelect);
		int seqno = DB.getSQLValue(null, "SELECT MAX(seqNo) FROM kdb_kanbanstatus WHERE kdb_kanbanboard_id=?", m_kanbanBoard_id);
		try{
			pstmt = DB.prepareStatement(sqlSelect, get_TrxName());
			//pstmt.setInt(1, kanbanBoard.getAD_Client_ID());
			rs = pstmt.executeQuery();
			String statusName = null;
			String reference = null;
			while (rs.next()) {
				seqno = seqno+10;
				MKanbanStatus kanbanStatus = new MKanbanStatus(getCtx(), 0, get_TrxName());
				statusName = rs.getString(1);
				reference = rs.getString(2);
				kanbanStatus.setKDB_KanbanBoard_ID(m_kanbanBoard_id);
				kanbanStatus.setName(statusName);
				if(isRefList)
					kanbanStatus.setKDB_StatusListValue(reference);
				else
					kanbanStatus.setKDB_StatusTableID(reference);

				kanbanStatus.setSeqNo(seqno);
				kanbanStatus.saveEx();
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, sqlSelect , e);
			throw e;
		} finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return null;
	}

}
