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
 * - Diego Ruiz - BX Service GmbH								       *
 **********************************************************************/
package org.kanbanboard.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MColumn;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.kanbanboard.utils.KanbanSQLUtils;

public class MKanbanSwimlaneConfiguration extends X_KDB_KanbanSwimlanes {

	/**
	 * 
	 */
	private static final long serialVersionUID = -634627635323262721L;
	public static final String SWIMLANE_SUMMARY_TOKEN = "@KanbanSwimlane@";

	private List<KanbanSwimlane> swimlanes = new ArrayList<KanbanSwimlane>();
	private MKanbanBoard kanbanBoard;

	public MKanbanSwimlaneConfiguration(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public MKanbanSwimlaneConfiguration(Properties ctx, int KDB_KanbanSwimlanes_ID, String trxName) {
		super(ctx, KDB_KanbanSwimlanes_ID, trxName);
	}
	
	public void setKanbanBoard(MKanbanBoard kanbanBoard) {
		this.kanbanBoard = kanbanBoard;
	}

	public int getValue() {
		return getKDB_Column_ID();
	}
	
	public String getColumnName() {
		return MColumn.get(getValue()).getColumnName();
	}

	public List<KanbanSwimlane> getSwimlanes() {
		if (swimlanes.isEmpty()) {
			MColumn column = MColumn.get(getValue());
			String sqlStatement = KanbanSQLUtils.getColumnSQLStatement(column, getWhereClause(), getOrderByClause());
			int parameter = column.getAD_Reference_ID() == DisplayType.List ? column.getAD_Reference_Value_ID() 
					: getAD_Client_ID();
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = KanbanSQLUtils.getKanbanPreparedStatement(sqlStatement, get_TrxName(), parameter);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					String statusName = rs.getString(1);
					String reference = rs.getString(2);

					KanbanSwimlane swimlane = new KanbanSwimlane(this, statusName, reference);
					swimlanes.add(swimlane);
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, sqlStatement, e);
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}
		return swimlanes;
	}
	
	public void refreshSwimlanes() {
		swimlanes.forEach(swimlane -> swimlane.setPrinted(false));
	}
	
	public String getSummarySQL() {
		String summarySql = getKDB_SummarySQL();
		return kanbanBoard.addWhereClauseValidation(summarySql);
	}
	
	public String getSwimlaneRecordsID(KanbanSwimlane swimlane) {
		String ids = "";
		StringBuilder recordIds = new StringBuilder();
		
		for (MKanbanStatus status : kanbanBoard.getStatuses()) {
			for (MKanbanCard card : status.getAllSwimlaneCards(swimlane))
				recordIds.append("'" + card.getRecordID() + "',");
		}
		
		ids = recordIds.toString();

		//Remove last comma
		if (ids.length() > 0 && ids.charAt(ids.length()-1) == ',')
			ids = ids.substring(0, ids.length()-1);
		
		return ids;
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		if (!isValidSummaryConfiguration()) {
			log.saveError("Error", Msg.getMsg(getCtx(), "KDB_SwimlaneSummaryError"));
			return false;
		}
		
		return super.beforeSave(newRecord);
	}
	
	/**
	 * A Summary configuration is valid if both message and sql are empty or filled
	 * If only one of the two is filled, this is an error
	 * @return true or false
	 */
	private boolean isValidSummaryConfiguration() {
		String summaryMessage = getKDB_SummaryMsg();
		String summarySQL = getKDB_SummarySQL();
		
		return (!Util.isEmpty(summaryMessage) && !Util.isEmpty(summarySQL)) ||
				 (Util.isEmpty(summaryMessage) && Util.isEmpty(summarySQL));
	}
}
