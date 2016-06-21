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

package org.idempiere.apps.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.kanbanboard.model.MKanbanBoard;
import org.kanbanboard.model.MKanbanCard;
import org.kanbanboard.model.MKanbanStatus;

public class KanbanBoard {

	public static CLogger log = CLogger.getCLogger(KanbanBoard.class);

	private MKanbanBoard        kanbanBoard = null;
	private List<MKanbanStatus> statuses    = null;
	private MKanbanStatus       activeStatus;
	private String              isReadWrite = null;
	private String              summarySql = null;		
	private int                 summaryCounter = 0;

	public int getNumberOfCards() {
		return kanbanBoard.getNumberOfCards();
	}

	public String getBackgroundColor(){
		return kanbanBoard.getBackgroundColor();
	}

	public KeyNamePair[] getProcessList(){
		String sql = null;
		KeyNamePair[] list;
		boolean baseLanguage = Env.isBaseLanguage(Env.getCtx(), MKanbanBoard.Table_Name);
		if (baseLanguage){
			sql = "SELECT k.KDB_KanbanBoard_ID, k.Name "
					+ "FROM KDB_KanbanBoard k "
					+ "WHERE k.AD_Client_ID IN (0, ?) AND k.IsActive='Y' "
					+ "AND k.KDB_KanbanBoard_ID IN (SELECT KDB_KanbanBoard_ID FROM KDB_KanbanControlAccess WHERE AD_Role_ID=?) "
					+ "ORDER BY k.Name";

			list = DB.getKeyNamePairs(null, sql, true, Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Role_ID(Env.getCtx()));
		}
		else{
			sql = "SELECT k.KDB_KanbanBoard_ID, kt.Name "
					+ "FROM KDB_KanbanBoard k JOIN KDB_KanbanBoard_Trl kt ON (k.KDB_KanbanBoard_ID=kt.KDB_KanbanBoard_ID) "
					+ "WHERE k.AD_Client_ID IN (0, ?) AND k.IsActive='Y' "
					+ "AND k.KDB_KanbanBoard_ID IN (SELECT KDB_KanbanBoard_ID FROM KDB_KanbanControlAccess WHERE AD_Role_ID=?) "
					+ "AND kt.AD_Language=? "
					+ "ORDER BY kt.Name";
			
			list = DB.getKeyNamePairs(null, sql, true, Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Role_ID(Env.getCtx()),Env.getAD_Language(Env.getCtx()));
		}
		return list;
	}

	public boolean isReadWrite(){
		if(isReadWrite==null){
			String sql = "SELECT isreadwrite FROM KDB_KanbanControlAccess " +
					"WHERE KDB_KanbanBoard_ID = ? AND AD_Role_ID= ? AND IsActive = 'Y'";

			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try{
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, kanbanBoard.getKDB_KanbanBoard_ID());
				pstmt.setInt(2, Env.getAD_Role_ID(Env.getCtx()));
				rs = pstmt.executeQuery();
				while(rs.next()){
					isReadWrite = rs.getString(1);
				}

			}catch (SQLException e) {
				log.log(Level.SEVERE, sql , e);
				//throw e;
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}
		if(isReadWrite.equals("Y"))
			return true;
		else 
			return false;
	}

	public MKanbanStatus getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(MKanbanStatus activeStatus) {
		this.activeStatus = activeStatus;
	}

	public MKanbanBoard getKanbanBoard() {
		return kanbanBoard;
	}

	public void setKanbanBoard(MKanbanBoard kanbanBoard) {
		this.kanbanBoard = kanbanBoard;
	}

	public void setKanbanBoard(int kanbanBoardId){
		//Check if it's it's a new kanban board or the one already selected
		if(kanbanBoardId==-1)
			kanbanBoard=null;
		else if( kanbanBoard == null || kanbanBoardId != kanbanBoard.get_ID() ){
			kanbanBoard = new MKanbanBoard(Env.getCtx(), kanbanBoardId, null);
			statuses=null;
			isReadWrite = null;
			kanbanBoard.setBoardContent();
			summarySql = null;		
			summaryCounter = 0;
		}
	}

	public void refreshBoard(){
		setKanbanBoard(-1);
	}

	public  List<MKanbanStatus> getStatuses(){
		if(statuses==null){
			setPrintableNames();
			statuses= kanbanBoard.getStatuses();
		}
		orderStatuses();
		return statuses;
	}


	public void resetStatusProperties() {
		kanbanBoard.resetStatusProperties();
	}

	public void orderStatuses(){
		Collections.sort(statuses, new Comparator<MKanbanStatus>() {
			@Override
			public int compare(MKanbanStatus status1, MKanbanStatus status2) {
				// order by SeqNo
				return status1.getSeqNo()-(status2.getSeqNo());
			}
		});
	}

	public boolean swapCard(MKanbanStatus startStatus, MKanbanStatus endStatus, MKanbanCard card){

		boolean statusChanged = card.changeStatus(kanbanBoard.getStatusColumn().getColumnName(), endStatus.getStatusValue());
		if(statusChanged){
			startStatus.removeRecord(card);
			endStatus.addRecord(card);
			card.setBelongingStatus(endStatus);
		}
		return statusChanged;
	}

	public int getAd_Table_id(){
		return kanbanBoard.getAD_Table_ID();
	}

	public int getNumberOfStatuses(){
		if(statuses==null)
			return kanbanBoard.getNumberOfStatuses();
		else
			return statuses.size();
	}

	public void setPrintableNames(){
		kanbanBoard.setPrintableNames();
	}

	public Object getPOObject(MTable table, int recordID, String trxName){
		PO object = table.getPO(recordID, trxName);
		return object;
	}
	
	public int getStdColumnWidth() {
		return kanbanBoard.getKDB_StdColumnWidth();
	}
	
	public int getStdCardheight() {
		return kanbanBoard.getKDB_StdCardHeight();
	}
	
	
	public String getSummarySql(){		
		if( summarySql == null && kanbanBoard.getKDB_SummarySQL() != null ){		
			summarySql = kanbanBoard.getSummarySql();		
		}		
		return summarySql;		
	}		
			
	public int getSummaryCounter(){		
		if(summaryCounter == 0)		
			summaryCounter = kanbanBoard.getSummaryCounter();		
		return summaryCounter;		
	}
	
	public boolean isHTML() {
		return kanbanBoard.get_ValueAsBoolean("IsHtml");
	}
}
