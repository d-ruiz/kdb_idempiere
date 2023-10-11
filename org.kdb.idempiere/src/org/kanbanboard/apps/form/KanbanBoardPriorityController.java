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

import org.kanbanboard.model.MKanbanBoard;
import org.kanbanboard.model.MKanbanCard;
import org.kanbanboard.model.MKanbanStatus;

public class KanbanBoardPriorityController {
	
	public final static int MOVE_UP_ID = -234567890;
	public final static int MOVE_DOWN_ID = -234567891;
	public final static int MOVE_TOP_ID = -234567892;
	public final static int MOVE_BOTTOM_ID = -234567893;
	
	private MKanbanBoard kanbanBoard;
	
	public KanbanBoardPriorityController(MKanbanBoard kanbanBoard) {
		this.kanbanBoard = kanbanBoard;
	}
	
	public boolean isMoveCardProcess(int AD_Process_ID) {
		return AD_Process_ID == MOVE_UP_ID || MOVE_DOWN_ID == AD_Process_ID ||
				AD_Process_ID == MOVE_TOP_ID || MOVE_BOTTOM_ID == AD_Process_ID;
	}
	
	public void moveCard(int AD_Process_ID, int referenceID) {
		MKanbanCard card = getClickedCard(referenceID);
		if (card == null)
			return;

		switch (AD_Process_ID) {
		case MOVE_UP_ID:
			moveUp(card);
			break;
		case MOVE_DOWN_ID:
			moveDown(card);
			break;
		case MOVE_TOP_ID:
			moveToTheTop(card);
			break;
		case MOVE_BOTTOM_ID:
			moveToTheBottom(card);
		}
	}
	
	/**
	 * Switch the values of the priority column 
	 * with the previous card to move it up one unit of priority 
	 * @param clickedCard
	 */
	private void moveUp(MKanbanCard clickedCard) {
		MKanbanStatus status = clickedCard.getBelongingStatus();
		MKanbanCard previousCard = status.getPreviousCard(clickedCard);
		if (previousCard != null) {
			switchPrioritiyValues(clickedCard, previousCard);
		}
	}
	
	private void switchPrioritiyValues(MKanbanCard card1, MKanbanCard card2) {
		int card1Priority = card1.getDBPriorityValue();
		int card2Priority = card2.getDBPriorityValue();
		
		card1.savePriorityValue(card2Priority);
		card2.savePriorityValue(card1Priority);
	}
	
	/**
	 * Switch the values of the priority column 
	 * with the next card to move it up one unit of priority 
	 * @param clickedCard
	 */
	private void moveDown(MKanbanCard clickedCard) {
		MKanbanStatus status = clickedCard.getBelongingStatus();
		MKanbanCard nextCard = status.getNextCard(clickedCard);
		if (nextCard != null) {
			switchPrioritiyValues(clickedCard, nextCard);
		}
	}

	/**
	 * Switch the values of the priority column 
	 * until the clicked card is at the top 
	 * @param clickedCard
	 */
	private void moveToTheTop(MKanbanCard clickedCard) {
		MKanbanStatus status = clickedCard.getBelongingStatus();
		MKanbanCard previousCard = status.getPreviousCard(clickedCard);
		while (previousCard != null) {
			switchPrioritiyValues(clickedCard, previousCard);			
			previousCard = status.getPreviousCard(previousCard);
		}
	}
	
	/**
	 * Switch the values of the priority column 
	 * with the next card to move it up one unit of priority 
	 * @param clickedCard
	 */
	private void moveToTheBottom(MKanbanCard clickedCard) {
		MKanbanStatus status = clickedCard.getBelongingStatus();
		MKanbanCard nextCard = status.getNextCard(clickedCard);
		while (nextCard != null) {
			switchPrioritiyValues(clickedCard, nextCard);			
			nextCard = status.getNextCard(nextCard);
		}
	}
	
	private MKanbanCard getClickedCard(int referenceID) {
		for (MKanbanStatus status : kanbanBoard.getStatuses()) {
			MKanbanCard card = status.getCard(referenceID);
			if (card != null) {
				return card;
			}
		}
		return null;
	}

}
