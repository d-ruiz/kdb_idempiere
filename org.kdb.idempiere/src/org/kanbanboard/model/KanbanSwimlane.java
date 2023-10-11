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

import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.kanbanboard.utils.KanbanSQLUtils;

public class KanbanSwimlane {
	
	private MKanbanSwimlaneConfiguration configurationRecord;
	private int totalNumberOfCards = 0;
	private String label;
	private String value;
	private boolean printed;
	
	public KanbanSwimlane(MKanbanSwimlaneConfiguration configurationRecord, String label, String value) {
		this.configurationRecord = configurationRecord;
		this.label = label;
		this.value = value;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getComponentLabel() {
		return getLabel() + Msg.getMsg(Env.getCtx(), "KDB_SwimlaneSummary", new Object[]{getTotalNumberOfCards()});
	}
	
	public String getSummary() {
		return hasSummary() ? getSummaryMsg() : "";
	}
	
	private String getSummaryMsg() {
		String summarySql = configurationRecord.getSummarySQL();
		String msgValue = configurationRecord.getKDB_SummaryMsg();

		if (summarySql != null) {
			summarySql = KanbanSQLUtils.replaceTokenWithValue(summarySql, MKanbanSwimlaneConfiguration.SWIMLANE_SUMMARY_TOKEN, "'" + value + "'");
			summarySql = KanbanSQLUtils.replaceTokenWithValue(summarySql, MKanbanBoard.RECORDS_IDS, configurationRecord.getSwimlaneRecordsID(this));
			return KanbanSQLUtils.getSummary(summarySql, msgValue);
		}
		return "";
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public int getTotalNumberOfCards() {
		return totalNumberOfCards;
	}

	public void addOneCard() {
		this.totalNumberOfCards++;
	}

	public void removeOneCard() {
		this.totalNumberOfCards--;
	}
	
	public boolean isPrinted() {
		return printed;
	}

	public void setPrinted(boolean printed) {
		this.printed = printed;
	}
	
	public boolean hasSummary() {
		return !Util.isEmpty(configurationRecord.getKDB_SummarySQL()) && totalNumberOfCards > 0;
	}
}
