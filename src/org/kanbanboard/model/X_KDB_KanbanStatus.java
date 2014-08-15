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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/** Generated Model for KDB_KanbanStatus
 *  @author iDempiere (generated) 
 *  @version Release 2.0 - $Id$ */
public class X_KDB_KanbanStatus extends PO implements I_KDB_KanbanStatus, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140508L;

    /** Standard Constructor */
    public X_KDB_KanbanStatus (Properties ctx, int KDB_KanbanStatus_ID, String trxName)
    {
      super (ctx, KDB_KanbanStatus_ID, trxName);
      /** if (KDB_KanbanStatus_ID == 0)
        {
			setKDB_KanbanBoard_ID (0);
			setKDB_KanbanStatus_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_KDB_KanbanStatus (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_KDB_KanbanStatus[")
        .append(get_ID()).append("]");
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

	/** Set Is Show Over.
		@param IsShowOver 
		It determines if a status shows more cards than the max number allowed
	  */
	public void setIsShowOver (boolean IsShowOver)
	{
		set_Value (COLUMNNAME_IsShowOver, Boolean.valueOf(IsShowOver));
	}

	/** Get Is Show Over.
		@return It determines if a status shows more cards than the max number allowed
	  */
	public boolean isShowOver () 
	{
		Object oo = get_Value(COLUMNNAME_IsShowOver);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** Set Kanban Status.
		@param KDB_KanbanStatus_ID Kanban Status	  */
	public void setKDB_KanbanStatus_ID (int KDB_KanbanStatus_ID)
	{
		if (KDB_KanbanStatus_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanStatus_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanStatus_ID, Integer.valueOf(KDB_KanbanStatus_ID));
	}

	/** Get Kanban Status.
		@return Kanban Status	  */
	public int getKDB_KanbanStatus_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_KanbanStatus_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set KDB_KanbanStatus_UU.
		@param KDB_KanbanStatus_UU KDB_KanbanStatus_UU	  */
	public void setKDB_KanbanStatus_UU (String KDB_KanbanStatus_UU)
	{
		set_Value (COLUMNNAME_KDB_KanbanStatus_UU, KDB_KanbanStatus_UU);
	}

	/** Get KDB_KanbanStatus_UU.
		@return KDB_KanbanStatus_UU	  */
	public String getKDB_KanbanStatus_UU () 
	{
		return (String)get_Value(COLUMNNAME_KDB_KanbanStatus_UU);
	}

	/** Set Status Ref List Value.
		@param KDB_StatusListValue 
		It shows the value of the reference list that represents the statuses
	  */
	public void setKDB_StatusListValue (String KDB_StatusListValue)
	{
		set_Value (COLUMNNAME_KDB_StatusListValue, KDB_StatusListValue);
	}

	/** Get Status Ref List Value.
		@return It shows the value of the reference list that represents the statuses
	  */
	public String getKDB_StatusListValue () 
	{
		return (String)get_Value(COLUMNNAME_KDB_StatusListValue);
	}

	/** Set Kanban Status Table ID.
		@param KDB_StatusTableID 
		Is the reference to the ID of the table that defines the statuses
	  */
	public void setKDB_StatusTableID (String KDB_StatusTableID)
	{
		set_Value (COLUMNNAME_KDB_StatusTableID, KDB_StatusTableID);
	}

	/** Get Kanban Status Table ID.
		@return Is the reference to the ID of the table that defines the statuses
	  */
	public String getKDB_StatusTableID () 
	{
		return (String)get_Value(COLUMNNAME_KDB_StatusTableID);
	}

	/** Set Max Number Cards.
		@param MaxNumberCards 
		Maximum number of cards in an spececific status
	  */
	public void setMaxNumberCards (BigDecimal MaxNumberCards)
	{
		set_Value (COLUMNNAME_MaxNumberCards, MaxNumberCards);
	}

	/** Get Max Number Cards.
		@return Maximum number of cards in an spececific status
	  */
	public BigDecimal getMaxNumberCards () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MaxNumberCards);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Sequence.
		@param SeqNo 
		Method of ordering records; lowest number comes first
	  */
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	public int getSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set SQLStatement.
		@param SQLStatement SQLStatement	  */
	public void setSQLStatement (String SQLStatement)
	{
		set_Value (COLUMNNAME_SQLStatement, SQLStatement);
	}

	/** Get SQLStatement.
		@return SQLStatement	  */
	public String getSQLStatement () 
	{
		return (String)get_Value(COLUMNNAME_SQLStatement);
	}

	/** Set Status Alias.
		@param StatusAlias Status Alias	  */
	public void setStatusAlias (String StatusAlias)
	{
		set_Value (COLUMNNAME_StatusAlias, StatusAlias);
	}

	/** Get Status Alias.
		@return Status Alias	  */
	public String getStatusAlias () 
	{
		return (String)get_Value(COLUMNNAME_StatusAlias);
	}
}