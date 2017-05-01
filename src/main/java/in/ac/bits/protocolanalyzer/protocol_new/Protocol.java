package in.ac.bits.protocolanalyzer.protocol_new;

import in.ac.bits.protocolanalyzer.analyzer.CustomAnalyzer;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j

public class Protocol
{
	private String name;
	private String analyzerName; //stores the fully qualified name of analyzer class 
	private int level;
	private ArrayList<String> clients;
	
	CustomAnalyzer analyzer;

	public Protocol()
	{
		name = null;
		analyzerName = null;
		level = -1;
		clients = null;
		analyzer = null;
	}
	
	public Protocol(String N, String A, int L, ArrayList<String> C)
	{
		name = N;
		analyzerName = A;
		level = L;
		clients = C;
	}
	
	public boolean attachAnalyzer()
	{
		try
		{
			Class<?> c = Class.forName(analyzerName);
			Object obj = c.newInstance();
			if(!(obj instanceof CustomAnalyzer))
				return false;
			analyzer = (CustomAnalyzer)obj;
			return true;
		}
		catch(Exception e)
		{
			log.error("Cant attach analyzer (Protocol.attachAnalyzer())", e);
			return false;
		}
	}

	public ArrayList<String> getClients()
	{
		return this.clients;
	}
	
	public boolean equals(Protocol P)
	{
		if( this.hashCode() == P.hashCode())
			return true;		
		return false;
	}
	
	public int hashCode()
	{
		int result = 17;
		try
		{
			result = 37 * result + name.hashCode();
			result = 37 * result + analyzerName.hashCode();
			result = 37 * result + level;
			result = 37 * result + clients.hashCode();
		}
		catch(Exception e)
		{
			log.error("Error while computing hashCode for Protocol.\n"
					+ "Probably protocol isn't configured properly.\n"
					+ "One of its fields might be null.\n", e);
			e.printStackTrace();
		}
		
		return result;
	}
}