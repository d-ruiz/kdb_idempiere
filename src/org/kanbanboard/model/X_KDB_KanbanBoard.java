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
/** Generated Model - DO NOT CHANGE */
package org.kanbanboard.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Model for KDB_KanbanBoard
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_KDB_KanbanBoard extends PO implements I_KDB_KanbanBoard, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20191002L;

    /** Standard Constructor */
    public X_KDB_KanbanBoard (Properties ctx, int KDB_KanbanBoard_ID, String trxName)
    {
      super (ctx, KDB_KanbanBoard_ID, trxName);
      /** if (KDB_KanbanBoard_ID == 0)
        {
			setAD_Table_ID (0);
			setIsHtml (false);
// N
			setKDB_KanbanBoard_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_KDB_KanbanBoard (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 6 - System - Client 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_KDB_KanbanBoard[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Table)MTable.get(getCtx(), org.compiere.model.I_AD_Table.Table_Name)
			.getPO(getAD_Table_ID(), get_TrxName());	}

	/** Set Table.
		@param AD_Table_ID 
		Database Table information
	  */
	public void setAD_Table_ID (int AD_Table_ID)
	{
		if (AD_Table_ID < 1) 
			set_Value (COLUMNNAME_AD_Table_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Table_ID, Integer.valueOf(AD_Table_ID));
	}

	/** Get Table.
		@return Database Table information
	  */
	public int getAD_Table_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Table_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Create Statuses.
		@param CreateStatuses Create Statuses	  */
	public void setCreateStatuses (String CreateStatuses)
	{
		set_Value (COLUMNNAME_CreateStatuses, CreateStatuses);
	}

	/** Get Create Statuses.
		@return Create Statuses	  */
	public String getCreateStatuses () 
	{
		return (String)get_Value(COLUMNNAME_CreateStatuses);
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** Set HTML.
		@param IsHtml 
		Text has HTML tags
	  */
	public void setIsHtml (boolean IsHtml)
	{
		set_Value (COLUMNNAME_IsHtml, Boolean.valueOf(IsHtml));
	}

	/** Get HTML.
		@return Text has HTML tags
	  */
	public boolean isHtml () 
	{
		Object oo = get_Value(COLUMNNAME_IsHtml);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public org.compiere.model.I_AD_PrintColor getKDB_BackgroundColor() throws RuntimeException
    {
		return (org.compiere.model.I_AD_PrintColor)MTable.get(getCtx(), org.compiere.model.I_AD_PrintColor.Table_Name)
			.getPO(getKDB_BackgroundColor_ID(), get_TrxName());	}

	/** Set Background Color.
		@param KDB_BackgroundColor_ID Background Color	  */
	public void setKDB_BackgroundColor_ID (int KDB_BackgroundColor_ID)
	{
		if (KDB_BackgroundColor_ID < 1) 
			set_Value (COLUMNNAME_KDB_BackgroundColor_ID, null);
		else 
			set_Value (COLUMNNAME_KDB_BackgroundColor_ID, Integer.valueOf(KDB_BackgroundColor_ID));
	}

	/** Get Background Color.
		@return Background Color	  */
	public int getKDB_BackgroundColor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_BackgroundColor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Kanban Card Tooltip.
		@param KDB_CardTooltip 
		Message shown when the user hovers the pointer over a card
	  */
	public void setKDB_CardTooltip (String KDB_CardTooltip)
	{
		set_Value (COLUMNNAME_KDB_CardTooltip, KDB_CardTooltip);
	}

	/** Get Kanban Card Tooltip.
		@return Message shown when the user hovers the pointer over a card
	  */
	public String getKDB_CardTooltip () 
	{
		return (String)get_Value(COLUMNNAME_KDB_CardTooltip);
	}

	public org.compiere.model.I_AD_Column getKDB_ColumnList() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Column)MTable.get(getCtx(), org.compiere.model.I_AD_Column.Table_Name)
			.getPO(getKDB_ColumnList_ID(), get_TrxName());	}

	/** Set Column List.
		@param KDB_ColumnList_ID Column List	  */
	public void setKDB_ColumnList_ID (int KDB_ColumnList_ID)
	{
		if (KDB_ColumnList_ID < 1) 
			set_Value (COLUMNNAME_KDB_ColumnList_ID, null);
		else 
			set_Value (COLUMNNAME_KDB_ColumnList_ID, Integer.valueOf(KDB_ColumnList_ID));
	}

	/** Get Column List.
		@return Column List	  */
	public int getKDB_ColumnList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_ColumnList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Column getKDB_ColumnTable() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Column)MTable.get(getCtx(), org.compiere.model.I_AD_Column.Table_Name)
			.getPO(getKDB_ColumnTable_ID(), get_TrxName());	}

	/** Set Column Table.
		@param KDB_ColumnTable_ID Column Table	  */
	public void setKDB_ColumnTable_ID (int KDB_ColumnTable_ID)
	{
		if (KDB_ColumnTable_ID < 1) 
			set_Value (COLUMNNAME_KDB_ColumnTable_ID, null);
		else 
			set_Value (COLUMNNAME_KDB_ColumnTable_ID, Integer.valueOf(KDB_ColumnTable_ID));
	}

	/** Get Column Table.
		@return Column Table	  */
	public int getKDB_ColumnTable_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_ColumnTable_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Kanban Board.
		@param KDB_KanbanBoard_ID Kanban Board	  */
	public void setKDB_KanbanBoard_ID (int KDB_KanbanBoard_ID)
	{
		if (KDB_KanbanBoard_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanBoard_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanBoard_ID, Integer.valueOf(KDB_KanbanBoard_ID));
	}

	/** Get Kanban Board.
		@return Kanban Board	  */
	public int getKDB_KanbanBoard_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_KanbanBoard_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set KDB_KanbanBoard_UU.
		@param KDB_KanbanBoard_UU KDB_KanbanBoard_UU	  */
	public void setKDB_KanbanBoard_UU (String KDB_KanbanBoard_UU)
	{
		set_Value (COLUMNNAME_KDB_KanbanBoard_UU, KDB_KanbanBoard_UU);
	}

	/** Get KDB_KanbanBoard_UU.
		@return KDB_KanbanBoard_UU	  */
	public String getKDB_KanbanBoard_UU () 
	{
		return (String)get_Value(COLUMNNAME_KDB_KanbanBoard_UU);
	}

	/** Set Kanban Card Content.
		@param KDB_KanbanCard Kanban Card Content	  */
	public void setKDB_KanbanCard (String KDB_KanbanCard)
	{
		set_Value (COLUMNNAME_KDB_KanbanCard, KDB_KanbanCard);
	}

	/** Get Kanban Card Content.
		@return Kanban Card Content	  */
	public String getKDB_KanbanCard () 
	{
		return (String)get_Value(COLUMNNAME_KDB_KanbanCard);
	}

	/** Set Priority SQL.
		@param KDB_PrioritySQL Priority SQL	  */
	public void setKDB_PrioritySQL (String KDB_PrioritySQL)
	{
		set_Value (COLUMNNAME_KDB_PrioritySQL, KDB_PrioritySQL);
	}

	/** Get Priority SQL.
		@return Priority SQL	  */
	public String getKDB_PrioritySQL () 
	{
		return (String)get_Value(COLUMNNAME_KDB_PrioritySQL);
	}

	/** Set Standard Card Height.
		@param KDB_StdCardHeight 
		Standard Card Height
	  */
	public void setKDB_StdCardHeight (int KDB_StdCardHeight)
	{
		set_Value (COLUMNNAME_KDB_StdCardHeight, Integer.valueOf(KDB_StdCardHeight));
	}

	/** Get Standard Card Height.
		@return Standard Card Height
	  */
	public int getKDB_StdCardHeight () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_StdCardHeight);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Standard Column Width.
		@param KDB_StdColumnWidth 
		Standard Column Width
	  */
	public void setKDB_StdColumnWidth (int KDB_StdColumnWidth)
	{
		set_Value (COLUMNNAME_KDB_StdColumnWidth, Integer.valueOf(KDB_StdColumnWidth));
	}

	/** Get Standard Column Width.
		@return Standard Column Width
	  */
	public int getKDB_StdColumnWidth () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_StdColumnWidth);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Summary Message.
		@param KDB_SummaryMsg 
		Message that will be present on every state of the Kanban Board
	  */
	public void setKDB_SummaryMsg (String KDB_SummaryMsg)
	{
		set_Value (COLUMNNAME_KDB_SummaryMsg, KDB_SummaryMsg);
	}

	/** Get Summary Message.
		@return Message that will be present on every state of the Kanban Board
	  */
	public String getKDB_SummaryMsg () 
	{
		return (String)get_Value(COLUMNNAME_KDB_SummaryMsg);
	}

	/** Set Summary SQL.
		@param KDB_SummarySQL 
		Defines the SQL code that sets the summary that is set on every state of the Kanban Board
	  */
	public void setKDB_SummarySQL (String KDB_SummarySQL)
	{
		set_Value (COLUMNNAME_KDB_SummarySQL, KDB_SummarySQL);
	}

	/** Get Summary SQL.
		@return Defines the SQL code that sets the summary that is set on every state of the Kanban Board
	  */
	public String getKDB_SummarySQL () 
	{
		return (String)get_Value(COLUMNNAME_KDB_SummarySQL);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getName());
    }

	/** Set Sql ORDER BY.
		@param OrderByClause 
		Fully qualified ORDER BY clause
	  */
	public void setOrderByClause (String OrderByClause)
	{
		set_Value (COLUMNNAME_OrderByClause, OrderByClause);
	}

	/** Get Sql ORDER BY.
		@return Fully qualified ORDER BY clause
	  */
	public String getOrderByClause () 
	{
		return (String)get_Value(COLUMNNAME_OrderByClause);
	}

	/** Set Sql WHERE.
		@param WhereClause 
		Fully qualified SQL WHERE clause
	  */
	public void setWhereClause (String WhereClause)
	{
		set_Value (COLUMNNAME_WhereClause, WhereClause);
	}

	/** Get Sql WHERE.
		@return Fully qualified SQL WHERE clause
	  */
	public String getWhereClause () 
	{
		return (String)get_Value(COLUMNNAME_WhereClause);
	}
}