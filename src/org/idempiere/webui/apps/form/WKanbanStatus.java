package org.idempiere.webui.apps.form;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.adempiere.webui.LayoutUtils;
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
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.idempiere.apps.form.KanbanBoard;
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
import org.zkoss.zul.North;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vlayout;

/**
*
* @author Diego Ruiz
*
*/

public class WKanbanStatus extends KanbanBoard implements IFormController, EventListener<Event>, ValueChangeListener {

	private CustomForm kForm = new CustomForm();;	

	private Borderlayout	mainLayout	= new Borderlayout();

	/**	Window No			*/
	public int            	m_WindowNo = 0;


	//private ConfirmPanel confirmPanel = new ConfirmPanel(true);
	private Panel panel = new Panel();
	private Grid gridLayout = GridFactory.newGridLayout();
	
	WEditor editorName = null;
	WEditor editorMaxCards = null;
	WEditor editorAlias = null;
	WEditor editorSeqNo = null;
	WEditor editorSqlQueue = null;
	WEditor editorShowOver = null;
	private ConfirmPanel confirmPanel = new ConfirmPanel(true);




	/*private Button bExport = new Button();
	private Button bImport = new Button();
	private Button bExportZIP = new Button();
	private Button bImportZIP = new Button();*/
	
	private Label lProcess = new Label();
	private Listbox cbProcess = ListboxFactory.newDropdownListbox();
	private int kanbanBoardId=-1;

	
	/*private StatusBarPanel statusBar = new StatusBarPanel();
	private ValueNamePair m_AD_Table;
	private boolean m_imp;
	private ValueNamePair m_AD_Language;*/

	// The grid components
	Group currentGroup;
	ArrayList<Row> rowList;

	Map<Integer, MKanbanStatus> mapCellColumn = new HashMap<Integer, MKanbanStatus>();
	Map<Cell, Integer> mapEmptyCellField = new HashMap<Cell, Integer>();

	Grid form;
	Listbox kanbanPanel;
	Vlayout centerVLayout;
	Vlayout westVLayout ;

	public WKanbanStatus() {
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
			log.log(Level.SEVERE, "init", ex);
		}
		m_WindowNo = kForm.getWindowNo();

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
		
		
		confirmPanel.addActionListener(Events.ON_CLICK, this);
		Button deleteBtn = confirmPanel.createButton(ConfirmPanel.A_DELETE);
		confirmPanel.addButton(deleteBtn);
		deleteBtn.setDroppable("true");
		deleteBtn.addEventListener(Events.ON_CLICK, this);
		deleteBtn.addEventListener(Events.ON_DROP, this);
		
		Grid propGrid = createPropertiesGrid();	
		East east = new East();
		LayoutUtils.addSclass("tab-editor-form-east-panel", east);
		mainLayout.appendChild(east);
		east.appendChild(propGrid);
		east.setWidth("320px");
		
		//North Panel
		panel.appendChild(gridLayout);
		lProcess.setText(Msg.translate(Env.getCtx(), "Process"));
		Rows rows = gridLayout.newRows();
		Row row = rows.newRow();
		row.appendChild(lProcess.rightAlign());
		row.appendChild(cbProcess);
		
		North north = new North();
		LayoutUtils.addSclass("tab-editor-form-north-panel", north);
		mainLayout.appendChild(north);
		north.appendChild(panel);
		
		
		//CenterPanel
		createKanbanBoardPanel();
		centerVLayout = new Vlayout();
		centerVLayout.setHeight("100%");
		centerVLayout.appendChild(kanbanPanel);
		centerVLayout.setStyle("overflow:auto");
		
		Center center = new Center();
		LayoutUtils.addSclass("tab-editor-form-center-panel", center);
		mainLayout.appendChild(center);
		center.appendChild(centerVLayout);
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
	
