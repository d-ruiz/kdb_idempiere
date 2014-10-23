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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

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
	
	private PO			m_po = null;
	private String 		kanbanCardText = null;


	public BigDecimal getPriorityValue() {
		return priorityValue;
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
		m_po = kanbanBoard.getTable().getPO(recordId, null);
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
	/*				else
						m_po.saveEx();
		*/		} catch (IllegalStateException e) {
					KDB_ErrorMessage = "KDB_InvalidTransition";
					return false;
				} catch (Exception e) {
					e.printStackTrace();
					KDB_ErrorMessage = e.getLocalizedMessage();
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

				if(priorityValue.compareTo(minValue)>=0&&priorityValue.compareTo(maxValue)<=0){
					MPrintColor priorityColor = new MPrintColor(Env.getCtx(), priorityRule.getKDB_PriorityColor_ID(), null);
					color = priorityColor.getName();
					break;
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
			kanbanCardText=kanbanBoard.get_Translation(MKanbanBoard.COLUMNNAME_KDB_KanbanCard);
		else
			kanbanCardText=Integer.toString(recordId);
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
			if(i!=-1)
			{
				StringBuilder outStr = new StringBuilder();
				outStr.append(variable.substring(0, i));
				variable = variable.substring(i+1, variable.length());
				outStr.append("_ID"); //Foreign Key column

				index = po.get_ColumnIndex(outStr.toString());
				
				Integer subRecordId;

				if (index != -1){
					MColumn column = MColumn.get(Env.getCtx(), po.get_TableName(), po.get_ColumnName(index));
					MTable table = MTable.get(Env.getCtx(),column.getReferenceTableName());

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
