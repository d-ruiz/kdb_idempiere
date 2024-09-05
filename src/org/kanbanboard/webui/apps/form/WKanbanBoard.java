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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.util.Callback;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.apps.BusyDialog;
import org.adempiere.webui.apps.ProcessModalDialog;
import org.adempiere.webui.apps.WProcessCtl;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Menupopup;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.ProcessInfoDialog;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.Dialog;
import org.compiere.model.GridField;
import org.compiere.model.GridFieldVO;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.MSysConfig;
import org.compiere.model.X_AD_Process;
import org.compiere.process.ProcessInfo;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.kanbanboard.apps.form.KanbanBoard;
import org.kanbanboard.apps.form.KanbanBoardProcessController;
import org.kanbanboard.apps.form.ProcessUIElement;
import org.kanbanboard.model.KanbanSwimlane;
import org.kanbanboard.model.MKanbanCard;
import org.kanbanboard.model.MKanbanParameter;
import org.kanbanboard.model.MKanbanStatus;
import org.kanbanboard.model.MKanbanSwimlaneConfiguration;
import org.zkoss.zhtml.Span;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menuseparator;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Separator;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Vlayout;

/**
 *
 * @author Diego Ruiz
 *
 */

public class WKanbanBoard extends KanbanBoard implements IFormController, EventListener<Event>, ValueChangeListener {

	private static final String KDB_PROCESS_MENUPOPUP = "KDB_ProcessMenu";
	private static final String KDB_REFRESH_BUTTON_ID = "refreshKdb";
	private static final String KDB_SWIMLANE_ATTRIBUTE = "KDB_SwimlaneValue";

	protected final static String PROCESS_ID_KEY = "processId";

	private CustomForm kForm = new CustomForm();;	

	private Borderlayout	mainLayout	= new Borderlayout();

	private Panel panel = new Panel();
	private Label lProcess = new Label();
	private Listbox kanbanListbox = ListboxFactory.newDropdownListbox();
	private Listbox swimlaneListbox = null;
	private int kanbanBoardId = -1;
	private Button bRefresh = new Button();
	private Timer timer;
	private Menupopup menupopup;
	private Menupopup cardpopup;
	private Hbox      northPanelHbox;
	
	//Process Functionality
	private Div boardButtonsDiv;
	private ArrayList<Integer>	m_results = new ArrayList<Integer>(3);
	private BusyDialog progressWindow;
	private int rightClickedCard = 0;
	
	//Parameters
	private Div boardParamsDiv;
	private Button bFilter = new Button();
	private Popup filterPopup;
    private ArrayList<WEditor> m_sEditors = new ArrayList<WEditor>();
    private ArrayList<WEditor> m_sEditorsTo = new ArrayList<WEditor>();
	private Map<WEditor, MKanbanParameter> mapEditorParameter = new HashMap<WEditor, MKanbanParameter>();
	private Map<WEditor, MKanbanParameter> mapEditorToParameter = new HashMap<WEditor, MKanbanParameter>();

	private Map<Cell, MKanbanCard> mapCellColumn = new HashMap<Cell, MKanbanCard>();
	private Map<Cell, MKanbanStatus> mapEmptyCellField = new HashMap<Cell, MKanbanStatus>();
	private Map<Cell, KanbanSwimlane> mapEmptyCellSwimlane = new HashMap<Cell, KanbanSwimlane>();
	private Map<String, List<Row>> swimlaneRowsMap = new HashMap<String, List<Row>>();

	private Grid kanbanPanel;
	private Vlayout centerVLayout;
	private int totalNumberOfColumns = 0;

	public WKanbanBoard() {
		super();
		initForm();
	}

	public void initForm() {

		try {
			windowNo = SessionManager.getAppDesktop().registerWindow(this);
			dynList();
			jbInit();
		} catch (Exception ex){}
	}

	/**
	 * 	Static init
	 *	@throws Exception
	 */
	private void jbInit() throws Exception {
		kForm.setClosable(true);
		kForm.setMaximizable(true);
		kForm.setWidth("100%");
		kForm.setHeight("100%");
		kForm.appendChild (mainLayout);
		LayoutUtils.addSclass("kanban-board-form-content", mainLayout);  // ?? debe definirse en un css, se puede integrar css en el plugin?
		kForm.setBorder("normal");

		//North Panel
		lProcess.setText(Msg.translate(Env.getCtx(), "Process"));
		if (ThemeManager.isUseFontIconForImage())
			bRefresh.setIconSclass("z-icon-Refresh");
		else
			bRefresh.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		bRefresh.setId(KDB_REFRESH_BUTTON_ID);
		bRefresh.setTooltiptext(Msg.getMsg(Env.getCtx(), "Refresh"));
		bRefresh.setHeight("70%");
		bRefresh.addEventListener(Events.ON_CLICK, this);

		northPanelHbox = new Hbox();
		northPanelHbox.setAlign("center");
		northPanelHbox.appendChild(lProcess);
		kanbanListbox.setHeight("70%");
		northPanelHbox.appendChild(kanbanListbox);
		northPanelHbox.appendChild(bRefresh);
		panel.setHeight("100%");
		northPanelHbox.setHeight("100%");
		panel.appendChild(northPanelHbox);

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
		
		// Auto refresh in milliseconds
		int refreshInterval = MSysConfig.getIntValue("KDB_KanbanBoard_RefreshInterval", 0,
				Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx()));

		if (refreshInterval > 0) {
			timer = new Timer();
			timer.setDelay(refreshInterval);
			timer.addEventListener(Events.ON_TIMER, this);
			timer.setRepeats(true);
			timer.start();
			timer.setVisible(false);
			centerVLayout.appendChild(timer);
		}

