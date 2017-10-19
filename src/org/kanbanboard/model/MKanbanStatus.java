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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.DB;
import org.compiere.util.Env;


public class MKanbanStatus extends X_KDB_KanbanStatus{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3464371316345451989L;

	private MKanbanBoard      kanbanBoard;
	private String            printableName;
	private List<MKanbanCard> records          = new ArrayList<MKanbanCard>();
	private List<MKanbanCard> queuedRecords    = new ArrayList<MKanbanCard>();
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
	}

	public void removeRecord(MKanbanCard card) {
		for (MKanbanCard c : records){
			if (c.equals(card)) {
				records.remove(card);
				break;
			}
		}
	}
	
	public void addQueuedRecord(MKanbanCard card) {
		queuedRecords.add(card);
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
		if (kanbanBoard.getOrderByClause() == null)
		{
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
		if (records.isEmpty())
			return false;
		else
			return true;
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
		if(getRecords().size() > getMaxNumCards())
			isExceed = true;
		return isExceed;
	}

	public void setExceed(boolean isExceed) {
		this.isExceed = isExceed;
	}

	public boolean hasQueue() {
		if(getSQLStatement() != null)
			return true;
		else
			return false;
	}

	public int getTotalCards() {
		return totalCards;
	}

	public void setTotalCards(int totalCards) {
		this.totalCards = totalCards;
	}

	public boolean hasMoreQueuedCards() {
		if(!hasQueue() || queuedCardNumber>queuedRecords.size()-1)
			return false;
		return true;
	}

	public MKanbanCard getQueuedCard() {
		MKanbanCard card = queuedRecords.get(queuedCardNumber);
		queuedCardNumber++;
		return card;
	}
	
	public String getSummary(String summarySql, int numberOfColumns) {		
		
		String result = null;

		if (summarySql != null) {		
			int j = summarySql.indexOf("@KanbanStatus@");		
			if (j > -1) {
				summarySql = summarySql.replaceAll("@KanbanStatus@", "'" + getStatusValue() + "'");		
			}
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				String sqlparsed = Env.parseContext(getCtx(), 0, summarySql, false);
				pstmt = DB.prepareStatement(sqlparsed, get_TrxName());
				pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
				rs = pstmt.executeQuery();
				StringBuilder resultQuery = new StringBuilder();

				if (rs.next()) {
					int column = 1;
					String value;
					while (column <= numberOfColumns)
					{
						value = rs.getString(column);
						if (value != null) {
							resultQuery.append(value);
							resultQuery.append(" /");
						}
						column++;
					}
					result = resultQuery.toString();
					if (result.length() > 0 && result.charAt(result.length()-1) == '/')
						result = result.substring(0, result.length()-1);
				}
			}
			catch (SQLException e){
				log.log(Level.SEVERE, summarySql, e);
			}
			finally{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}
		return result;
	} //getSummary

}