	private Grid createPropertiesGrid()
	{
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
		editorSqlQueue = new WStringEditor(MKanbanStatus.COLUMNNAME_Name, false, false, true, 0, 0, null, null);
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
	 * Create the panel where the kanban board
	 * is going to be painted
	 * @throws SQLException 
	 */
	public void createKanbanBoardPanel(){
		mapCellColumn.clear();
		mapEmptyCellField.clear();
		kanbanPanel = new Listbox();

		if(kanbanBoardId!=-1){
			setKanbanBoard(kanbanBoardId);
			currentGroup = null;
			rowList = null;


			//form.makeNoStrip();
			kanbanPanel.setHflex("1");
			kanbanPanel.setHeight(null);
			kanbanPanel.setVflex(false);

			int numCols=0;
			numCols = getNumberOfStatuses();

			if (numCols <= 0) {
				System.out.println("No statuses pre configured");
			}

			// set size in percentage per column leaving a MARGIN on right
			ListHead columns = new ListHead();

			/*kanbanPanel.setSizedByContent(true);
			kanbanPanel.setHeight("100px");
			kanbanPanel.setStyle("overflow:auto");;*/
			int equalWidth = 98 / numCols;

			/*
			 * Create columns based on the states of the kanban board
			 */
			ListHeader  column;

			for(MKanbanStatus status: getStatuses()){
				System.out.println(status.getName()+ " "+status.getPrintableName()+" "+status.getSeqNo());
			}

			for(MKanbanStatus status: getStatuses()){
				//Group group = new Group(Msg.getMsg(Env.getCtx(), "Property"));
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
			
			System.out.println(mapCellColumn);

			kanbanPanel.appendChild(columns);
		}
	}
	
	private void setColumnProps(ListHeader column, int columnID, MKanbanStatus status) {
		column.setDraggable("true");
		column.setDroppable("true");
		column.addEventListener(Events.ON_DROP, this);
		column.addEventListener(Events.ON_CLICK, this);
		//column.addEventListener(Events.ON_DOUBLE_CLICK, this);
		mapCellColumn.put(columnID, status);
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
		// Check event ONCLICK on a cell -- set it active and show the properties
		else if (Events.ON_CLICK.equals(e.getName()) && (e.getTarget() instanceof ListHeader)) {
			ListHeader column = (ListHeader) e.getTarget();
			int columnId=column.getColumnIndex();
			MKanbanStatus status = mapCellColumn.get(columnId);
			System.out.println("Click "+ columnId);
			if(status!=null){
				setProperties(status);
			}
		}

		else if (e instanceof DropEvent ) {
			System.out.println("Drop Enter");
			DropEvent me = (DropEvent) e;
			ListHeader startHeader = null;
			if (me.getDragged() instanceof ListHeader) {
				System.out.println("Drop");
				startHeader = (ListHeader) me.getDragged();
			} 
			ListHeader endHeader = null;
			if (me.getTarget() instanceof ListHeader) {
				endHeader = (ListHeader) me.getTarget();

				MKanbanStatus startStatus = mapCellColumn.get(startHeader.getColumnIndex());
				MKanbanStatus endStatus = mapCellColumn.get(endHeader.getColumnIndex());

				swapStatuses(startStatus, endStatus);
				setProperties(startStatus); //update SeqNo
				repaintGrid();

				System.out.println("Se atrapa "+startStatus.getName()+" se suelta"+endStatus.getName());

			} else if (me.getTarget() instanceof Button) {
				Button button = (Button) me.getTarget();
				if(button.getId().equals(ConfirmPanel.A_DELETE))
					System.out.println("Borrar ");
			}
		}

		else if (e.getTarget().getId().equals("Cancel"))
		{
			kForm.dispose();
			//SessionManager.getAppDesktop().closeWindow(m_WindowNo);
			//SessionManager.getAppDesktop().closeActiveWindow();
			/*kForm.onClose();
					kForm*/
		}

		//	OK - Save
		else if (e.getTarget().getId().equals("Ok"))
		{
			if (saveStatuses())
				kForm.detach();
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

	

	private void repaintGrid(){
		centerVLayout.removeChild(kanbanPanel);
		if (kanbanPanel.getListHead() != null)
			kanbanPanel.removeChild(kanbanPanel.getListHead());
		createKanbanBoardPanel();
		centerVLayout.appendChild(kanbanPanel);
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

}
