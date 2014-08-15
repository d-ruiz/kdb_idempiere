package org.kanbanboard.model;

import java.math.BigDecimal;

import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.print.MPrintColor;
import org.compiere.util.Env;



public class MKanbanCard{
	

	private int recordId;
	private MKanbanStatus belongingStatus;
	private BigDecimal priorityValue;


	public BigDecimal getPriorityValue() {
		return priorityValue;
	}


	public void setPriorityValue(BigDecimal priorityValue) {
		this.priorityValue = priorityValue;
	}


	public MKanbanStatus getBelongingStatus() {
		return belongingStatus;
	}


	public void setBelongingStatus(MKanbanStatus belongingStatus) {
		this.belongingStatus = belongingStatus;
	}
	
	public String getContent(){//parsear que se retorna
		return Integer.toString(recordId);
	}

	public int getRecordID() {
		return recordId;
	}
	
	public void setRecordId(int name) {
		this.recordId = name;
	}


	public MKanbanCard(int cardRecord){
		recordId = cardRecord;
	}

	public boolean changeStatus(MTable table, String statusColumn){
		PO object = table.getPO(recordId, null);
		
		//System.out.println(object.get_Value("documentno")+currentStatus);//replace with the values people wants
		//Verificar el workflow permitido, verificar el estado y sacar mensaje de confirmacion
		boolean a = object.set_ValueOfColumnReturningBoolean(statusColumn, belongingStatus.getStatusValue());
		object.saveEx();
		return a;
	}


	public String getColor() {
		String color = null;
		MKanbanBoard kanbanBoard = belongingStatus.getKanbanBoard();
		
		if(kanbanBoard.hasPriorityOrder()&&kanbanBoard.getPriorityRules().size()>0){
			for(MKanbanPriority priorityRule:kanbanBoard.getPriorityRules()){
				BigDecimal minValue = new BigDecimal(priorityRule.getMinValue());
				BigDecimal maxValue = new BigDecimal(priorityRule.getMaxValue());

				if(priorityValue.compareTo(minValue)==1&&priorityValue.compareTo(maxValue)==-1){
					MPrintColor priorityColor = new MPrintColor(Env.getCtx(), priorityRule.getKDB_PriorityColor_ID(), null);
					color = priorityColor.getName();
					//Validation code
				}
			} 
		}
		return color;
	}

}
