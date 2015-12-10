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

	private MTable table = MTable.get(Env.getCtx(), getAD_Table_ID());
	private List<MKanbanStatus> statuses = new ArrayList<MKanbanStatus>();
	private List<MKanbanPriority> priorityRules = new ArrayList<MKanbanPriority>();
	private int numberOfCards =0;
	private boolean isRefList = true;
	private boolean statusProcessed = false;
	private String summarySql;
	private int summaryCounter   = 0;
	
	//Associated Processes
	private boolean processRead = false;
	private List<MKanbanProcess> associatedProcesses = new ArrayList<MKanbanProcess>();

	public MKanbanBoard(Properties ctx, int KDB_KanbanBoard_ID, String trxName) {
		super(ctx, KDB_KanbanBoard_ID, trxName);
	}

	public MKanbanBoard(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public void setBoardContent(){
		getStatuses();
		getKanbanCards();
		for(MKanbanStatus status:statuses){
			if(status.hasQueue()&&
					!status.getSQLStatement().equals("C"))
				getKanbanQueuedCards(status);
		}
	}

	public MTable getTable() {
		return table;
	}

	public void setTable(MTable table) {
		this.table = table;
	}

	public int getNumberOfCards() {
		if(numberOfCards<=0)
			getKanbanCards();
		return numberOfCards;
	}

	public boolean isRefList(){
		if (getKDB_ColumnList_ID()!=0){
			isRefList=true;
		}else if (getKDB_ColumnTable_ID()!=0){
			isRefList=false;
		}
		return isRefList;
	}

	public MColumn getStatusColumn(){
		int columnId = 0;
		if (isRefList())
			columnId = getKDB_ColumnList_ID();
		else
			columnId = getKDB_ColumnTable_ID();

		MColumn column = new MColumn(Env.getCtx(), columnId, get_TrxName());

		return column;

	}

	public void setPrintableNames(){

		if (statuses.size()==0){
			statuses = getStatuses();
		}

		MColumn column = getStatusColumn();
		ValueNamePair list[] = MRefList.getList(getCtx(), column.getAD_Reference_Value_ID(), false);
		if(column.getAD_Reference_Value_ID()!=0&&list.length>0){
			int posStatus;
			for(posStatus=0;posStatus<statuses.size();posStatus++){
				int posList=0;
				boolean match = false;
				while(posList<list.length&&!match){
					if(statuses.get(posStatus).getKDB_StatusListValue().equals(list[posList].getValue())){
						statuses.get(posStatus).setPrintableName(list[posList].toString());
						match=true;
					}
					posList++;
				}
			}
		}else
		{
			MTable table =  MTable.get(getCtx(),column.getReferenceTableName());
			//table.getPO("RRequest Estado", get_TrxName());
			if (table!=null){
				for(MKanbanStatus status: statuses){
					String name = table.get_Translation(column.getName()); //No esta funcionando, necesito traducir el nombre
					if(name==null)
						name=status.getName();

					status.setPrintableName(name);
				}
			}
		}
	}//setPrintableNames

	public MKanbanStatus getStatus(String statusName){
		for(MKanbanStatus status: statuses){
			String statusN;
			statusN = status.getStatusValue();
			if(statusN.equals(statusName)){
				return status;
			}
		}
		return null;
	}
	
	public MKanbanStatus getStatus(int statusID){		
		for(MKanbanStatus status: statuses){		
			if(status.getKDB_KanbanStatus_ID() == statusID){		
				return status;		
			}		
		}		
		return null;		
	}

	public List<MKanbanStatus> getStatuses(){

		if(!statusProcessed){

			statusProcessed=true;

			statuses = new Query(getCtx(), MKanbanStatus.Table_Name, " KDB_KanbanBoard_ID = ? AND AD_Client_ID IN (0, ?) AND IsActive='Y' ", get_TrxName())
			.setParameters(new Object[]{getKDB_KanbanBoard_ID(),Env.getAD_Client_ID(Env.getCtx())})
			.setOnlyActiveRecords(true)
			.setOrderBy("SeqNo")
 			.list();
			
		}

		return statuses;
	}//getStatuses
	
	/**		
	 * Fills the associatedProcesses List with all the process associated to the board		
	 * @return		
	 */		
	public List<MKanbanProcess> getAssociatedProcesses(){

		if( !processRead ){
			
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
	protected void checkProcessRight (List<MKanbanProcess> list) {		
		Iterator<MKanbanProcess> iterator = list.iterator();		
		while (iterator.hasNext()){		
			MKanbanProcess testKanbanProcess = iterator.next();		
			Boolean access = MRole.getDefault().getProcessAccess(testKanbanProcess.getAD_Process_ID());		
			if (access == null || !access.booleanValue()) {		
				list.remove(testKanbanProcess);		
			}		
		}		
	}//checkProcessRight

	public List<MKanbanPriority> getPriorityRules(){

		if(priorityRules.size()==0){

			priorityRules = new Query(getCtx(), MKanbanPriority.Table_Name, " KDB_KanbanBoard_id = ? AND AD_Client_ID IN (0, ?) AND IsActive='Y' ", get_TrxName())
			.setParameters(new Object[]{getKDB_KanbanBoard_ID(),Env.getAD_Client_ID(Env.getCtx())})
			.setOnlyActiveRecords(true)
			.setOrderBy("MinValue")    
 			.list();
			
		}
		return priorityRules;
	}//getPriorityRules

	public int getNumberOfStatuses(){
		if(!statusProcessed)
			getStatuses();
		return statuses.size();
	}//getNumberOfStatuses
	
	/** Return the total amount of processes associated to the board
	 * @return
	 */
	public int getNumberOfProcesses(){
		if(!processRead)
			getAssociatedProcesses();
		return associatedProcesses.size();
	}//getNumberOfProcesses

	public boolean saveStatuses(){
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
	public void getKanbanCards(){

		if(numberOfCards<=0){
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");


			MTable table = getTable();
			MColumn column = getStatusColumn();
			String llaves[] = table.getKeyColumns();

			sql.append(llaves[0]); 
			sql.append(","+column.getColumnName());

			if(hasPriorityOrder())
				sql.append(", "+getKDB_PrioritySQL());

			sql.append(" FROM "+table.getTableName());

			StringBuilder whereClause = new StringBuilder();
			whereClause.append(" WHERE ");

			if(getWhereClause()!=null)
				whereClause.append(getWhereClause()+" AND ");

			whereClause.append(column.getColumnName()+ " IN ");

			whereClause.append(getInValues());

			whereClause.append(" AND AD_Client_ID IN (0, ?) AND IsActive='Y' ");

			sql.append(whereClause.toString());
			
			if (getOrderByClause()!=null)
			{
				sql.append(" ORDER BY "+getOrderByClause());
			}
			else if(hasPriorityOrder())
				sql.append(" ORDER BY "+getKDB_PrioritySQL()+" DESC");

			log.info(sql.toString());

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				String sqlparsed = Env.parseContext(getCtx(), 0, sql.toString(), false);
				pstmt = DB.prepareStatement(sqlparsed, get_TrxName());
				pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
				rs = pstmt.executeQuery();
				int id = -1;
				String correspondingColumn= null;
				while (rs.next())
				{
					id = rs.getInt(1);
					correspondingColumn = rs.getString(2);
					MKanbanStatus status = getStatus(correspondingColumn);
					if(status.hasQueue()&&status.getSQLStatement().equals("C")    //Queued Records
							&&status.getMaxNumCards()<=status.getRecords().size()){
						MKanbanCard card = new MKanbanCard(id,status);
						if(hasPriorityOrder()){
							BigDecimal priorityValue = rs.getBigDecimal(3);
							card.setPriorityValue(priorityValue);
						}
						status.addQueuedRecord(card);
						numberOfCards++;
						status.setTotalCards(status.getTotalCards()+1);
						card.setQueued(true);
					}
					else if(status.getMaxNumCards()==0&&!status.isShowOver()){
						status.setTotalCards(status.getTotalCards()+1);
						continue;
					}
					else if(status.isShowOver()||status.getMaxNumCards()>status.getRecords().size()){
						MKanbanCard card = new MKanbanCard(id,status);
						if(hasPriorityOrder()){
							BigDecimal priorityValue = rs.getBigDecimal(3);
							card.setPriorityValue(priorityValue);
						}
						status.addRecord(card);
						numberOfCards++;
						status.setTotalCards(status.getTotalCards()+1);
					}
					else if(!status.isShowOver()){
						status.setTotalCards(status.getTotalCards()+1);
						status.setExceed(true);	
					}
				}
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, sql.toString(), e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}
	}//getKanbanCards
	
	/**
	 *Get every card from the board
	 *and assign them to its respective status
	 */
	public void getKanbanQueuedCards(MKanbanStatus status){

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");

		MTable table = getTable();
		MColumn column = getStatusColumn();
		String llaves[] = table.getKeyColumns();

		sql.append(llaves[0]); 
		
		sql.append(" FROM "+table.getTableName());

		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" WHERE ");

		if(getWhereClause()!=null)
			whereClause.append(getWhereClause()+" AND ");

		whereClause.append(column.getColumnName()+ " IN ");

		whereClause.append(getInValues());

		whereClause.append(" AND AD_Client_ID IN (0, ?) AND IsActive='Y'");
		whereClause.append(" AND "+status.getSQLStatement());
		
		sql.append(whereClause.toString());

		log.info("Queue SQL"+sql.toString());

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String sqlparsed = Env.parseContext(getCtx(), 0, sql.toString(), false);
			pstmt = DB.prepareStatement(sqlparsed, get_TrxName());
			pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
			rs = pstmt.executeQuery();
			int id = -1;
			while (rs.next())
			{
				id = rs.getInt(1);
				MKanbanCard card = status.getCard(id);
				if(card!=null){
					status.removeRecord(card);
					status.addQueuedRecord(card);
					card.setQueued(true);	
				}
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
	}//getKanbanQueuedCards

	private String getInValues(){

		StringBuilder values = new StringBuilder();
		values.append("(");
		for(MKanbanStatus status:statuses){
			if(isRefList)
				values.append("'"+status.getStatusValue()+"'");
			else
				values.append(status.getStatusValue());

			if(status.equals(statuses.get(statuses.size()-1)))
				values.append(")");
			else 
				values.append(",");
		}

		return values.toString();
	}//getInValues

	boolean hasPriorityOrder(){
		//Check if there's a  valid priority rule 
		if(getKDB_PrioritySQL()!=null) 
			return true;
		else
			return false;
	}

	public void resetStatusProperties() {
		for(MKanbanStatus status:statuses){
			status.setCardNumber(0);
			status.setQueuedCardNumber(0);
			if(hasPriorityOrder())
				status.orderCards();
		}
	}

	public boolean deleteStatus(MKanbanStatus status) {
		if(status.delete(true, get_TrxName())){
			statuses.remove(status);
			return true;
		}
		return false;
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Check if it is a valid priority rule
		String priorityRule = getKDB_PrioritySQL();

		if(priorityRule!=null){
			String sql = "Select "+priorityRule+" FROM "+getAD_Table().getTableName();

			if(DB.getSQLValue(get_TrxName(), sql)==-1){
				log.saveError("Error", Msg.getMsg(Env.getCtx(), "KDB_InvalidPriority"));
				return false;
			}
		}

		return super.beforeSave(newRecord);
	}

	public String getBackgroundColor() {
		MPrintColor priorityColor = new MPrintColor(Env.getCtx(), getKDB_BackgroundColor_ID(), null);
		return priorityColor.getName();
	}
	
	public String getSummarySql(){

		String summaryText = getKDB_SummarySQL();

		if( summarySql == null && summaryText != null ){

			int i = summaryText.indexOf("@SQL=");

			if( i > -1 ){

				i = summaryText.indexOf('=');
				String inStr;
				inStr = summaryText.substring(i+1, summaryText.length());	// from =

				if(summarySql == null )
					summarySql = addWhereClauseValidation(inStr);
			}
		}

		return summarySql;
	}//getSummarySql
	
	private int getSummaryNumberOfColumns(String sqlQuery) {

		if( summaryCounter == 0){
			int counter = 0;
			int i = sqlQuery.indexOf("SELECT");

			if( i > -1 ){

				int j = sqlQuery.indexOf("FROM");
				if( j > -1 ){
					counter++;
					String selectClause = sqlQuery.substring(i, j);

					for( int k=0; k<selectClause.length(); k++ ) {
						if( selectClause.charAt(k) == ',' ) {
							counter++;
						} 
					}
				}
			}
			summaryCounter = counter;
		}
		return summaryCounter;
	}//getSummaryNumberofColumns
	
	public int getSummaryCounter(){
		if(summarySql != null)
			return getSummaryNumberOfColumns(summarySql);
		return summaryCounter;
	}//getSummaryCounter
	
	/**
	 * Adds the AD_CLIENT_ID condition for info access security
	 * @param sqlQuery
	 */
	private String addWhereClauseValidation(String sqlQuery){
		
		StringBuilder whereClause = new StringBuilder(""); 
		int i = sqlQuery.indexOf("WHERE");
		if( i > -1 ){
			whereClause.append(sqlQuery.substring(i, sqlQuery.length()) + " AND " );
			sqlQuery = sqlQuery.substring(0, i);
		}
		else{
			whereClause.append(" WHERE ");
		}

		whereClause.append(table.getTableName() + ".AD_Client_ID IN (0, ?) AND "+ table.getTableName()+ ".IsActive='Y' ");
		
		sqlQuery = sqlQuery + whereClause;
		
		return sqlQuery;

	}//addWhereClauseValidation

}
