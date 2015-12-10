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

/** Generated Model for KDB_KanbanProcess
 *  @author iDempiere (generated) 
 *  @version Release 2.1 - $Id$ */
public class X_KDB_KanbanProcess extends PO implements I_KDB_KanbanProcess, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20150623L;

    /** Standard Constructor */
    public X_KDB_KanbanProcess (Properties ctx, int KDB_KanbanProcess_ID, String trxName)
    {
      super (ctx, KDB_KanbanProcess_ID, trxName);
      /** if (KDB_KanbanProcess_ID == 0)
        {
			setKDB_KanbanBoard_ID (0);
			setKDB_KanbanProcess_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_KDB_KanbanProcess (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_KDB_KanbanProcess[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Process getAD_Process() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Process)MTable.get(getCtx(), org.compiere.model.I_AD_Process.Table_Name)
			.getPO(getAD_Process_ID(), get_TrxName());	}

	/** Set Process.
		@param AD_Process_ID 
		Process or Report
	  */
	public void setAD_Process_ID (int AD_Process_ID)
	{
		if (AD_Process_ID < 1) 
			set_Value (COLUMNNAME_AD_Process_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Process_ID, Integer.valueOf(AD_Process_ID));
	}

	/** Get Process.
		@return Process or Report
	  */
	public int getAD_Process_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Process_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Kanban Associated Process.
		@param KDB_KanbanProcess_ID Kanban Associated Process	  */
	public void setKDB_KanbanProcess_ID (int KDB_KanbanProcess_ID)
	{
		if (KDB_KanbanProcess_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanProcess_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanProcess_ID, Integer.valueOf(KDB_KanbanProcess_ID));
	}

	/** Get Kanban Associated Process.
		@return Kanban Associated Process	  */
	public int getKDB_KanbanProcess_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_KanbanProcess_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set KDB_KanbanProcess_UU.
		@param KDB_KanbanProcess_UU KDB_KanbanProcess_UU	  */
	public void setKDB_KanbanProcess_UU (String KDB_KanbanProcess_UU)
	{
		set_Value (COLUMNNAME_KDB_KanbanProcess_UU, KDB_KanbanProcess_UU);
	}

	/** Get KDB_KanbanProcess_UU.
		@return KDB_KanbanProcess_UU	  */
	public String getKDB_KanbanProcess_UU () 
	{
		return (String)get_Value(COLUMNNAME_KDB_KanbanProcess_UU);
	}

	/** Board = B */
	public static final String KDB_PROCESSSCOPE_Board = "B";
	/** Status = S */
	public static final String KDB_PROCESSSCOPE_Status = "S";
	/** Card = C */
	public static final String KDB_PROCESSSCOPE_Card = "C";
	/** Set Scope.
		@param KDB_ProcessScope 
		Defines the scope of the defined process
	  */
	public void setKDB_ProcessScope (String KDB_ProcessScope)
	{

		set_Value (COLUMNNAME_KDB_ProcessScope, KDB_ProcessScope);
	}

	/** Get Scope.
		@return Defines the scope of the defined process
	  */
	public String getKDB_ProcessScope () 
	{
		return (String)get_Value(COLUMNNAME_KDB_ProcessScope);
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
}