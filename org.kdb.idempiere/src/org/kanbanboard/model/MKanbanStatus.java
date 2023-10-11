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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.DBException;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.kanbanboard.utils.KanbanSQLUtils;


public class MKanbanStatus extends X_KDB_KanbanStatus {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3464371316345451989L;
	public static final String QUEUE_CARDS_BY_NUMBER = "C";
	private static final String STATUS_SUMMARY_TOKEN = "@KanbanStatus@";

	private MKanbanBoard      kanbanBoard;
	private String            printableName;
	private List<MKanbanCard> records          = new ArrayList<MKanbanCard>();
	private List<MKanbanCard> queuedRecords    = new ArrayList<MKanbanCard>();
	private Map<KanbanSwimlane, List<MKanbanCard>> swimlaneCards = new HashMap<KanbanSwimlane, List<MKanbanCard>>();
	private Map<KanbanSwimlane, List<MKanbanCard>> queuedSwimlaneCards = new HashMap<KanbanSwimlane, List<MKanbanCard>>();
	private boolean           isExceed         = false;
	private int               maxNumCards      = 100;
	private int               cardNumber       = 0;
	private int 			  queuedCardNumber = 0;
	private int				  totalCards       = 0;

	public MKanbanBoard getKanbanBoard() {
		return kanbanBoard;
	}
	
	public void setKanbanBoard(MKanbanBoard kanbanBoard) {
		this.kanbanBoard = kanbanBoard;
	}

	/**
	 * Returns all non queued records
	 * @return
	 */
	public List<MKanbanCard> getRecords() {
		return records;
	}
	
	public List<MKanbanCard> getQueuedRecords() {
		return queuedRecords;
	}

	public void setRecords(List<MKanbanCard> records) {
		this.records = records;
	}

