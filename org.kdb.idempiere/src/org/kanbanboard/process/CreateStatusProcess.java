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
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.kanbanboard.model.MKanbanBoard;
import org.kanbanboard.model.MKanbanStatus;
import org.kanbanboard.utils.KanbanSQLUtils;

public class CreateStatusProcess extends SvrProcess {

	private int m_kanbanBoard_id;
	private MKanbanBoard kanbanBoard;
	private int seqno;
	private int cnt;

	@Override
	protected void prepare() {
		m_kanbanBoard_id = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {

		if (m_kanbanBoard_id == 0)
			throw new IllegalArgumentException("KanbanBoard_ID == 0");

		kanbanBoard = new MKanbanBoard(getCtx(),m_kanbanBoard_id,get_TrxName());
		MColumn column = kanbanBoard.getStatusColumn();
		String sqlStatement = KanbanSQLUtils.getColumnSQLStatement(column);
		int parameter = kanbanBoard.isRefList() ? column.getAD_Reference_Value_ID() : kanbanBoard.getAD_Client_ID();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		seqno = getKanbanMaxSeqNo();
		cnt = 0;
		try {
			pstmt = KanbanSQLUtils.getKanbanPreparedStatement(sqlStatement, get_TrxName(), parameter);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String statusName = rs.getString(1);
				String reference = rs.getString(2);
				createKanbanStatus(statusName, reference);
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, sqlStatement, e);
			throw e;
		} finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return "@KDB_KanbanStatus_ID@ @Inserted@=" + cnt;
	}
	
	private void createKanbanStatus(String statusName, String reference) {
		if (!statusExistsAlready(reference)) {
			seqno = seqno+10;
			MKanbanStatus kanbanStatus = new MKanbanStatus(getCtx(), 0, get_TrxName());
			kanbanStatus.setKDB_KanbanBoard_ID(m_kanbanBoard_id);
			kanbanStatus.setName(statusName);
			if (kanbanBoard.isRefList())
				kanbanStatus.setKDB_StatusListValue(reference);
			else
				kanbanStatus.setKDB_StatusTableID(reference);

			kanbanStatus.setSeqNo(seqno);
			kanbanStatus.saveEx();
			cnt ++;
		}
	}
	
	private boolean statusExistsAlready(String reference) {
		for (MKanbanStatus status : kanbanBoard.getStatuses()) {
			if (reference.equals(status.getStatusValue())) {
				return true;
			}
		}
		return false;
	}
	
	private int getKanbanMaxSeqNo() {
		return DB.getSQLValueEx(get_TrxName(), "SELECT MAX(SeqNo) FROM KDB_KanbanStatus WHERE KDB_KanbanBoard_ID=?", m_kanbanBoard_id);	
	}
}
