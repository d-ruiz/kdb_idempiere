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

package org.idempiere.webui.apps.form;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.theme.ThemeManager;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.idempiere.apps.form.KanbanBoard;
import org.kanbanboard.model.MKanbanCard;
import org.kanbanboard.model.MKanbanStatus;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vlayout;

/**
 *
 * @author Diego Ruiz
 *
 */

public class WKanbanBoard extends KanbanBoard implements IFormController, EventListener<Event>{

	private CustomForm kForm = new CustomForm();;	

	private Borderlayout	mainLayout	= new Borderlayout();

	private Panel panel = new Panel();
	private Grid gridLayout = GridFactory.newGridLayout();
	private Label lProcess = new Label();
	private Listbox cbProcess = ListboxFactory.newDropdownListbox();
	private int kanbanBoardId=-1;
	private Button bRefresh = new Button();

	Map<Cell, MKanbanCard> mapCellColumn = new HashMap<Cell, MKanbanCard>();
	Map<Cell, MKanbanStatus> mapEmptyCellField = new HashMap<Cell, MKanbanStatus>();

	Grid kanbanPanel;
	Vlayout centerVLayout;

	public WKanbanBoard() {
		super();
		initForm();
	}

	public void initForm() {

		try
		{
			dynList();
			jbInit();
			LayoutUtils.sendDeferLayoutEvent(mainLayout, 100);
		}
		catch (Exception ex)
		{
		}
	}

	/**
	 * 	Static init
	 *	@throws Exception
	 */
	private void jbInit() throws Exception
	{
		kForm.setSizable(true);
		kForm.setClosable(true);
		kForm.setMaximizable(true);
		kForm.setWidth("95%");
		kForm.setHeight("95%");
		kForm.appendChild (mainLayout);
		LayoutUtils.addSclass("kanban-board-form-content", mainLayout);  // ?? debe definirse en un css, se puede integrar css en el plugin?
		kForm.setBorder("normal");

		//North Panel
		panel.appendChild(gridLayout);
		lProcess.setText(Msg.translate(Env.getCtx(), "Process"));
		Rows rows = gridLayout.newRows();
		Row row = rows.newRow();
		bRefresh.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		bRefresh.setTooltiptext(Msg.getMsg(Env.getCtx(), "Refresh"));
		bRefresh.addEventListener(Events.ON_CLICK, this);
		Hbox hbox = new Hbox();
		hbox.appendChild(lProcess.rightAlign());
		hbox.appendChild(cbProcess);
		hbox.appendChild(bRefresh);
		Cell cell = new Cell();
		cell.setColspan(3);
		cell.setRowspan(1);
		cell.setAlign("left");
		cell.appendChild(hbox);
		row.appendChild(cell);

		North north = new North();
		north.setSize("5%");
		LayoutUtils.addSclass("tab-editor-form-north-panel", north);
		mainLayout.appendChild(north);
		north.appendChild(panel);


		//CenterPanel
		createKanbanBoardPanel();
		centerVLayout = new Vlayout();
		centerVLayout.setHeight("100%");
		centerVLayout.appendChild(kanbanPanel);
		centerVLayout.setStyle("overflow:auto");

		South south = new South();
		LayoutUtils.addSclass("tab-editor-form-center-panel", south);
		south.setSize("95%");
		south.appendChild(centerVLayout);

		mainLayout.appendChild(south);
	}	//	jbInit


	/**
	 *  Initialize List of existing processes
	 */
	private void dynList()
	{
		//	Fill Process
		for(KeyNamePair process: getProcessList())
			cbProcess.addItem(process);

		cbProcess.addEventListener(Events.ON_SELECT, this);

	}   //  dynList

