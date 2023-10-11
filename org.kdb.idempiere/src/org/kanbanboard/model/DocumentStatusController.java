/**********************************************************************
 * This file is part of iDempiere ERP Open Source                      *
 * http://www.idempiere.org                                            *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz -                                                      *
 **********************************************************************/
package org.kanbanboard.model;

import java.util.HashMap;

import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Trx;

public class DocumentStatusController {
	
	private String errorMessage = "";
	private PO po = null;
	private HashMap<String, String> targetAction;
	
	public DocumentStatusController(PO po) {
		this.po = po;
		initTargetAction();
	}
	
	/**
	 * Maps the DocStatus to the corresponding DocAction
	 */
	private void initTargetAction() {
		targetAction = new HashMap<>();
		
		//No movement to this states manually
		targetAction.put(DocAction.STATUS_Drafted, null);
		targetAction.put(DocAction.STATUS_Invalid, null);
		targetAction.put(DocAction.STATUS_Unknown, null);
		targetAction.put(DocAction.STATUS_WaitingConfirmation, null);
		targetAction.put(DocAction.STATUS_WaitingPayment, null);

		//Map the DocStatus to DocAction 
		targetAction.put(DocAction.STATUS_Completed, DocAction.ACTION_Complete);
		targetAction.put(DocAction.STATUS_NotApproved, DocAction.ACTION_Reject);
		targetAction.put(DocAction.STATUS_Voided, DocAction.ACTION_Void);
		targetAction.put(DocAction.STATUS_Approved, DocAction.ACTION_Approve);		
		targetAction.put(DocAction.STATUS_Reversed, DocAction.ACTION_Reverse_Correct);
		targetAction.put(DocAction.STATUS_Closed, DocAction.ACTION_Close);
		targetAction.put(DocAction.STATUS_InProgress, DocAction.ACTION_Prepare);
	}
	
	public String getDocAction(String newDocStatus) {
		return targetAction.get(newDocStatus);
	}
	
	public boolean changeDocStatus(String targetDocAction) {
		if (isValidDocActionPO()) {
			Trx trx = Trx.get(Trx.createTrxName("DCK"), true);
			try {
				String p_docAction = getDocAction(targetDocAction);
				//No valid action
				if (p_docAction == null)
					throw new DocStatusChangeException();

				po.set_ValueOfColumn("DocAction", p_docAction);
				po.set_TrxName(trx.getTrxName());
				if (!((DocAction) po).processIt(p_docAction)) {
					throw new DocStatusChangeException(((DocAction) po).getProcessMsg());
				} else
					po.saveEx();

				trx.commit();
			} catch (DocStatusChangeException e) {
				errorMessage = e.getMessage() != null ? e.getMessage() : "KDB_InvalidTransition";
				trx.rollback();
				return false;
			} catch (IllegalStateException e) { //Thrown by the processIt method internally
				errorMessage = "@KDB_InvalidTransition@";
				trx.rollback();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				errorMessage = e.getLocalizedMessage();
				trx.rollback();
				return false;
			} finally {
				trx.close();
			}
		}
		
		return true;
	}
	
	private boolean isValidDocActionPO() {
		return po instanceof DocAction && po.get_ColumnIndex("DocAction") >= 0;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public static class DocStatusChangeException extends RuntimeException {

		private static final long serialVersionUID = 7661989611920362894L;
        
		public DocStatusChangeException() {
	        super();
	    }

	    public DocStatusChangeException(String message) {
	        super(message);
	    }
    }
}
