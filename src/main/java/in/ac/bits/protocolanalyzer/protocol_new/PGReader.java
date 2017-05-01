package in.ac.bits.protocolanalyzer.protocol_new;

import java.util.Iterator;

public interface PGReader
{	
	void initFile(String f);
	Iterator<Protocol> iterator();
	Protocol readProtocol (String protocolName);
}
