package org.kanbanboard.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.compiere.model.MBPartner;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.print.MPrintColor;
import org.compiere.process.DocAction;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;



public class MKanbanCard{
	
	
	public static String KDB_ErrorMessage = "KDB_InvalidTransition";
	
	private int 		  recordId;
	private MKanbanBoard  kanbanBoard;
	private MKanbanStatus belongingStatus;
	private BigDecimal 	  priorityValue;
	
	private MBPartner	m_bpartner = null;
	private PO			m_po = null;
	private String 		kanbanCardText = null;


	public BigDecimal getPriorityValue() {
		return priorityValue;
	}
	
	public MBPartner getM_bpartner() {
		return m_bpartner;
	}

	public void setM_bpartner(MBPartner m_bpartner) {
		this.m_bpartner = m_bpartner;
	}

	public PO getM_po() {
		return m_po;
	}

	public void setM_po(PO m_po) {
		this.m_po = m_po;
	}

	public void setKanbanCardText(String kanbanCardText) {
		this.kanbanCardText = kanbanCardText;
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

	public int getRecordID() {
		return recordId;
	}
	
	public void setRecordId(int name) {
		this.recordId = name;
	}


	public MKanbanCard(int cardRecord){
		recordId = cardRecord;
	}
	
	public MKanbanCard(int cardRecord, MKanbanStatus status){
		recordId = cardRecord;
		belongingStatus=status;
		kanbanBoard=belongingStatus.getKanbanBoard();
		MTable table = (MTable)kanbanBoard.getAD_Table();
		m_po = table.getPO(recordId, null);
	}

	public boolean changeStatus(String statusColumn, String newStatusValue){
		
		if(m_po==null)
			return false;
		boolean success=true;
		
		if(statusColumn.equals(MKanbanBoard.STATUSCOLUMN_DocStatus)){
			if(m_po instanceof DocAction && m_po.get_ColumnIndex("DocAction") >= 0){
				String p_docAction = newStatusValue;
				m_po.set_ValueOfColumn("DocAction", p_docAction);
				try {
					if (!((DocAction) m_po).processIt(p_docAction))
					{
						throw new IllegalStateException();
					}
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					KDB_ErrorMessage = "KDB_InvalidTransition";
					return false;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}			
		}
		else{
			if(m_po.get_ColumnIndex("DocAction") >= 0){
				if(((DocAction) m_po).getDocStatus().equals(DocAction.STATUS_Completed)||
						((DocAction) m_po).getDocStatus().equals(DocAction.STATUS_Voided)||
						((DocAction) m_po).getDocStatus().equals(DocAction.STATUS_Reversed)||
						((DocAction) m_po).getDocStatus().equals(DocAction.STATUS_Closed)){
					KDB_ErrorMessage = "KDB_CompletedCard";
					return false;
				}
			}
			success = m_po.set_ValueOfColumnReturningBoolean(statusColumn, newStatusValue);
			m_po.saveEx();
		}
		return success;
	}


	public String getColor() {
		String color = null;
		
		if(kanbanBoard.hasPriorityOrder()&&kanbanBoard.getPriorityRules().size()>0){
			for(MKanbanPriority priorityRule:kanbanBoard.getPriorityRules()){
				BigDecimal minValue = new BigDecimal(priorityRule.getMinValue());
				BigDecimal maxValue = new BigDecimal(priorityRule.getMaxValue());

				if(priorityValue.compareTo(minValue)==1&&priorityValue.compareTo(maxValue)==-1){
					MPrintColor priorityColor = new MPrintColor(Env.getCtx(), priorityRule.getKDB_PriorityColor_ID(), null);
					color = priorityColor.getName();
				}
			} 
		}
		return color;
	}
	
	public String getKanbanCardText(){
		if(kanbanCardText==null)
			translate();
		return parse(kanbanCardText);
	}
	
	/**
	 * 	Translate to BPartner Language
	 */
	private void translate()
	{
		//	Default if no Translation
		if(kanbanBoard.getKDB_KanbanCard()!=null)
			kanbanCardText=kanbanBoard.getKDB_KanbanCard();
		else
			kanbanCardText=Integer.toString(recordId);
		/*if ((m_bpartner != null && m_bpartner.getAD_Language() != null) || !Util.isEmpty(m_language))
		{
			String adLanguage = m_bpartner != null ? m_bpartner.getAD_Language() : m_language;
			StringBuilder key = new StringBuilder().append(adLanguage).append(get_ID());
			MMailTextTrl trl = s_cacheTrl.get(key.toString());
			if (trl == null)
			{
				trl = getTranslation(adLanguage);
				if (trl != null)
					s_cacheTrl.put(key.toString(), trl);
			}
			if (trl != null)
			{
				m_MailHeader = trl.MailHeader;
				m_MailText = trl.MailText;
				m_MailText2 = trl.MailText2;
				m_MailText3 = trl.MailText3;
			}
		}*/

	}	//	translate
	
	private String parse (String text)
	{
		if (text.indexOf('@') == -1)
			return text;
		//	Parse PO
		text = parse (text, m_po);
		return text;
	}	//	parse
	
	/**
	 * 	Parse text
	 *	@param text text
	 *	@param po object
	 *	@return parsed text
	 */
	private String parse (String text, PO po)
	{
		if (po == null || text.indexOf('@') == -1)
			return text;
		
		String inStr = text;
		String token;
		StringBuilder outStr = new StringBuilder();

		int i = inStr.indexOf('@');
		while (i != -1)
		{
			outStr.append(inStr.substring(0, i));			// up to @
			inStr = inStr.substring(i+1, inStr.length());	// from first @

			int j = inStr.indexOf('@');						// next @
			if (j < 0)										// no second tag
			{
				inStr = "@" + inStr;
				break;
			}

			token = inStr.substring(0, j);
			outStr.append(parseVariable(token, po));		// replace context

			inStr = inStr.substring(j+1, inStr.length());	// from second @
			i = inStr.indexOf('@');
			outStr.append(System.getProperty("line.separator"));
		}

		outStr.append(inStr);				//	add remainder
		return outStr.toString();
	}	//	parse
	
	/**
	 * 	Parse Variable
	 *	@param variable variable
	 *	@param po po
	 *	@return translated variable or if not found the original tag
	 */
	private String parseVariable (String variable, PO po)
	{
		int index = po.get_ColumnIndex(variable);
		if (index == -1){
			int i = variable.indexOf('.');
			if(i!=1)
			{
				StringBuilder outStr = new StringBuilder();
				outStr.append(variable.substring(0, i));
				variable = variable.substring(i+1, variable.length());
				
				MTable table = MTable.get(Env.getCtx(), outStr.toString());
				outStr.append("_ID");
				index = po.get_ColumnIndex(outStr.toString());
				Integer subRecordId;
				if (index != -1){
					subRecordId = (Integer)po.get_Value(outStr.toString());
					if(subRecordId==null)
						return "";
					PO subPo = table.getPO(subRecordId, null);						
					return parseVariable(variable,subPo);
				}
			}
			
			StringBuilder msgreturn = new StringBuilder("@").append(variable).append("@");
			return msgreturn.toString();	//	keep for next
		}	
		//
		MColumn col = MColumn.get(Env.getCtx(), po.get_TableName(), variable);
		Object value = null;
		if (col != null && col.isSecure()) {
			value = "********";
		} else if (col.getAD_Reference_ID() == DisplayType.Date || col.getAD_Reference_ID() == DisplayType.DateTime || col.getAD_Reference_ID() == DisplayType.Time) {
			SimpleDateFormat sdf = DisplayType.getDateFormat(col.getAD_Reference_ID());
			value = sdf.format (po.get_Value(index));	
		} else if (col.getAD_Reference_ID() == DisplayType.YesNo) {
			if (po.get_ValueAsBoolean(variable))
				value = Msg.getMsg(Env.getCtx(), "Yes");
			else
				value = Msg.getMsg(Env.getCtx(), "No");
		} else {
			value = po.get_Value(index);
		}
		if (value == null)
			return "";
		return value.toString();
	}	//	parseVariable
	
}
