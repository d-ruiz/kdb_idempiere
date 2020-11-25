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

package org.kanbanboard.webui.apps.form;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Group;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListHead;
import org.adempiere.webui.component.ListHeader;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.session.SessionManager;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.kanbanboard.apps.form.KanbanStatus;
import org.kanbanboard.model.MKanbanStatus;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Columns;
import org.zkoss.zul.East;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.Separator;

/**
 *
 * @author Diego Ruiz
 *
 */

public class WKanbanStatus extends KanbanStatus implements IFormController, EventListener<Event>, ValueChangeListener {

	private CustomForm kForm = new CustomForm();;	

	private Borderlayout	mainLayout	= new Borderlayout();


	private Panel panel = new Panel();
	private Grid gridLayout = GridFactory.newGridLayout();

	WEditor editorName = null;
	WEditor editorMaxCards = null;
	WEditor editorAlias = null;
	WEditor editorSeqNo = null;
	WEditor editorSqlQueue = null;
	WEditor editorShowOver = null;
	private ConfirmPanel confirmPanel = new ConfirmPanel(true);

	private Label lProcess = new Label();
	private Listbox cbProcess = ListboxFactory.newDropdownListbox();
	private int kanbanBoardId=-1;

	// The grid components
	Group currentGroup;
	ArrayList<Row> rowList;

	Map<Integer, MKanbanStatus> mapCellColumn = new HashMap<Integer, MKanbanStatus>();
	Map<Cell, Integer> mapEmptyCellField = new HashMap<Cell, Integer>();

	Listbox kanbanPanel;
	Hlayout centerHLayout;

	public WKanbanStatus() {
		super();
		initForm();
	}

	public void initForm() {

		try {
			dynList();
			jbInit();
		} catch (Exception ex) {
			log.log(Level.SEVERE, "init", ex);
		}
	}

	/**
	 * 	Static init
	 *	@throws Exception
	 */
	private void jbInit() throws Exception {
		kForm.setSizable(true);
		kForm.setClosable(true);
		kForm.setMaximizable(true);
		kForm.setWidth("100%");
		kForm.setHeight("100%");
		kForm.appendChild (mainLayout);
		kForm.setBorder("normal");


		confirmPanel.addActionListener(Events.ON_CLICK, this);
		Button deleteBtn = confirmPanel.createButton(ConfirmPanel.A_DELETE);
		confirmPanel.addButton(deleteBtn);
		deleteBtn.setDroppable("true");
		deleteBtn.addEventListener(Events.ON_CLICK, this);
		deleteBtn.addEventListener(Events.ON_DROP, this);


		//North Panel - comboBox and Buttons
		panel.appendChild(gridLayout);
		lProcess.setText(Msg.translate(Env.getCtx(), "Process"));
		Rows rows = gridLayout.newRows();
		Row row = rows.newRow();
		row.appendChild(lProcess.rightAlign());
		row.appendChild(cbProcess);

		North north = new North();
		mainLayout.appendChild(north);
		north.appendChild(panel);

		//East - Properties Grid
		Grid propGrid = createPropertiesGrid();	
		East east = new East();
		east.setSplittable(true);
		east.setCollapsible(true);
		east.setSize("29%");
		mainLayout.appendChild(east);
		east.appendChild(propGrid);

		//CenterPanel - Status Grid Panel
		createKanbanBoardPanel();
		centerHLayout = new Hlayout();
		centerHLayout.setHeight("100%");
		centerHLayout.setWidth("100%");
		centerHLayout.appendChild(kanbanPanel);
		centerHLayout.setStyle("overflow:auto");

		Center center = new Center();
		mainLayout.appendChild(center);
		center.appendChild(centerHLayout);
	}	//	jbInit


	/**
	 *  Initialize List of existing processes
	 */
	private void dynList() {
		//		Fill Process
		for (KeyNamePair process : getProcessList())
			cbProcess.addItem(process);

		cbProcess.addEventListener(Events.ON_SELECT, this);

	}   //  dynList

