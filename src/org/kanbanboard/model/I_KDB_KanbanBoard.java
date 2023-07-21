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

/** Generated Interface for KDB_KanbanBoard
 *  @author iDempiere (generated) 
 *  @version Release 6.2
 */
@SuppressWarnings("all")
public interface I_KDB_KanbanBoard 
{

    /** TableName=KDB_KanbanBoard */
    public static final String Table_Name = "KDB_KanbanBoard";

    /** AD_Table_ID=1000015 */
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

    /** Column name AD_Table_ID */
    public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";

	/** Set Table.
	  * Database Table information
	  */
	public void setAD_Table_ID (int AD_Table_ID);

	/** Get Table.
	  * Database Table information
	  */
	public int getAD_Table_ID();

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException;

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

    /** Column name CreateStatuses */
    public static final String COLUMNNAME_CreateStatuses = "CreateStatuses";

	/** Set Create Statuses	  */
	public void setCreateStatuses (String CreateStatuses);

	/** Get Create Statuses	  */
	public String getCreateStatuses();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name Help */
    public static final String COLUMNNAME_Help = "Help";

	/** Set Comment/Help.
	  * Comment or Hint
	  */
	public void setHelp (String Help);

	/** Get Comment/Help.
	  * Comment or Hint
	  */
	public String getHelp();

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

    /** Column name IsHtml */
    public static final String COLUMNNAME_IsHtml = "IsHtml";

	/** Set HTML.
	  * Text has HTML tags
	  */
	public void setIsHtml (boolean IsHtml);

	/** Get HTML.
	  * Text has HTML tags
	  */
	public boolean isHtml();

    /** Column name KDB_BackgroundColor_ID */
    public static final String COLUMNNAME_KDB_BackgroundColor_ID = "KDB_BackgroundColor_ID";

	/** Set Background Color	  */
	public void setKDB_BackgroundColor_ID (int KDB_BackgroundColor_ID);

	/** Get Background Color	  */
	public int getKDB_BackgroundColor_ID();

	public org.compiere.model.I_AD_PrintColor getKDB_BackgroundColor() throws RuntimeException;

    /** Column name KDB_CardTooltip */
    public static final String COLUMNNAME_KDB_CardTooltip = "KDB_CardTooltip";

	/** Set Kanban Card Tooltip.
	  * Message shown when the user hovers the pointer over a card
	  */
	public void setKDB_CardTooltip (String KDB_CardTooltip);

	/** Get Kanban Card Tooltip.
	  * Message shown when the user hovers the pointer over a card
	  */
	public String getKDB_CardTooltip();

    /** Column name KDB_ColumnList_ID */
    public static final String COLUMNNAME_KDB_ColumnList_ID = "KDB_ColumnList_ID";

	/** Set Column List	  */
	public void setKDB_ColumnList_ID (int KDB_ColumnList_ID);

	/** Get Column List	  */
	public int getKDB_ColumnList_ID();

	public org.compiere.model.I_AD_Column getKDB_ColumnList() throws RuntimeException;

    /** Column name KDB_ColumnTable_ID */
    public static final String COLUMNNAME_KDB_ColumnTable_ID = "KDB_ColumnTable_ID";

	/** Set Column Table	  */
	public void setKDB_ColumnTable_ID (int KDB_ColumnTable_ID);

	/** Get Column Table	  */
	public int getKDB_ColumnTable_ID();

	public org.compiere.model.I_AD_Column getKDB_ColumnTable() throws RuntimeException;

    /** Column name KDB_KanbanBoard_ID */
    public static final String COLUMNNAME_KDB_KanbanBoard_ID = "KDB_KanbanBoard_ID";

	/** Set Kanban Board	  */
	public void setKDB_KanbanBoard_ID (int KDB_KanbanBoard_ID);

	/** Get Kanban Board	  */
	public int getKDB_KanbanBoard_ID();

    /** Column name KDB_KanbanBoard_UU */
    public static final String COLUMNNAME_KDB_KanbanBoard_UU = "KDB_KanbanBoard_UU";

	/** Set KDB_KanbanBoard_UU	  */
	public void setKDB_KanbanBoard_UU (String KDB_KanbanBoard_UU);

	/** Get KDB_KanbanBoard_UU	  */
	public String getKDB_KanbanBoard_UU();

    /** Column name KDB_KanbanCard */
    public static final String COLUMNNAME_KDB_KanbanCard = "KDB_KanbanCard";

	/** Set Kanban Card Content	  */
	public void setKDB_KanbanCard (String KDB_KanbanCard);

	/** Get Kanban Card Content	  */
	public String getKDB_KanbanCard();

    /** Column name KDB_PrioritySQL */
    public static final String COLUMNNAME_KDB_PrioritySQL = "KDB_PrioritySQL";

	/** Set Priority SQL	  */
	public void setKDB_PrioritySQL (String KDB_PrioritySQL);

	/** Get Priority SQL	  */
	public String getKDB_PrioritySQL();

    /** Column name KDB_StdCardHeight */
    public static final String COLUMNNAME_KDB_StdCardHeight = "KDB_StdCardHeight";

	/** Set Standard Card Height.
	  * Standard Card Height
	  */
	public void setKDB_StdCardHeight (int KDB_StdCardHeight);

	/** Get Standard Card Height.
	  * Standard Card Height
	  */
	public int getKDB_StdCardHeight();

    /** Column name KDB_StdColumnWidth */
    public static final String COLUMNNAME_KDB_StdColumnWidth = "KDB_StdColumnWidth";

	/** Set Standard Column Width.
	  * Standard Column Width
	  */
	public void setKDB_StdColumnWidth (int KDB_StdColumnWidth);

	/** Get Standard Column Width.
	  * Standard Column Width
	  */
	public int getKDB_StdColumnWidth();

    /** Column name KDB_SummaryMsg */
    public static final String COLUMNNAME_KDB_SummaryMsg = "KDB_SummaryMsg";

	/** Set Summary Message.
	  * Message that will be present on every state of the Kanban Board
	  */
	public void setKDB_SummaryMsg (String KDB_SummaryMsg);

	/** Get Summary Message.
	  * Message that will be present on every state of the Kanban Board
	  */
	public String getKDB_SummaryMsg();

    /** Column name KDB_SummarySQL */
    public static final String COLUMNNAME_KDB_SummarySQL = "KDB_SummarySQL";

	/** Set Summary SQL.
	  * Defines the SQL code that sets the summary that is set on every state of the Kanban Board
	  */
	public void setKDB_SummarySQL (String KDB_SummarySQL);

	/** Get Summary SQL.
	  * Defines the SQL code that sets the summary that is set on every state of the Kanban Board
	  */
	public String getKDB_SummarySQL();

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name OrderByClause */
    public static final String COLUMNNAME_OrderByClause = "OrderByClause";

	/** Set Sql ORDER BY.
	  * Fully qualified ORDER BY clause
	  */
	public void setOrderByClause (String OrderByClause);

	/** Get Sql ORDER BY.
	  * Fully qualified ORDER BY clause
	  */
	public String getOrderByClause();

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

    /** Column name WhereClause */
    public static final String COLUMNNAME_WhereClause = "WhereClause";

	/** Set Sql WHERE.
	  * Fully qualified SQL WHERE clause
	  */
	public void setWhereClause (String WhereClause);

	/** Get Sql WHERE.
	  * Fully qualified SQL WHERE clause
	  */
	public String getWhereClause();
}
