/**********************************************************************
* This file is part of iDempiere ERP Open Source                      *
* http://www.idempiere.org                                            *
*                                                                     *
* Copyright (C) Contributors                                          *
*                                                                     *
* This program is free software; you can redistribute it and/or       *
* modify it under the terms of the GNU General Public License         *
* as published by the Free Software Foundation; either version 2      *
* of the License, or (at your option) any later version.              *
*                                                                     *
* This program is distributed in the hope that it will be useful,     *
* but WITHOUT ANY WARRANTY; without even the implied warranty of      *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
* GNU General Public License for more details.                        *
*                                                                     *
* You should have received a copy of the GNU General Public License   *
* along with this program; if not, write to the Free Software         *
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
* MA 02110-1301, USA.                                                 *
*                                                                     *
* Contributors:                                                       *
* - Diego Ruiz - Universidad Distrital Francisco Jose de Caldas       *
**********************************************************************/

package org.kanbanboard.process;

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

public class CreateStatusProcess extends SvrProcess {

	private int m_kanbanBoard_id;

	@Override
	protected void prepare() {
		m_kanbanBoard_id = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {

		boolean isRefList = true;

		if (m_kanbanBoard_id == 0)
			throw new IllegalArgumentException("KanbanBoard_ID == 0");

		MKanbanBoard kanbanBoard = new MKanbanBoard(getCtx(),m_kanbanBoard_id,get_TrxName());

		int columnId=0;
		StringBuilder sqlSelect = new StringBuilder();

		MColumn column = null;
		if (kanbanBoard.getKDB_ColumnList_ID() != 0) {
			columnId = kanbanBoard.getKDB_ColumnList_ID();
			column = new MColumn(getCtx(), columnId, get_TrxName());

			//Reference List
			if (column.getAD_Reference_ID() == DisplayType.List) {
				if (column.getAD_Reference_Value_ID() != 0) {
					// Reference Key is not a table but a RefList
					sqlSelect.append("SELECT DISTINCT Name, Value FROM AD_Ref_List ")
						.append("WHERE AD_Reference_ID = ? AND IsActive = 'Y'");
				}
			}
			isRefList= true;
		} else if (kanbanBoard.getKDB_ColumnTable_ID() != 0) {
			columnId = kanbanBoard.getKDB_ColumnTable_ID();
			column = new MColumn(getCtx(), columnId, get_TrxName());
			//Table, Table direct or Search Reference
			if (column.getAD_Reference_ID() == DisplayType.Table ||
					column.getAD_Reference_ID() == DisplayType.Search ||
					column.getAD_Reference_ID() == DisplayType.TableDir) {					

				MTable table =  MTable.get(getCtx(),column.getReferenceTableName());
				String llaves[] = table.getKeyColumns();
				String iden[]=table.getIdentifierColumns();

				sqlSelect.append("SELECT DISTINCT ").append(iden[0]).append(", ").append(llaves[0])
					.append(" FROM ").append(table.getTableName())
					.append(" WHERE ")
					.append(" AD_Client_ID IN (0, ?) AND")
					.append(" IsActive = 'Y'");
			}
			isRefList=false;
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int seqno = DB.getSQLValueEx(get_TrxName(), "SELECT MAX(SeqNo) FROM KDB_KanbanStatus WHERE KDB_KanbanBoard_ID=?", m_kanbanBoard_id);
		int cnt = 0;
		try {
			pstmt = DB.prepareStatement(sqlSelect.toString(), get_TrxName());
			if (isRefList) {
				pstmt.setInt(1, column.getAD_Reference_Value_ID());
			} else {
				pstmt.setInt(1, kanbanBoard.getAD_Client_ID());
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String statusName = rs.getString(1);
				String reference = rs.getString(2);

				boolean exists = false;
				for (MKanbanStatus status : kanbanBoard.getStatuses()) {
					if (reference.equals(status.getStatusValue())) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					seqno = seqno+10;
					MKanbanStatus kanbanStatus = new MKanbanStatus(getCtx(), 0, get_TrxName());
					kanbanStatus.setKDB_KanbanBoard_ID(m_kanbanBoard_id);
					kanbanStatus.setName(statusName);
					if (isRefList)
						kanbanStatus.setKDB_StatusListValue(reference);
					else
						kanbanStatus.setKDB_StatusTableID(reference);

					kanbanStatus.setSeqNo(seqno);
					kanbanStatus.saveEx();
					cnt ++;
				}
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, sqlSelect.toString(), e);
			throw e;
		} finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return "@KDB_KanbanStatus_ID@ @Inserted@=" + cnt;
	}
}
