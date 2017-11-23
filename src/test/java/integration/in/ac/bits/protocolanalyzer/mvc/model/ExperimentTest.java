package integration.in.ac.bits.protocolanalyzer.mvc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import in.ac.bits.protocolanalyzer.mvc.model.Experiment;
import integration.config.in.ac.bits.protocolanalyzer.mvc.model.ExperimentTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ExperimentTestConfig.class, loader = AnnotationConfigWebContextLoader.class)
public class ExperimentTest{
	@Autowired
	private WebApplicationContext context;

	@Autowired
	@Qualifier("Experiment")
	private Experiment experiment;
	
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void autowiringTest() {
		assertNotNull(experiment);
	}
	
	@Test
	public void testExperiment() throws Exception {
		String protocolGraphPath = System.getProperty("user.dir") + "/data/graph.p4";
		String pcapPath = System.getProperty("user.dir") + "/data/packet/DNS_Traffic000.pcap";
		experiment.init(pcapPath  , protocolGraphPath);
		String experimentResults = experiment.analyze();
	    JSONObject jsonObj = new JSONObject(experimentResults);
	    assertEquals(100, jsonObj.get("packetCount"));
	    assertEquals("success", jsonObj.get("status"));
	}
}