	/**
	 * Create the panel where the kanban board
	 * is going to be painted
	 * @throws SQLException 
	 */
	public void createKanbanBoardPanel(){
		mapCellColumn.clear();
		mapEmptyCellField.clear();
		kanbanPanel = new Grid();

		if(kanbanBoardId!=-1){

			setKanbanBoard(kanbanBoardId);
			kanbanPanel.makeNoStrip();
			kanbanPanel.setVflex(true);
			kanbanPanel.setSizedByContent(true);
			kanbanPanel.setSpan("true");

			int numCols=0;
			numCols = getNumberOfStatuses();

			if(numCols>0){
				// set size in percentage per column leaving a MARGIN on right
				Columns columns = new Columns();
				columns.setMenupopup("auto");

				int equalWidth = 100 ;
				Auxhead auxhead = new Auxhead();

				//Create columns based on the states of the kanban board
				Column  column;
				Auxheader auxheader;
				for(MKanbanStatus status: getStatuses()){
					if(status.hasQueue()){
						column = new Column();
						column.setWidth(equalWidth/2 + "%");
						columns.appendChild(column);
						column.setAlign("right");
						column.setLabel(status.getPrintableName().substring(0, 1)+" Queue");
						column.setStyle("background-color: yellow;");
						columns.appendChild(column);
						if( status.getKanbanBoard().getKDB_SummarySQL() != null ){
							auxheader = new Auxheader();
							auxhead.appendChild(auxheader);
						}
					}
					column = new Column();
					column.setWidth(equalWidth + "%");
					columns.appendChild(column);
					column.setAlign("center");
					columns.appendChild(column);
					if(status.getTotalCards()!=0)
						column.setLabel(status.getPrintableName()+"("+status.getTotalCards()+")");
					else
						column.setLabel(status.getPrintableName());
					if(status.isExceed())
						column.setStyle("background-color: red;");
					if( getSummarySql() != null ){
						column.setStyle("background-color: #d9e3ec");
						auxheader = new Auxheader();
						auxheader.setLabel(status.getSummary(getSummarySql(),getSummaryCounter()));
						auxheader.setTooltiptext(status.getSummary(getSummarySql(),getSummaryCounter()));
						auxhead.appendChild(auxheader);
					}
				}
				columns.setSizable(true);
				createRows();	
				kanbanPanel.appendChild(columns);
				kanbanPanel.appendChild(auxhead);
			}

			if (numCols <= 0) {
				Messagebox.show(Msg.getMsg(Env.getCtx(), "KDB_NoStatuses"));
			}
		}
	}//createKanbanBoardPanel

	public void createRows(){
		mapCellColumn.clear();
		mapEmptyCellField.clear();
		Rows rows = kanbanPanel.newRows();
		Row row = new Row();
		resetStatusProperties();
		int numberOfCards = getNumberOfCards();
		while(numberOfCards>0){
			for(MKanbanStatus status: getStatuses()){
				row.setStyle("background-color:" + getBackgroundColor() + ";");
				if(!status.hasMoreCards()){
					if(status.hasQueue()){
						createEmptyCell(row,status);
					}
					createEmptyCell(row,status);
				}
				else{
					if(status.hasQueue()){
						if(!status.hasMoreQueuedCards()){
							createEmptyCell(row,status);
							createCardCell(row,status);
							numberOfCards--;
						}else{
							MKanbanCard queuedCard = status.getQueuedCard();
							Vlayout l = createCell(queuedCard);
							row.appendCellChild(l);
							if(!isReadWrite())
								setOnlyReadCellProps(row.getLastCell(), queuedCard);
							else
								setQueuedCellProps(row.getLastCell(), queuedCard);
							numberOfCards--;
							if(status.hasMoreStatusCards()){
								createCardCell(row,status);
								numberOfCards--;
							}else{
								createEmptyCell(row,status);
							}
						}
					}else{
						createCardCell(row,status);
						numberOfCards--;	
					}
				}
			}
			rows.appendChild(row);
			row=new Row();
		}
	}//createRows

	private void createEmptyCell(Row row, MKanbanStatus status){
		row.appendCellChild(createSpacer());
		setEmptyCellProps(row.getLastCell(),status);	
	}

	private void createCardCell(Row row, MKanbanStatus status){
		MKanbanCard card = status.getCard();
		Vlayout l = createCell(card);
		row.appendCellChild(l);
		if(isReadWrite())
			setCellProps(row.getLastCell(), card);
		else
			setOnlyReadCellProps(row.getLastCell(), card);
	}

	private Vlayout createCell(MKanbanCard card){
		Vlayout div = new Vlayout();
		String[] tokens = card.getKanbanCardText().split(System.getProperty("line.separator"));
		for(String token:tokens){
			Label label = new Label(token);
			div.appendChild(label);
		}
		if(!card.isQueued())
			div.setStyle("cursor:hand;cursor:pointer; text-align: left; background-color:" + card.getColor() + ";");
		else
			div.setStyle("text-align: left; background-color:" + card.getColor() + ";");
		return div;
	}//CreateCell

