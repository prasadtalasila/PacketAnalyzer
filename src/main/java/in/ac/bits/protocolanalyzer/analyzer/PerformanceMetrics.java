package in.ac.bits.protocolanalyzer.analyzer;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class PerformanceMetrics {
	
	private String sessionName;
	private String pcapPath;
	private double pcapSize;

	private long linkStart;
	private long linkEnd;

	private long networkStart;
	private long networkEnd;

	private long transportStart;
	private long transportEnd;

	private long endTime;

	private long packetCount;

	public void calculateMetrics() {

		log.info("==========================================");
		log.info("Session Name : + sessionName");
		log.info("Pcap Path: " + pcapPath);
		log.info("Pcap Size: " + pcapSize + " MB");
		log.info("Packet Count: " + packetCount);

		long start_time = Math.min(linkStart, Math.min(networkStart, transportStart));
		double duration = 0;
		log.info("------------------------------------------");
		log.info("Link Cell start : " + linkStart);
		log.info("Link cell end   : " + linkEnd);
		duration = (double) (linkEnd - linkStart) / 1000;
		log.info("Link Cell total : " + duration + "s");
		log.info("------------------------------------------");
		log.info("Network Cell start : " + networkStart);
		log.info("Network cell end   : " + networkEnd);
		duration = (double) (networkEnd - networkStart) / 1000;
		log.info("Network Cell total : " + duration + "s");
		log.info("------------------------------------------");
		log.info("Transport Cell start : " + transportStart);
		log.info("Transport cell end   : " + transportEnd);
		duration = (double) (transportEnd - transportStart) / 1000;
		log.info("Transport Cell total : " + duration + "s");

		long analysis_end = Math.max(linkEnd, Math.max(networkEnd, transportEnd));

		log.info("------------------------------------------");
		duration = (double) (analysis_end - start_time) / 1000;
		log.info("Analysis Time without ES : " + duration + "s");
		duration = (double) (endTime - start_time) / 1000;
		log.info("Total analysis Time : " + duration + "s");
		log.info("==========================================");
	}
}