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

package org.idempiere.apps.form;

import org.kanbanboard.model.MKanbanStatus;

public class KanbanStatus extends KanbanBoard{
	
	public boolean saveStatuses() {
		if(getKanbanBoard()!=null)
			return getKanbanBoard().saveStatuses();
		return false;
	}
	
	public boolean deleteStatus(MKanbanStatus status){
		return getKanbanBoard().deleteStatus(status);
	}
	
	public void swapStatuses(MKanbanStatus startStatus, MKanbanStatus endStatus){
		int startSeqNo = startStatus.getSeqNo();
		int endSeqNo = endStatus.getSeqNo();
		for(MKanbanStatus status: getStatuses()){
			if(status.get_ID()==startStatus.get_ID())
				status.setSeqNo(endSeqNo);
			else if(status.get_ID()==endStatus.get_ID())
				status.setSeqNo(startSeqNo);
		}
	}
}
