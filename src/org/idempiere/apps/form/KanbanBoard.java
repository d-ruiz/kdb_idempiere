package org.idempiere.apps.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.kanbanboard.model.MKanbanBoard;
import org.kanbanboard.model.MKanbanCard;
import org.kanbanboard.model.MKanbanStatus;

public class KanbanBoard {
	
	public static CLogger log = CLogger.getCLogger(KanbanBoard.class);
	/**	Window No			*/
	public int         	m_WindowNo = 0;
	private MKanbanBoard kanbanBoard=null;
	private List<MKanbanStatus> statuses=null;
	
	public int getNumberOfCards() {
		return kanbanBoard.getNumberOfCards();
	}
	


	public ArrayList<KeyNamePair> getProcessList(){
		ArrayList<KeyNamePair> list = new ArrayList<KeyNamePair>();
		
		list.add(new KeyNamePair (-1, ""));
		
		String sql = "SELECT name, KDB_KanbanBoard_ID "
			+ "FROM KDB_KanbanBoard "
			+ "WHERE IsActive='Y' "
			+ "ORDER BY KDB_KanbanBoard_ID";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				KeyNamePair kp = new KeyNamePair (rs.getInt(2), rs.getString(1));
				list.add(kp);
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		return list;
	}
	
	public void setKanbanBoard(int kanbanBoardId){
		//Check if it's it's a new kanban board or the one already selected
		if(kanbanBoard==null||kanbanBoardId!=kanbanBoard.get_ID()){
			kanbanBoard = new MKanbanBoard(Env.getCtx(), kanbanBoardId, null);
			statuses=null;
			//kanbanCardsContent=null;
		}
	}
	
	
	public  List<MKanbanStatus> getStatuses(){
		if(statuses==null){
			setPrintableNames();
			statuses= kanbanBoard.getStatuses();
		}
		orderStatuses();
		return statuses;
	}
	
	
	public void resetCounter() {
		// TODO Auto-generated method stub
		kanbanBoard.resetCounter();
	}
	
	public void orderStatuses(){
		Collections.sort(statuses, new Comparator<MKanbanStatus>() {
			@Override
			public int compare(MKanbanStatus status1, MKanbanStatus status2) {
				return status1.getSeqNo()-(status2.getSeqNo());
			}
		});
		
	}
	
	public void swapStatuses(MKanbanStatus startStatus, MKanbanStatus endStatus){
		int startSeqNo = startStatus.getSeqNo();
		int endSeqNo = endStatus.getSeqNo();
		for(MKanbanStatus status: statuses){
			if(status.get_ID()==startStatus.get_ID())
				status.setSeqNo(endSeqNo);
			else if(status.get_ID()==endStatus.get_ID())
				status.setSeqNo(startSeqNo);
		}

	}
	
	public void swapCard(MKanbanStatus startStatus, MKanbanStatus endStatus, MKanbanCard card){
		
		startStatus.removeRecord(card);
		endStatus.addRecord(card);
		
		boolean statusChanged = card.changeStatus((MTable)kanbanBoard.getAD_Table(), kanbanBoard.getStatusColumn().getColumnName());
		if(statusChanged)
			card.setBelongingStatus(endStatus);
		else
			System.out.println("No se pudo cambiar el estado");//Cambiar por el mensaje de error
	}
	
	public int getAd_Table_id(){
		return kanbanBoard.getAD_Table_ID();
	}
	
	public int getNumberOfStatuses(){
		if(statuses==null)
			return kanbanBoard.getNumberOfStatuses();
		else
			return statuses.size();
	}
	
	public void setPrintableNames(){
		kanbanBoard.setPrintableNames();
	}
	
	public boolean saveStatuses() {
		if(kanbanBoard!=null)
			return kanbanBoard.saveStatuses();
		return false;
	}

	public Object getPOObject(MTable table, int recordID, String trxName){
		PO object = table.getPO(recordID, trxName);
		return object;
	}
	
	public void getCards(String currentStatus, Map<String, MKanbanCard> kanbanCardsContent, MTable table){
		if(kanbanCardsContent.get(currentStatus)!=null){
		/*MKanbanCard cards = kanbanCardsContent.get(currentStatus);
		List<String> values = cards.getRecords();
		for(String curCard: values){
				PO object = table.getPO(Integer.parseInt(curCard), null);
				//System.out.println(object.get_Value("documentno")+currentStatus);//replace with the values people wants
				//kanbanCardsContent.remove(currentStatus);
			}*/
		}
	}
	
	/*public MKanbanCard getKCards(String currentStatus){
		MKanbanCard cards = null;
		if(kanbanCardsContent==null){
			getKanbanCards();
		}
		if(kanbanCardsContent.get(currentStatus)!=null){
		cards = kanbanCardsContent.get(currentStatus);
		/*for(String curCard: cards){
		 * 	MTable table = (MTable) kanbanBoard.getAD_Table();
				PO object = table.getPO(Integer.parseInt(curCard), null);
				System.out.println(object.get_Value("documentno"));//replace with the values people wants
				kanbanCardsContent.remove(currentStatus);
			}
		}
		return cards;
	}*/
	
	/*public List<String> getCards(String currentStatus){
		List <String> cards = null;
		if(kanbanCardsContent==null){
			getKanbanCards();
		}
		if(kanbanCardsContent.get(currentStatus)!=null){
		//cards = kanbanCardsContent.get(currentStatus).getRecords();
		/*for(String curCard: cards){
		 * 	MTable table = (MTable) kanbanBoard.getAD_Table();
				PO object = table.getPO(Integer.parseInt(curCard), null);
				System.out.println(object.get_Value("documentno"));//replace with the values people wants
				kanbanCardsContent.remove(currentStatus);
			}
		}
		return cards;
	}*/
	
	/*public boolean isRefList() {
		return isRefList;
	}*/

	/*public void setRefList(boolean isRefList) {
		this.isRefList = isRefList;
	}*/
}
