package in.ac.bits.protocolanalyzer.analyzer;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Getter
@Setter
@Log4j
public class PerformanceMetrics {

	private String sessionName;
	private String pcapPath;
	private double pcapSize;

	private long linkStart;
	private long linkEnd;
	private double linkDuration;

	private long networkStart;
	private long networkEnd;
	private double networkDuration;

	private long transportStart;
	private long transportEnd;
	private double transportDuration;

	private long analysis_end;
	private long endTime;

	private long packetCount;

	private double totalTime;

	public void calculateMetrics() {

		log.info("==========================================");
		log.info("Session Name : " + sessionName);
		log.info("Pcap Path: " + pcapPath);
		log.info("Pcap Size: " + pcapSize + " MB");
		log.info("Packet Count: " + packetCount);

		long start_time = Math.min(linkStart,
				Math.min(networkStart, transportStart));
		
		log.info("------------------------------------------");
		log.info("Link Cell start : " + linkStart);
		log.info("Link cell end   : " + linkEnd);
		linkDuration = getDuration(linkStart, linkEnd);
		log.info("Link Cell total : " + linkDuration + "s");

		log.info("------------------------------------------");
		log.info("Network Cell start : " + networkStart);
		log.info("Network cell end   : " + networkEnd);
		networkDuration = getDuration(networkStart, networkEnd);

		log.info("Network Cell total : " + networkDuration + "s");
		log.info("------------------------------------------");
		log.info("Transport Cell start : " + transportStart);
		log.info("Transport cell end   : " + transportEnd);
		transportDuration = getDuration(transportStart, transportEnd);
		log.info("Transport Cell total : " + transportDuration + "s");

		log.info("------------------------------------------");
		totalTime = getDuration(start_time, endTime);
		log.info("Total experiment Duration : " + totalTime + "s");
		log.info("==========================================");
	}

	private static double getDuration(long start, long end) {
		return (double) (end - start) / 1000;
	}
}