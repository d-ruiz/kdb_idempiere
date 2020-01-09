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

package org.kanbanboard.apps.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import org.compiere.model.GridField;
import org.compiere.model.GridFieldVO;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Util;
import org.kanbanboard.model.MKanbanBoard;
import org.kanbanboard.model.MKanbanCard;
import org.kanbanboard.model.MKanbanParameter;
import org.kanbanboard.model.MKanbanProcess;
import org.kanbanboard.model.MKanbanStatus;

public class KanbanBoard {

	public static CLogger log = CLogger.getCLogger(KanbanBoard.class);
	
	protected final static String PROCESS_TYPE = "processType"; 
	protected final static String CARD_PROCESS = "cardProcess";
	protected final static String STATUS_PROCESS = "statusProcess";
	protected final static String BOARD_PROCESS = "boardProcess";

	private MKanbanBoard        kanbanBoard = null;
	private List<MKanbanStatus> statuses    = null;
	private MKanbanStatus       activeStatus;
	private String              isReadWrite = null;
	private String              summarySql = null;		
	
	//Associated processes
	private List<MKanbanProcess> processes  = null;
	private List<MKanbanProcess> statusProcesses  = null;
	private List<MKanbanProcess> boardProcesses  = null;
	private List<MKanbanProcess> cardProcesses  = null;
	
	//Parameters
	private List<MKanbanParameter> boardParameters  = null;

	protected int windowNo = 0;
	
	public int getNumberOfCards() {
		return kanbanBoard.getNumberOfCards();
	}

	public String getBackgroundColor() {
		return kanbanBoard.getBackgroundColor();
	}

	public KeyNamePair[] getProcessList() {
		String sql = null;
		KeyNamePair[] list;
		boolean baseLanguage = Env.isBaseLanguage(Env.getCtx(), MKanbanBoard.Table_Name);
		if (baseLanguage) {
			sql = "SELECT k.KDB_KanbanBoard_ID, k.Name "
					+ "FROM KDB_KanbanBoard k "
					+ "WHERE k.AD_Client_ID IN (0, ?) AND k.IsActive='Y' "
					+ "AND k.KDB_KanbanBoard_ID IN (SELECT KDB_KanbanBoard_ID FROM KDB_KanbanControlAccess WHERE (AD_Role_ID = ? " 
					+ "                                                              OR AD_Role_ID IN (SELECT Included_Role_ID " 
					+ "                                                                              FROM   AD_Role_Included " 
					+ "                                                                              WHERE  AD_Role_id = ? " 
					+ "                                                                              AND IsActive = 'Y'))) "
					+ "ORDER BY k.Name";

			list = DB.getKeyNamePairs(null, sql, true, Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Role_ID(Env.getCtx()), Env.getAD_Role_ID(Env.getCtx()));
		} else {
			sql = "SELECT k.KDB_KanbanBoard_ID, kt.Name "
					+ "FROM KDB_KanbanBoard k JOIN KDB_KanbanBoard_Trl kt ON (k.KDB_KanbanBoard_ID=kt.KDB_KanbanBoard_ID) "
					+ "WHERE k.AD_Client_ID IN (0, ?) AND k.IsActive='Y' "
					+ "AND k.KDB_KanbanBoard_ID IN (SELECT KDB_KanbanBoard_ID FROM KDB_KanbanControlAccess WHERE (AD_Role_ID = ? " 
					+ "                                                              OR AD_Role_ID IN (SELECT Included_Role_ID " 
					+ "                                                                              FROM   AD_Role_Included " 
					+ "                                                                              WHERE  AD_Role_id = ? " 
					+ "                                                                              AND IsActive = 'Y'))) "
					+ "AND kt.AD_Language=? "
					+ "ORDER BY kt.Name";
			
			list = DB.getKeyNamePairs(null, sql, true, Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Role_ID(Env.getCtx()), Env.getAD_Role_ID(Env.getCtx()), Env.getAD_Language(Env.getCtx()));
		}
		return list;
	}

