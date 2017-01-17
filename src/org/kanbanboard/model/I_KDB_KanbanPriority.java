/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.kanbanboard.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for KDB_KanbanPriority
 *  @author iDempiere (generated) 
 *  @version Release 3.1
 */
@SuppressWarnings("all")
public interface I_KDB_KanbanPriority 
{

    /** TableName=KDB_KanbanPriority */
    public static final String Table_Name = "KDB_KanbanPriority";

    /** AD_Table_ID=1000032 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 6 - System - Client 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(6);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name KDB_KanbanBoard_ID */
    public static final String COLUMNNAME_KDB_KanbanBoard_ID = "KDB_KanbanBoard_ID";

	/** Set Kanban Board	  */
	public void setKDB_KanbanBoard_ID (int KDB_KanbanBoard_ID);

	/** Get Kanban Board	  */
	public int getKDB_KanbanBoard_ID();

	public org.kanbanboard.model.I_KDB_KanbanBoard getKDB_KanbanBoard() throws RuntimeException;

    /** Column name KDB_KanbanPriority_ID */
    public static final String COLUMNNAME_KDB_KanbanPriority_ID = "KDB_KanbanPriority_ID";

	/** Set Kanban Priority	  */
	public void setKDB_KanbanPriority_ID (int KDB_KanbanPriority_ID);

	/** Get Kanban Priority	  */
	public int getKDB_KanbanPriority_ID();

    /** Column name KDB_KanbanPriority_UU */
    public static final String COLUMNNAME_KDB_KanbanPriority_UU = "KDB_KanbanPriority_UU";

	/** Set KDB_KanbanPriority_UU	  */
	public void setKDB_KanbanPriority_UU (String KDB_KanbanPriority_UU);

	/** Get KDB_KanbanPriority_UU	  */
	public String getKDB_KanbanPriority_UU();

    /** Column name KDB_PriorityColor_ID */
    public static final String COLUMNNAME_KDB_PriorityColor_ID = "KDB_PriorityColor_ID";

	/** Set Priority Color	  */
	public void setKDB_PriorityColor_ID (int KDB_PriorityColor_ID);

	/** Get Priority Color	  */
	public int getKDB_PriorityColor_ID();

	public org.compiere.model.I_AD_PrintColor getKDB_PriorityColor() throws RuntimeException;

    /** Column name KDB_PriorityTextColor_ID */
    public static final String COLUMNNAME_KDB_PriorityTextColor_ID = "KDB_PriorityTextColor_ID";

	/** Set Text Color.
	  * Text color in the card
	  */
	public void setKDB_PriorityTextColor_ID (int KDB_PriorityTextColor_ID);

	/** Get Text Color.
	  * Text color in the card
	  */
	public int getKDB_PriorityTextColor_ID();

	public org.compiere.model.I_AD_PrintColor getKDB_PriorityTextColor() throws RuntimeException;

    /** Column name MaxValue */
    public static final String COLUMNNAME_MaxValue = "MaxValue";

	/** Set Max Value	  */
	public void setMaxValue (int MaxValue);

	/** Get Max Value	  */
	public int getMaxValue();

    /** Column name MinValue */
    public static final String COLUMNNAME_MinValue = "MinValue";

	/** Set Min Value	  */
	public void setMinValue (int MinValue);

	/** Get Min Value	  */
	public int getMinValue();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
}
