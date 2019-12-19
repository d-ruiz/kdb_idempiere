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
import java.text.ChoiceFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.DBException;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.util.DB;
import org.compiere.util.Env;


public class MKanbanStatus extends X_KDB_KanbanStatus {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3464371316345451989L;
	public static final String QUEUE_CARDS_BY_NUMBER = "C";
	public static final String STATUS_RECORDS_IDS = "@STATUSRECORDS_ID@";

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
		if (getRecords().size() > getMaxNumCards())
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

		if (summarySql != null) {
			//Replace @KanbanStatus@ with the proper value
			int j = summarySql.indexOf("@KanbanStatus@");
			if (j > -1) {
				summarySql = summarySql.replaceAll("@KanbanStatus@", "'" + getStatusValue() + "'");		
			}
			
			//Replace @KanbanStatus@ with the proper value
			j = summarySql.indexOf(STATUS_RECORDS_IDS);
			if (j > -1) {
				summarySql = summarySql.replaceAll(STATUS_RECORDS_IDS, getStatusRecordsID());		
			}
			
			//Parse context variables if existing
			if (summarySql.indexOf("@") >= 0) {
				summarySql = Env.parseContext(Env.getCtx(), 0, summarySql, false, false);
				if (summarySql.length() == 0) {
					return null;
				}
			}
			
			MessageFormat mf = null;
			String msgValue = kanbanBoard.get_Translation(MKanbanBoard.COLUMNNAME_KDB_SummaryMsg);
			try {
				mf = new MessageFormat(msgValue, Env.getLanguage(getCtx()).getLocale());
			} catch (Exception e) {
				log.log(Level.SEVERE, msgValue, e);
			}
			
			if (mf == null)
				return null;

			Format[] fmts = mf.getFormatsByArgumentIndex();
			Object[] arguments = new Object[fmts.length];
			boolean filled = false;

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(summarySql, get_TrxName());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					for (int idx = 0; idx < fmts.length; idx++) {
						Format fmt = fmts[idx];
						Object obj;
						if (fmt instanceof DecimalFormat || fmt instanceof ChoiceFormat) {
							obj = rs.getDouble(idx+1);
						} else if (fmt instanceof SimpleDateFormat) {
							obj = rs.getTimestamp(idx+1);
						} else {
							obj = rs.getString(idx+1);
						}
						arguments[idx] = obj;
					}
					filled = true;
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, summarySql, e);
			} finally{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
			if (filled)
				return mf.format(arguments);
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

}