	public boolean isReadWrite() {
		if (isReadWrite == null) {
			String sql = "SELECT isreadwrite FROM KDB_KanbanControlAccess " +
					"WHERE KDB_KanbanBoard_ID = ? AND IsActive = 'Y' AND (AD_Role_ID = ? "
					+ "                                                OR AD_Role_ID IN (SELECT Included_Role_ID " 
					+ "                                                                  FROM   AD_Role_Included " 
					+ "                                                                  WHERE  AD_Role_id = ? " 
					+ "                                                                  AND IsActive = 'Y'))";

			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try{
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, kanbanBoard.getKDB_KanbanBoard_ID());
				pstmt.setInt(2, Env.getAD_Role_ID(Env.getCtx()));
				pstmt.setInt(3, Env.getAD_Role_ID(Env.getCtx()));
				rs = pstmt.executeQuery();
				while (rs.next()) {
					isReadWrite = rs.getString(1);
				}

			} catch (SQLException e) {
				log.log(Level.SEVERE, sql , e);
				//throw e;
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}
		if (!Util.isEmpty(isReadWrite) && isReadWrite.equals("Y"))
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

	public void setKanbanBoard(int kanbanBoardId) {
		//Check if it's it's a new kanban board or the one already selected
		if (kanbanBoardId==-1)
			kanbanBoard=null;
		else if (kanbanBoard == null || kanbanBoardId != kanbanBoard.get_ID()) {
			kanbanBoard = new MKanbanBoard(Env.getCtx(), kanbanBoardId, null);
			statuses = null;
			processes = null;
			statusProcesses = null;
			boardParameters = null;
			cardProcesses= null;
			boardProcesses = null;
			isReadWrite = null;
			kanbanBoard.setBoardContent();
			getBoardParameters();
			kanbanBoard.getKanbanCards();
			kanbanBoard.setKanbanQueuedCards();
			summarySql = null;		
		}
	}

	public void refreshBoard() {
		setKanbanBoard(-1);
	}
	
	protected void refreshCards() {
		kanbanBoard.refreshCards();
	}

	public List<MKanbanStatus> getStatuses() {
		if (statuses == null) {
			setPrintableNames();
			statuses= kanbanBoard.getStatuses();
		}
		orderStatuses();
		return statuses;
	}
	
	public List<MKanbanParameter> getBoardParameters() {
		if (boardParameters == null) {
			boardParameters = kanbanBoard.getParameters();
			
			for (MKanbanParameter param : boardParameters)
				getGridField(param);
		}
		return boardParameters;
	}
	
	public List<MKanbanProcess> getProcesses() {
		if (processes == null) {
			processes = kanbanBoard.getAssociatedProcesses();
		}
		return processes;
	}

	public List<MKanbanProcess> getStatusProcesses() {
		if (statusProcesses == null)
			statusProcesses = new ArrayList<MKanbanProcess>();
		return statusProcesses;
	}

	public List<MKanbanProcess> getBoardProcesses() {
		if (boardProcesses == null)
			boardProcesses = new ArrayList<MKanbanProcess>();
		return boardProcesses;
	}

	public List<MKanbanProcess> getCardProcesses() {
		if (cardProcesses == null)
			cardProcesses = new ArrayList<MKanbanProcess>();
		return cardProcesses;
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

	public boolean swapCard(MKanbanStatus startStatus, MKanbanStatus endStatus, MKanbanCard card) {

		boolean statusChanged = card.changeStatus(kanbanBoard.getStatusColumn().getColumnName(), endStatus.getStatusValue());
		if (statusChanged) {
			startStatus.removeRecord(card);
			endStatus.addRecord(card);
			card.setBelongingStatus(endStatus);
		}
		return statusChanged;
	}

	public int getAd_Table_id() {
		return kanbanBoard.getAD_Table_ID();
	}

	public int getNumberOfStatuses() {
		if (statuses == null)
			return kanbanBoard.getNumberOfStatuses();
		else
			return statuses.size();
	}
	
	public int getNumberOfProcesses() {
		if (processes==null)
			return kanbanBoard.getNumberOfProcesses();
		else
			return processes.size();
	}

	public void setPrintableNames() {
		kanbanBoard.setPrintableNames();
	}

	public Object getPOObject(MTable table, int recordID, String trxName) {
		PO object = table.getPO(recordID, trxName);
		return object;
	}
	
	public int getStdColumnWidth() {
		return kanbanBoard.getKDB_StdColumnWidth();
	}
	
	public int getStdCardheight() {
		return kanbanBoard.getKDB_StdCardHeight();
	}
	
	
	public String getSummarySql() {
		if (summarySql == null && kanbanBoard.getKDB_SummarySQL() != null) {
			summarySql = kanbanBoard.getSummarySql();
		}
		return summarySql;		
	}		

	public Collection<KeyNamePair> getSaveKeys (String processType, int referenceID) {
		// clear result from prev time
    	Collection<KeyNamePair> saveKeys = new ArrayList <KeyNamePair>();
    	
    	if (processType.equals(CARD_PROCESS)) {
    		// Record-ID - Kanban Board -ID
    		saveKeys.add(new KeyNamePair(referenceID, Integer.toString(kanbanBoard.getKDB_KanbanBoard_ID())));
    	} else if(processType.equals(STATUS_PROCESS)) {
    		// - Status ID -- (Table Reference ID)
    		String statusValue = null;
    		if (kanbanBoard.getStatus(referenceID) != null)
    			statusValue = kanbanBoard.getStatus(referenceID).getStatusValue();
    		
    		saveKeys.add(new KeyNamePair(referenceID, statusValue));
    	} else if (processType.equals(BOARD_PROCESS)) {
    		//Kanban Board ID - Table ID
    		saveKeys.add (new KeyNamePair(kanbanBoard.getKDB_KanbanBoard_ID(), Integer.toString(getAd_Table_id())));
    	} else
    		return null;
    	
    	return saveKeys;
	}
	
	public boolean isHTML() {
		return kanbanBoard.get_ValueAsBoolean("IsHtml");
	}
	
	protected GridField getGridField(MKanbanParameter parameter) {

		if (parameter.getGridField() == null) {
			
			String sql;
			if (!Env.isBaseLanguage(Env.getCtx(), kanbanBoard.getTable().getTableName())){
				sql = "SELECT * FROM AD_Field_vt WHERE AD_Column_ID=? AND AD_Table_ID=?"
						+ " AND AD_Language='" + Env.getAD_Language(Env.getCtx()) + "'";
			}
			else{
				sql = "SELECT * FROM AD_Field_v WHERE AD_Column_ID=? AND AD_Table_ID=?";
			}

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, parameter.getKDB_ColumnTable_ID());
				pstmt.setInt(2, kanbanBoard.getAD_Table_ID());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					GridFieldVO voF = GridFieldVO.create(Env.getCtx(), 
							windowNo, 0, 
							rs.getInt("ad_window_id"), rs.getInt("ad_tab_id"), 
							false, rs);
					GridField gridField = new GridField(voF);
					parameter.setGridField(gridField);
				}
			} catch (Exception e) {
				CLogger.get().log(Level.SEVERE, "", e);
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}
		
		return parameter.getGridField();
	}
}
