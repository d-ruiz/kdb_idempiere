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

/** Generated Model for KDB_Parameter
 *  @author iDempiere (generated) 
 *  @version Release 5.1 - $Id$ */
public class X_KDB_Parameter extends PO implements I_KDB_Parameter, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190127L;

    /** Standard Constructor */
    public X_KDB_Parameter (Properties ctx, int KDB_Parameter_ID, String trxName)
    {
      super (ctx, KDB_Parameter_ID, trxName);
      /** if (KDB_Parameter_ID == 0)
        {
			setIsRange (false);
			setKDB_ColumnTable_ID (0);
			setKDB_KanbanBoard_ID (0);
			setKDB_Parameter_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_KDB_Parameter (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_KDB_Parameter[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Default Logic.
		@param DefaultValue 
		Default value hierarchy, separated by ;
	  */
	public void setDefaultValue (String DefaultValue)
	{
		set_Value (COLUMNNAME_DefaultValue, DefaultValue);
	}

	/** Get Default Logic.
		@return Default value hierarchy, separated by ;
	  */
	public String getDefaultValue () 
	{
		return (String)get_Value(COLUMNNAME_DefaultValue);
	}

	/** Set Default Logic 2.
		@param DefaultValue2 
		Default value hierarchy, separated by ;
	  */
	public void setDefaultValue2 (String DefaultValue2)
	{
		set_Value (COLUMNNAME_DefaultValue2, DefaultValue2);
	}

	/** Get Default Logic 2.
		@return Default value hierarchy, separated by ;
	  */
	public String getDefaultValue2 () 
	{
		return (String)get_Value(COLUMNNAME_DefaultValue2);
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

	/** Set Range.
		@param IsRange 
		The parameter is a range of values
	  */
	public void setIsRange (boolean IsRange)
	{
		set_Value (COLUMNNAME_IsRange, Boolean.valueOf(IsRange));
	}

	/** Get Range.
		@return The parameter is a range of values
	  */
	public boolean isRange () 
	{
		Object oo = get_Value(COLUMNNAME_IsRange);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** Set Kanban Parameter.
		@param KDB_Parameter_ID Kanban Parameter	  */
	public void setKDB_Parameter_ID (int KDB_Parameter_ID)
	{
		if (KDB_Parameter_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_KDB_Parameter_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_KDB_Parameter_ID, Integer.valueOf(KDB_Parameter_ID));
	}

	/** Get Kanban Parameter.
		@return Kanban Parameter	  */
	public int getKDB_Parameter_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_Parameter_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set KDB_Parameter_UU.
		@param KDB_Parameter_UU KDB_Parameter_UU	  */
	public void setKDB_Parameter_UU (String KDB_Parameter_UU)
	{
		set_Value (COLUMNNAME_KDB_Parameter_UU, KDB_Parameter_UU);
	}

	/** Get KDB_Parameter_UU.
		@return KDB_Parameter_UU	  */
	public String getKDB_Parameter_UU () 
	{
		return (String)get_Value(COLUMNNAME_KDB_Parameter_UU);
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

	/** QueryOperator AD_Reference_ID=200061 */
	public static final int QUERYOPERATOR_AD_Reference_ID=200061;
	/** Like = Like */
	public static final String QUERYOPERATOR_Like = "Like";
	/** = = = */
	public static final String QUERYOPERATOR_Eq = "=";
	/** > = > */
	public static final String QUERYOPERATOR_Gt = ">";
	/** >= = >= */
	public static final String QUERYOPERATOR_GtEq = ">=";
	/** < = < */
	public static final String QUERYOPERATOR_Le = "<";
	/** <= = <= */
	public static final String QUERYOPERATOR_LeEq = "<=";
	/** != = != */
	public static final String QUERYOPERATOR_NotEq = "!=";
	/** Full Like = LIKE */
	public static final String QUERYOPERATOR_FullLike = "LIKE";
	/** Set Query Operator.
		@param QueryOperator 
		Operator for database query
	  */
	public void setQueryOperator (String QueryOperator)
	{

		set_Value (COLUMNNAME_QueryOperator, QueryOperator);
	}

	/** Get Query Operator.
		@return Operator for database query
	  */
	public String getQueryOperator () 
	{
		return (String)get_Value(COLUMNNAME_QueryOperator);
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
}