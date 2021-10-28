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
import org.kanbanboard.utils.KanbanSQLUtils;

public class MKanbanSwimlaneConfiguration extends X_KDB_KanbanSwimlanes {

	/**
	 * 
	 */
	private static final long serialVersionUID = -634627635323262721L;
	private List<KanbanSwimlane> swimlanes = new ArrayList<KanbanSwimlane>();

	public MKanbanSwimlaneConfiguration(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public MKanbanSwimlaneConfiguration(Properties ctx, int KDB_KanbanSwimlanes_ID, String trxName) {
		super(ctx, KDB_KanbanSwimlanes_ID, trxName);
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

					KanbanSwimlane swimlane = new KanbanSwimlane(statusName, reference);
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
}
