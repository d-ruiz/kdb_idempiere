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
import java.util.HashMap;
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
import org.compiere.process.DocAction;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.ValueNamePair;

public class MKanbanBoard extends X_KDB_KanbanBoard {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5599415208221180263L;

	/** Special column DocStatus = DocStatus */
	public static final String STATUSCOLUMN_DocStatus = "DocStatus";

	private MTable table = MTable.get(getAD_Table_ID());
	private String keyColumn;
	private List<MKanbanStatus> statuses = new ArrayList<MKanbanStatus>();
	private List<MKanbanPriority> priorityRules = new ArrayList<MKanbanPriority>();
	private int numberOfCards = 0;
	private boolean isRefList = true;
	private boolean statusProcessed = false;
	private String summarySql;
	private HashMap<String, String> targetAction;
	
	//Associated Processes
	private boolean processRead = false;
	private List<MKanbanProcess> associatedProcesses = new ArrayList<MKanbanProcess>();

	//Kanban Parameters
	private List<MKanbanParameter> parameters = new ArrayList<MKanbanParameter>();
	
	public MKanbanBoard(Properties ctx, int KDB_KanbanBoard_ID, String trxName) {
		super(ctx, KDB_KanbanBoard_ID, trxName);
	}

	public MKanbanBoard(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	public void setBoardContent() {
 		initTargetAction();
 		getStatuses();
	}
	
	/**
	 * Maps the DocStatus to the corresponding DocAction
	 */
	private void initTargetAction() {
		targetAction = new HashMap<>();
		
		//No movement to this states manually
		targetAction.put(DocAction.STATUS_Drafted, null);
		targetAction.put(DocAction.STATUS_Invalid, null);
		targetAction.put(DocAction.STATUS_Unknown, null);
		targetAction.put(DocAction.STATUS_WaitingConfirmation, null);
		targetAction.put(DocAction.STATUS_WaitingPayment, null);

		//Map the DocStatus to DocAction 
		targetAction.put(DocAction.STATUS_Completed, DocAction.ACTION_Complete);
		targetAction.put(DocAction.STATUS_NotApproved, DocAction.ACTION_Reject);
		targetAction.put(DocAction.STATUS_Voided, DocAction.ACTION_Void);
		targetAction.put(DocAction.STATUS_Approved, DocAction.ACTION_Approve);		
		targetAction.put(DocAction.STATUS_Reversed, DocAction.ACTION_Reverse_Correct);
		targetAction.put(DocAction.STATUS_Closed, DocAction.ACTION_Close);
		targetAction.put(DocAction.STATUS_InProgress, DocAction.ACTION_Prepare);
	}
	
	public String getDocAction(String newDocStatus) {
		return targetAction.get(newDocStatus);
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
		if (getKDB_ColumnList_ID() != 0) {
			isRefList=true;
		} else if (getKDB_ColumnTable_ID() != 0) {
			isRefList=false;
		}
		return isRefList;
	}

	public MColumn getStatusColumn() {
		int columnId = 0;
		if (isRefList())
			columnId = getKDB_ColumnList_ID();
		else
			columnId = getKDB_ColumnTable_ID();
		return MColumn.get(columnId);
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
			MTable table =  MTable.get(getCtx(),column.getReferenceTableName());
			if (table != null) {
				for (MKanbanStatus status : statuses) {
					String name = table.get_Translation(column.getName()); //No esta funcionando, necesito traducir el nombre
					if (name == null)
						name=status.getName();

					status.setPrintableName(name);
				}
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

		if (priorityRules.size() == 0) {

			priorityRules = new Query(getCtx(), MKanbanPriority.Table_Name, " KDB_KanbanBoard_id = ? AND AD_Client_ID IN (0, ?) AND IsActive='Y' ", get_TrxName())
			.setParameters(new Object[]{getKDB_KanbanBoard_ID(),Env.getAD_Client_ID(Env.getCtx())})
			.setOnlyActiveRecords(true)
			.setOrderBy("MinValue")    
 			.list();
			
		}
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
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");


			MTable table = getTable();
			MColumn column = getStatusColumn();
			String llaves[] = table.getKeyColumns();
			keyColumn = llaves[0]; 
			
			sql.append(keyColumn); 
			sql.append(",");
			if (column.isVirtualColumn()) {
				sql.append("(").append(column.getColumnSQL()).append(") AS ");
			}
			sql.append(column.getColumnName());

			if (hasPriorityOrder())
				sql.append(", "+getKDB_PrioritySQL());

			sql.append(" FROM "+table.getTableName());

			StringBuilder whereClause = new StringBuilder();
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

			sql.append(whereClause.toString());
			
			if (getOrderByClause() != null) {
				sql.append(" ORDER BY "+getOrderByClause());
			} else if(hasPriorityOrder())
				sql.append(" ORDER BY "+getKDB_PrioritySQL()+" DESC");

			log.info(sql.toString());

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				String sqlparsed = Env.parseContext(getCtx(), 0, sql.toString(), false);
				pstmt = DB.prepareStatement(sqlparsed, get_TrxName());
				pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
				rs = pstmt.executeQuery();
				int id = -1;
				String correspondingColumn= null;
				while (rs.next()) {
					id = rs.getInt(1);
					correspondingColumn = rs.getString(2);
					MKanbanStatus status = getStatus(correspondingColumn);
					if (status.hasQueue() && status.getSQLStatement().equals(MKanbanStatus.QUEUE_CARDS_BY_NUMBER)    //Queued Records
							&& status.getMaxNumCards() <= status.getRecords().size()) {
						MKanbanCard card = new MKanbanCard(id,status);
						if (hasPriorityOrder()) {
							BigDecimal priorityValue = rs.getBigDecimal(3);
							card.setPriorityValue(priorityValue);
						}
						status.addQueuedRecord(card);
						numberOfCards++;
						status.setTotalCards(status.getTotalCards()+1);
						card.setQueued(true);
					} else if (status.getMaxNumCards() == 0 && !status.isShowOver()) {
						status.setTotalCards(status.getTotalCards()+1);
						continue;
					} else if (status.isShowOver() || status.getMaxNumCards() > status.getRecords().size()) {
						MKanbanCard card = new MKanbanCard(id,status);
						if (hasPriorityOrder()) {
							BigDecimal priorityValue = rs.getBigDecimal(3);
							card.setPriorityValue(priorityValue);
						}
						status.addRecord(card);
						numberOfCards++;
						status.setTotalCards(status.getTotalCards()+1);
					} else if (!status.isShowOver()) {
						status.setTotalCards(status.getTotalCards()+1);
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
		}
	}//getKanbanCards
	
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

	boolean hasPriorityOrder() {
		//Check if there's a  valid priority rule 
		if (getKDB_PrioritySQL() != null) 
			return true;
		else
			return false;
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

		if (priorityRule != null) {
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
		MPrintColor priorityColor = new MPrintColor(Env.getCtx(), getKDB_BackgroundColor_ID(), null);
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
	private String addWhereClauseValidation(String sqlQuery) {
		
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
		whereClause.append(MKanbanStatus.STATUS_RECORDS_IDS);
		whereClause.append(") ");
		
		sqlQuery = sqlQuery + whereClause.toString() + groupByClause;
		
		return sqlQuery;
	}//addWhereClauseValidation

	public List<MKanbanParameter> getParameters() {

		if (parameters.size() == 0) {

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
	}
}