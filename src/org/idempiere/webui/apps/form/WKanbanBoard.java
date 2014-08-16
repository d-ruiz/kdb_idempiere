package org.idempiere.webui.apps.form;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Group;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
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
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
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

public class WKanbanBoard extends KanbanBoard implements IFormController, EventListener<Event>, ValueChangeListener{
	
	private CustomForm kForm = new CustomForm();;	

	private Borderlayout	mainLayout	= new Borderlayout();

	/**	Window No			*/
	public int            	m_WindowNo = 0;


	//private ConfirmPanel confirmPanel = new ConfirmPanel(true);
	private Panel panel = new Panel();
	private Grid gridLayout = GridFactory.newGridLayout();
	
	private Label lProcess = new Label();
	private Listbox cbProcess = ListboxFactory.newDropdownListbox();
	private int kanbanBoardId=-1;

	
/*	private StatusBarPanel statusBar = new StatusBarPanel();
	private ValueNamePair m_AD_Table;
	private boolean m_imp;
	private ValueNamePair m_AD_Language;*/

	// The grid components
	Group currentGroup;
	ArrayList<Row> rowList;

	Map<Cell, MKanbanCard> mapCellColumn = new HashMap<Cell, MKanbanCard>();
	Map<Cell, MKanbanStatus> mapEmptyCellField = new HashMap<Cell, MKanbanStatus>();

	Grid kanbanPanel;
	Vlayout centerVLayout;
	//Vlayout westVLayout ;


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
		LayoutUtils.addSclass("kanban-boardr-form-content", mainLayout);
		kForm.setBorder("normal");
		
		//North Panel
		panel.appendChild(gridLayout);
		lProcess.setText(Msg.translate(Env.getCtx(), "Process"));
		Rows rows = gridLayout.newRows();
		Row row = rows.newRow();
		row.appendChild(lProcess.rightAlign());
		row.appendChild(cbProcess);
		
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
		//centerVLayout.setStyle("overflow:auto");
		
		/*Center center = new Center();
		LayoutUtils.addSclass("tab-editor-form-center-panel", center);
		//center.setSize("95%");
		mainLayout.appendChild(center);
		center.appendChild(centerVLayout);*/
		
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
		//		Fill Process
		ArrayList<KeyNamePair> processes = getProcessList();
		for(KeyNamePair process: processes)
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
			currentGroup = null;
			rowList = null;


			kanbanPanel.makeNoStrip();
			kanbanPanel.setHflex("1");
			kanbanPanel.setHeight(null);
			kanbanPanel.setVflex(false);
			kanbanPanel.setSizedByContent(true);
			//kanbanPanel.setHeight("100px");
			//kanbanPanel.setStyle("overflow:auto");

			int numCols=0;
			numCols = getNumberOfStatuses();

			if (numCols <= 0) {
				System.out.println("No statuses pre configured");
			}

			// set size in percentage per column leaving a MARGIN on right
			Columns columns = new Columns();
			int equalWidth = 98 / numCols;

			/*
			 * Create columns based on the states of the kanban board
			 */
			Column  column;
			for(MKanbanStatus status: getStatuses()){
				column = new Column();
				column.setWidth(equalWidth + "%");
				columns.appendChild(column);
				column.setHflex("min");
				column.setAlign("right");
				columns.appendChild(column);
				column.setLabel(status.getPrintableName());
			}
			createRows();	
			kanbanPanel.appendChild(columns);
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

				if(!status.hasMoreCards()){
					row.appendCellChild(createSpacer());
					setEmptyCellProps(row.getLastCell(),status);
				}
				else{
					MKanbanCard card = status.getCard();
					Label label = new Label(card.getContent());
					Div div = new Div();
					div.setStyle("text-align: center;");
					div.appendChild(label);
					div.setStyle("background-color:" + card.getColor() + ";");
					row.appendCellChild(div);
					setCellProps(row.getLastCell(), card);
					numberOfCards--;
				}
			}
			rows.appendChild(row);
			row=new Row();
		}
	}//createRows


	private void setCellProps(Cell cell, MKanbanCard card) {
		cell.setDraggable("true");
		cell.setDroppable("true");
		cell.addEventListener(Events.ON_DROP, this);
		cell.addEventListener(Events.ON_CLICK, this);
		cell.addEventListener(Events.ON_DOUBLE_CLICK, this);
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
					Messagebox.show("The card wasn't changed of status due to errors, check everything and try again", "Error", Messagebox.OK, Messagebox.ERROR);
					//Reemplazar con un mensaje traducible				
				repaintRows();
				
			} else if (me.getTarget() instanceof Button) {
				//Button button = (Button) me.getTarget();

			}
		}
	}//onEvent
	
	private void zoom(int recordId, int ad_table_id) {
		AEnv.zoom(ad_table_id, recordId);
		/*if (m_node != null) {
			if (MWFNode.ACTION_UserWindow.equals(m_node.getAction())) {
				AEnv.zoom(m_node.getAD_Window_ID(), null);
				
			} else if (MWFNode.ACTION_UserForm.equals(m_node.getAction())) {
				int AD_Form_ID = m_node.getAD_Form_ID();
				ADForm form = ADForm.openForm(AD_Form_ID);
				form.setAttribute(Window.MODE_KEY, form.getWindowMode());
				AEnv.showWindow(form);
			}
		}*/
	}

	private Component createSpacer() {
		return new Space();
	}

	/**
	 * 	Dispose
	 */
	public void dispose()
	{
		//SessionManager.getAppDesktop().closeActiveWindow();
	}	//	dispose


	public ADForm getForm()
	{
		return kForm;
	}

	private void repaintRows(){
		if (kanbanPanel.getRows() != null)
			kanbanPanel.removeChild(kanbanPanel.getRows());
		createRows();
	}

	private void repaintGrid(){
		centerVLayout.removeChild(kanbanPanel);
		if (kanbanPanel.getRows() != null)
			kanbanPanel.removeChild(kanbanPanel.getRows());
		createKanbanBoardPanel();
		centerVLayout.appendChild(kanbanPanel);
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
}