		South south = new South();
		LayoutUtils.addSclass("tab-editor-form-center-panel", south);
		south.setSize("95%");
		south.appendChild(centerVLayout);

		mainLayout.appendChild(south);
	}	//	jbInit


	/**
	 *  Initialize List of existing processes
	 */
	private void dynList() {
		//	Fill Process
		String userPreferenceKanban = Env.getContext(Env.getCtx(), "P|KDB_KanbanBoard_ID");
		for (KeyNamePair process : getProcessList()) {
			kanbanListbox.addItem(process);
			if (!Util.isEmpty(userPreferenceKanban)
					&& process.getKey() == Integer.parseInt(userPreferenceKanban)) {
				kanbanBoardId = Integer.parseInt(userPreferenceKanban);
				kanbanListbox.setSelectedKeyNamePair(process);
			}
		}

		kanbanListbox.addEventListener(Events.ON_SELECT, this);
	}   //  dynList
	
	/**
	 * Load parameters for Kanban Board
	 */
	private void initParameters() {

		if (kanbanPanel == null)
			return;
		
		if (boardParamsDiv != null) {
			northPanelHbox.removeChild(boardParamsDiv);
			boardParamsDiv = null;
		}
		
		if (filterPopup != null) {
			kForm.removeChild(filterPopup);
			bFilter = new Button();
			filterPopup = null;
		}

		if (getBoardParameters() != null && getBoardParameters().size() > 0) {
			
			fillParameterEditors();
			boardParamsDiv = new Div();
			boardParamsDiv.setHeight("100%");
			boardParamsDiv.setStyle("padding-left: 5px; display: table-cell; vertical-align: middle;");
			if (m_sEditors.size() > 1 && MSysConfig.getBooleanValue("KDB_GROUP_PARAMETERS", true, Env.getAD_Client_ID(Env.getCtx()))) {
				bFilter.setLabel(Msg.getMsg(Env.getCtx(), "KDB_QuickFilter"));
				if (ThemeManager.isUseFontIconForImage())
					bFilter.setIconSclass("z-icon-MoveDown");
				else
					bFilter.setImage(ThemeManager.getThemeResource("images/MoveDown16.png"));
				bFilter.setDir("reverse");
				bFilter.setTooltiptext(Msg.getMsg(Env.getCtx(), "filter.by"));
				filterPopup = getParamPopup();
				kForm.appendChild(filterPopup);
				boardParamsDiv.appendChild(bFilter);
				bFilter.setHeight("70%");
				bFilter.addEventListener(Events.ON_CLICK, e -> {
					filterPopup.open(bFilter, "after_start");
				});
			} else {
				for (int i = 0; i < m_sEditors.size(); i++) {
		        	WEditor editor = m_sEditors.get(i);
		        	boardParamsDiv.appendChild(new Separator("vertical"));
		        	boardParamsDiv.appendChild(editor.getLabel());
		        	editor.getLabel().setStyle("padding-right:3px;");
		            if (m_sEditorsTo.get(i) != null) {
		            	boardParamsDiv.appendChild(editor.getComponent());
		            	boardParamsDiv.appendChild(new Label(" - "));
		            	boardParamsDiv.appendChild(m_sEditorsTo.get(i).getComponent());
					} else {
						boardParamsDiv.appendChild(editor.getComponent());
					}
		        }
				boardParamsDiv.appendChild(new Separator("vertical"));
			}

			northPanelHbox.appendChild(boardParamsDiv);
		}
	}
	
	private void fillParameterEditors() {
		for (MKanbanParameter param : getBoardParameters()) {
			WEditor editor = WebEditorFactory.getEditor(param.getGridField(), true);
			if (param.getValue() != null) {
				editor.setValue(param.getValue());
			}
			editor.setMandatory(false);
	        editor.setReadWrite(true);
	        editor.dynamicDisplay();
	        editor.updateStyle(false);
	        editor.addValueChangeListener(this);

	        Label label = editor.getLabel();
	        //Fix miss label of check box
	        label.setValue(param.getLabel());

	        m_sEditors.add(editor);
			mapEditorParameter.put(editor, param);
			if (param.isRange()) {
				GridFieldVO voF2 = GridFieldVO.createParameter(param.getGridField().getVO());
				GridField mField2 = new GridField(voF2);
				// The Editor
				WEditor editor2 = WebEditorFactory.getEditor(mField2, false);
				// Set Default
				if (param.getValueTo() != null) {
					editor2.setValue(param.getValueTo());
				}
				
				editor2.setMandatory(false);
				editor2.setReadWrite(true);
				editor2.dynamicDisplay();
				editor2.updateStyle(false);
				editor2.addValueChangeListener(this);
				mapEditorToParameter.put(editor2, param);
				m_sEditorsTo.add(editor2);
			} else 
				m_sEditorsTo.add(null);
		}
	}
	
	private Popup getParamPopup() {
        Popup popup = new Popup() {
        	/**
			 * 
			 */
			private static final long serialVersionUID = -5991248152956632527L;

			@Override
        	public void onPageAttached(Page newpage, Page oldpage) {
        		super.onPageAttached(newpage, oldpage);
        		if (newpage != null) {
        			if (bFilter.getPopup() != null) {
        				bFilter.setPopup(this);
        			}
        		}
        	}
        };

		Vlayout vbox = new Vlayout();
        Hlayout row;
        for (int i = 0; i < m_sEditors.size(); i++) {
        	WEditor editor = m_sEditors.get(i);
        	row = new Hlayout();
            row.appendChild(editor.getLabel());
			row.setHflex("2");
			editor.getLabel().setHflex("1");
			editor.getLabel().setStyle("text-align: right;");
            if (m_sEditorsTo.get(i) != null) {
            	Hbox toParams = new Hbox();
            	toParams.appendChild(editor.getComponent());
            	toParams.appendChild(new Label(" - "));
            	toParams.appendChild(m_sEditorsTo.get(i).getComponent());
            	toParams.setWidth("100%");
            	row.appendChild(toParams);
			} else {
				row.appendChild(editor.getComponent());
				ZKUpdateUtil.setHflex((HtmlBasedComponent) editor.getComponent(), "1");
			}
            vbox.appendChild(row);
        }
        popup.appendChild(vbox);
        return popup;
    }
	
	/**
	 * Load info process for Kanban Board
	 */
	private void initKanbanProcess() {

		if (kanbanPanel == null)
			return;
		if (menupopup != null) {
			kForm.removeChild(menupopup);
			menupopup = null;
		}
		
		if (cardpopup != null) {
			kForm.removeChild(cardpopup);
			cardpopup = null;
		}
		
		if (boardButtonsDiv != null) {
			northPanelHbox.removeChild(boardButtonsDiv);
			boardButtonsDiv = null;
		}

		if (kanbanHasProcesses()) {
			resetAndPopulateArrays();
			setStatusProcessMenupopup();
			setCardMenupopup();
			setBoardProcess();
		}
	}

	/**
	 * Create the panel where the kanban board
	 * is going to be painted
	 * @throws SQLException 
	 */
	public void createKanbanBoardPanel() {
		mapCellColumn.clear();
		mapEmptyCellField.clear();
		mapEmptyCellSwimlane.clear();
		mapEditorParameter.clear();
		mapEditorToParameter.clear();
		m_sEditors.clear();
		m_sEditorsTo.clear();
		kanbanPanel = new Grid();

		if (kanbanBoardId != -1) {

			setKanbanBoard(kanbanBoardId);
			kanbanPanel.makeNoStrip();
			kanbanPanel.setVflex(true);
			kanbanPanel.setSizedByContent(true);
			kanbanPanel.setSpan("true");
			initParameters();
			initKanbanProcess();
			initSwimlanes();

			totalNumberOfColumns = getNumberOfStatuses();

			if (totalNumberOfColumns > 0) {
				// set size in percentage per column leaving a MARGIN on right
				Columns columns = new Columns();
				if (menupopup == null)
					columns.setMenupopup(getBoardMenupopup());

				int equalWidth = 100 ;
				int stdColumnWidth = getStdColumnWidth();
				Auxhead auxhead = new Auxhead();

				//Create columns based on the states of the kanban board
				Column  column;
				Auxheader auxheader;
				for (MKanbanStatus status : getStatuses()) {
					if (status.hasQueue()) {
						column = new Column();
						if (stdColumnWidth == 0)
							column.setWidth(equalWidth/2 + "%");
						else
							column.setWidth(stdColumnWidth/2 + "px");
						columns.appendChild(column);
						column.setAlign("right");
						column.setVflex("min");
						column.setLabel(status.getPrintableName().substring(0, 1)+" Queue");
						column.setStyle("background-color: yellow;");
						columns.appendChild(column);
						if (status.getKanbanBoard().getKDB_SummarySQL() != null) {
							auxheader = new Auxheader();
							auxhead.appendChild(auxheader);
						}
						totalNumberOfColumns++;
					}
					column = new Column();
					column.setId(Integer.toString(status.get_ID()));
					if (menupopup != null) {
						column.setPopup(getBoardMenupopup());
					}
					
					if (stdColumnWidth == 0)
						column.setWidth(equalWidth + "%");
					else
						column.setWidth(stdColumnWidth + "px");
					
					columns.appendChild(column);
					column.setAlign("center");
					columns.appendChild(column);
					if (status.getTotalCards() != 0)
						column.setLabel(status.getPrintableName()+"("+status.getTotalCards()+")");
					else
						column.setLabel(status.getPrintableName());
					if (status.isExceed())
						column.setStyle("background-color: red;");
					if (getSummarySql() != null) {
						column.setStyle("background-color: #d9e3ec");
						auxheader = new Auxheader();
						if (status.getTotalCards()!= 0) {
							String statusSummary = status.getSummary();
							if (statusSummary != null) {
								auxheader.setLabel(statusSummary);
								auxheader.setTooltiptext(statusSummary);							
							}							
						}
						auxhead.appendChild(auxheader);
					}
				}
				columns.setSizable(true);
				createRows();	
				kanbanPanel.appendChild(columns);
				kanbanPanel.appendChild(auxhead);
			} else {
				Messagebox.show(Msg.getMsg(Env.getCtx(), "KDB_NoStatuses"));
			}
		}
	}//createKanbanBoardPanel
	
	private void initSwimlanes() {
		if (currentboardUsesSwimlane()) {
			swimlaneListbox = ListboxFactory.newDropdownListbox();
			
			swimlaneListbox.appendItem("", -1);
			for (MKanbanSwimlaneConfiguration swimlane : getSwimlaneConfigurationRecords()) {
				ListItem item = swimlaneListbox.appendItem(swimlane.getName(), swimlane.getValue());
				if (swimlane.equals(getActiveSwimlane()))
					swimlaneListbox.setSelectedItem(item);
			}
			swimlaneListbox.addEventListener(Events.ON_SELECT, this);
			
			Span swimlaneDiv = new Span();
			swimlaneDiv.setStyle("position: absolute; right: 0;top: 50%;transform: translate(0, -50%);");
			Label label = new Label(Msg.getCleanMsg(Env.getCtx(), "GroupedBy"));
			label.setStyle("padding:3px;");
			swimlaneDiv.appendChild(label);
			swimlaneListbox.setHeight("70%");
			swimlaneDiv.appendChild(swimlaneListbox);
			northPanelHbox.appendChild(swimlaneDiv);
		}
	}

	public void createRows() {
		mapCellColumn.clear();
		mapEmptyCellField.clear();
		mapEmptyCellSwimlane.clear();
		Rows rows = kanbanPanel.newRows();
		resetStatusProperties();
		if (paintSwimlanes()) {
			createRowsWithSwimlanes(rows);
		} else {
			createRegularRows(rows);
		}
	}//createRows
	
	private void createRowsWithSwimlanes(Rows rows) {
		Row row;

		for (KanbanSwimlane swimlane : getSwimlanes()) {
			if (!swimlane.isPrinted()) {
				Row swimlaneRow = createSwimlaneRow(swimlane);
				rows.appendChild(swimlaneRow);
			}
			while (swimlane.getTotalNumberOfCards() > 0) {
				row = new Row();
				for (MKanbanStatus status : getStatuses()) {
					setRowStyle(row);
					if (!status.hasMoreCards(swimlane)) {
						createStatusCellWithNoCards(row, status, swimlane);
					} else {
						if (status.hasQueue()) {
							if (!status.hasMoreQueuedCards(swimlane)) {
								createEmptyCell(row, status, swimlane);
								createCardCell(row, status.getCard(swimlane));
								swimlane.removeOneCard();
							} else {
								createQueuedCardCell(row, status.getQueuedCard(swimlane));
								swimlane.removeOneCard();
								if (status.hasMoreStatusCards(swimlane)) {
									createCardCell(row, status.getCard(swimlane));
									swimlane.removeOneCard();
								} else {
									createEmptyCell(row, status, swimlane);
								}
							}
						} else {
							createCardCell(row, status.getCard(swimlane));
							swimlane.removeOneCard();
						}
					}
				}
				rows.appendChild(row);
				swimlaneRowsMap.get(swimlane.getValue()).add(row);
			}
		}
	}
	
	private Row createSwimlaneRow(KanbanSwimlane swimlane) {
		Row row = new Row();
		createSwinlane(row, swimlane.getComponentLabel(), swimlane.getSummary());
		row.setStyle(getSwimlaneCSS());
		swimlane.setPrinted(true);
		setCollapsibleProperties(row, swimlane.getValue());
		return row;
	}
	
	private void setCollapsibleProperties(Row row, String value) {
		row.setDroppable("true");
		row.addEventListener(Events.ON_DROP, this);
		row.addEventListener(Events.ON_CLICK, this);
		row.setAttribute(KDB_SWIMLANE_ATTRIBUTE, value);
		swimlaneRowsMap.put(value, new ArrayList<Row>());
	}
	
	private void setRowStyle(Row row) {
		// [matica1] use background style instead of background-color and set transparent if no colors are set
		if (!Util.isEmpty(getBackgroundColor())) {
			row.setStyle("background:" + getBackgroundColor() + ";");
		} else {
			row.setStyle("background: transparent;");
		}
	}
	
	private void createRegularRows(Rows rows) {
		int numberOfCards = getNumberOfCards();
		Row row = new Row();
		
		while (numberOfCards > 0) {

			for (MKanbanStatus status : getStatuses()) {
				setRowStyle(row);

				if (!status.hasMoreCards()) {
					createStatusCellWithNoCards(row, status);
				} else {
					if (status.hasQueue()) {
						if (!status.hasMoreQueuedCards()) {
							createEmptyCell(row,status);
							createCardCell(row,status.getCard());
							numberOfCards--;
						} else {
							createQueuedCardCell(row, status.getQueuedCard());
							numberOfCards--;

							if (status.hasMoreStatusCards()) {
								createCardCell(row,status.getCard());
								numberOfCards--;
							} else {
								createEmptyCell(row,status);
							}
						}
					} else {
						createCardCell(row, status.getCard());
						numberOfCards--;
					}
				}
			}
			rows.appendChild(row);
			row=new Row();
		}
	}
	
	private void createStatusCellWithNoCards(Row row, MKanbanStatus status, KanbanSwimlane swimlane) {
		if (status.hasQueue()) { //Creates the extra cell for the queue space
			createEmptyCell(row, status, swimlane);
		}
		createEmptyCell(row, status, swimlane);
	}
	
	private void createStatusCellWithNoCards(Row row, MKanbanStatus status) {
		if (status.hasQueue()) { //Creates the extra cell for the queue space
			createEmptyCell(row, status);
		}
		createEmptyCell(row, status);
	}
	
	private void createSwinlane(Row row, String label, String summary) {
		Cell cell = new Cell();
		Label swimlaneLabel = new Label(label+" ");
		cell.setParent(row);
		cell.appendChild(swimlaneLabel);
		if (!Util.isEmpty(summary)) {
			Html htmlCard = new Html();
	        htmlCard.setContent(summary);
	        cell.appendChild(htmlCard);
		}
		cell.setColspan(totalNumberOfColumns);
		row.appendChild(cell);
	}
	
	private void createEmptyCell(Row row, MKanbanStatus status, KanbanSwimlane swimlane) {
		createEmptyCell(row, status);
		mapEmptyCellSwimlane.put(row.getLastCell(), swimlane);
	}

	private void createEmptyCell(Row row, MKanbanStatus status) {
		row.appendCellChild(createSpacer());
		setEmptyCellProps(row.getLastCell(),status);	
	}
	
	private void createCardCell(Row row, MKanbanCard card) {
		Vlayout cardCell = createCell(card);
		row.appendCellChild(cardCell);
		if (!isReadOnly())
			setCellProps(row.getLastCell(), card);
		else
			setOnlyReadCellProps(row.getLastCell(), card);
	}
	
	private void createQueuedCardCell(Row row, MKanbanCard card) {
		Vlayout cardCell = createCell(card);
		row.appendCellChild(cardCell);
		if (!isReadOnly())
			setQueuedCellProps(row.getLastCell(), card);
		else
			setOnlyReadCellProps(row.getLastCell(), card);
	}

	private void setCellProps(Cell cell, MKanbanCard card) {
		cell.setDraggable("true");
		cell.setDroppable("true");
		cell.addEventListener(Events.ON_DROP, this);
		cell.addEventListener(Events.ON_CLICK, this);
		cell.addEventListener(Events.ON_DOUBLE_CLICK, this);
		cell.addEventListener(Events.ON_RIGHT_CLICK, this);
		cell.setStyle(getCellCSSStyle(card));
		cell.setContext(cardpopup);
		mapCellColumn.put(cell, card);
	}

	private Vlayout createCell(MKanbanCard card) {
		Vlayout div = new Vlayout();		
		StringBuilder divStyle = new StringBuilder();
		
		divStyle.append("text-align: left; ");
		
		if (!card.isQueued())
			divStyle.append("cursor:hand; cursor:pointer; ");

		if (getStdCardheight() != 0) {
			div.setHeight(getStdCardheight() + "px");
			divStyle.append("overflow:auto");
		}
		
		if (!Util.isEmpty(card.getCardColor())) {
			divStyle.append("margin-left: 15%;");
		}
		
		div.setStyle(divStyle.toString());
		
		if (isHTML()) {
			String htmlText = card.getKanbanCardText();

			Div content = new Div();
	    	div.appendChild(content);
	    	content.setStyle("color:"+card.getTextColor());
	    	Html htmlCard = new Html();
	        content.appendChild(htmlCard);
	        htmlCard.setContent(htmlText);
	        
		} else {
			String[] tokens = card.getKanbanCardText().split(System.getProperty("line.separator"));

			for (String token : tokens) {
				Label label = new Label(token);
				label.setStyle("color:"+card.getTextColor());
				div.appendChild(label);
			}
		}

		div.setTooltiptext(card.getTooltiptext());
		return div;
	}//CreateCell

	private void setQueuedCellProps(Cell cell, MKanbanCard card) {
		cell.addEventListener(Events.ON_DOUBLE_CLICK, this);
		cell.setStyle(getCellCSSStyle(card));
		mapCellColumn.put(cell, card);
	}

	private void setOnlyReadCellProps(Cell cell, MKanbanCard card) {
		cell.addEventListener(Events.ON_CLICK, this);
		cell.addEventListener(Events.ON_DOUBLE_CLICK, this);
		cell.setStyle(getCellCSSStyle(card));
		mapCellColumn.put(cell, card);
	}


	private void setEmptyCellProps(Cell lastCell, MKanbanStatus status) {
		lastCell.setDroppable("true");
		lastCell.addEventListener(Events.ON_DROP, this);
		mapEmptyCellField.put(lastCell, status);
	}
	
	/**
	 * Set Process Menupopup if there are status scope processes
	 */
	private void setStatusProcessMenupopup() {
		
		if (kanbanHasStatusProcess()) {
			menupopup = new Menupopup();
			menupopup.setId(KDB_PROCESS_MENUPOPUP+windowNo);
			menupopup.addEventListener(Events.ON_OPEN, this);
			Menuitem menuitem;
			
			//Add the processes
			for (ProcessUIElement element : getStatusProcessElements()) {
				menuitem = new Menuitem();
				menuitem.setId(Integer.toString(element.getElementID()));
				menuitem.setLabel(element.getName());
				if (ThemeManager.isUseFontIconForImage())
					menuitem.setIconSclass("z-icon-Process");
				else
					menuitem.setImage(ThemeManager.getThemeResource("images/Process16.png"));
				menuitem.setAttribute(PROCESS_ID_KEY, Integer.valueOf(element.getAD_Process_ID()));
				menuitem.setAttribute(PROCESS_TYPE, STATUS_PROCESS);
				menuitem.addEventListener(Events.ON_CLICK, this);
				menupopup.appendChild(menuitem);
			}
			
			Menuseparator ms = new Menuseparator();
			menupopup.appendChild(ms);
			
			//Reproduce behavior of "auto" for customized menupopup
			for (MKanbanStatus status : getStatuses()) {
				menuitem = new Menuitem();
				menuitem.setId(Integer.toString(status.get_ID()));
				menuitem.setLabel(status.getPrintableName());
				menuitem.setChecked(true);
				menuitem.setCheckmark(true);
				menuitem.setAutocheck(true);
				menuitem.addEventListener(Events.ON_CLICK, this);
				
				menupopup.appendChild(menuitem);
			}
			
			kForm.appendChild(menupopup);
		}
	} //setStatusProcessMenupopup
	
	/**
	 * Set Card Menupopup if there are card scope processes
	 */
	private void setCardMenupopup() {
		
		cardpopup = new Menupopup();
		
		if (kanbanHasCardProcess()) {
			
			cardpopup.setId("cardMenu");
			Menuitem menuitem;

			//Add the processes
			for (ProcessUIElement element : getCardProcessElements()) {
				menuitem = new Menuitem();
				menuitem.setId(Integer.toString(element.getElementID()));
				menuitem.setLabel(element.getName());
				if (ThemeManager.isUseFontIconForImage())
					menuitem.setIconSclass("z-icon-Process");
				else
					menuitem.setImage(ThemeManager.getThemeResource("images/Process16.png"));
				menuitem.setAttribute(PROCESS_ID_KEY, Integer.valueOf(element.getAD_Process_ID()));
				menuitem.setAttribute(PROCESS_TYPE, CARD_PROCESS);
				menuitem.addEventListener(Events.ON_CLICK, this);
				cardpopup.appendChild(menuitem);
			}
		}
		kForm.appendChild(cardpopup);
	}//setCardMenupopup
	
	/**
	 * Set Process Buttons if there are board scope processes
	 */
	private void setBoardProcess() {

		if (kanbanHasBoardProcess()) {
			boardButtonsDiv = new Div();
			boardButtonsDiv.setHeight("100%");
			boardButtonsDiv.setStyle("display: table-cell; vertical-align: middle;");
			Button b;
			for (ProcessUIElement element : getBoardProcessElements()) {
				b = new Button();
				b.setId(Integer.toString(element.getElementID()));
				b.setImage(null);
				b.setLabel(element.getName());
				b.setAttribute(PROCESS_ID_KEY, Integer.valueOf(element.getAD_Process_ID()));
				b.setAttribute(PROCESS_TYPE, BOARD_PROCESS);
				b.addEventListener(Events.ON_CLICK, this);
				b.setHeight("70%");
				boardButtonsDiv.appendChild(b);
			}
			northPanelHbox.appendChild(boardButtonsDiv);
		}
	}//setBoardProcess
	
	/**
	 * When no processes are set up returns the default menupopup
	 * Otherwise returns a customize menupopup with the processes defined
	 * @return
	 */
	private String getBoardMenupopup() {
	
		String idMenupopup = "auto";
		if (menupopup != null) {
			idMenupopup = KDB_PROCESS_MENUPOPUP+windowNo;
		}
		return idMenupopup;
	}//getBoardMenupopup

	/**************************************************************************
	 *  Action Listener
	 *  @param e event
	 */
	public void onEvent(Event e) {

		if (isInteractionWithAList(e)) {
			if (e.getTarget().equals(kanbanListbox)) {
				selectKanbanBoard();
			} else if (e.getTarget().equals(swimlaneListbox)) {
				selectSwimlane();
			}
		} else if (isDoubleClickOnCard(e)) {
			MKanbanCard card = mapCellColumn.get(e.getTarget());
			int recordId = card.getRecordID();
			int AD_Table_ID = getAd_Table_id();
			zoom(recordId,AD_Table_ID);
		} else if (e instanceof DropEvent) {
			DropEvent me = (DropEvent) e;
			Cell startItem = null;

			if (me.getDragged() instanceof Cell) {
				startItem = (Cell) me.getDragged();
			} 

			if (me.getTarget() instanceof Cell) {
				Cell endItem = (Cell) me.getTarget();

				MKanbanCard startField = mapCellColumn.get(startItem);
				MKanbanStatus startStatus = startField.getBelongingStatus(); 
				MKanbanCard endField = mapCellColumn.get(endItem);
				MKanbanStatus endStatus;

				if (endField == null && mapEmptyCellField.get(me.getTarget()) != null) {
					// check empty cells
					endStatus= mapEmptyCellField.get(me.getTarget());
				} else {
					endStatus = endField.getBelongingStatus();
				}
				

				if (!swapCard(startStatus, endStatus, startField))
					Dialog.warn(windowNo, Msg.parseTranslation(Env.getCtx(), startField.getStatusChangeMessage()));
				else {
					//Change swimlane as well if it is active
					if (getActiveSwimlane() != null) {
						String endSwimlaneValue = endField != null ? endField.getSwimlaneValue() : mapEmptyCellSwimlane.get(me.getTarget()).getValue();
						if (!swapSwimlanes(startField, endSwimlaneValue))
							Dialog.warn(windowNo, Msg.parseTranslation(Env.getCtx(), startField.getStatusChangeMessage()));
					}
					repaintCards();
				}
			} else if (me.getTarget() instanceof Row) { //Swim lane Header
				Row endSwimlane = (Row) me.getTarget();
				MKanbanCard draggedCard = mapCellColumn.get(startItem);
				if (!swapSwimlanes(draggedCard, endSwimlane))
					Dialog.warn(windowNo, Msg.parseTranslation(Env.getCtx(), draggedCard.getStatusChangeMessage()));
				else 
					repaintCards();
			}
		} else if (isClickOnBoardProcess(e)) {
			Button clickedButton = (Button) e.getTarget();

			if (clickedButton.getId().equals(KDB_REFRESH_BUTTON_ID)) {
				if (kanbanBoardId != -1) {
					repaintCards();
				}
			} else {
				runProcess(clickedButton.getAttribute(PROCESS_ID_KEY), getSaveKeys(BOARD_PROCESS, 0));
			}
		} else if (isClickOnMenuItem(e)) {
			Menuitem selectedItem = (Menuitem) e.getTarget();
			if (selectedItem.isCheckmark()) { 			//Reproduce behavior of "auto" for customized menupopup
				changeColumnVisibility(selectedItem);
			} else {
				runMenuItemProcess(selectedItem, e);
			}
		} else if (isClickOnSwimlane(e)) {
			collapseSwimlane((Row) e.getTarget());
		} else if (isRightClickOnCard(e)) {
			//Sets the record ID of the selected card to use in the associated process
			MKanbanCard card = mapCellColumn.get(e.getTarget());
			rightClickedCard = card.getRecordID();
		} else if (Events.ON_OPEN.equals(e.getName()) && (e.getTarget() instanceof Menupopup)) {

			OpenEvent openEvt = (OpenEvent) e;
			if (openEvt.isOpen()) {
				Menupopup popup = (Menupopup)openEvt.getTarget();
				Component referencedComponent = openEvt.getReference();

				// set the referenced object in a hidden reference of the popup
				popup.setAttribute("columnRef", referencedComponent);				
			}
		} else if (Events.ON_TIMER.equals(e.getName())) {
			//Auto refresh
			if (kanbanBoardId != -1) {
				repaintCards();
			}
		}
	}//onEvent
	
	/**
	 * Lists are Kanban Board list or Swimlane list
	 * @param Event e
	 * @return true if the user interacted with a list component
	 */
	private boolean isInteractionWithAList(Event e) {
		return Events.ON_SELECT.equals(e.getName()) && e.getTarget() instanceof Listbox;
	}
	
	/**
	 * Check event ONDoubleCLICK on a cell Navigate into documents
	 * @param Event e
	 * @return
	 */
	private boolean isDoubleClickOnCard(Event e) {
		return Events.ON_DOUBLE_CLICK.equals(e.getName()) && (e.getTarget() instanceof Cell);
	}
	
	/**
	 * Board Process includes the refresh button
	 * @param e
	 * @return true if the user clicked on a button with board access 
	 */
	private boolean isClickOnBoardProcess(Event e) {
		return Events.ON_CLICK.equals(e.getName()) && e.getTarget() instanceof Button;	
	}
	
	/**
	 * Menu items are either status processes or card processes
	 * @param e
	 * @return true if the user clicked a menu item
	 */
	private boolean isClickOnMenuItem(Event e) {
		return Events.ON_CLICK.equals(e.getName()) && e.getTarget() instanceof Menuitem;	
	}
	
	/**
	 * @param e
	 * @return true if the user clicked on a swimlane header 
	 */
	private boolean isClickOnSwimlane(Event e) {
		return Events.ON_CLICK.equals(e.getName()) && e.getTarget() instanceof Row;	
	}
	
	/**
	 * @param e
	 * @return true if the user right clicked on a card 
	 */
	private boolean isRightClickOnCard(Event e) {
		return Events.ON_RIGHT_CLICK.equals(e.getName()) && (e.getTarget() instanceof Cell);	
	}
	
	private void changeColumnVisibility(Menuitem selectedItem) {
		Column column = (Column) kanbanPanel.getColumns().getFirstChild();
		while (column != null) {
			if(column.getId().equals(selectedItem.getId())) {
				column.setVisible(selectedItem.isChecked());
				break;
			}
			column = (Column) column.getNextSibling();
		}
	}
	
	private void runMenuItemProcess(Menuitem selectedItem, Event e) {
		enableButtons(false);
		int referenceID = 0;
		
		if (CARD_PROCESS.equals(selectedItem.getAttribute(PROCESS_TYPE))) {
			referenceID = rightClickedCard;
			Env.setContext(Env.getCtx(), windowNo, "KDB_Record_ID", referenceID);
		} else if (STATUS_PROCESS.equals(selectedItem.getAttribute(PROCESS_TYPE))) {
			Menupopup popup = (Menupopup) e.getTarget().getParent();
			Column clickedColumn = (Column) popup.getAttribute("columnRef");
			referenceID = Integer.parseInt(clickedColumn.getId());
		}
		runMenuItemProcess(selectedItem, referenceID);
	}
	
	private void runMenuItemProcess(Menuitem selectedItem, int referenceID) {
		Integer AD_Process_ID = (Integer) selectedItem.getAttribute(PROCESS_ID_KEY);
		if (AD_Process_ID == KanbanBoardProcessController.COMPLETE_ALL_ID)
			runCompleteAllCards(referenceID);
		else if (isMoveCardProcess(AD_Process_ID)) {
			moveCard(AD_Process_ID, referenceID);
			repaintCards();
		} else
			runProcess(AD_Process_ID, getSaveKeys((String) selectedItem.getAttribute(PROCESS_TYPE),referenceID));
	}
	
	private void runCompleteAllCards(int referenceID) {
		Dialog.ask(windowNo, "KDB_CompleteAll?", new Callback<Boolean>() {
			@Override
			public void onCallback(Boolean result) {
				if (result) {
					showBusyDialog();
					try {
						String message = completeAllCardsInStatus(referenceID);
						if (!"OK".equals(message))
							Dialog.warn(windowNo, message);
					} finally {
						repaintCards();
						hideBusyDialog();
					}
				}
			}
		});
		
	}
	
	private void collapseSwimlane(Row selectedRow) {
		String value = (String) selectedRow.getAttribute(KDB_SWIMLANE_ATTRIBUTE);
		
		for (Row row : swimlaneRowsMap.get(value))
			row.setVisible(!row.isVisible());
	}
	
	private boolean swapSwimlanes(MKanbanCard draggedCard, Row endSwimlane) {
		String swimlaneValue = (String) endSwimlane.getAttribute(KDB_SWIMLANE_ATTRIBUTE); 
		return swapSwimlanes(draggedCard, swimlaneValue);
	}
	
	private void selectKanbanBoard() {
		if (kanbanListbox.getSelectedIndex() != -1) {

			KeyNamePair kanbanKeyNamePair = null;
			kanbanBoardId = -1;
			kanbanKeyNamePair = (KeyNamePair) kanbanListbox.getSelectedItem().toKeyNamePair();	
			if (kanbanKeyNamePair != null)
				kanbanBoardId = kanbanKeyNamePair.getKey();
			fullRefresh();
		}
	}
	
	private void selectSwimlane() {
		if (swimlaneListbox.getSelectedIndex() != -1) {
			selectSwimlane(swimlaneListbox.getValue());
			repaintCards();
		}
	}
	
	@Override
	public void valueChange(ValueChangeEvent evt) {
		if (evt != null && evt.getSource() instanceof WEditor) {
			WEditor changedEditor = (WEditor)evt.getSource();
			Object value = evt.getNewValue();

			if (mapEditorParameter.containsKey(changedEditor)) {
				MKanbanParameter changedParam = mapEditorParameter.get(changedEditor);
				changedParam.setValue(value);
			} else if (mapEditorToParameter.containsKey(changedEditor)) {
				MKanbanParameter changedParamTo = mapEditorToParameter.get(changedEditor);
				changedParamTo.setValueTo(value);
			}
			repaintCards();
			if (filterPopup != null)
				filterPopup.open(bFilter, "after_start");
        }
	} //valueChange

	private void zoom(int recordId, int ad_table_id) {
		AEnv.zoom(ad_table_id, recordId);
	}
	
	private void fullRefresh() {
		refreshBoard();
		repaintGrid();
	}
	
	private void repaintCards() {
		refreshCards();
		repaintGrid();
	}
	
	/**
     * Run a process.
     * show process dialog,
     * before start process, save id of record selected
     * after run process, show message report result 
     * @param processIdObj
	 * @param collection 
     */
    protected void runProcess (Object processIdObj, final Collection<KeyNamePair> saveKeys) {
    	final Integer processId = (Integer)processIdObj;
    	final MProcess mProcess = MProcess.get(processId);
    	final ProcessInfo m_pi = new ProcessInfo(mProcess.getName(), processId);
		m_pi.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
		m_pi.setAD_Client_ID(Env.getAD_Client_ID(Env.getCtx()));
		MPInstance instance = new MPInstance(Env.getCtx(), processId, -1, 0, null);
		instance.saveEx();
		final int pInstanceID = instance.getAD_PInstance_ID();
		// Execute Process
		m_pi.setAD_PInstance_ID(pInstanceID);
		setProcessEnvVariables();
		
		//HengSin - to let process end with message and requery
		WProcessCtl.process(windowNo, m_pi, null, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				ProcessModalDialog processModalDialog = (ProcessModalDialog)event.getTarget();
				if (DialogEvents.ON_BEFORE_RUN_PROCESS.equals(event.getName())) {
					// store in T_Selection table selected rows for Execute Process that retrieves from T_Selection in code.
					DB.createT_SelectionNew(pInstanceID, saveKeys, null);
					showBusyDialog();
				} else if (ProcessModalDialog.ON_WINDOW_CLOSE.equals(event.getName())) {  
					if (processModalDialog.isCancel()) {
						m_results.clear();
					} else if (m_pi.isError() || X_AD_Process.SHOWHELP_ShowHelp.equals(mProcess.getShowHelp())) {
						ProcessInfoDialog.showProcessInfo(m_pi, windowNo, kForm, true);
					} 
					// enable or disable control button rely selected record status 
					enableButtons();
					cleanEnvVariables();
					repaintCards();
					hideBusyDialog();
				}
		//HengSin -- end --
			}
		}); 
    }
    
    /**
     * Set Env variables for processes
     * 1 - set Kanban board id to be able to use it in the process to get more info (f.e. where clause)
     * 2 - Set Kanban parameters and values to use the same filter in the process if needed    
     * Read it in the process as -> Env.getContext(Env.getCtx(), "#KDB_KanbanBoard_ID"));
     *  and Env.getContext(Env.getCtx(), "#KDB_Params"));
     */
    private void setProcessEnvVariables() {
    	Env.setContext(Env.getCtx(), "#KDB_KanbanBoard_ID", kanbanBoardId);
    	Env.setContext(Env.getCtx(), "#KDB_Params", getKanbanBoard().getParamWhere());
    }
    
    private void cleanEnvVariables() {
    	Env.setContext(Env.getCtx(), "#KDB_KanbanBoard_ID", "");
    	Env.setContext(Env.getCtx(), "#KDB_Params", "");
    	Env.setContext(Env.getCtx(), windowNo, "KDB_Record_ID", "");
    }
    
    /**
	 * enable all control button or disable all rely to selected record 
	 */
	protected void enableButtons() {
		boolean enable = true;
		enableButtons(enable);
	}//enableButtons
	
	private void showBusyDialog() {
		progressWindow = new BusyDialog();
		progressWindow.setPage(kForm.getPage());
		progressWindow.doHighlighted();				
	}//showBusyDialog
	
	private void hideBusyDialog() {
		if (progressWindow != null) {
			progressWindow.dispose();
			progressWindow = null;
		}
	}//hideBusyDialog
	
	/**
	 * enable or disable all control button
	 *  Enable OK, History, Zoom if row/s selected
     *  ---
     *  Changes: Changed the logic for accommodating multiple selection
     *  @author ashley
	 */
	protected void enableButtons(boolean enable) {
		if (boardButtonsDiv != null && boardButtonsDiv.getChildren() != null) {
			for (Component btProcess : boardButtonsDiv.getChildren()) {
				((Button) btProcess).setEnabled(enable);
			}
		}
	}//  enableButtons

	private Component createSpacer() {
		return new Space();
	}

	public ADForm getForm() {
		return kForm;
	}

	private void repaintGrid() {
		centerVLayout.removeChild(kanbanPanel);
		if (kanbanPanel.getRows() != null)
			kanbanPanel.removeChild(kanbanPanel.getRows());
		cleanNorthPanel();
		createKanbanBoardPanel();
		centerVLayout.appendChild(kanbanPanel);
	}
	
	private void cleanNorthPanel() {
		List<Component> childsToRemove = new ArrayList<Component>();
		for (Component component : northPanelHbox.getChildren()) {
			if (!component.equals(lProcess) 
					&& !component.equals(kanbanListbox) 
					&& !component.equals(bRefresh))
				childsToRemove.add(component);
		}
		for (Component component : childsToRemove)
			northPanelHbox.removeChild(component);
	}
}

