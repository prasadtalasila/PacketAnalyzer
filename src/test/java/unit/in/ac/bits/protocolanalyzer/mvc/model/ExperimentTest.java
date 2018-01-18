package unit.in.ac.bits.protocolanalyzer.mvc.model;

import in.ac.bits.protocolanalyzer.analyzer.Session;
import in.ac.bits.protocolanalyzer.mvc.model.Experiment;
import in.ac.bits.protocolanalyzer.protocol.Protocol;
import in.ac.bits.protocolanalyzer.protocol.ProtocolChecker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

import org.mockito.MockitoAnnotations;
import org.springframework.web.context.WebApplicationContext;

public class ExperimentTest {
	private static final String CURRENT_DIRECTORY = "user.dir";
	@InjectMocks
	private Experiment experiment;
	
	@Mock
	private Session session;	
	@Mock
	public WebApplicationContext context;
	@Mock
	private ProtocolChecker checker;
	@Mock
	private Protocol protocol;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void autowiringTest() {
		assertNotNull(experiment);
	}
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void testCheckFileAccessTrue() throws Exception {
		try{
  		   experiment.checkFileAccess(System.getProperty(CURRENT_DIRECTORY)
  				   + "/data/packet/DNS_Traffic000.pcap");
  	   }
  	   catch(Exception e){
  	      fail("Should not have thrown any exception,"
  	      		+ " but the following exception was thrown : \n " + e.getMessage());
  	   }
	} 
    
    @Test
	public void testCheckFileAccessOnEmptyFile() throws Exception {
    	expectedEx.expect(Exception.class);
        expectedEx.expectMessage("Error in reading file(s) : Empty file(s)");
    	experiment.checkFileAccess(System.getProperty(CURRENT_DIRECTORY)
    			+ "/data/packet/test_files/empty_test_file.pcap");  
    } 
    @Test
	public void testCheckFileAccessOnNonExistentFile() throws Exception {
        expectedEx.expectMessage("Error in reading file(s) : No such file(s) found");
    	experiment.checkFileAccess(System.getProperty(CURRENT_DIRECTORY)
    			+ "/data/packet/test_files/non_existent_file.pcap");   
    }

    @Ignore
    @Test
	public void testCheckFileAccessOnLockedFile() throws Exception {
        expectedEx.expectMessage("Error in reading file(s) : Access denied to file(s)");
    	experiment.checkFileAccess(System.getProperty(CURRENT_DIRECTORY)
    			+ "/data/packet/test_files/locked_test_file.pcap");    	
    }
    
    @Test
    public void testInitWithPcapFileCheck() throws Exception {
    	try {
	    	when(context.getBean(Session.class)).thenReturn(session);
	    	String pcapPath = System.getProperty(CURRENT_DIRECTORY)
	    			+ "/data/packet/DNS_Traffic000.pcap";
	    	
	    	String protocolGraphStr = "graph start {\n" +"\n" 
 					+"	ethernet;\n" +"\n" +"}\n" +"\n" 
 					+"graph ethernet {\n" 
 					+"	switch(ethertype) {\n" 
 					+"		case 0800:			 ipv4;\n" 
 					+"	}\n" +"}\n" 
 					+"graph ipv4 {\n" 
 					+"	switch(protocol) {\n" 
 					+"		case 06: tcp;\n" 
 					+"	}\n" +"}\n" +"\n" +"\n" 
 					+"graph tcp {\n" +"\n" +"}\n" +"\n" 
 					+"graph end {\n" +"}";
			experiment.initWithPcapFileCheck(pcapPath, protocolGraphStr);
			assertEquals(pcapPath,experiment.getPcapPath());
			assertEquals(protocolGraphStr,experiment.getProtocolGraphStr());
    	}
    	catch(Exception e){
    	      fail("Should not have thrown any exception,"
    	      		+ " but the following exception was thrown : \n " + e.getMessage());
    	}
    }
}
