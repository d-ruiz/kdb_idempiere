package org.kanbanboard.model;

import java.math.BigDecimal;

import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.print.MPrintColor;
import org.compiere.process.DocAction;
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

	public boolean changeStatus(MTable table, String statusColumn, String newStatusValue){
		
		PO object = table.getPO(recordId, null);
		boolean success=true;
		
		//Verificar el workflow permitido, verificar el estado y sacar mensaje de confirmacion
		if(statusColumn.equals(MKanbanBoard.STATUSCOLUMN_DocStatus)){
			if(object instanceof DocAction && object.get_ColumnIndex("DocAction") >= 0){
				String p_docAction = newStatusValue;
				//StringBuilder processMsg = new StringBuilder().append(object.getDocumentNo());  
				object.set_ValueOfColumn("DocAction", p_docAction);
				try {
					if (!((DocAction) object).processIt(p_docAction))
					{
						throw new IllegalStateException("Failed when processing document - " + object.get_ID());
					    /*processMsg.append(" (NOT Processed)");
					    StringBuilder msglog = new StringBuilder("Cash Processing failed: ").append(cash).append(" - ").append(cash.getProcessMsg());
					    log.warning(msglog.toString());
					    msglog = new StringBuilder("Cash Processing failed: ").append(cash).append(" - ")
								.append(cash.getProcessMsg())
								.append(" / please complete it manually");
					    addLog(cash.getC_Cash_ID(), cash.getStatementDate(), null,msglog.toString());
					    throw new  IllegalStateException("Cash Processing failed: " + cash + " - " + cash.getProcessMsg());
					*/}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}			
		}
		else{
			if(object.get_ColumnIndex("DocAction") >= 0){
				if(((DocAction) object).getDocStatus().equals(DocAction.STATUS_Completed)||
						((DocAction) object).getDocStatus().equals(DocAction.STATUS_Voided)||
						((DocAction) object).getDocStatus().equals(DocAction.STATUS_Reversed)||
						((DocAction) object).getDocStatus().equals(DocAction.STATUS_Closed)){
					return false;
				}
			}
			success = object.set_ValueOfColumnReturningBoolean(statusColumn, newStatusValue);
			object.saveEx();
		}
		return success;
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
