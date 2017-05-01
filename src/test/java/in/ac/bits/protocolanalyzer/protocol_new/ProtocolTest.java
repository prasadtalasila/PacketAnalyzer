package in.ac.bits.protocolanalyzer.protocol_new;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.*;

import in.ac.bits.protocolanalyzer.protocol_new.Protocol;

public class ProtocolTest
{
	Protocol P;
	
	@Before
	public void setUp()
	{
		P = new Protocol();
	}
	
	@After
	public void tearDown()
	{
		P = null;
	}
	
	@Test
	public void analyzerAttachment()
	{
		P.setName("Ethernet");
		P.setAnalyzerName("in.ac.bits.protocolanalyzer.analyzer.link.EthernetAnalyzer");
						
		P.attachAnalyzer();
		
		assertThat(P.getAnalyzer(), is(not(nullValue())));
		assertThat(P.getAnalyzer(), is(instanceOf(in.ac.bits.protocolanalyzer.analyzer.CustomAnalyzer.class)));
	}
	
	@Test
	public void equalsTest()
	{
		ArrayList<String> clients = new ArrayList<String>();
		clients.add("Tcp");
		clients.add("Udp");
		clients.add("Icmp");
		
		
		P.setName("Ethernet");
		P.setAnalyzerName("in.ac.bits.protocolanalyzer.analyzer.link.EthernetAnalyzer");
		P.setLevel(3);
		P.setClients(clients);
		P.attachAnalyzer();
		
		Protocol Q = new Protocol();
		Q.setName("Ethernet");
		Q.setAnalyzerName("in.ac.bits.protocolanalyzer.analyzer.link.EthernetAnalyzer");
		Q.setLevel(3);
		Q.setClients(clients);
		Q.attachAnalyzer();
		
		assertThat(P.equals(Q), is(true));
		
		Q.setName("Ethernet1");
		assertThat(P.equals(Q), is(false));
	}
}