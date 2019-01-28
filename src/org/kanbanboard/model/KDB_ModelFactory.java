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

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

public class KDB_ModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		if (MKanbanStatus.Table_Name.equals(tableName))
			return MKanbanStatus.class;
		else if (MKanbanPriority.Table_Name.equals(tableName))
			return MKanbanPriority.class;
		else if (MKanbanBoard.Table_Name.equals(tableName))
			return MKanbanBoard.class;
		else if (MKanbanProcess.Table_Name.equals(tableName))
			return MKanbanProcess.class;
		else if (MKanbanParameter.Table_Name.equals(tableName))
			return MKanbanParameter.class;
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		if (MKanbanStatus.Table_Name.equals(tableName))
			return new MKanbanStatus(Env.getCtx(), Record_ID, trxName);
		else if (MKanbanPriority.Table_Name.equals(tableName))
			return new MKanbanPriority(Env.getCtx(), Record_ID, trxName);
		else if (MKanbanBoard.Table_Name.equals(tableName))
			return new MKanbanBoard(Env.getCtx(), Record_ID, trxName);
		else if (MKanbanProcess.Table_Name.equals(tableName))
			return new MKanbanProcess(Env.getCtx(), Record_ID, trxName);
		else if (MKanbanParameter.Table_Name.equals(tableName))
			return new MKanbanParameter(Env.getCtx(), Record_ID, trxName);
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		if (MKanbanStatus.Table_Name.equals(tableName))
			return new MKanbanStatus(Env.getCtx(), rs, trxName);
		else if (MKanbanPriority.Table_Name.equals(tableName))
			return new MKanbanPriority(Env.getCtx(), rs, trxName);
		else if (MKanbanBoard.Table_Name.equals(tableName))
			return new MKanbanBoard(Env.getCtx(), rs, trxName);
		else if (MKanbanProcess.Table_Name.equals(tableName))
			return new MKanbanProcess(Env.getCtx(), rs, trxName);
		else if (MKanbanParameter.Table_Name.equals(tableName))
			return new MKanbanParameter(Env.getCtx(), rs, trxName);
		return null;
	}

}
