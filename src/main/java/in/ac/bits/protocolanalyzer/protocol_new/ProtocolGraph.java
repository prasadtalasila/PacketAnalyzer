package in.ac.bits.protocolanalyzer.protocol_new;

import java.util.HashMap;
import java.util.Iterator;

public class ProtocolGraph
{
	private HashMap<String, Protocol> graph;
	
	public ProtocolGraph()
	{
		graph = new HashMap<String, Protocol>();
	}
	
	public Protocol getProtocol(String protocolName)
	{
		return graph.get(protocolName);
	}
	
	public void addProtocols(PGReader reader)
	{
		Iterator<Protocol> it = reader.iterator();
		while(it.hasNext())
		{
			Protocol P = it.next();
			// Add the protocol if it doesnt already exist
			if(!graph.containsValue(P))
				graph.put(P.getName(), P);
		}
	}
}
