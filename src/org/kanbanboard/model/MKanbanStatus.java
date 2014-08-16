package org.kanbanboard.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;


public class MKanbanStatus extends X_KDB_KanbanStatus{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5456585742385586400L;
	private MKanbanBoard kanbanBoard;
	private String printableName;
	private List<MKanbanCard> records = new ArrayList<MKanbanCard>();
	private int cardNumber = 0;

	public MKanbanBoard getKanbanBoard() {
		return kanbanBoard;
	}

	public List<MKanbanCard> getRecords() {
		return records;
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

	public void setPrintableName (String printingName){
		this.printableName = printingName;
	}

	public String getPrintableName(){
		//Si el alias esta completo tomar ese valor
		return printableName;
	}

	public MKanbanStatus(Properties ctx, int KDB_KanbanStatuses_ID,
			String trxName) {
		super(ctx, KDB_KanbanStatuses_ID, trxName);
		kanbanBoard = new MKanbanBoard(ctx, getKDB_KanbanBoard_ID(), trxName);
		// TODO Auto-generated constructor stub
	}

	public void addRecord(MKanbanCard card){
		records.add(card);
	}

	public void removeRecord(MKanbanCard card){
		for(MKanbanCard c:records){
			if(c.equals(card))
				records.remove(card);
			break;
		}
	}

	public boolean hasMoreCards(){
		if(!hasCards()||cardNumber>records.size()-1)
			return false;
		return true;
	}
	
	public void orderCards(){
		Collections.sort(records, Collections.reverseOrder(new Comparator<MKanbanCard>() {
			@Override
			public int compare(MKanbanCard card1, MKanbanCard card2) {
				return card1.getPriorityValue().intValue()-(card2.getPriorityValue().intValue());
			}
		}));
	}

	public MKanbanCard getCard(){
		MKanbanCard card = records.get(cardNumber);
		cardNumber++;
		return card;
	}

	public boolean hasCards(){
		if(records.isEmpty())
			return false;
		else
			return true;
	}

	public String getStatusValue(){
		String statusValue;
		if(kanbanBoard.isRefList())
			statusValue =  getKDB_StatusListValue();
		else
			statusValue = getKDB_StatusTableID();
		return statusValue;
	}

}
