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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MColumn;
import org.compiere.model.MRefList;
import org.compiere.model.MTable;
import org.compiere.util.DB;
import org.compiere.util.ValueNamePair;

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
			ValueNamePair refList[] = MRefList.getList(getCtx(), column.getAD_Reference_Value_ID(), false);
			if (column.getAD_Reference_Value_ID() != 0 && refList.length > 0) {
				KanbanSwimlane swimlane;
				for (ValueNamePair listItem : refList) {
					swimlane = new KanbanSwimlane(listItem.getName(), listItem.getValue());
					swimlanes.add(swimlane);
				}
				if (getOrderByClause() != null) {
					Collections.sort(swimlanes, Collections.reverseOrder(new Comparator<KanbanSwimlane>() {
						@Override
						public int compare(KanbanSwimlane card1, KanbanSwimlane card2) {
							return card1.getValue().compareTo(card2.getValue()); //TODO Order?
						}
					}));
				}
			} else {
				MTable table =  MTable.get(getCtx(), column.getReferenceTableName());
				StringBuilder sqlSelect = new StringBuilder();
				String llaves[] = table.getKeyColumns();
				String iden[]=table.getIdentifierColumns();

				sqlSelect.append("SELECT DISTINCT ").append(iden[0]).append(", ").append(llaves[0])
				.append(" FROM ").append(table.getTableName())
				.append(" WHERE ")
				.append(getWhereClause() + " AND ")
				.append(" AD_Client_ID IN (0, ?) AND")
				.append(" IsActive = 'Y'")
				.append(" ORDER BY " + getOrderByClause());

				//Access
				if (table != null) {
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try {
						pstmt = DB.prepareStatement(sqlSelect.toString(), get_TrxName());
						pstmt.setInt(1, getAD_Client_ID());
						rs = pstmt.executeQuery();
						while (rs.next()) {
							String statusName = rs.getString(1);
							String reference = rs.getString(2);

							KanbanSwimlane swimlane = new KanbanSwimlane(statusName, reference);
							swimlanes.add(swimlane);
						}
					} catch (SQLException e) {
						log.log(Level.SEVERE, sqlSelect.toString(), e);
					} finally {
						DB.close(rs, pstmt);
						rs = null;
						pstmt = null;
					}
				}
			}
		}
		return swimlanes;
	}
}
