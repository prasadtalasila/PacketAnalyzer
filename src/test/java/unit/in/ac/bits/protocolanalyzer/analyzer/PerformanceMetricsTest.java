package unit.in.ac.bits.protocolanalyzer.analyzer;

import in.ac.bits.protocolanalyzer.analyzer.PerformanceMetrics;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import unit.config.in.ac.bits.protocolanalyzer.analyzer.PerformanceMetricsConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PerformanceMetricsConfig.class,
					  loader = AnnotationConfigContextLoader.class)
public class PerformanceMetricsTest {

	@Autowired
	private PerformanceMetrics metrics;

	@Test
	public void autowiringTest() {
		assertThat(metrics,is(notNullValue()));
	}

	@Test
	public void testGetterSetter_A() {

		
		String sessionName = "session_1234";
		assertThat(null, equalTo(metrics.getSessionName()));
		metrics.setSessionName(sessionName);
		assertThat(sessionName, equalTo(metrics.getSessionName()));

		String pcapPath = "path/to/pcap/file.pcap";
		assertThat(null, equalTo(metrics.getPcapPath()));
		metrics.setPcapPath(pcapPath);
		assertThat(pcapPath, equalTo(metrics.getPcapPath()));

		double pcapSize = 123456789.1234;
		assertThat(metrics.getPcapSize(), is(0D));
		metrics.setPcapSize(pcapSize);
		assertThat(metrics.getPcapSize(), is(pcapSize));
		
		long packetCount = 20123;
		assertThat(metrics.getPacketCount(), is(0L));
		metrics.setPacketCount(packetCount);
		assertThat(metrics.getPacketCount(), is(packetCount));
		
		long linkStart = System.currentTimeMillis();
		long networkStart = linkStart;
		assertThat(metrics.getNetworkStart(), is(0L));
		metrics.setNetworkStart(networkStart);
		assertThat(metrics.getNetworkStart(), is(networkStart));
	}

	@Test
	public void testGetterSetter_B() {

		long linkStart = System.currentTimeMillis();
		metrics.setLinkStart(linkStart);
		assertThat(metrics.getLinkStart(), is(linkStart));
		
		long linkEnd = System.currentTimeMillis() + 5000;
		metrics.setLinkEnd(linkEnd);
		assertThat(metrics.getLinkEnd(), is(linkEnd));

		long networkEnd = linkEnd;
		assertThat(metrics.getNetworkEnd(), is(0L));
		metrics.setNetworkEnd(networkEnd);
		assertThat(metrics.getNetworkEnd(), is(networkEnd));

		long transportStart = linkStart;
		assertThat(metrics.getTransportStart(), is(0L));
		metrics.setTransportStart(transportStart);
		assertThat(metrics.getTransportStart(), is(transportStart));

		long transportEnd = linkEnd + 1;
		assertThat(metrics.getTransportEnd(), is(0L));
		metrics.setTransportEnd(transportEnd);
		assertThat(metrics.getTransportEnd(), is(transportEnd));

		long endTime = linkEnd + 5000;
		assertThat(metrics.getEndTime(), is(0L));
		metrics.setEndTime(endTime);
		assertThat(metrics.getEndTime(), is(endTime));

	}
	
	@Test
	public void testDuration() {
		long linkStart = System.currentTimeMillis();
		long linkEnd = System.currentTimeMillis() + 5000;

		metrics.setLinkStart(linkStart);
		metrics.setLinkEnd(linkEnd);
		metrics.calculateMetrics();

		assertThat(metrics.getLinkDuration(),
				is((double) (linkEnd - linkStart) / 1000));
	}
}