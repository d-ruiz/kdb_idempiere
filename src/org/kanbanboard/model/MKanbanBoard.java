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

package org.kanbanboard.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MColumn;
import org.compiere.model.MRefList;
import org.compiere.model.MRole;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.print.MPrintColor;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.util.ValueNamePair;

public class MKanbanBoard extends X_KDB_KanbanBoard {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5599415208221180263L;

	/** Special column DocStatus = DocStatus */
	public static final String STATUSCOLUMN_DocStatus = "DocStatus";
	public static final String RECORDS_IDS = "@RECORDS_ID@";

	private MTable table = MTable.get(getAD_Table_ID());
	private String keyColumn;
	private List<MKanbanStatus> statuses = new ArrayList<MKanbanStatus>();
	private List<MKanbanPriority> priorityRules = null;
	private List<MKanbanSwimlaneConfiguration> swimlaneConfigurationRecords;
	private List<KanbanSwimlane> swimlanesArray = new ArrayList<KanbanSwimlane>();
	private int numberOfCards = 0;
	private boolean isRefList = true;
	private boolean statusProcessed = false;
	private String summarySql;
	private MKanbanSwimlaneConfiguration activeSwimlaneRecord;
	
	private int lastColumnIndex;
	private int idColumnIndex; 
	private int statusColumnIndex;
	private int priorityColumnIndex;
	private int swimlaneColumnIndex;
	
	//Associated Processes
	private boolean processRead = false;
	private List<MKanbanProcess> associatedProcesses = new ArrayList<MKanbanProcess>();

	//Kanban Parameters
	private List<MKanbanParameter> parameters = null;
	
	public MKanbanBoard(Properties ctx, int KDB_KanbanBoard_ID, String trxName) {
		super(ctx, KDB_KanbanBoard_ID, trxName);
	}