	private void setCellProps(Cell cell, MKanbanCard card) {
		cell.setDraggable("true");
		cell.setDroppable("true");
		cell.addEventListener(Events.ON_DROP, this);
		cell.addEventListener(Events.ON_CLICK, this);
		cell.addEventListener(Events.ON_DOUBLE_CLICK, this);
		cell.setStyle("text-align: left;");
		cell.setStyle("border-style: outset; ");
		mapCellColumn.put(cell, card);
	}

	private void setQueuedCellProps(Cell cell, MKanbanCard card) {
		cell.addEventListener(Events.ON_DOUBLE_CLICK, this);
		cell.setStyle("text-align: left;");
		cell.setStyle("border-style: outset; ");
		mapCellColumn.put(cell, card);
	}

	private void setOnlyReadCellProps(Cell cell, MKanbanCard card) {
		cell.addEventListener(Events.ON_CLICK, this);
		cell.addEventListener(Events.ON_DOUBLE_CLICK, this);
		cell.setStyle("text-align: left;");
		cell.setStyle("border-style: outset; ");
		mapCellColumn.put(cell, card);
	}


	private void setEmptyCellProps(Cell lastCell, MKanbanStatus status) {
		lastCell.setDroppable("true");
		lastCell.addEventListener(Events.ON_DROP, this);
		mapEmptyCellField.put(lastCell, status);
	}

	/**************************************************************************
	 *  Action Listener
	 *  @param e event
	 */
	public void onEvent(Event e){

		// select an item within the list -- set it active and show the properties
		if (Events.ON_SELECT.equals(e.getName()) && e.getTarget() instanceof Listbox) {
			if (cbProcess.getSelectedIndex() != -1) {

				KeyNamePair MKanban = null;
				kanbanBoardId = -1;
				MKanban = (KeyNamePair)cbProcess.getSelectedItem().toKeyNamePair();	
				if (MKanban != null)
					kanbanBoardId = MKanban.getKey();
				repaintGrid();
			}
		}
		// Check event ONDoubleCLICK on a cell Navigate into documents
		else if (Events.ON_DOUBLE_CLICK.equals(e.getName()) && (e.getTarget() instanceof Cell)) {
			MKanbanCard card = mapCellColumn.get(e.getTarget());
			int recordId = card.getRecordID();
			int ad_table_id = getAd_Table_id();
			zoom(recordId,ad_table_id);
		}

		else if (e instanceof DropEvent ) {
			DropEvent me = (DropEvent) e;
			Cell startItem = null;

			if (me.getDragged() instanceof Cell) {
				startItem = (Cell) me.getDragged();
			} 

			Cell endItem = null;
			if (me.getTarget() instanceof Cell) {
				endItem = (Cell) me.getTarget();

				MKanbanCard startField = mapCellColumn.get(startItem);
				MKanbanStatus startStatus = startField.getBelongingStatus(); 
				MKanbanCard endField = mapCellColumn.get(endItem);
				MKanbanStatus endStatus;

				if (endField == null) {
					// check empty cells
					endStatus= mapEmptyCellField.get(me.getTarget());
				}

				else
					endStatus = endField.getBelongingStatus();

				if(!swapCard(startStatus, endStatus, startField))
					Messagebox.show(Msg.getMsg(Env.getCtx(), MKanbanCard.KDB_ErrorMessage));
				else{
					refreshBoard();
					repaintGrid();
				}
			}
		}else if (Events.ON_CLICK.equals(e.getName()) && e.getTarget() instanceof Button) {
			if(kanbanBoardId!=-1){
				refreshBoard();
				repaintGrid();
			}
		}//OnCLICK
	}//onEvent

	private void zoom(int recordId, int ad_table_id) {
		AEnv.zoom(ad_table_id, recordId);
	}

	private Component createSpacer() {
		return new Space();
	}

	public ADForm getForm()
	{
		return kForm;
	}

	/*private void repaintRows(){
		if (kanbanPanel.getRows() != null)
			kanbanPanel.removeChild(kanbanPanel.getRows());
		createRows();
	}*/

	private void repaintGrid(){
		centerVLayout.removeChild(kanbanPanel);
		if (kanbanPanel.getRows() != null)
			kanbanPanel.removeChild(kanbanPanel.getRows());
		createKanbanBoardPanel();
		centerVLayout.appendChild(kanbanPanel);
	}

}
