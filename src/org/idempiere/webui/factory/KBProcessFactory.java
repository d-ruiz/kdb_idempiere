package org.idempiere.webui.factory;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;
import org.idempiere.process.CreateStatusProcess;

public class KBProcessFactory implements IProcessFactory{

	static final String KDB_CREATESTATUSES_CLASS = "org.idempiere.process.CreateStatusProcess";
	
	@Override
	public ProcessCall newProcessInstance(String className) {
		if (KDB_CREATESTATUSES_CLASS.equals(className))
			return new CreateStatusProcess();
		else
			return null;
	}

}
