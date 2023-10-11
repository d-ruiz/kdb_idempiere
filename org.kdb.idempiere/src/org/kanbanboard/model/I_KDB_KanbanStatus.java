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

/** Generated Interface for KDB_KanbanStatus
 *  @author iDempiere (generated) 
 *  @version Release 2.0
 */
@SuppressWarnings("all")
public interface I_KDB_KanbanStatus 
{

    /** TableName=KDB_KanbanStatus */
    public static final String Table_Name = "KDB_KanbanStatus";

    /** AD_Table_ID=1000008 */
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

    /** Column name IsShowOver */
    public static final String COLUMNNAME_IsShowOver = "IsShowOver";

	/** Set Is Show Over.
	  * It determines if a status shows more cards than the max number allowed
	  */
	public void setIsShowOver (boolean IsShowOver);

	/** Get Is Show Over.
	  * It determines if a status shows more cards than the max number allowed
	  */
	public boolean isShowOver();

    /** Column name KDB_KanbanBoard_ID */
    public static final String COLUMNNAME_KDB_KanbanBoard_ID = "KDB_KanbanBoard_ID";

	/** Set Kanban Board	  */
	public void setKDB_KanbanBoard_ID (int KDB_KanbanBoard_ID);

	/** Get Kanban Board	  */
	public int getKDB_KanbanBoard_ID();

	public org.kanbanboard.model.I_KDB_KanbanBoard getKDB_KanbanBoard() throws RuntimeException;

    /** Column name KDB_KanbanStatus_ID */
    public static final String COLUMNNAME_KDB_KanbanStatus_ID = "KDB_KanbanStatus_ID";

	/** Set Kanban Status	  */
	public void setKDB_KanbanStatus_ID (int KDB_KanbanStatus_ID);

	/** Get Kanban Status	  */
	public int getKDB_KanbanStatus_ID();

    /** Column name KDB_KanbanStatus_UU */
    public static final String COLUMNNAME_KDB_KanbanStatus_UU = "KDB_KanbanStatus_UU";

	/** Set KDB_KanbanStatus_UU	  */
	public void setKDB_KanbanStatus_UU (String KDB_KanbanStatus_UU);

	/** Get KDB_KanbanStatus_UU	  */
	public String getKDB_KanbanStatus_UU();

    /** Column name KDB_StatusListValue */
    public static final String COLUMNNAME_KDB_StatusListValue = "KDB_StatusListValue";

	/** Set Status Ref List Value.
	  * It shows the value of the reference list that represents the statuses
	  */
	public void setKDB_StatusListValue (String KDB_StatusListValue);

	/** Get Status Ref List Value.
	  * It shows the value of the reference list that represents the statuses
	  */
	public String getKDB_StatusListValue();

    /** Column name KDB_StatusTableID */
    public static final String COLUMNNAME_KDB_StatusTableID = "KDB_StatusTableID";

	/** Set Kanban Status Table ID.
	  * Is the reference to the ID of the table that defines the statuses
	  */
	public void setKDB_StatusTableID (String KDB_StatusTableID);

	/** Get Kanban Status Table ID.
	  * Is the reference to the ID of the table that defines the statuses
	  */
	public String getKDB_StatusTableID();

    /** Column name MaxNumberCards */
    public static final String COLUMNNAME_MaxNumberCards = "MaxNumberCards";

	/** Set Max Number Cards.
	  * Maximum number of cards in an spececific status
	  */
	public void setMaxNumberCards (BigDecimal MaxNumberCards);

	/** Get Max Number Cards.
	  * Maximum number of cards in an spececific status
	  */
	public BigDecimal getMaxNumberCards();

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

    /** Column name SeqNo */
    public static final String COLUMNNAME_SeqNo = "SeqNo";

	/** Set Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public void setSeqNo (int SeqNo);

	/** Get Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public int getSeqNo();

    /** Column name SQLStatement */
    public static final String COLUMNNAME_SQLStatement = "SQLStatement";

	/** Set SQLStatement	  */
	public void setSQLStatement (String SQLStatement);

	/** Get SQLStatement	  */
	public String getSQLStatement();

    /** Column name StatusAlias */
    public static final String COLUMNNAME_StatusAlias = "StatusAlias";

	/** Set Status Alias	  */
	public void setStatusAlias (String StatusAlias);

	/** Get Status Alias	  */
	public String getStatusAlias();

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
