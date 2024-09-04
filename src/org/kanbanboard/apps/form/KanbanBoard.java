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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MRole;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.kanbanboard.model.KanbanSwimlane;
import org.kanbanboard.model.MKanbanBoard;
import org.kanbanboard.model.MKanbanCard;
import org.kanbanboard.model.MKanbanParameter;
import org.kanbanboard.model.MKanbanStatus;
import org.kanbanboard.model.MKanbanSwimlaneConfiguration;

public class KanbanBoard {

	public static CLogger log = CLogger.getCLogger(KanbanBoard.class);
	
	protected final static String PROCESS_TYPE = KanbanBoardProcessController.PROCESS_TYPE; 
	protected final static String CARD_PROCESS = KanbanBoardProcessController.CARD_PROCESS;
	protected final static String STATUS_PROCESS = KanbanBoardProcessController.STATUS_PROCESS;
	protected final static String BOARD_PROCESS = KanbanBoardProcessController.BOARD_PROCESS;
	
	private final static String DEFAULT_SWIMLANE_CSS = "border: 1px solid;";

	private MKanbanBoard        kanbanBoard = null;
	private List<MKanbanStatus> statuses    = null;
	private MKanbanStatus       activeStatus;
	private String              isReadWrite = null;
	private boolean             canRoleUpdate = false;
	private boolean             roleAccessChecked = false;
	private String              summarySql = null;		
	
	private KanbanBoardProcessController processController;
	private KanbanBoardPriorityController priorityController;
	
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
	
	public boolean isReadOnly() {
		if (!roleAccessChecked) {
			canRoleUpdate = MRole.getDefault(Env.getCtx(), false).isColumnAccess(getAd_Table_id(), kanbanBoard.getStatusColumn().getAD_Column_ID(), false);
			roleAccessChecked = true;
		}
		return !(canRoleUpdate && isReadWrite());
	}

