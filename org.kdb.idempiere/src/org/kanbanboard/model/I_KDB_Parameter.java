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

/** Generated Interface for KDB_Parameter
 *  @author iDempiere (generated) 
 *  @version Release 5.1
 */
@SuppressWarnings("all")
public interface I_KDB_Parameter 
{

    /** TableName=KDB_Parameter */
    public static final String Table_Name = "KDB_Parameter";

    /** AD_Table_ID=1000012 */
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

    /** Column name DefaultValue */
    public static final String COLUMNNAME_DefaultValue = "DefaultValue";

	/** Set Default Logic.
	  * Default value hierarchy, separated by ;

	  */
	public void setDefaultValue (String DefaultValue);

	/** Get Default Logic.
	  * Default value hierarchy, separated by ;

	  */
	public String getDefaultValue();

    /** Column name DefaultValue2 */
    public static final String COLUMNNAME_DefaultValue2 = "DefaultValue2";

	/** Set Default Logic 2.
	  * Default value hierarchy, separated by ;

	  */
	public void setDefaultValue2 (String DefaultValue2);

	/** Get Default Logic 2.
	  * Default value hierarchy, separated by ;

	  */
	public String getDefaultValue2();

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

    /** Column name IsRange */
    public static final String COLUMNNAME_IsRange = "IsRange";

	/** Set Range.
	  * The parameter is a range of values
	  */
	public void setIsRange (boolean IsRange);

	/** Get Range.
	  * The parameter is a range of values
	  */
	public boolean isRange();

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

	public org.kanbanboard.model.I_KDB_KanbanBoard getKDB_KanbanBoard() throws RuntimeException;

    /** Column name KDB_Parameter_ID */
    public static final String COLUMNNAME_KDB_Parameter_ID = "KDB_Parameter_ID";

	/** Set Kanban Parameter	  */
	public void setKDB_Parameter_ID (int KDB_Parameter_ID);

	/** Get Kanban Parameter	  */
	public int getKDB_Parameter_ID();

    /** Column name KDB_Parameter_UU */
    public static final String COLUMNNAME_KDB_Parameter_UU = "KDB_Parameter_UU";

	/** Set KDB_Parameter_UU	  */
	public void setKDB_Parameter_UU (String KDB_Parameter_UU);

	/** Get KDB_Parameter_UU	  */
	public String getKDB_Parameter_UU();

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

    /** Column name QueryOperator */
    public static final String COLUMNNAME_QueryOperator = "QueryOperator";

	/** Set Query Operator.
	  * Operator for database query
	  */
	public void setQueryOperator (String QueryOperator);

	/** Get Query Operator.
	  * Operator for database query
	  */
	public String getQueryOperator();

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
