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

package org.kanbanboard.webui.factory;

import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.kanbanboard.webui.apps.form.WKanbanBoard;
import org.kanbanboard.webui.apps.form.WKanbanStatus;

public class KBFormFactory implements IFormFactory{
	
	@Override
	public ADForm newFormInstance(String formName) {

		if (WKanbanBoard.class.getName().equals(formName))
			return new WKanbanBoard().getForm();
		else if (WKanbanStatus.class.getName().equals(formName))
			return new WKanbanStatus().getForm();

		return null;
	}
}
