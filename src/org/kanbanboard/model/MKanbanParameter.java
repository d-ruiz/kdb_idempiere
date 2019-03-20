package org.kanbanboard.model;

import static org.compiere.model.SystemIDs.REFERENCE_YESNO;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.compiere.model.GridField;
import org.compiere.model.GridFieldVO;
import org.compiere.model.Lookup;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.X_AD_InfoColumn;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;

public class MKanbanParameter extends X_KDB_Parameter  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GridField mGridField = null;
	private MKanbanBoard      kanbanBoard;
	private Object value;
	private Object valueTo;

	public MKanbanParameter(Properties ctx, int KDB_Parameter_ID, String trxName) {
		super(ctx, KDB_Parameter_ID, trxName);
	}
	
	public MKanbanParameter(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object getValueTo() {
		return valueTo;
	}

	public void setValueTo(Object value) {
		this.valueTo = value;
	}

	public MKanbanBoard getKanbanBoard() {
		return kanbanBoard;
	}
	
	public void setKanbanBoard(MKanbanBoard kanbanBoard) {
		this.kanbanBoard = kanbanBoard;
	}
	
	public GridField getGridField() {
		return mGridField;
	}
	
	public void setGridField(GridField gridField) {
		
        if (DisplayType.isText(gridField.getVO().displayType)) {
        	// for string fields allow searching long strings - useful for like and similar to searches
        	gridField.getVO().FieldLength = 32767;  // a conservative max literal string - like oracle extended
        	gridField.getVO().DisplayLength = gridField.getVO().FieldLength;
        }
        if (gridField.getVO().displayType == DisplayType.YesNo || gridField.isEncrypted() || gridField.isEncryptedColumn()) {
			// Make Yes-No searchable as list
			GridFieldVO vo = gridField.getVO();
			GridFieldVO ynvo = vo.clone(getCtx(), vo.WindowNo, vo.TabNo, vo.AD_Window_ID, vo.AD_Tab_ID, vo.tabReadOnly);
			ynvo.IsDisplayed = true;
			ynvo.displayType = DisplayType.List;
			ynvo.AD_Reference_Value_ID = REFERENCE_YESNO;

			ynvo.lookupInfo = MLookupFactory.getLookupInfo (ynvo.ctx, ynvo.WindowNo, ynvo.AD_Column_ID, ynvo.displayType,
					Env.getLanguage(ynvo.ctx), ynvo.ColumnName, ynvo.AD_Reference_Value_ID,
					ynvo.IsParent, ynvo.ValidationCode);

			GridField ynfield = new GridField(ynvo);

			// replace the original field by the YN List field
			mGridField = ynfield;
		} else if  (gridField.getVO().displayType == DisplayType.Button) {
			// Make Buttons searchable
			GridFieldVO vo = gridField.getVO();
			if (vo.AD_Reference_Value_ID > 0) {
				GridFieldVO postedvo = vo.clone(getCtx(), vo.WindowNo, vo.TabNo, vo.AD_Window_ID, vo.AD_Tab_ID, vo.tabReadOnly);
				postedvo.IsDisplayed = true;
				postedvo.displayType = DisplayType.List;

				postedvo.lookupInfo = MLookupFactory.getLookupInfo (postedvo.ctx, postedvo.WindowNo, postedvo.AD_Column_ID, postedvo.displayType,
						Env.getLanguage(postedvo.ctx), postedvo.ColumnName, postedvo.AD_Reference_Value_ID,
						postedvo.IsParent, postedvo.ValidationCode);
				GridField postedfield = new GridField(postedvo);

				mGridField = postedfield;
			}
		} else {
			// clone the field and clean gridtab - IDEMPIERE-1105
	        GridField findField = (GridField) gridField.clone(getCtx());
	        if (findField.isLookup()) {
	        	Lookup lookup = findField.getLookup();
	        	if (lookup != null && lookup instanceof MLookup) {
	        		MLookup mLookup = (MLookup) lookup;
	        		mLookup.getLookupInfo().ctx = getCtx();
	        	}
	        }
	        findField.setGridTab(null);
	        mGridField = findField;
		}
        
        //Init default value
        if (mGridField != null) {
    		value = getDefault();
    		if (isRange())
    			valueTo = getDefault2();
        }
	}
	
	private Object getDefault() {
		return getDefaultValue(getDefaultValue());
	}
	
	private Object getDefault2() {
		return getDefaultValue(getDefaultValue2());
	}
	
	private Object getDefaultValue(String defaultStr) {
		/**
		 * 	(c) Parameter DefaultValue		=== similar code in GridField.getDefault ===
		 */
		if (defaultStr != null && !defaultStr.equals("") && !defaultStr.startsWith("@SQL=")) {
			String defStr = "";		//	problem is with texts like 'sss;sss'
			//	It is one or more variables/constants
			StringTokenizer st = new StringTokenizer(defaultStr, ",;", false);
			while (st.hasMoreTokens()) {
				defStr = st.nextToken().trim();
				if (defStr.equals("@SysDate@"))				//	System Time
					return new Timestamp (System.currentTimeMillis());
				else if (defStr.indexOf('@') != -1)			//	it is a variable
					defStr = Env.parseContext(getCtx(), mGridField.getWindowNo(), defStr.trim(), false, false);
				else if (defStr.indexOf("'") != -1)			//	it is a 'String'
					defStr = defStr.replace('\'', ' ').trim();

				if (!defStr.equals("")) {
					return createDefault(defStr);
				 }
			}	//	while more Tokens
		}	//	Default value
		
		return null;
	}
	
	private Object createDefault(String value) {
		//	true NULL
		if (value == null || value.toString().length() == 0 || value.toUpperCase().equals("NULL") ||
				mGridField == null)
			return null;

		try {
			//	IDs & Integer & CreatedBy/UpdatedBy
			if (mGridField.getColumnName().endsWith("atedBy")
					|| (mGridField.getColumnName().endsWith("_ID") && DisplayType.isID(mGridField.getDisplayType()))) {
				try	{ //	defaults -1 => null
					int ii = Integer.parseInt(value);
					if (ii < 0)
						return null;
					return Integer.valueOf(ii);
				} catch (Exception e) {
					log.warning("Cannot parse: " + value + " - " + e.getMessage());
				}
				return Integer.valueOf(0);
			}
			//	Integer
			if (mGridField.getDisplayType() == DisplayType.Integer)
				return Integer.valueOf(value);
			
			//	Number
			if (DisplayType.isNumeric(mGridField.getDisplayType()))
				return new BigDecimal(value);
			
			//	Timestamps
			if (DisplayType.isDate(mGridField.getDisplayType())) {
				// try timestamp format - then date format -- [ 1950305 ]
				java.util.Date date = null;
				SimpleDateFormat dateTimeFormat = DisplayType.getTimestampFormat_Default();
				SimpleDateFormat dateFormat = DisplayType.getDateFormat_JDBC();
				SimpleDateFormat timeFormat = DisplayType.getTimeFormat_Default();
				try {
					if (mGridField.getDisplayType() == DisplayType.Date) {
						date = dateFormat.parse (value);
					} else if (mGridField.getDisplayType() == DisplayType.Time) {
						date = timeFormat.parse (value);
					} else {
						date = dateTimeFormat.parse (value);
					}
				} catch (java.text.ParseException e) {
					date = DisplayType.getDateFormat_JDBC().parse (value);
				}
				return new Timestamp (date.getTime());
			}
			
			//	Boolean
			if (mGridField.getDisplayType() == DisplayType.YesNo)
				return Boolean.valueOf ("Y".equals(value));
			
			//	Default
			return value;
		} catch (Exception e) {
			log.log(Level.SEVERE, mGridField.getColumnName() + " - " + e.getMessage());
		}
		return null;
	}	//	createDefault
	
	public String getSQLClause() {
		if (getValue() == null)
			return null;
		
		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append(getColumnName()).append(" ");
		
		Object value = getValue();
		if (isRange() && getValueTo() != null) {
			sqlWhere.append(" BETWEEN ");
			sqlWhere.append(getParamValue(value));
			sqlWhere.append(" AND ");
			sqlWhere.append(getParamValue(getValueTo()));
		} else {
			if (value instanceof Boolean || getQueryOperator() == null)
				setQueryOperator(QUERYOPERATOR_Eq);
			else if (value instanceof String) {
				StringBuilder valueStr = new StringBuilder(value.toString());
				if (getQueryOperator().equals(X_AD_InfoColumn.QUERYOPERATOR_Like)) {
					if (!valueStr.toString().endsWith("%"))
						valueStr.append("%");
				} else if (getQueryOperator().equals(X_AD_InfoColumn.QUERYOPERATOR_FullLike)) {
					if (!valueStr.toString().startsWith("%"))
						valueStr.insert(0, "%");
					if (!valueStr.toString().endsWith("%"))
						valueStr.append("%");
				}
				value = valueStr.toString();
			}

			sqlWhere.append(getQueryOperator()).append(" ");
			sqlWhere.append(getParamValue(value));
		}

		return sqlWhere.toString();
	}
	
	private String getParamValue(Object value) {
		if (value instanceof String)
			return DB.TO_STRING((String)value);
		else if (value instanceof Boolean)
			return ((Boolean)value).booleanValue() ? "'Y'" : "'N'";
		else if (value instanceof Timestamp)
			return DB.TO_DATE((Timestamp)value);
		else
			return value.toString();
	}
	
	public String getColumnName() {
		return getGridField() != null ? getGridField().getColumnName() : null;
	}

}