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
import java.util.List;

import org.kanbanboard.model.MKanbanProcess;

public class KanbanBoardProcessController {
	
	//Associated process arrays
	private List<MKanbanProcess> processes  = null;
	private List<MKanbanProcess> statusProcesses = new ArrayList<MKanbanProcess>();
	private List<MKanbanProcess> boardProcesses  = new ArrayList<MKanbanProcess>();
	private List<MKanbanProcess> cardProcesses  = new ArrayList<MKanbanProcess>();
	
	public KanbanBoardProcessController(List<MKanbanProcess> processes) {
		this.processes = processes;
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
		return getNumberOfProcesses() > 0  && getProcesses() != null;
	}
	
	public boolean kanbanHasStatusProcess() {
		return !statusProcesses.isEmpty();
	}
	
	public boolean kanbanHasCardProcess() {
		return !cardProcesses.isEmpty();
	}
	
	public boolean kanbanHasBoardProcess() {
		return !boardProcesses.isEmpty();
	}

	private int getNumberOfProcesses() {
		return processes.size();
	}
	
	public List<ProcessUIElement> getStatusProcessElements() {
		return getProcessElements(statusProcesses);
	}
	
	public List<ProcessUIElement> getCardProcessElements() {
		return getProcessElements(cardProcesses);
	}
	
	public List<ProcessUIElement> getBoardProcessElements() {
		return getProcessElements(boardProcesses);
	}
	
	private List<ProcessUIElement> getProcessElements(List<MKanbanProcess> kanbanProcesses) {
		List<ProcessUIElement> processElements = new ArrayList<ProcessUIElement>();
		ProcessUIElement element;
		for (MKanbanProcess process : kanbanProcesses) {
			element = new ProcessUIElement();
			element.setElementID(process.getKDB_KanbanProcess_ID());
			element.setName(process.getProcessName());
			element.setAD_Process_ID(process.getAD_Process_ID());

			processElements.add(element);
		}
		return processElements;
	}
}
