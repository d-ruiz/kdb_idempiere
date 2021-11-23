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

/** Generated Model for KDB_KanbanSwimlanes
 *  @author iDempiere (generated) 
 *  @version Release 8.2 - $Id$ */
public class X_KDB_KanbanSwimlanes extends PO implements I_KDB_KanbanSwimlanes, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211123L;

    /** Standard Constructor */
    public X_KDB_KanbanSwimlanes (Properties ctx, int KDB_KanbanSwimlanes_ID, String trxName)
    {
      super (ctx, KDB_KanbanSwimlanes_ID, trxName);
      /** if (KDB_KanbanSwimlanes_ID == 0)
        {
			setIsDefault (false);
// N
			setKDB_Column_ID (0);
			setKDB_KanbanBoard_ID (0);
			setKDB_KanbanSwimlanes_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_KDB_KanbanSwimlanes (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
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
      StringBuilder sb = new StringBuilder ("X_KDB_KanbanSwimlanes[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
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

	/** Set Inline Style.
		@param InlineStyle 
		CSS Inline Style
	  */
	public void setInlineStyle (String InlineStyle)
	{
		set_Value (COLUMNNAME_InlineStyle, InlineStyle);
	}

	/** Get Inline Style.
		@return CSS Inline Style
	  */
	public String getInlineStyle () 
	{
		return (String)get_Value(COLUMNNAME_InlineStyle);
	}

	/** Set Default.
		@param IsDefault 
		Default value
	  */
	public void setIsDefault (boolean IsDefault)
	{
		set_Value (COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
	}

	/** Get Default.
		@return Default value
	  */
	public boolean isDefault () 
	{
		Object oo = get_Value(COLUMNNAME_IsDefault);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public org.compiere.model.I_AD_Column getKDB_Column() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Column)MTable.get(getCtx(), org.compiere.model.I_AD_Column.Table_Name)
			.getPO(getKDB_Column_ID(), get_TrxName());	}

	/** Set Swimlane Column.
		@param KDB_Column_ID Swimlane Column	  */
	public void setKDB_Column_ID (int KDB_Column_ID)
	{
		if (KDB_Column_ID < 1) 
			set_Value (COLUMNNAME_KDB_Column_ID, null);
		else 
			set_Value (COLUMNNAME_KDB_Column_ID, Integer.valueOf(KDB_Column_ID));
	}

	/** Get Swimlane Column.
		@return Swimlane Column	  */
	public int getKDB_Column_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_Column_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.kanbanboard.model.I_KDB_KanbanBoard getKDB_KanbanBoard() throws RuntimeException
    {
		return (org.kanbanboard.model.I_KDB_KanbanBoard)MTable.get(getCtx(), org.kanbanboard.model.I_KDB_KanbanBoard.Table_Name)
			.getPO(getKDB_KanbanBoard_ID(), get_TrxName());	}

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

	/** Set Kanban Swimlanes.
		@param KDB_KanbanSwimlanes_ID Kanban Swimlanes	  */
	public void setKDB_KanbanSwimlanes_ID (int KDB_KanbanSwimlanes_ID)
	{
		if (KDB_KanbanSwimlanes_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanSwimlanes_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanSwimlanes_ID, Integer.valueOf(KDB_KanbanSwimlanes_ID));
	}

	/** Get Kanban Swimlanes.
		@return Kanban Swimlanes	  */
	public int getKDB_KanbanSwimlanes_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_KanbanSwimlanes_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set KDB_KanbanSwimlanes_UU.
		@param KDB_KanbanSwimlanes_UU KDB_KanbanSwimlanes_UU	  */
	public void setKDB_KanbanSwimlanes_UU (String KDB_KanbanSwimlanes_UU)
	{
		set_Value (COLUMNNAME_KDB_KanbanSwimlanes_UU, KDB_KanbanSwimlanes_UU);
	}

	/** Get KDB_KanbanSwimlanes_UU.
		@return KDB_KanbanSwimlanes_UU	  */
	public String getKDB_KanbanSwimlanes_UU () 
	{
		return (String)get_Value(COLUMNNAME_KDB_KanbanSwimlanes_UU);
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