	private boolean isReadWrite() {
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
		if (kanbanBoardId == -1) {
			kanbanBoard = null;
			processController = null;
			priorityController = null;
		} else if (kanbanBoard == null || kanbanBoardId != kanbanBoard.get_ID()) {
			kanbanBoard = new MKanbanBoard(Env.getCtx(), kanbanBoardId, null);
			processController = new KanbanBoardProcessController(kanbanBoard);
			priorityController = new KanbanBoardPriorityController(kanbanBoard);

			statuses = null;
			boardParameters = null;
			isReadWrite = null;
			roleAccessChecked = false;
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
	
	public List<MKanbanSwimlaneConfiguration> getSwimlaneConfigurationRecords() {
		return kanbanBoard.getSwimlaneConfigurationRecords();
	}
	
	protected boolean currentboardUsesSwimlane() {
		return kanbanBoard.usesSwimlane();
	}
	
	protected boolean paintSwimlanes() {
		return currentboardUsesSwimlane() && getActiveSwimlane() != null && !getSwimlanes().isEmpty();
	}
	
	public List<MKanbanParameter> getBoardParameters() {
		if (boardParameters == null) {
			boardParameters = kanbanBoard.getParameters();
			
			for (MKanbanParameter param : boardParameters)
				param.setGridField(windowNo);
		}
		return boardParameters;
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

		boolean statusChanged = card.changeStatus(kanbanBoard.getStatusColumnName(), endStatus.getStatusValue());
		if (statusChanged) {
			startStatus.removeRecord(card);
			endStatus.addRecord(card);
			card.setBelongingStatus(endStatus);
		}
		return statusChanged;
	}
	
	protected boolean swapSwimlanes(MKanbanCard card, String newSwimlaneValue) {
		boolean success = card.changeStatus(getActiveSwimlane().getColumnName(), newSwimlaneValue);
		if (success) {
			card.setSwimlaneValue(newSwimlaneValue);
		}
		return success;
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

	public boolean isHTML() {
		return kanbanBoard.get_ValueAsBoolean("IsHtml");
	}
	
	protected void selectSwimlane(Object value) {
		kanbanBoard.setActiveSwimlaneRecord(value);
	}
	
	protected MKanbanSwimlaneConfiguration getActiveSwimlane() {
		return kanbanBoard.getActiveSwimlaneRecord();
	}
	
	protected List<KanbanSwimlane> getSwimlanes() {
		return kanbanBoard.getSwimlanes();
	}
	
	protected String getSwimlaneCSS() {
		String cssStyle = getActiveSwimlane().getInlineStyle() != null ? getActiveSwimlane().getInlineStyle() : DEFAULT_SWIMLANE_CSS;
		return cssStyle + "cursor: pointer;";
	}
	
	protected String getCardColorCSS(String color) {
		return "background: linear-gradient(to left, transparent 5%, transparent 93%, " + color + " 1%);";
	}
	
	protected String getCellCSSStyle(MKanbanCard card) {
		String colorCSS = card.getCardColor() != null ? getCardColorCSS(card.getCardColor()) : "";
		return "text-align: left;" + "border-style: outset; " + colorCSS;
	}
	
	protected boolean kanbanHasProcesses() {
		return processController.kanbanHasProcesses();
	}
	
	protected void resetAndPopulateArrays() {
		processController.resetAndPopulateArrays();
	}
	
	protected boolean kanbanHasStatusProcess() {
		return processController.kanbanHasStatusProcess();
	}
	
	protected boolean kanbanHasCardProcess() {
		return processController.kanbanHasCardProcess();
	}
	
	protected boolean kanbanHasBoardProcess() {
		return processController.kanbanHasBoardProcess();
	}
	
	protected List<ProcessUIElement> getStatusProcessElements() {
		return processController.getStatusProcessElements();
	}
	
	protected List<ProcessUIElement> getCardProcessElements() {
		return processController.getCardProcessElements();
	}
	
	protected List<ProcessUIElement> getBoardProcessElements() {
		return processController.getBoardProcessElements();
	}
	
	protected Collection<KeyNamePair> getSaveKeys (String processType, int referenceID) {
		return processController.getSaveKeys(processType, referenceID);
	}
	
	protected String completeAllCardsInStatus(int referenceID) {
		MKanbanStatus startStatus = kanbanBoard.getStatus(referenceID);
		if (startStatus != null) {
			MKanbanStatus endStatus = getCompleteDocActionStatus();
			Iterator<MKanbanCard> it = startStatus.getRecords().iterator();

			while (it.hasNext()) {
				MKanbanCard card = it.next();
				boolean cardCompleted = completeNextCard(card, endStatus);
				if (cardCompleted) 
					it.remove();
				else 
					return Msg.parseTranslation(Env.getCtx(), card.getStatusChangeMessage()) + System.lineSeparator() +  card.getKanbanCardText();
			}
		}
		return "OK";
	}
	
	private MKanbanStatus getCompleteDocActionStatus() {
		MKanbanStatus endStatus = kanbanBoard.getStatus("CO");
		if (endStatus == null)
			throw new AdempiereException(Msg.getMsg(Env.getLanguage(Env.getCtx()),"KDB_MissingComplete"));

		return endStatus;
	}
	
	private boolean completeNextCard(MKanbanCard card, MKanbanStatus completeStatus) {
		boolean cardCompleted = card.changeStatus(kanbanBoard.getStatusColumnName(), completeStatus.getStatusValue());
		if (cardCompleted) {
			completeStatus.addRecord(card);
			card.setBelongingStatus(completeStatus);
		}
		return cardCompleted;
	}
	
	protected boolean isPriorityColumn() {
		return kanbanBoard.isPriorityColumn();
	}
	
	protected boolean isMoveCardProcess(int AD_Process_ID) {
		return priorityController.isMoveCardProcess(AD_Process_ID);
	}
	
	protected void moveCard(int AD_Process_ID, int referenceID) {
		priorityController.moveCard(AD_Process_ID, referenceID);
	}
}
