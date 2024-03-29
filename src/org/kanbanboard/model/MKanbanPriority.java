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

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.Query;
import org.compiere.util.Env;

public class MKanbanPriority extends X_KDB_KanbanPriority{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4695767361669099152L;

	public MKanbanPriority(Properties ctx, int KDB_KanbanPriority_ID, String trxName) {
		super(ctx, KDB_KanbanPriority_ID, trxName);
	}

	public MKanbanPriority(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public static List<MKanbanPriority> getPriorityRules(int KDB_KanbanBoard_ID) {
		return new Query(Env.getCtx(), MKanbanPriority.Table_Name, " KDB_KanbanBoard_ID = ? AND AD_Client_ID IN (0, ?) ", null)
				.setParameters(new Object[]{KDB_KanbanBoard_ID, Env.getAD_Client_ID(Env.getCtx())})
				.setOnlyActiveRecords(true)
				.setOrderBy("MinValue")
				.list();
	}

}
