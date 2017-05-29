package in.ac.bits.protocolanalyzer.analyzer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import config.in.ac.bits.protocolanalyzer.analyzer.PerformanceMetricsConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PerformanceMetricsConfig.class, loader = AnnotationConfigContextLoader.class)
public class PerformanceMetricsTest {

	@Autowired
	private PerformanceMetrics metrics;

	@Test
	public void autowiringTest() {
		assertNotNull(metrics);
	}

	@Test
	public void testGetterSetter() {

		String sessionName = "session_1234";
		String pcapPath = "path/to/pcap/file.pcap";
		double pcapSize = 123456789.1234;

		long linkStart = System.currentTimeMillis();
		long linkEnd = System.currentTimeMillis() + 5000;

		long networkStart = linkStart;
		long networkEnd = linkEnd;

		long transportStart = linkStart;
		long transportEnd = linkEnd + 1;

		long endTime = linkEnd + 5000;

		long packetCount = 20123;

		assertEquals(null, metrics.getSessionName());
		metrics.setSessionName(sessionName);
		assertEquals(sessionName, metrics.getSessionName());

		assertEquals(null, metrics.getPcapPath());
		metrics.setPcapPath(pcapPath);
		assertEquals(pcapPath, metrics.getPcapPath());

		assertThat(metrics.getPcapSize(), is(0D));
		metrics.setPcapSize(pcapSize);
		assertThat(metrics.getPcapSize(), is(pcapSize));

		metrics.setLinkStart(linkStart);
		assertThat(metrics.getLinkStart(), is(linkStart));

		metrics.setLinkEnd(linkEnd);
		assertThat(metrics.getLinkEnd(), is(linkEnd));

		assertThat(metrics.getNetworkStart(), is(0L));
		metrics.setNetworkStart(networkStart);
		assertThat(metrics.getNetworkStart(), is(networkStart));

		assertThat(metrics.getNetworkEnd(), is(0L));
		metrics.setNetworkEnd(networkEnd);
		assertThat(metrics.getNetworkEnd(), is(networkEnd));

		assertThat(metrics.getTransportStart(), is(0L));
		metrics.setTransportStart(transportStart);
		assertThat(metrics.getTransportStart(), is(transportStart));

		assertThat(metrics.getTransportEnd(), is(0L));
		metrics.setTransportEnd(transportEnd);
		assertThat(metrics.getTransportEnd(), is(transportEnd));

		assertThat(metrics.getEndTime(), is(0L));
		metrics.setEndTime(endTime);
		assertThat(metrics.getEndTime(), is(endTime));

		assertThat(metrics.getPacketCount(), is(0L));
		metrics.setPacketCount(packetCount);
		assertThat(metrics.getPacketCount(), is(packetCount));
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