	public MKanbanBoard(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	public void setBoardContent() {
 		getStatuses();
 		setDefaultSwimlane();
	}

	public MTable getTable() {
		return table;
	}

	public void setTable(MTable table) {
		this.table = table;
	}

	public int getNumberOfCards() {
		if (numberOfCards <= 0)
			getKanbanCards();
		return numberOfCards;
	}

	public boolean isRefList() {
		return getKDB_ColumnList_ID() != 0;
	}

	public MColumn getStatusColumn() {
		int columnId = 0;
		if (isRefList())
			columnId = getKDB_ColumnList_ID();
		else
			columnId = getKDB_ColumnTable_ID();
		return MColumn.get(columnId);
	}
	
	public String getStatusColumnName() {
		return getStatusColumn().getColumnName();
	}

	public void setPrintableNames() {

		if (statuses.size() == 0) {
			statuses = getStatuses();
		}

		MColumn column = getStatusColumn();
		ValueNamePair list[] = MRefList.getList(getCtx(), column.getAD_Reference_Value_ID(), false);
		if (column.getAD_Reference_Value_ID() != 0 && list.length > 0) {
			int posStatus;
			for (posStatus=0; posStatus<statuses.size(); posStatus++) {
				int posList=0;
				boolean match = false;
				while (posList < list.length && !match) {
					if (statuses.get(posStatus).getKDB_StatusListValue() != null &&
							statuses.get(posStatus).getKDB_StatusListValue().equals(list[posList].getValue())) {
						statuses.get(posStatus).setPrintableName(list[posList].toString());
						match=true;
					}
					posList++;
				}
			}
		} else {
			for (MKanbanStatus status : statuses) {
				status.setPrintableName(status.getName());
			}
		}
	}//setPrintableNames

	public MKanbanStatus getStatus(String statusName) {
		if (statusName == null)
			return null;
		for (MKanbanStatus status : statuses) {
			String statusN;
			statusN = status.getStatusValue();
			if (statusName.equals(statusN)) {
				return status;
			}
		}
		return null;
	}
	
	public MKanbanStatus getStatus(int statusID) {		
		for (MKanbanStatus status : statuses) {		
			if (status.getKDB_KanbanStatus_ID() == statusID) {		
				return status;		
			}		
		}		
		return null;		
	}

	public List<MKanbanStatus> getStatuses() {

		if (!statusProcessed) {

			statusProcessed=true;

			statuses = new Query(getCtx(), MKanbanStatus.Table_Name, " KDB_KanbanBoard_ID = ? AND AD_Client_ID IN (0, ?) AND IsActive='Y' ", get_TrxName())
			.setParameters(new Object[]{getKDB_KanbanBoard_ID(),Env.getAD_Client_ID(Env.getCtx())})
			.setOnlyActiveRecords(true)
			.setOrderBy("SeqNo")
 			.list();
			
			for (MKanbanStatus status : statuses)
				status.setKanbanBoard(this);
		}

		return statuses;
	}//getStatuses
	
	public List<MKanbanSwimlaneConfiguration> getSwimlaneConfigurationRecords() {
		if (swimlaneConfigurationRecords == null) {
			swimlaneConfigurationRecords = new Query(getCtx(), MKanbanSwimlaneConfiguration.Table_Name, " KDB_KanbanBoard_ID = ? ", get_TrxName())
			.setParameters(getKDB_KanbanBoard_ID())
			.setOnlyActiveRecords(true)
			.setOrderBy("Name")
 			.list();
			
			for (MKanbanSwimlaneConfiguration swimlaneConfigRecord : swimlaneConfigurationRecords)
				swimlaneConfigRecord.setKanbanBoard(this);
		}

		return swimlaneConfigurationRecords;
	}
	
	public boolean usesSwimlane() {
		return getSwimlaneConfigurationRecords().size() > 0;
	}
	
	/**		
	 * Fills the associatedProcesses List with all the process associated to the board		
	 * @return		
	 */		
	public List<MKanbanProcess> getAssociatedProcesses() {

		if (!processRead) {
			
			processRead = true;
			
			associatedProcesses = new Query(getCtx(), MKanbanProcess.Table_Name, " KDB_KanbanBoard_ID = ? AND AD_Client_ID IN (0, ?) AND IsActive='Y' ", get_TrxName())
			.setParameters(new Object[]{getKDB_KanbanBoard_ID(),Env.getAD_Client_ID(Env.getCtx())})
			.setOnlyActiveRecords(true)
 			.list();

			checkProcessRight(associatedProcesses);
		}
		return associatedProcesses;
	}//getAssociatedProcesses
			
	/**		
    * if user haven't right to run a process, set kanbanProcess to null 		
    * @param associatedProcesses		
    */		
	protected void checkProcessRight(List<MKanbanProcess> list) {		
		Iterator<MKanbanProcess> iterator = list.iterator();		
		while (iterator.hasNext()) {		
			MKanbanProcess testKanbanProcess = iterator.next();		
			Boolean access = MRole.getDefault().getProcessAccess(testKanbanProcess.getAD_Process_ID());		
			if (access == null || !access.booleanValue()) {		
				iterator.remove();
			}		
		}		
	}//checkProcessRight

	public List<MKanbanPriority> getPriorityRules() {

		if (priorityRules == null)
			priorityRules = MKanbanPriority.getPriorityRules(getKDB_KanbanBoard_ID());

		return priorityRules;
	}//getPriorityRules

	public int getNumberOfStatuses() {
		if (!statusProcessed)
			getStatuses();
		return statuses.size();
	}//getNumberOfStatuses
	
	/** Return the total amount of processes associated to the board
	 * @return
	 */
	public int getNumberOfProcesses() {
		if (!processRead)
			getAssociatedProcesses();
		return associatedProcesses.size();
	}//getNumberOfProcesses

	public boolean saveStatuses() {
		for (MKanbanStatus status : statuses) {
			if (status.isActive())
				status.save(get_TrxName());
		}
		return true;
	}

	/**
	 *Get every card from the board
	 *and assign them to its respective status
	 */
	public void getKanbanCards() {

		if (numberOfCards <= 0) {
			
			initIndexes();
			String sql = getCardsSQLStatement();
			if (log.isLoggable(Level.INFO)) 
				log.info(sql.toString());

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				String sqlparsed = Env.parseContext(getCtx(), 0, sql, false);
				pstmt = DB.prepareStatement(sqlparsed, get_TrxName());
				pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
				rs = pstmt.executeQuery();
				int id = -1;
				String correspondingColumn= null;
				while (rs.next()) {
					id = rs.getInt(idColumnIndex);
					correspondingColumn = rs.getString(statusColumnIndex);
					MKanbanStatus status = getStatus(correspondingColumn);
					BigDecimal priorityValue = hasPriorityOrder() ? rs.getBigDecimal(priorityColumnIndex) : BigDecimal.ZERO;
					String swimlaneValue = isSwimlaneSelected() ? rs.getString(swimlaneColumnIndex) : "";

					if (status.isPutCardOnQueue()) {
						MKanbanCard card = new MKanbanCard(id,status);
						card.setPriorityValue(priorityValue);
						card.setSwimlaneValue(swimlaneValue);
						status.addQueuedRecord(card);
						numberOfCards++;
						card.setQueued(true);
					} else if (status.getMaxNumCards() == 0 && !status.isShowOver()) {
						status.increaseTotalCardsByOne();
						continue;
					} else if (status.isPutCardOnStatus()) {
						MKanbanCard card = new MKanbanCard(id,status);
						card.setSwimlaneValue(swimlaneValue);
						card.setPriorityValue(priorityValue);
						status.addRecord(card);
						numberOfCards++;
					} else if (!status.isShowOver()) {
						status.increaseTotalCardsByOne();
						status.setExceed(true);	
					}
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, sql.toString(), e);
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
			
			if (isSwimlaneSelected()) {
				for (MKanbanStatus status : statuses) {
					status.configureSwimlanes(swimlanesArray);
				}
			}
		}
	}//getKanbanCards
	
	private void initIndexes() {
		lastColumnIndex = 1;
		idColumnIndex = lastColumnIndex++; 
		statusColumnIndex = lastColumnIndex++;
		priorityColumnIndex = 0;
		swimlaneColumnIndex = 0;
	}
	
	private String getCardsSQLStatement() {
		StringBuilder sql = new StringBuilder();
		sql.append(getSelectClause());
		sql.append(" FROM " + getTable().getTableName());
		sql.append(getFullWhereClause());
		sql.append(getOrderBySQLClause());
		
		return sql.toString();
	}
	
	private String getSelectClause() {
		StringBuilder sqlSelect = new StringBuilder("SELECT ");
		
		MTable table = getTable();
		String keyColumns[] = table.getKeyColumns();
		keyColumn = keyColumns[0]; 
		
		sqlSelect.append(keyColumn);
		sqlSelect.append(",");
		sqlSelect.append(getColumnSQLQuery(getStatusColumn()));

		if (hasPriorityOrder()) {
			sqlSelect.append(", " + getKDB_PrioritySQL());
			priorityColumnIndex = lastColumnIndex++;
		}
		
		if (isSwimlaneSelected()) {
			sqlSelect.append(", " + getColumnSQLQuery(MColumn.get(activeSwimlaneRecord.getValue())));
			swimlaneColumnIndex = lastColumnIndex++;
		}
		
		return sqlSelect.toString();
	}
	
	private String getColumnSQLQuery(MColumn column) {
		StringBuilder columnQuery = new StringBuilder();
		if (column.isVirtualColumn()) {
			columnQuery.append("(").append(column.getColumnSQL()).append(") AS ");
		}
		columnQuery.append(column.getColumnName());
		
		return columnQuery.toString();
	}
	
	private String getFullWhereClause() {
		StringBuilder whereClause = new StringBuilder();
		MColumn column = getStatusColumn();

		whereClause.append(" WHERE ");

		if (getWhereClause() != null)
			whereClause.append(getWhereClause()+" AND ");

		if (column.isVirtualColumn()) {
			whereClause.append("(").append(column.getColumnSQL()).append(")");
		} else {
			whereClause.append(column.getColumnName());
		}
		whereClause.append(" IN ");

		whereClause.append(getInValues());

		whereClause.append(" AND AD_Client_ID IN (0, ?) AND IsActive='Y' ");

		String paramWhere = getParamWhere();
		if (!paramWhere.isEmpty())
			whereClause.append(" AND ").append(paramWhere);

		return whereClause.toString();
	}
	
	private String getOrderBySQLClause() {
		StringBuilder sql = new StringBuilder();

		if (getOrderByClause() != null) {
			sql.append(" ORDER BY " + getOrderByClause());
		} else if(hasPriorityOrder()) {
			sql.append(" ORDER BY " + getKDB_PrioritySQL() + " DESC");
		}

		return sql.toString();
	}
	
	public void setKanbanQueuedCards() {
		for (MKanbanStatus status : getStatuses()) {
			status.setSQLQueuedCards();
		}
	}
	
	private String getInValues() {

		StringBuilder values = new StringBuilder();
		values.append("(");
		for (MKanbanStatus status : statuses) {
			if (isRefList)
				values.append("'"+status.getStatusValue()+"'");
			else
				values.append(status.getStatusValue());

			if (status.equals(statuses.get(statuses.size()-1)))
				values.append(")");
			else 
				values.append(",");
		}

		return values.toString();
	}//getInValues

	public boolean hasPriorityOrder() {
		return !Util.isEmpty(getKDB_PrioritySQL());
	}

	public void resetStatusProperties() {
		for (MKanbanStatus status : statuses) {
			status.setCardNumber(0);
			status.setQueuedCardNumber(0);
			if (hasPriorityOrder())
				status.orderCards();
		}
	}

	public boolean deleteStatus(MKanbanStatus status) {
		if (status.delete(true, get_TrxName())) {
			statuses.remove(status);
			return true;
		}
		return false;
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Check if it is a valid priority rule
		String priorityRule = getKDB_PrioritySQL();

		if (!Util.isEmpty(priorityRule)) {
			String sql = "Select "+priorityRule+" FROM "+getAD_Table().getTableName();

			if (DB.getSQLValue(get_TrxName(), sql) == -1) {
				log.saveError("Error", Msg.getMsg(Env.getCtx(), "KDB_InvalidPriority"));
				return false;
			}
		}
		
		// Allow only advanced users to display SQL results in the Kanban Board
		String kanbanCardContent = getKDB_KanbanCard();
		if (kanbanCardContent != null && kanbanCardContent.contains("@SQL=")) {
			MRole role = MRole.getDefault();
			if (!role.isAccessAdvanced()) {
				log.saveError("Error", Msg.getMsg(getCtx(), "ActionNotAllowedHere"));
				return false;
			}
		}
		
		// Can't change status related data unless no status child 
		if (!newRecord && (is_ValueChanged(COLUMNNAME_AD_Table_ID) || 
				is_ValueChanged(COLUMNNAME_KDB_ColumnList_ID) || 
						is_ValueChanged(COLUMNNAME_KDB_ColumnTable_ID))) {
			String sql = "SELECT COUNT(*) FROM KDB_KanbanStatus WHERE KDB_KanbanBoard_ID=?";
			int noStatus = DB.getSQLValue(get_TrxName(), sql, getKDB_KanbanBoard_ID());
			if (noStatus > 0) {
				log.saveError("Error", Msg.getMsg(getCtx(), "KDB_SaveErrorStatuses"));
				return false;
			}
		}
		
		return super.beforeSave(newRecord);
	}

	public String getBackgroundColor() {
		MPrintColor priorityColor = MPrintColor.get(Env.getCtx(), getKDB_BackgroundColor_ID());
		return priorityColor.getName();
	}
	
	public String getSummarySql() {

		String summaryText = getKDB_SummarySQL();

		if (summarySql == null && summaryText != null) {

			//Remove @SQL= if it brings it
			if (summaryText.indexOf("@SQL=") > -1) {

				int i = summaryText.indexOf('=');
				summaryText = summaryText.substring(i+1, summaryText.length());	// from =
			}

			if (summarySql == null)
				summarySql = addWhereClauseValidation(summaryText);

		}

		return summarySql;
	}//getSummarySql
	
	/**
	 * Records ID that belong to the status 
	 * @param sqlQuery
	 */
	public String addWhereClauseValidation(String sqlQuery) {
		
		StringBuilder whereClause = new StringBuilder("");
		String groupByClause = "";
		int i = sqlQuery.indexOf("WHERE");
		if (i > -1) {
			int j = sqlQuery.indexOf("GROUP BY");
			if (j > -1) {
				whereClause.append(sqlQuery.substring(i, j) + " AND " );
				groupByClause = sqlQuery.substring(j, sqlQuery.length());
			} else {
				whereClause.append(sqlQuery.substring(i, sqlQuery.length()) + " AND " );
			}
			sqlQuery = sqlQuery.substring(0, i); //From SELECT until WHERE
		}
		else {
			whereClause.append(" WHERE ");
		}
		
		whereClause.append(table.getTableName());
		whereClause.append(".");
		whereClause.append(keyColumn);
		whereClause.append(" IN (");
		whereClause.append(RECORDS_IDS);
		whereClause.append(") ");
		
		sqlQuery = sqlQuery + whereClause.toString() + groupByClause;
		
		return sqlQuery;
	}//addWhereClauseValidation

	public List<MKanbanParameter> getParameters() {

		if (parameters == null) {
			parameters = new Query(getCtx(), MKanbanParameter.Table_Name, " KDB_KanbanBoard_ID = ? AND AD_Client_ID IN (0, ?) AND IsActive='Y' ", get_TrxName())
			.setParameters(new Object[]{getKDB_KanbanBoard_ID(),Env.getAD_Client_ID(Env.getCtx())})
			.setOnlyActiveRecords(true)
			.setOrderBy("SeqNo")
 			.list();
			
			for (MKanbanParameter parameter : parameters)
				parameter.setKanbanBoard(this);
		}
		return parameters;
	} //getParameters
	
	public String getParamWhere() {
		
		if (parameters.isEmpty())
			getParameters();

		StringBuilder paramWhereSql = new StringBuilder();
		
		for (MKanbanParameter param : parameters) {
			if (param.getSQLClause() == null || param.getSQLClause().isEmpty())
				continue;

			if (paramWhereSql.length() > 0)
				paramWhereSql.append(" AND ");

			paramWhereSql.append(param.getSQLClause());
			paramWhereSql.append(" ");
		}

		return paramWhereSql.toString();
	} //addParamSQL
	
	public void refreshCards() {
		for (MKanbanStatus status : statuses) {
			status.clearCards();
		}
		numberOfCards = 0;
		getKanbanCards();
		refreshSwimlanes();
	}
	
	private void refreshSwimlanes() {
		if (activeSwimlaneRecord != null) {
			activeSwimlaneRecord.refreshSwimlanes();
		}
	}

	public void setActiveSwimlaneRecord(Object value) {
		Integer columnID = (Integer) value;
		activeSwimlaneRecord = swimlaneConfigurationRecords.stream()
				  .filter(swimlane -> columnID == swimlane.getValue())
				  .findAny()
				  .orElse(null);
		
		if (activeSwimlaneRecord != null) {
			swimlanesArray = activeSwimlaneRecord.getSwimlanes();
		}

	}
	
	public MKanbanSwimlaneConfiguration getActiveSwimlaneRecord() {
		return activeSwimlaneRecord;
	}
	
	private void setDefaultSwimlane() {
		if (usesSwimlane()) {
			for (MKanbanSwimlaneConfiguration swimConfig : getSwimlaneConfigurationRecords()) {
				if (swimConfig.isDefault()) {
					setActiveSwimlaneRecord(swimConfig.getValue());
				}
			}
		}
	}
	
	private boolean isSwimlaneSelected() {
		return usesSwimlane() && activeSwimlaneRecord != null;
	}
	
	public List<KanbanSwimlane> getSwimlanes() {
		return swimlanesArray;
	}
	
	public KanbanSwimlane getSwimlane(String value) {
		for (KanbanSwimlane swimlane : getSwimlanes()) {
			if (swimlane.getValue().equals(value))
				return swimlane;
		}
		return null;
	}
	
	public boolean isDocActionKanbanBoard() {
		return STATUSCOLUMN_DocStatus.equals(getStatusColumnName());
	}	
	
	/**
	 * Returns wether or not the kanban priority has a valid priority column
	 * @return true if the priority SQL is a non-virtual column of the table and it is an integer
	 */
	public boolean isPriorityColumn() {
		if (hasPriorityOrder()) {
			String prioritySQL = getKDB_PrioritySQL();
			MTable table = getTable();
			if (table.columnExistsInDB(prioritySQL)) {
				MColumn column = table.getColumn(prioritySQL);
				return column.getAD_Reference_ID() == DisplayType.Integer;
			}
		}
		return false;
	}
}