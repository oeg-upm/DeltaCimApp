package cim.exceptions;

import org.jivesoftware.smack.UnparseableStanza;
import org.jivesoftware.smack.parsing.ParsingExceptionCallback;

public class CimParsingExceptionCallback implements ParsingExceptionCallback{

	@Override
	public void handleUnparsableStanza(UnparseableStanza stanzaData) throws Exception {
		String msg =  stanzaData.getContent().toString();
		System.out.println("[CimParsingExceptionCallback]> "+msg);
	}

}