	public int getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(int cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	public int getQueuedCardNumber() {
		return queuedCardNumber;
	}

	public void setQueuedCardNumber(int queuedCardNumber) {
		this.queuedCardNumber = queuedCardNumber;
	}

	public void setPrintableName (String printingName) {
		this.printableName = printingName;
	}

	public String getPrintableName() {
		if (getStatusAlias() != null)
			return getStatusAlias();
		return printableName;
	}

	public MKanbanStatus(Properties ctx, int KDB_KanbanStatuses_ID, String trxName) {
		super(ctx, KDB_KanbanStatuses_ID, trxName);
		kanbanBoard = new MKanbanBoard(ctx, getKDB_KanbanBoard_ID(), trxName);
	}

	public MKanbanStatus(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		kanbanBoard = new MKanbanBoard(ctx, getKDB_KanbanBoard_ID(), trxName);
	}

	public void addRecord(MKanbanCard card) {
		records.add(card);
		increaseTotalCardsByOne();
	}

	public void removeRecord(MKanbanCard card) {
		for (MKanbanCard c : records){
			if (c.equals(card)) {
				records.remove(card);
				break;
			}
		}
	}
	
	public void clearCards() {
		records.clear();
		queuedRecords.clear();
		isExceed = false;
		cardNumber = 0;
		queuedCardNumber = 0;
		totalCards = 0;
	}
	
	public void addQueuedRecord(MKanbanCard card) {
		queuedRecords.add(card);
		increaseTotalCardsByOne();
	}

	public void removeQueuedRecord(MKanbanCard card) {
		for (MKanbanCard c : queuedRecords) {
			if (c.equals(card))
				queuedRecords.remove(card);
			break;
		}
	}

	public boolean hasMoreCards() {
		if ((!hasMoreStatusCards() && !hasQueue()) || totalCards <= cardNumber+queuedCardNumber)
			return false;
		return true;
	}
	
	public boolean hasMoreStatusCards() {
		if (!hasCards() || cardNumber>records.size()-1)
			return false;
		return true;
	}

	public void orderCards() {
		if (kanbanBoard.getOrderByClause() == null) {
			Collections.sort(records, Collections.reverseOrder(new Comparator<MKanbanCard>() {
				@Override
				public int compare(MKanbanCard card1, MKanbanCard card2) {
					return card1.getPriorityValue().intValue()-(card2.getPriorityValue().intValue());
				}
			}));
		}
	}

	public MKanbanCard getCard() {
		MKanbanCard card = records.get(cardNumber);
		cardNumber++;
		return card;
	}
	
	public MKanbanCard getCard(int recordId) {
		for (MKanbanCard card : records) {
			if (card.getRecordID() == recordId) {
				return card;
			}
		}
		return null;
	}

	public boolean hasCards() {
		return !records.isEmpty(); 
	}

	public String getStatusValue() {
		String statusValue;
		if (kanbanBoard.isRefList())
			statusValue =  getKDB_StatusListValue();
		else
			statusValue = getKDB_StatusTableID();
		return statusValue;
	}

	public int getMaxNumCards() {
		if (getMaxNumberCards().intValue() == -1) //This validates that the field is not empty
			return 0;
		if (getMaxNumberCards().intValue() != 0)
			return getMaxNumberCards().intValue();

		return maxNumCards;
	}

	public void setMaxNumCards(int maxNumCards) {
		this.maxNumCards = maxNumCards;
	}

	public boolean isExceed() {
		if (getRecords().size() > getMaxNumCards())
			isExceed = true;
		return isExceed;
	}

	public void setExceed(boolean isExceed) {
		this.isExceed = isExceed;
	}

	public boolean hasQueue() {
		return getSQLStatement() != null;
	}

	public int getTotalCards() {
		return totalCards;
	}

	public void setTotalCards(int totalCards) {
		this.totalCards = totalCards;
	}

	public boolean hasMoreQueuedCards() {
		if (!hasQueue() || queuedCardNumber > queuedRecords.size()-1)
			return false;
		return true;
	}

	public MKanbanCard getQueuedCard() {
		MKanbanCard card = queuedRecords.get(queuedCardNumber);
		queuedCardNumber++;
		return card;
	}
	
	public String getSummary() {

		String summarySql = kanbanBoard.getSummarySql();
		String msgValue = kanbanBoard.get_Translation(MKanbanBoard.COLUMNNAME_KDB_SummaryMsg);
		if (summarySql != null && getMaxNumCards() > 0) {
			summarySql = KanbanSQLUtils.replaceTokenWithValue(summarySql, STATUS_SUMMARY_TOKEN, "'" + getStatusValue() + "'");
			summarySql = KanbanSQLUtils.replaceTokenWithValue(summarySql, MKanbanBoard.RECORDS_IDS, getStatusRecordsID());
			return KanbanSQLUtils.getSummary(summarySql, msgValue);
		}
		return null;
	} //getSummary
	
	private String getStatusRecordsID() {
		String ids = "";

		if ((getRecords() != null && !getRecords().isEmpty()) ||
				(getQueuedRecords() != null && !getQueuedRecords().isEmpty())) {

			StringBuilder recordIds = new StringBuilder();

			for (MKanbanCard record : getRecords())
				recordIds.append("'" + record.getRecordID() + "',");

			for (MKanbanCard queuedRecord : getQueuedRecords())
				recordIds.append("'" + queuedRecord.getRecordID() + "',");

			ids = recordIds.toString();
			//Remove last comma
			if (ids.length() > 0 && ids.charAt(ids.length()-1) == ',')
				ids = ids.substring(0, ids.length()-1);
		}

		return ids;
	}
	
	public void setSQLQueuedCards() {
		if (hasQueue() && !QUEUE_CARDS_BY_NUMBER.equals(getSQLStatement())) {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");

			MTable table = kanbanBoard.getTable();
			MColumn column = kanbanBoard.getStatusColumn();
			
			String keys[] = table.getKeyColumns();
			sql.append(keys[0]); 
			sql.append(" FROM " + table.getTableName());

			StringBuilder whereClause = new StringBuilder();
			whereClause.append(" WHERE ");

			if (kanbanBoard.getWhereClause() != null)
				whereClause.append(kanbanBoard.getWhereClause() + " AND ");

			whereClause.append(column.getColumnName() + " = ");
			
			if (kanbanBoard.isRefList())
				whereClause.append("'" + getStatusValue() + "'");
			else
				whereClause.append(getStatusValue());

			whereClause.append(" AND AD_Client_ID IN (0, ?)");
			whereClause.append(" AND " + getSQLStatement());
			
			sql.append(whereClause.toString());
			log.info("Queue SQL" + sql.toString());

			try {
				String sqlparsed = Env.parseContext(getCtx(), 0, sql.toString(), false);
				int[] ids = DB.getIDsEx(get_TrxName(), sqlparsed, Env.getAD_Client_ID(Env.getCtx()));
				for (int id : ids) {
					MKanbanCard card = getCard(id);
					if (card != null) {
						removeRecord(card);
						addQueuedRecord(card);
						card.setQueued(true);
					}
				}
			} catch (DBException e) {
				log.log(Level.SEVERE, sql.toString(), e);
			}
		}
	} // setSQLQueuedCards
	
	public boolean isPutCardOnQueue() {
		return hasQueue() && getSQLStatement().equals(MKanbanStatus.QUEUE_CARDS_BY_NUMBER)    //Queued Records
				&& getMaxNumCards() <= getRecords().size();
	}
	
	public boolean isPutCardOnStatus() {
		return isShowOver() || getMaxNumCards() > getRecords().size();
	}
	
	public void increaseTotalCardsByOne() {
		setTotalCards(getTotalCards()+1);
	}
	
	public void configureSwimlanes(List<KanbanSwimlane> swimlanes) {
		for (KanbanSwimlane swimlane : swimlanes) {
			fillSwimlaneCards(swimlane);
			fillSwimlaneQueuedCards(swimlane);
		}
	}
	
	private void fillSwimlaneCards(KanbanSwimlane swimlane) {
		if (swimlaneCards.get(swimlane) == null) {
			swimlaneCards.put(swimlane, new ArrayList<MKanbanCard>());
		}
		for (MKanbanCard card : records) {
			if (swimlane.getValue().equals(card.getSwimlaneValue())) {
				swimlaneCards.get(swimlane).add(card);
				swimlane.addOneCard();
			}
		}
	}
	
	private void fillSwimlaneQueuedCards(KanbanSwimlane swimlane) {
		if (!hasQueue())
			return;

		if (queuedSwimlaneCards.get(swimlane) == null) {
			queuedSwimlaneCards.put(swimlane, new ArrayList<MKanbanCard>());
		}
		for (MKanbanCard card : queuedRecords) {
			if (swimlane.getValue().equals(card.getSwimlaneValue())) {
				queuedSwimlaneCards.get(swimlane).add(card);
				swimlane.addOneCard();
			}
		}
	}
	
	public boolean hasMoreCards(KanbanSwimlane swimlane) {
		return hasMoreStatusCards(swimlane) || hasMoreQueuedCards(swimlane);
	}
	
	public boolean hasMoreStatusCards(KanbanSwimlane swimlane) {
		return swimlaneCards.get(swimlane) != null && !swimlaneCards.get(swimlane).isEmpty();
	}
	
	public boolean hasMoreQueuedCards(KanbanSwimlane swimlane) {
		return queuedSwimlaneCards.get(swimlane) != null && !queuedSwimlaneCards.get(swimlane).isEmpty();
	}
	
	public MKanbanCard getCard(KanbanSwimlane swimlane) {
		Iterator<MKanbanCard> iter = swimlaneCards.get(swimlane).iterator();
	    while (iter.hasNext()) {
	    	MKanbanCard c = iter.next();
            iter.remove();
            return c; 
	    }
	    return null;
	}
	
	public MKanbanCard getQueuedCard(KanbanSwimlane swimlane) {
		Iterator<MKanbanCard> iter = queuedSwimlaneCards.get(swimlane).iterator();
	    while (iter.hasNext()) {
	    	MKanbanCard c = iter.next();
            iter.remove();
            return c; 
	    }
	    return null;
	}
	
	public List<MKanbanCard> getAllSwimlaneCards(KanbanSwimlane swimlane) {
		List<MKanbanCard> allCards = getCards(swimlane);
		allCards.addAll(getQueuedCards(swimlane));
		return allCards;
	}
	
	private List<MKanbanCard> getQueuedCards(KanbanSwimlane swimlane) {
	    return queuedSwimlaneCards.get(swimlane) != null ? queuedSwimlaneCards.get(swimlane) : Collections.emptyList();
	}
	
	private List<MKanbanCard> getCards(KanbanSwimlane swimlane) {
	    return swimlaneCards.get(swimlane) != null ? swimlaneCards.get(swimlane) : new ArrayList<MKanbanCard>();
	}

	/**
	 * @param card 
	 * @return previous card or null if the card is the first one in the array
	 */
	public MKanbanCard getPreviousCard(MKanbanCard card) {
		int clickedIndex = getRecords().indexOf(card);
		return clickedIndex > 0 ? getRecords().get(clickedIndex-1) : null;
	}
	
	/**
	 * @param card 
	 * @return next card or null if the card is the first one in the array
	 */
	public MKanbanCard getNextCard(MKanbanCard card) {
		int clickedIndex = getRecords().indexOf(card);
		return clickedIndex < getRecords().size() - 1 ? getRecords().get(clickedIndex+1) : null;
	}

}