	private Grid createPropertiesGrid() {
		Grid gridView = GridFactory.newGridLayout();
		//
		Columns columns = new Columns();
		gridView.appendChild(columns);
		//
		Column  column = new Column();
		columns.appendChild(column);
		column.setHflex("min");
		column.setAlign("right");

		column = new Column();
		columns.appendChild(column);
		column.setHflex("1");
		Rows rows = new Rows();
		gridView.appendChild(rows);

		Row row = null;

		row = new Row();
		Group group = new Group(Msg.getMsg(Env.getCtx(), "Property"));
		Cell cell = (Cell) group.getFirstChild();
		cell.setSclass("z-group-inner");
		cell.setColspan(2);
		group.setOpen(true);
		rows.appendChild(group);

		row = new Row();
		Label labelName =  new Label(Msg.getElement(Env.getCtx(), MKanbanStatus.COLUMNNAME_Name));
		editorName = new WStringEditor(MKanbanStatus.COLUMNNAME_Name, false, false, true, 0, 0, null, null);
		((WStringEditor) editorName).getComponent().setHflex("1");
		row.appendChild(labelName.rightAlign());
		row.appendChild(editorName.getComponent());
		editorName.addValueChangeListener(this);
		row.setGroup(group);
		rows.appendChild(row);

		row = new Row();
		Label labelAlias =  new Label(Msg.getElement(Env.getCtx(),MKanbanStatus.COLUMNNAME_StatusAlias));
		editorAlias = new WStringEditor(MKanbanStatus.COLUMNNAME_StatusAlias, false, false, true, 0, 0, null, null);
		((WStringEditor) editorName).getComponent().setHflex("1");
		row.appendChild(labelAlias.rightAlign());
		row.appendChild(editorAlias.getComponent());
		editorAlias.addValueChangeListener(this);
		row.setGroup(group);
		rows.appendChild(row);

		row = new Row();
		Label labelSeqNo =  new Label(Msg.getElement(Env.getCtx(), MKanbanStatus.COLUMNNAME_SeqNo));
		editorSeqNo = new WNumberEditor(MKanbanStatus.COLUMNNAME_SeqNo, false, false, true, DisplayType.Integer, labelSeqNo.getValue());
		row.appendChild(labelSeqNo.rightAlign());
		row.appendChild(editorSeqNo.getComponent());
		editorSeqNo.addValueChangeListener(this);
		row.setGroup(group);
		rows.appendChild(row);

		row = new Row();
		Label labelSqlQueue =  new Label(Msg.getElement(Env.getCtx(), MKanbanStatus.COLUMNNAME_SQLStatement));
		editorSqlQueue = new WStringEditor(MKanbanStatus.COLUMNNAME_SQLStatement, false, false, true, 0, 0, null, null);
		((WStringEditor) editorSqlQueue).getComponent().setHflex("1");
		row.appendChild(labelSqlQueue.rightAlign());
		row.appendChild(editorSqlQueue.getComponent());
		editorSqlQueue.addValueChangeListener(this);
		row.setGroup(group);
		rows.appendChild(row);

		row = new Row();
		Label labelMaxCards =  new Label(Msg.getElement(Env.getCtx(), MKanbanStatus.COLUMNNAME_MaxNumberCards));
		editorMaxCards = new WNumberEditor(MKanbanStatus.COLUMNNAME_MaxNumberCards, false, false, true, DisplayType.Integer, labelMaxCards.getValue());
		row.appendChild(labelMaxCards.rightAlign());
		row.appendChild(editorMaxCards.getComponent());
		editorMaxCards.addValueChangeListener(this);
		row.setGroup(group);
		rows.appendChild(row);

		row = new Row();
		Label labelIsShowOver =  new Label(Msg.getElement(Env.getCtx(), MKanbanStatus.COLUMNNAME_IsShowOver));
		editorShowOver = new WYesNoEditor(MKanbanStatus.COLUMNNAME_IsShowOver, "", labelIsShowOver.getValue(), false, false, true);
		row.appendChild(labelIsShowOver.rightAlign());
		row.appendChild(editorShowOver.getComponent());
		editorShowOver.addValueChangeListener(this);
		row.setGroup(group);
		rows.appendChild(row);

		row = new Row();
		Separator esep = new Separator("horizontal");
		esep.setSpacing("10px");
		row.appendCellChild(esep, 2);
		row.setGroup(group);
		rows.appendChild(row);

		row = new Row();
		row.appendCellChild(confirmPanel, 2);
		row.setGroup(group);
		rows.appendChild(row);

		return gridView;
	}

	/**
	 * Create the panel where the list of statuses
	 * is going to be painted
	 * @throws SQLException 
	 */
	public void createKanbanBoardPanel() {
		mapCellColumn.clear();
		mapEmptyCellField.clear();
		kanbanPanel = new Listbox();

		if (kanbanBoardId != -1) {
			setKanbanBoard(kanbanBoardId);
			currentGroup = null;
			rowList = null;

			int numCols=0;
			numCols = getNumberOfStatuses();

			if (numCols <= 0) {
				log.warning("No statuses pre configured");
			}

			// set size in percentage per column leaving a MARGIN on right
			ListHead columns = new ListHead();

			int equalWidth=0;
			if (numCols != 0)
				equalWidth = 98 / numCols;

			/*
			 * Create columns based on the states of the kanban board
			 */
			ListHeader  column;

			for (MKanbanStatus status : getStatuses()) {
				column = new ListHeader();
				column.setWidth(equalWidth + "%");
				columns.appendChild(column);
				column.setHflex("min");
				column.setAlign("right");
				columns.appendChild(column);
				column.setLabel(status.getPrintableName());
				int columnId = column.getColumnIndex();
				setColumnProps(column, columnId, status);
			}

			kanbanPanel.appendChild(columns);
		}
	}

