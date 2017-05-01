package in.ac.bits.protocolanalyzer.protocol_new;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.*;

import in.ac.bits.protocolanalyzer.analyzer.CustomAnalyzer;
import in.ac.bits.protocolanalyzer.protocol_new.PGReader;
import in.ac.bits.protocolanalyzer.protocol_new.PGReaderYAML;
import in.ac.bits.protocolanalyzer.protocol_new.Protocol;

import java.util.Iterator;

public class PGReaderYAMLTest
{
    PGReader PGR;
	
	@Before
	public void setUp()
	{
        PGR = new PGReaderYAML("TestFile.yaml");
	}
	
	@After
	public void tearDown()
	{
		PGR = null;
	}
	
	@Test
	public void getIteratorWithValidFile()
	{
		Iterator<Protocol> it = PGR.iterator();
		assertThat(it, is(not(nullValue())));
		
		int count = 0;		
		while(it.hasNext())
		{
			count++;
			it.next();
		}
		assertThat(count, is(6));
	}
	
	@Test
	public void tryToGetIteratorWithInvalidFile()
	{
		PGR = new PGReaderYAML("dummyName.yaml");
		Iterator<Protocol> it = PGR.iterator();
		assertThat(it, is(nullValue()));
	}
	
	@Test
	public void ensureCorrectProtocolObjects()
	{
		Iterator<Protocol> it = PGR.iterator();
		while(it.hasNext())
		{
			Protocol P = it.next();
			
			assertThat(P.getName(), is(not(nullValue())));
			assertThat(P.getAnalyzerName(), is(not(nullValue())));
			assertThat(P.getLevel(), is(not(nullValue())));
			assertThat(P.getAnalyzer(), is(not(nullValue())));
			assertThat(P.getAnalyzer(), is(instanceOf(CustomAnalyzer.class)));
		}
	}
	
	@Test
	public void getProtocol()
	{
		Protocol P = PGR.readProtocol("Ethernet");
		assertThat(P, is(not(nullValue())));
		assertThat(P.getAnalyzerName(), is("in.ac.bits.protocolanalyzer.analyzer.link.EthernetAnalyzer"));
		
		P = PGR.readProtocol("IPv4");
		assertThat(P, is(not(nullValue())));
		assertThat(P.getAnalyzerName(), is("in.ac.bits.protocolanalyzer.analyzer.network.IPv4Analyzer"));
		
		P = PGR.readProtocol("Icmp");
		assertThat(P, is(not(nullValue())));
		assertThat(P.getAnalyzerName(), is("in.ac.bits.protocolanalyzer.analyzer.transport.IcmpAnalyzer"));
		
		P = PGR.readProtocol("dummy");
		assertThat(P, is(nullValue()));
	}
}