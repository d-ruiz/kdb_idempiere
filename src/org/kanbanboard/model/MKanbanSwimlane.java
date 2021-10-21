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
 * - Diego Ruiz   													   *
 **********************************************************************/
package org.kanbanboard.model;

public class MKanbanSwimlane {

	private int totalNumberOfCards = 0;
	private String name;
	private String databaseValue;
	private boolean printed;
	
	public MKanbanSwimlane(Object databaseValue) {
		this.databaseValue = String.valueOf(databaseValue);
	}
	
	public int getTotalNumberOfCards() {
		return totalNumberOfCards;
	}

	public void addOneCard() {
		this.totalNumberOfCards++;
	}
	
	public void removeOneCard() {
		this.totalNumberOfCards--;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDatabaseValue() {
		return databaseValue;
	}

	public void setDatabaseValue(String databaseValue) {
		this.databaseValue = databaseValue;
	}

	public boolean isPrinted() {
		return printed;
	}

	public void setPrinted(boolean printed) {
		this.printed = printed;
	}
	
}