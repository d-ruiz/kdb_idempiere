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

package org.kanbanboard.apps.form;

import org.kanbanboard.model.MKanbanStatus;

public class KanbanStatus extends KanbanBoard {

	final int SEQNO_RAISE = 10;

	public boolean saveStatuses() {
		if (getKanbanBoard() != null )
			return getKanbanBoard().saveStatuses();
		return false;
	}

	public boolean deleteStatus(MKanbanStatus status) {
		return getKanbanBoard().deleteStatus(status);
	}

	public void swapStatuses(MKanbanStatus startStatus, MKanbanStatus endStatus) {
		int startSeqNo = startStatus.getSeqNo();
		int endSeqNo = endStatus.getSeqNo();
		String statusName = null;
		boolean leftToRight = false;
		
		if (startSeqNo > endSeqNo) {
			startStatus.setSeqNo(endSeqNo);
			statusName = startStatus.getName();
		} else if (startSeqNo < endSeqNo) {
			startStatus.setSeqNo(endSeqNo);
			statusName = startStatus.getName();
			leftToRight=true;
		}
		
		for (MKanbanStatus status : getStatuses()) {
			if (leftToRight) {
				if (status.getSeqNo() > startSeqNo &&
						(status.getSeqNo() <= endSeqNo && !status.getName().equals(statusName)))
					status.setSeqNo(status.getSeqNo()-SEQNO_RAISE);
			} else {
				if (status.getSeqNo() >= endSeqNo &&
						(status.getSeqNo() < startSeqNo && !status.getName().equals(statusName)))
					status.setSeqNo(status.getSeqNo()+SEQNO_RAISE);
			}
		}
	}
}
