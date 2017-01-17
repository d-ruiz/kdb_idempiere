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

/** Generated Model for KDB_KanbanPriority
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_KDB_KanbanPriority extends PO implements I_KDB_KanbanPriority, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20151209L;

    /** Standard Constructor */
    public X_KDB_KanbanPriority (Properties ctx, int KDB_KanbanPriority_ID, String trxName)
    {
      super (ctx, KDB_KanbanPriority_ID, trxName);
      /** if (KDB_KanbanPriority_ID == 0)
        {
			setKDB_KanbanBoard_ID (0);
			setKDB_KanbanPriority_ID (0);
        } */
    }

    /** Load Constructor */
    public X_KDB_KanbanPriority (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_KDB_KanbanPriority[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Kanban Priority.
		@param KDB_KanbanPriority_ID Kanban Priority	  */
	public void setKDB_KanbanPriority_ID (int KDB_KanbanPriority_ID)
	{
		if (KDB_KanbanPriority_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanPriority_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanPriority_ID, Integer.valueOf(KDB_KanbanPriority_ID));
	}

	/** Get Kanban Priority.
		@return Kanban Priority	  */
	public int getKDB_KanbanPriority_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_KanbanPriority_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set KDB_KanbanPriority_UU.
		@param KDB_KanbanPriority_UU KDB_KanbanPriority_UU	  */
	public void setKDB_KanbanPriority_UU (String KDB_KanbanPriority_UU)
	{
		set_Value (COLUMNNAME_KDB_KanbanPriority_UU, KDB_KanbanPriority_UU);
	}

	/** Get KDB_KanbanPriority_UU.
		@return KDB_KanbanPriority_UU	  */
	public String getKDB_KanbanPriority_UU () 
	{
		return (String)get_Value(COLUMNNAME_KDB_KanbanPriority_UU);
	}

	public org.compiere.model.I_AD_PrintColor getKDB_PriorityColor() throws RuntimeException
    {
		return (org.compiere.model.I_AD_PrintColor)MTable.get(getCtx(), org.compiere.model.I_AD_PrintColor.Table_Name)
			.getPO(getKDB_PriorityColor_ID(), get_TrxName());	}

	/** Set Priority Color.
		@param KDB_PriorityColor_ID Priority Color	  */
	public void setKDB_PriorityColor_ID (int KDB_PriorityColor_ID)
	{
		if (KDB_PriorityColor_ID < 1) 
			set_Value (COLUMNNAME_KDB_PriorityColor_ID, null);
		else 
			set_Value (COLUMNNAME_KDB_PriorityColor_ID, Integer.valueOf(KDB_PriorityColor_ID));
	}

	/** Get Priority Color.
		@return Priority Color	  */
	public int getKDB_PriorityColor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_PriorityColor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_PrintColor getKDB_PriorityTextColor() throws RuntimeException
    {
		return (org.compiere.model.I_AD_PrintColor)MTable.get(getCtx(), org.compiere.model.I_AD_PrintColor.Table_Name)
			.getPO(getKDB_PriorityTextColor_ID(), get_TrxName());	}

	/** Set Text Color.
		@param KDB_PriorityTextColor_ID 
		Text color in the card
	  */
	public void setKDB_PriorityTextColor_ID (int KDB_PriorityTextColor_ID)
	{
		if (KDB_PriorityTextColor_ID < 1) 
			set_Value (COLUMNNAME_KDB_PriorityTextColor_ID, null);
		else 
			set_Value (COLUMNNAME_KDB_PriorityTextColor_ID, Integer.valueOf(KDB_PriorityTextColor_ID));
	}

	/** Get Text Color.
		@return Text color in the card
	  */
	public int getKDB_PriorityTextColor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_PriorityTextColor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Max Value.
		@param MaxValue Max Value	  */
	public void setMaxValue (int MaxValue)
	{
		set_Value (COLUMNNAME_MaxValue, Integer.valueOf(MaxValue));
	}

	/** Get Max Value.
		@return Max Value	  */
	public int getMaxValue () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MaxValue);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Min Value.
		@param MinValue Min Value	  */
	public void setMinValue (int MinValue)
	{
		set_Value (COLUMNNAME_MinValue, Integer.valueOf(MinValue));
	}

	/** Get Min Value.
		@return Min Value	  */
	public int getMinValue () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MinValue);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}