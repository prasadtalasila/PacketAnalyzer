package in.ac.bits.protocolanalyzer.protocol_new;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import com.esotericsoftware.yamlbeans.YamlReader;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j

public class PGReaderYAML implements PGReader
{	
	private String fileName;
	private File yamlFile;
	
	public PGReaderYAML(String fileName)
	{
		initFile(fileName);
	}
	
	public void initFile(String f)
	{
		fileName = f;
		
		try
		{
			ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource("META-INF/" + fileName);
			URI uri = new URI(url.toString());
			yamlFile = new File(uri.getPath());
		}
		catch(Exception e)
		{
			log.info("Couldn't find protocol configuration file.", e);
		}
	}
	
	public Iterator<Protocol> iterator()
	{
		try
		{	
			FileReader fr = new FileReader(yamlFile);
			YamlReader reader = new YamlReader(fr);
			
			ArrayList<Protocol> protocols = new ArrayList<Protocol>();
			
			while (true)
			{
				Protocol P = reader.read(Protocol.class);
				if (P == null) break;
				
				P.attachAnalyzer();
				protocols.add(P);
				
				/*
				 *  DEGUG:
				 *	log.info(P.getName() + " added successfully!");
				 *	log.info("Clients: " + P.getClients() + "\n");
				 */
			}
			return protocols.iterator();
		}
		catch(Exception e)
		{
			log.error("Exception occured while trying to read the protocol configuration file.", e);
			return null;
		}
	}
	
	public Protocol readProtocol (String protocolName)
	{
		try
		{
			FileReader fr = new FileReader(yamlFile);
			YamlReader reader = new YamlReader(fr);
			
			while (true)
			{
				Protocol P = reader.read(Protocol.class);
				
				if (P == null) break; //stop if end of file occurs
				
				if(P.getName().equals(protocolName))
				{
					P.attachAnalyzer();
					return P;
				}
			}
		}
		catch(Exception e)
		{
			log.error("Exception caught while trying to read the protocol configuration file.", e);
			return null;
		}
		
		return null;
	}
}
