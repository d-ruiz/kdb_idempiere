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
import java.util.Map;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.apps.BusyDialog;
import org.adempiere.webui.apps.ProcessModalDialog;
import org.adempiere.webui.apps.WProcessCtl;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
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
import org.kanbanboard.model.MKanbanCard;
import org.kanbanboard.model.MKanbanParameter;
import org.kanbanboard.model.MKanbanProcess;
import org.kanbanboard.model.MKanbanStatus;
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
	protected final static String PROCESS_ID_KEY = "processId";

	private CustomForm kForm = new CustomForm();;	

	private Borderlayout	mainLayout	= new Borderlayout();

	private Panel panel = new Panel();
	private Grid gridLayout = GridFactory.newGridLayout();
	private Label lProcess = new Label();
	private Listbox kanbanListbox = ListboxFactory.newDropdownListbox();
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

	Map<Cell, MKanbanCard> mapCellColumn = new HashMap<Cell, MKanbanCard>();
	Map<Cell, MKanbanStatus> mapEmptyCellField = new HashMap<Cell, MKanbanStatus>();

	Grid kanbanPanel;
	Vlayout centerVLayout;

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
		panel.appendChild(gridLayout);
		lProcess.setText(Msg.translate(Env.getCtx(), "Process"));
		Rows rows = gridLayout.newRows();
		Row row = rows.newRow();
		if (ThemeManager.isUseFontIconForImage())
			bRefresh.setIconSclass("z-icon-Refresh");
		else
			bRefresh.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		bRefresh.setId(KDB_REFRESH_BUTTON_ID);
		bRefresh.setTooltiptext(Msg.getMsg(Env.getCtx(), "Refresh"));
		bRefresh.addEventListener(Events.ON_CLICK, this);

		northPanelHbox = new Hbox();
		northPanelHbox.appendChild(lProcess.rightAlign());
		northPanelHbox.appendChild(kanbanListbox);
		northPanelHbox.appendChild(bRefresh);
		Cell cell = new Cell();
		cell.setColspan(3);
		cell.setRowspan(1);
		cell.setAlign("left");
		cell.appendChild(northPanelHbox);
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
			boardParamsDiv.setSclass("padding-left: 5px;");
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
			WEditor editor = WebEditorFactory.getEditor(getGridField(param), true);
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
	        label.setValue(getGridField(param).getHeader());

	        m_sEditors.add(editor);
			mapEditorParameter.put(editor, param);
			if (param.isRange()) {
				GridFieldVO voF2 = GridFieldVO.createParameter(getGridField(param).getVO());
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

		if (getNumberOfProcesses() > 0  && getProcesses() != null) {
			//Clear them to avoid duplicants when refreshing
			getStatusProcesses().clear();
			getBoardProcesses().clear();
			getCardProcesses().clear();
			//Fill the lists - (Status,board,card) process
			for (MKanbanProcess process: getProcesses()) {
				if (MKanbanProcess.KDB_PROCESSSCOPE_Status.equals(process.getKDB_ProcessScope()))
					getStatusProcesses().add(process);
				else if (MKanbanProcess.KDB_PROCESSSCOPE_Board.equals(process.getKDB_ProcessScope()))
					getBoardProcesses().add(process);
				else if (MKanbanProcess.KDB_PROCESSSCOPE_Card.equals(process.getKDB_ProcessScope()))
					getCardProcesses().add(process);
			}
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

			int numCols=0;
			numCols = getNumberOfStatuses();

			if (numCols > 0) {
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
			}

			if (numCols <= 0) {
				Messagebox.show(Msg.getMsg(Env.getCtx(), "KDB_NoStatuses"));
			}
		}
	}//createKanbanBoardPanel

	public void createRows() {
		mapCellColumn.clear();
		mapEmptyCellField.clear();
		Rows rows = kanbanPanel.newRows();
		Row row = new Row();
		resetStatusProperties();
		int numberOfCards = getNumberOfCards();
		while (numberOfCards > 0) {
			for (MKanbanStatus status : getStatuses()) {
				// [matica1] use background style instead of background-color and set transparent if no colors are set
				if (getBackgroundColor() != null && !getBackgroundColor().equals("")) {
					row.setStyle("background:" + getBackgroundColor() + ";");
				} else {
					row.setStyle("background: transparent;");
				}
				
				if (!status.hasMoreCards()) {
					if (status.hasQueue()) {
						createEmptyCell(row,status);
					}
					createEmptyCell(row,status);
				} else {
					if (status.hasQueue()) {
						if (!status.hasMoreQueuedCards()) {
							createEmptyCell(row,status);
							createCardCell(row,status);
							numberOfCards--;
						} else {
							MKanbanCard queuedCard = status.getQueuedCard();
							Vlayout l = createCell(queuedCard);
							row.appendCellChild(l);
							if (!isReadWrite())
								setOnlyReadCellProps(row.getLastCell(), queuedCard);
							else
								setQueuedCellProps(row.getLastCell(), queuedCard);
							numberOfCards--;
							if (status.hasMoreStatusCards()) {
								createCardCell(row,status);
								numberOfCards--;
							} else {
								createEmptyCell(row,status);
							}
						}
					} else {
						createCardCell(row,status);
						numberOfCards--;	
					}
				}
			}
			rows.appendChild(row);
			row=new Row();
		}
	}//createRows

	private void createEmptyCell(Row row, MKanbanStatus status) {
		row.appendCellChild(createSpacer());
		setEmptyCellProps(row.getLastCell(),status);	
	}

	private void createCardCell(Row row, MKanbanStatus status) {
		MKanbanCard card = status.getCard();
		Vlayout l = createCell(card);
		row.appendCellChild(l);
		if (isReadWrite())
			setCellProps(row.getLastCell(), card);
		else
			setOnlyReadCellProps(row.getLastCell(), card);
	}

	private Vlayout createCell(MKanbanCard card) {
		Vlayout div = new Vlayout();		
		StringBuilder divStyle = new StringBuilder();
		
		divStyle.append("text-align: left; ");
		divStyle.append("background-color:" + card.getCardColor() + "; ");
		
		if (!card.isQueued())
			divStyle.append("cursor:hand; cursor:pointer; ");

		if (getStdCardheight() != 0) {
			div.setHeight(getStdCardheight() + "px");
			divStyle.append("overflow:auto");
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

	private void setCellProps(Cell cell, MKanbanCard card) {
		cell.setDraggable("true");
		cell.setDroppable("true");
		cell.addEventListener(Events.ON_DROP, this);
		cell.addEventListener(Events.ON_CLICK, this);
		cell.addEventListener(Events.ON_DOUBLE_CLICK, this);
		cell.addEventListener(Events.ON_RIGHT_CLICK, this);
		cell.setStyle("text-align: left;");
		cell.setStyle("border-style: outset; ");
		cell.setContext(cardpopup);
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
	
	/**
	 * Set Process Menupopup if there are status scope processes
	 */
	private void setStatusProcessMenupopup() {
		
		if (getStatusProcesses() != null && getStatusProcesses().size() > 0) {
			menupopup = new Menupopup();
			menupopup.setId(KDB_PROCESS_MENUPOPUP+windowNo);
			menupopup.addEventListener(Events.ON_OPEN, this);
			Menuitem menuitem;
			
			//Add the processes
			for (MKanbanProcess process : getStatusProcesses()) {
				menuitem = new Menuitem();
				menuitem.setId(Integer.toString(process.getKDB_KanbanProcess_ID()));
				menuitem.setLabel(process.getName());
				if (ThemeManager.isUseFontIconForImage())
					menuitem.setIconSclass("z-icon-Process");
				else
					menuitem.setImage(ThemeManager.getThemeResource("images/Process16.png"));
				menuitem.setAttribute(PROCESS_ID_KEY, Integer.valueOf(process.getAD_Process_ID()));
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
		
		if (getCardProcesses() != null && getCardProcesses().size() > 0) {
			
			cardpopup.setId("cardMenu");
			Menuitem menuitem;
			
			//Add the processes
			for (MKanbanProcess process : getCardProcesses()) {
				menuitem = new Menuitem();
				menuitem.setId(Integer.toString(process.getKDB_KanbanProcess_ID()));
				menuitem.setLabel(process.getName());
				if (ThemeManager.isUseFontIconForImage())
					menuitem.setIconSclass("z-icon-Process");
				else
					menuitem.setImage(ThemeManager.getThemeResource("images/Process16.png"));
				menuitem.setAttribute(PROCESS_ID_KEY, Integer.valueOf(process.getAD_Process_ID()));
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

		if (getBoardProcesses() != null && getBoardProcesses().size() > 0) {
			boardButtonsDiv = new Div();
			Button b;
			for (MKanbanProcess process : getBoardProcesses()) {
				b = new Button();
				b.setId(Integer.toString(process.getKDB_KanbanProcess_ID()));
				b.setImage(null);
				b.setLabel(process.getProcess().get_Translation(MProcess.COLUMNNAME_Name));
				b.setAttribute(PROCESS_ID_KEY, Integer.valueOf(process.getAD_Process_ID()));
				b.setAttribute(PROCESS_TYPE, BOARD_PROCESS);
				b.addEventListener(Events.ON_CLICK, this);
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

		// select an item within the list -- set it active and show the properties
		if (Events.ON_SELECT.equals(e.getName()) && e.getTarget() instanceof Listbox) {
			if (kanbanListbox.getSelectedIndex() != -1) {

				KeyNamePair kanbanKeyNamePair = null;
				kanbanBoardId = -1;
				kanbanKeyNamePair = (KeyNamePair)kanbanListbox.getSelectedItem().toKeyNamePair();	
				if (kanbanKeyNamePair != null)
					kanbanBoardId = kanbanKeyNamePair.getKey();
				fullRefresh();
			}
		}
		// Check event ONDoubleCLICK on a cell Navigate into documents
		else if (Events.ON_DOUBLE_CLICK.equals(e.getName()) && (e.getTarget() instanceof Cell)) {
			MKanbanCard card = mapCellColumn.get(e.getTarget());
			int recordId = card.getRecordID();
			int AD_Table_ID = getAd_Table_id();
			zoom(recordId,AD_Table_ID);
		} else if (e instanceof DropEvent ) {
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

				if (endField == null && mapEmptyCellField.get(me.getTarget()) != null) {
					// check empty cells
					endStatus= mapEmptyCellField.get(me.getTarget());
				}

				else
					endStatus = endField.getBelongingStatus();

				if (!swapCard(startStatus, endStatus, startField))
					Messagebox.show(Msg.getMsg(Env.getCtx(), MKanbanCard.KDB_ErrorMessage));
				else {
					repaintCards();
				}
			}
		}
		//Check Event on click for processes
		else if (Events.ON_CLICK.equals(e.getName()) && e.getTarget() instanceof Button) {
			Button clickedButton = (Button) e.getTarget();

			if (clickedButton.getId().equals(KDB_REFRESH_BUTTON_ID)) {
				if (kanbanBoardId != -1) {
					repaintCards();
				}
			} else {
				runProcess(clickedButton.getAttribute(PROCESS_ID_KEY), getSaveKeys(BOARD_PROCESS, 0));
			}
		} else if (Events.ON_CLICK.equals(e.getName()) && e.getTarget() instanceof Menuitem) {
			Menuitem selectedItem = (Menuitem) e.getTarget();
			//Reproduce behavior of "auto" for customized menupopup
			if (selectedItem.isCheckmark()) {
				Column column = (Column) kanbanPanel.getColumns().getFirstChild();
				while (column != null) {
					if(column.getId().equals(selectedItem.getId())) {
						column.setVisible(selectedItem.isChecked());
						break;
					}
					column = (Column) column.getNextSibling();
				}
			} else {
				enableButtons(false);
				int referenceID = 0;
				
				if (CARD_PROCESS.equals(selectedItem.getAttribute(PROCESS_TYPE))) {
					referenceID = rightClickedCard;
				} else if (STATUS_PROCESS.equals(selectedItem.getAttribute(PROCESS_TYPE))) {
					Menupopup popup = (Menupopup) e.getTarget().getParent();
					Column clickedColumn = (Column) popup.getAttribute("columnRef");
					referenceID = Integer.parseInt(clickedColumn.getId());
				}
				runProcess(selectedItem.getAttribute(PROCESS_ID_KEY), getSaveKeys((String) selectedItem.getAttribute(PROCESS_TYPE),referenceID));
			}
		}
		//Right click on cards for associated process
		else if (Events.ON_RIGHT_CLICK.equals(e.getName()) && (e.getTarget() instanceof Cell)) {
			//Sets the record ID of the selected card to use in the associated process
			MKanbanCard card = mapCellColumn.get(e.getTarget());
			rightClickedCard = card.getRecordID();
		}

		else if (Events.ON_OPEN.equals(e.getName()) && (e.getTarget() instanceof Menupopup)) {

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
	
	@Override
	public void valueChange(ValueChangeEvent evt) {
		if (evt != null && evt.getSource() instanceof WEditor) {
			WEditor changedEditor = (WEditor)evt.getSource();
			Object value = evt.getNewValue();

			if (mapEditorParameter.containsKey(changedEditor)) {
				MKanbanParameter changedParam = mapEditorParameter.get(changedEditor);
				changedEditor.setValue(value);
				changedParam.setValue(value);
			} else if (mapEditorToParameter.containsKey(changedEditor)) {
				MKanbanParameter changedParamTo = mapEditorToParameter.get(changedEditor);
				changedParamTo.setValueTo(value);
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
		MPInstance instance = new MPInstance(Env.getCtx(), processId, 0);
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
		if (boardButtonsDiv != null) {
			northPanelHbox.removeChild(boardButtonsDiv);
			boardButtonsDiv = null;
		}
		if (boardParamsDiv != null) {
			northPanelHbox.removeChild(boardParamsDiv);
			boardParamsDiv = null;
		}
		createKanbanBoardPanel();
		centerVLayout.appendChild(kanbanPanel);
	}

}

