package in.ac.bits.protocolanalyzer.protocol_new;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.*;

import in.ac.bits.protocolanalyzer.analyzer.link.EthernetAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.network.IPv4Analyzer;
import in.ac.bits.protocolanalyzer.analyzer.network.IPv6Analyzer;
import in.ac.bits.protocolanalyzer.analyzer.transport.IcmpAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.transport.TcpAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.transport.UdpAnalyzer;
import in.ac.bits.protocolanalyzer.protocol_new.PGReader;
import in.ac.bits.protocolanalyzer.protocol_new.PGReaderYAML;
import in.ac.bits.protocolanalyzer.protocol_new.ProtocolGraph;

public class ProtocolGraphTest
{
	ProtocolGraph PG;
    PGReader PGR;
	
	@Before
	public void setUp()
	{
		PG = new ProtocolGraph();
        PGR = new PGReaderYAML("TestFile.yaml");
	}
	
	@After
	public void tearDown()
	{
		PG = null;
		PGR = null;
	}
	
	@Test
	public void addProtocolsFromFile()
	{
		assertThat(PG.getProtocol("Ethernet"), is(nullValue()));
		assertThat(PG.getProtocol("IPv4"), is(nullValue()));
		assertThat(PG.getProtocol("IPv6"), is(nullValue()));
		assertThat(PG.getProtocol("Tcp"), is(nullValue()));
		assertThat(PG.getProtocol("Udp"), is(nullValue()));
		assertThat(PG.getProtocol("Icmp"), is(nullValue()));
		
		PG.addProtocols(PGR);
		
		assertThat(PG.getProtocol("Ethernet"), is(not(nullValue())));
		assertThat(PG.getProtocol("Ethernet").getAnalyzer(), is(instanceOf(EthernetAnalyzer.class)));
		
		assertThat(PG.getProtocol("IPv4"), is(not(nullValue())));
		assertThat(PG.getProtocol("IPv4").getAnalyzer(), is(instanceOf(IPv4Analyzer.class)));
		
		assertThat(PG.getProtocol("IPv6"), is(not(nullValue())));
		assertThat(PG.getProtocol("IPv6").getAnalyzer(), is(instanceOf(IPv6Analyzer.class)));
		
		assertThat(PG.getProtocol("Tcp"), is(not(nullValue())));
		assertThat(PG.getProtocol("Tcp").getAnalyzer(), is(instanceOf(TcpAnalyzer.class)));
		
		assertThat(PG.getProtocol("Udp"), is(not(nullValue())));
		assertThat(PG.getProtocol("Udp").getAnalyzer(), is(instanceOf(UdpAnalyzer.class)));
		
		assertThat(PG.getProtocol("Icmp"), is(not(nullValue())));
		assertThat(PG.getProtocol("Icmp").getAnalyzer(), is(instanceOf(IcmpAnalyzer.class)));
	}
	
	@Test
	public void getProtocolWhenItDoesntExist()
	{
		assertThat(PG.getProtocol("dummy"), is(nullValue()));
	}
	
	@Test
	public void getProtocolWhenItExists()
	{
		PG.addProtocols(PGR);
		
		assertThat(PG.getProtocol("Ethernet"), is(not(nullValue())));
		assertThat(PG.getProtocol("Ethernet").getAnalyzer(), is(instanceOf(EthernetAnalyzer.class)));
		
		assertThat(PG.getProtocol("IPv4"), is(not(nullValue())));
		assertThat(PG.getProtocol("IPv4").getAnalyzer(), is(instanceOf(IPv4Analyzer.class)));
		
		assertThat(PG.getProtocol("IPv6"), is(not(nullValue())));
		assertThat(PG.getProtocol("IPv6").getAnalyzer(), is(instanceOf(IPv6Analyzer.class)));
		
		assertThat(PG.getProtocol("Tcp"), is(not(nullValue())));
		assertThat(PG.getProtocol("Tcp").getAnalyzer(), is(instanceOf(TcpAnalyzer.class)));
		
		assertThat(PG.getProtocol("Udp"), is(not(nullValue())));
		assertThat(PG.getProtocol("Udp").getAnalyzer(), is(instanceOf(UdpAnalyzer.class)));
		
		assertThat(PG.getProtocol("Icmp"), is(not(nullValue())));
		assertThat(PG.getProtocol("Icmp").getAnalyzer(), is(instanceOf(IcmpAnalyzer.class)));
	}
}