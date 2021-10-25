package org.kanbanboard.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MKanbanSwimlane extends X_KDB_KanbanSwimlanes {

	/**
	 * 
	 */
	private static final long serialVersionUID = -634627635323262721L;

	public MKanbanSwimlane(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	public MKanbanSwimlane(Properties ctx, int KDB_KanbanSwimlanes_ID, String trxName) {
		super(ctx, KDB_KanbanSwimlanes_ID, trxName);
	}

}
