package org.idempiere.apps.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
	
	
	public void resetStatusProperties() {
		// TODO Auto-generated method stub
		kanbanBoard.resetStatusProperties();
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
	
	public boolean swapCard(MKanbanStatus startStatus, MKanbanStatus endStatus, MKanbanCard card){
		
		boolean statusChanged = card.changeStatus(kanbanBoard.getStatusColumn().getColumnName(), endStatus.getStatusValue());
		if(statusChanged){
			startStatus.removeRecord(card);
			endStatus.addRecord(card);
			card.setBelongingStatus(endStatus);
		}
		return statusChanged;
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
}
