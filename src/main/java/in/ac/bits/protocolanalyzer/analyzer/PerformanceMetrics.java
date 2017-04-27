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

	private double timeWithoutES;
	private double timeWithES;

	public void calculateMetrics() {

		log.info("==========================================");
		log.info("Session Name : + sessionName");
		log.info("Pcap Path: " + pcapPath);
		log.info("Pcap Size: " + pcapSize + " MB");
		log.info("Packet Count: " + packetCount);

		long start_time = Math.min(linkStart,
				Math.min(networkStart, transportStart));
		// double duration = 0;
		log.info("------------------------------------------");
		log.info("Link Cell start : " + linkStart);
		log.info("Link cell end   : " + linkEnd);
		// duration = (double) (linkEnd - linkStart) / 1000;
		linkDuration = getDuration(linkStart, linkEnd);
		log.info("Link Cell total : " + linkDuration + "s");
		log.info("------------------------------------------");
		log.info("Network Cell start : " + networkStart);
		log.info("Network cell end   : " + networkEnd);
		// duration = (double) (networkEnd - networkStart) / 1000;
		networkDuration = getDuration(networkStart, networkEnd);
		log.info("Network Cell total : " + networkDuration + "s");
		log.info("------------------------------------------");
		log.info("Transport Cell start : " + transportStart);
		log.info("Transport cell end   : " + transportEnd);
		// duration = (double) (transportEnd - transportStart) / 1000;
		transportDuration = getDuration(transportStart, transportEnd);
		log.info("Transport Cell total : " + transportDuration + "s");

		analysis_end = Math.max(linkEnd, Math.max(networkEnd, transportEnd));

		log.info("------------------------------------------");
		// duration = (double) (analysis_end - start_time) / 1000;
		timeWithoutES = getDuration(start_time, analysis_end);
		log.info("Experiment Duration without ES : " + timeWithoutES + "s");
		// duration = (double) (endTime - start_time) / 1000;
		timeWithES = getDuration(start_time, endTime);
		log.info("Total experiment Duration : " + timeWithES + "s");
		log.info("==========================================");
	}

	private static double getDuration(long start, long end) {
		return (double) (end - start) / 1000;
	}
}