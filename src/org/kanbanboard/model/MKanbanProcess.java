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
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/

package org.kanbanboard.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MProcess;

public class MKanbanProcess extends X_KDB_KanbanProcess{

	private static final long serialVersionUID = 1974920563608031327L;


	public MKanbanProcess(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	public MKanbanProcess(Properties ctx, int KDB_KanbanProcess_ID, String trxName) {
		super(ctx, KDB_KanbanProcess_ID, trxName);
	}
	
	public MProcess getProcess() {
		return MProcess.get(getAD_Process_ID());
	}
	
	public String getProcessName() {
		return getProcess().get_Translation(MProcess.COLUMNNAME_Name);
	}

}
