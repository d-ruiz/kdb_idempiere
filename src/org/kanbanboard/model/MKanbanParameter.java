package org.kanbanboard.model;

import static org.compiere.model.SystemIDs.REFERENCE_YESNO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.GridField;
import org.compiere.model.GridFieldVO;
import org.compiere.model.Lookup;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.X_AD_InfoColumn;
import org.compiere.util.CLogger;
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
	
	public GridField setGridField(int windowNo) {

		if (mGridField == null) {
			
			String sql;
			if (!Env.isBaseLanguage(Env.getCtx(), kanbanBoard.getTable().getTableName())){
				sql = "SELECT * FROM AD_Field_vt WHERE AD_Column_ID=? AND AD_Table_ID=?"
						+ " AND AD_Language='" + Env.getAD_Language(Env.getCtx()) + "'";
			} else {
				sql = "SELECT * FROM AD_Field_v WHERE AD_Column_ID=? AND AD_Table_ID=?";
			}

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, getKDB_ColumnTable_ID());
				pstmt.setInt(2, kanbanBoard.getAD_Table_ID());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					GridFieldVO voF = GridFieldVO.create(Env.getCtx(), 
							windowNo, 0, 
							rs.getInt("ad_window_id"), rs.getInt("ad_tab_id"), 
							false, rs);
					GridField gridField = new GridField(voF);
					setGridField(gridField);
				}
			} catch (Exception e) {
				CLogger.get().log(Level.SEVERE, "", e);
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}
		
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
        
        initDefaultValues();
	}
	
	private void initDefaultValues() {
        if (mGridField != null) {
        	mGridField.setDefaultLogic(getDefaultValue());
    		value = mGridField.getDefault();
    		
    		if (isRange()) {
    			GridField toField = mGridField.clone(getCtx());
    			toField.setDefaultLogic(getDefaultValue2());
    			valueTo = toField.getDefault();
    		}
        }		
	}
	
	public String getSQLClause() {
		if (getValue() == null && getValueTo() == null)
			return null;
		
		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append(getColumnName()).append(" ");
		
		Object value = getValue();
		if (isRange()) {
			if (getValue() != null) {
				sqlWhere.append(QUERYOPERATOR_GtEq);
				sqlWhere.append(getParamValue(value));
				if (getValueTo() != null)
					sqlWhere.append(" AND ").append(getColumnName());
			}
			if (getValueTo() != null) {
				sqlWhere.append(QUERYOPERATOR_LeEq);
				sqlWhere.append(getParamValue(getValueTo()));
			}
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
			return DB.TO_DATE((Timestamp)value, false);
		else
			return value.toString();
	}
	
	public String getColumnName() {
		return getGridField() != null ? getGridField().getColumnSQL(false) : null;
	}
	
	public String getLabel() {
		return isKDB_IsShowParameterName() ? getName() : getGridField().getHeader();
	}

}