	private void setColumnProps(ListHeader column, int columnID, MKanbanStatus status) {
		column.setDraggable("true");
		column.setDroppable("true");
		column.addEventListener(Events.ON_DROP, this);
		column.addEventListener(Events.ON_CLICK, this);
		mapCellColumn.put(columnID, status);
	}

	/**************************************************************************
	 *  Action Listener
	 *  @param e event
	 */
	public void onEvent(Event e) {

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
		// Check event ONCLICK on a cell -- set it active and show the properties
		else if (Events.ON_CLICK.equals(e.getName()) && (e.getTarget() instanceof ListHeader)) {
			ListHeader column = (ListHeader) e.getTarget();
			int columnId=column.getColumnIndex();
			MKanbanStatus status = mapCellColumn.get(columnId);
			if (status != null) {
				setProperties(status);
				setActiveStatus(status);
			}
		}

		else if (e instanceof DropEvent ) {
			DropEvent me = (DropEvent) e;
			ListHeader startHeader = null;
			MKanbanStatus startStatus = null;
			if (me.getDragged() instanceof ListHeader) {
				startHeader = (ListHeader) me.getDragged();
				startStatus = mapCellColumn.get(startHeader.getColumnIndex());
			} 
			ListHeader endHeader = null;
			if (me.getTarget() instanceof ListHeader) {

				endHeader = (ListHeader) me.getTarget();
				MKanbanStatus endStatus = mapCellColumn.get(endHeader.getColumnIndex());

				swapStatuses(startStatus, endStatus);
				setProperties(startStatus); //update SeqNo

				repaintGrid();

			} else if (me.getTarget() instanceof Button) {
				Button button = (Button) me.getTarget();
				if(button.getId().equals(ConfirmPanel.A_DELETE)){
					if(!deleteStatus(startStatus))
						Messagebox.show(Msg.getMsg(Env.getCtx(), "AccessCannotDelete"));

					repaintGrid();
				}
			}
		} else if (e.getTarget().getId().equals("Cancel")) {
			SessionManager.getAppDesktop().closeActiveWindow();
		} //	OK - Save
		else if (e.getTarget().getId().equals("Ok")) {
			if (!saveStatuses())
				Messagebox.show(Msg.getMsg(Env.getCtx(), "AccessCannotDelete"));
		}

	}//Listeners


	private void setProperties(MKanbanStatus status) {
		editorName.setValue(status.getName());
		editorSeqNo.setValue(status.getSeqNo());
		editorAlias.setValue(status.getStatusAlias());
		editorShowOver.setValue(status.isShowOver());
		editorSqlQueue.setValue(status.getSQLStatement());
		editorMaxCards.setValue(status.getMaxNumberCards());

	} // Set Properties


	public ADForm getForm() {
		return kForm;
	}

	private void repaintGrid() {
		centerHLayout.removeChild(kanbanPanel);
		if (kanbanPanel.getListHead() != null)
			kanbanPanel.removeChild(kanbanPanel.getListHead());
		createKanbanBoardPanel();
		centerHLayout.appendChild(kanbanPanel);
	}

	@Override
	public void valueChange(ValueChangeEvent e) {
		// changed a value on the properties editors
		MKanbanStatus status = getActiveStatus();
		if (status != null) {
			String propertyName = e.getPropertyName();
			if (e.getNewValue() != null){
				if (MKanbanStatus.COLUMNNAME_Name.equals(propertyName)) {
					status.setName((String) e.getNewValue());
				} else if (MKanbanStatus.COLUMNNAME_SeqNo.equals(propertyName)) {
					status.setSeqNo((Integer) e.getNewValue());
				} else if (MKanbanStatus.COLUMNNAME_StatusAlias.equals(propertyName)) {
					status.setStatusAlias((String) e.getNewValue());
				} else if (MKanbanStatus.COLUMNNAME_IsShowOver.equals(propertyName)) {
					status.setIsShowOver((Boolean) e.getNewValue());
				}
				else if (MKanbanStatus.COLUMNNAME_SQLStatement.equals(propertyName)) {
					status.setSQLStatement((String) e.getNewValue());
				}
				else if (MKanbanStatus.COLUMNNAME_MaxNumberCards.equals(propertyName)) {
					status.setMaxNumberCards(new BigDecimal(String.valueOf((Integer) e.getNewValue())));
				}
			}
			setProperties(status);
			repaintGrid();
		}
	}

}
