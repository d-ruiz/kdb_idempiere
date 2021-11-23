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

/** Generated Interface for KDB_KanbanSwimlanes
 *  @author iDempiere (generated) 
 *  @version Release 8.2
 */
@SuppressWarnings("all")
public interface I_KDB_KanbanSwimlanes 
{

    /** TableName=KDB_KanbanSwimlanes */
    public static final String Table_Name = "KDB_KanbanSwimlanes";

    /** AD_Table_ID=1000009 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

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

    /** Column name InlineStyle */
    public static final String COLUMNNAME_InlineStyle = "InlineStyle";

	/** Set Inline Style.
	  * CSS Inline Style
	  */
	public void setInlineStyle (String InlineStyle);

	/** Get Inline Style.
	  * CSS Inline Style
	  */
	public String getInlineStyle();

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

    /** Column name IsDefault */
    public static final String COLUMNNAME_IsDefault = "IsDefault";

	/** Set Default.
	  * Default value
	  */
	public void setIsDefault (boolean IsDefault);

	/** Get Default.
	  * Default value
	  */
	public boolean isDefault();

    /** Column name KDB_Column_ID */
    public static final String COLUMNNAME_KDB_Column_ID = "KDB_Column_ID";

	/** Set Swimlane Column	  */
	public void setKDB_Column_ID (int KDB_Column_ID);

	/** Get Swimlane Column	  */
	public int getKDB_Column_ID();

	public org.compiere.model.I_AD_Column getKDB_Column() throws RuntimeException;

    /** Column name KDB_KanbanBoard_ID */
    public static final String COLUMNNAME_KDB_KanbanBoard_ID = "KDB_KanbanBoard_ID";

	/** Set Kanban Board	  */
	public void setKDB_KanbanBoard_ID (int KDB_KanbanBoard_ID);

	/** Get Kanban Board	  */
	public int getKDB_KanbanBoard_ID();

	public org.kanbanboard.model.I_KDB_KanbanBoard getKDB_KanbanBoard() throws RuntimeException;

    /** Column name KDB_KanbanSwimlanes_ID */
    public static final String COLUMNNAME_KDB_KanbanSwimlanes_ID = "KDB_KanbanSwimlanes_ID";

	/** Set Kanban Swimlanes	  */
	public void setKDB_KanbanSwimlanes_ID (int KDB_KanbanSwimlanes_ID);

	/** Get Kanban Swimlanes	  */
	public int getKDB_KanbanSwimlanes_ID();

    /** Column name KDB_KanbanSwimlanes_UU */
    public static final String COLUMNNAME_KDB_KanbanSwimlanes_UU = "KDB_KanbanSwimlanes_UU";

	/** Set KDB_KanbanSwimlanes_UU	  */
	public void setKDB_KanbanSwimlanes_UU (String KDB_KanbanSwimlanes_UU);

	/** Get KDB_KanbanSwimlanes_UU	  */
	public String getKDB_KanbanSwimlanes_UU();

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
