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
 * - Diego Ruiz -                                                      *
 **********************************************************************/
package org.kanbanboard.apps.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.kanbanboard.model.MKanbanBoard;
import org.kanbanboard.model.MKanbanProcess;

public class KanbanBoardProcessController {
	
	public final static int COMPLETE_ALL_ID = -123456789; 
	
	protected final static String PROCESS_TYPE = "processType"; 
	protected final static String CARD_PROCESS = "cardProcess";
	protected final static String STATUS_PROCESS = "statusProcess";
	protected final static String BOARD_PROCESS = "boardProcess";
	
	private MKanbanBoard kanbanBoard;
	
	//Associated process arrays
	private List<MKanbanProcess> processes  = null;
	private List<MKanbanProcess> statusProcesses = new ArrayList<MKanbanProcess>();
	private List<MKanbanProcess> boardProcesses  = new ArrayList<MKanbanProcess>();
	private List<MKanbanProcess> cardProcesses  = new ArrayList<MKanbanProcess>();
	
	public KanbanBoardProcessController(MKanbanBoard kanbanBoard) {
		this.kanbanBoard = kanbanBoard;
		this.processes = kanbanBoard.getAssociatedProcesses();
	}
	
	public void resetAndPopulateArrays() {
		clearProcessArrays();
		fillProcessArrays();
	}
	
	/**
	 * Clear process arrays to avoid duplicates when refreshing
	 */
	private void clearProcessArrays() {
		statusProcesses.clear();
		boardProcesses.clear();
		cardProcesses.clear();
	}
	
	/**
	 * Fill the arrays 
	 * Status, board and card processes
	 */
	private void fillProcessArrays() {
		for (MKanbanProcess process: processes) {
			if (MKanbanProcess.KDB_PROCESSSCOPE_Status.equals(process.getKDB_ProcessScope()))
				statusProcesses.add(process);
			else if (MKanbanProcess.KDB_PROCESSSCOPE_Board.equals(process.getKDB_ProcessScope()))
				boardProcesses.add(process);
			else if (MKanbanProcess.KDB_PROCESSSCOPE_Card.equals(process.getKDB_ProcessScope()))
				cardProcesses.add(process);
		}
	}
	
	private List<MKanbanProcess> getProcesses() {
		return processes;
	}
	
	public boolean kanbanHasProcesses() {
		return (getNumberOfProcesses() > 0  && getProcesses() != null) || kanbanBoard.isDocActionKanbanBoard() || kanbanBoard.isPriorityColumn();
	}
	
	public boolean kanbanHasStatusProcess() {
		return !statusProcesses.isEmpty() || kanbanBoard.isDocActionKanbanBoard();
	}
	
	public boolean kanbanHasCardProcess() {
		return !cardProcesses.isEmpty() || kanbanBoard.isPriorityColumn();
	}
	
	public boolean kanbanHasBoardProcess() {
		return !boardProcesses.isEmpty();
	}

	private int getNumberOfProcesses() {
		return processes.size();
	}
	
	public List<ProcessUIElement> getStatusProcessElements() {
		List<ProcessUIElement> processElements = getProcessElements(statusProcesses);
		if (kanbanBoard.isDocActionKanbanBoard()) {
			ProcessUIElement element = getProcessUIElement(Msg.getMsg(Env.getLanguage(Env.getCtx()), "KDB_CompleteAllProcessName"), COMPLETE_ALL_ID);
			processElements.add(element);
		}
			
		return processElements;
	}
	
	public List<ProcessUIElement> getCardProcessElements() {
		List<ProcessUIElement> processElements = getProcessElements(cardProcesses);

		ProcessUIElement element = getProcessUIElement(Msg.getMsg(Env.getLanguage(Env.getCtx()), "KDB_MoveTop"), KanbanBoardPriorityController.MOVE_TOP_ID);
		processElements.add(element);

		element = getProcessUIElement(Msg.getMsg(Env.getLanguage(Env.getCtx()), "KDB_MoveUp"), KanbanBoardPriorityController.MOVE_UP_ID);
		processElements.add(element);
		
		element = getProcessUIElement(Msg.getMsg(Env.getLanguage(Env.getCtx()), "KDB_MoveDown"), KanbanBoardPriorityController.MOVE_DOWN_ID);
		processElements.add(element);
		
		element = getProcessUIElement(Msg.getMsg(Env.getLanguage(Env.getCtx()), "KDB_MoveBottom"), KanbanBoardPriorityController.MOVE_BOTTOM_ID);
		processElements.add(element);

		return processElements;
	}
	
	public List<ProcessUIElement> getBoardProcessElements() {
		return getProcessElements(boardProcesses);
	}
	
	private List<ProcessUIElement> getProcessElements(List<MKanbanProcess> kanbanProcesses) {
		List<ProcessUIElement> processElements = new ArrayList<ProcessUIElement>();
		ProcessUIElement element;
		for (MKanbanProcess process : kanbanProcesses) {
			element = getProcessUIElement(process.getProcessName(), process.getKDB_KanbanProcess_ID(), process.getAD_Process_ID());
			processElements.add(element);
		}
		return processElements;
	}
	
	private ProcessUIElement getProcessUIElement(String name, int AD_Process_ID) {
		return getProcessUIElement(name, AD_Process_ID, AD_Process_ID);
	}
	
	private ProcessUIElement getProcessUIElement(String name, int elementID, int AD_Process_ID) {
		ProcessUIElement element = new ProcessUIElement();
		element.setElementID(elementID);
		element.setName(name);
		element.setAD_Process_ID(AD_Process_ID);
		
		return element;
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
    		saveKeys.add (new KeyNamePair(kanbanBoard.getKDB_KanbanBoard_ID(), Integer.toString(kanbanBoard.getAD_Table_ID())));
    	} else
    		return null;
    	
    	return saveKeys;
	}
}
