package in.ac.bits.protocolanalyzer.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import in.ac.bits.protocolanalyzer.analyzer.event.EndAnalysisEvent;
import in.ac.bits.protocolanalyzer.analyzer.link.LinkAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.network.NetworkAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.transport.TransportAnalyzer;
import in.ac.bits.protocolanalyzer.persistence.repository.AnalysisRepository;
import in.ac.bits.protocolanalyzer.protocol.Protocol;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

/**
 * 
 * @author crygnus
 *
 */
@Component
@Scope(value = "prototype")
@Getter
@Log4j
public class Session {

	public static final String CONTROLLER_BUS_PREFIX = "pipeline_controller_bus";

	private String controllerBus;

	@Autowired
	private PcapAnalyzer pcapAnalyzer;

	@Autowired
	private PerformanceMetrics metrics;

	@Autowired
	private AnalyzerCell linkCell;

	@Autowired
	private LinkAnalyzer linkAnalyzer;

	@Autowired
	private AnalyzerCell networkCell;
	@Autowired
	private NetworkAnalyzer networkAnalyzer;

	@Autowired
	private AnalyzerCell transportCell;
	@Autowired
	private TransportAnalyzer transportAnalyzer;

	@Autowired
	private EventBusFactory factory;

	@Autowired
	private Protocol protocol;

	@Autowired
	private AnalysisRepository repository;

	private long packetProcessedCount = 0;
	private long packetReadCount = 0;

	private ExecutorService executorService;
	private String sessionName;
	private Map<Integer, AnalyzerCell> cellMap;

	public void init(String sessionName, String pcapPath) {
		this.sessionName = sessionName;
		this.controllerBus = CONTROLLER_BUS_PREFIX + "_" + this.sessionName;
		log.info("The session name = " + sessionName);

		this.metrics.setSessionName(this.sessionName);
		this.metrics.setPcapPath(pcapPath);

		this.cellMap = new HashMap<Integer, AnalyzerCell>();
		setLinkCell();
		setNetworkCell();
		setTransportCell();
		repository.configure(factory.getEventBus(this.controllerBus));
		/* Create pcap analyzer and connect linkCell with it */
		this.pcapAnalyzer.setNextAnalyzerCell(linkCell);
		this.pcapAnalyzer.setPcapPath(pcapPath);
		this.pcapAnalyzer.setMetrics(metrics);
		/* Register pcap analyzer to controller event bus */
		factory.getEventBus(this.controllerBus).register(pcapAnalyzer);
	}

	public long startExperiment() {
		executorService = Executors.newFixedThreadPool(5);
		long time = System.currentTimeMillis();
		log.info("Session " + this.sessionName + "::Starting linkcell at: " + time);
		this.metrics.setLinkStart(time);
		executorService.execute(linkCell);
		time = System.currentTimeMillis();
		log.info("Session " + this.sessionName + "::Starting networkcell at: " + time);
		this.metrics.setNetworkStart(time);
		executorService.execute(networkCell);
		time = System.currentTimeMillis();
		log.info("Session " + this.sessionName + "::Starting transportcell at: " + time);
		this.metrics.setTransportStart(time);
		executorService.execute(transportCell);
		repository.start();
		this.packetReadCount = pcapAnalyzer.readFile();
		this.metrics.setPacketCount(this.packetReadCount);
		log.info("Read count at session " + this.sessionName + ":: = " + packetReadCount
				+ " Process count now = " + packetProcessedCount);
		if (packetReadCount == packetProcessedCount) {
			this.endSession();
		}
		return packetReadCount;
	}

	public void attachCustomAnalyzer(int cellNumber,
			CustomAnalyzer customAnalyzer) {
		AnalyzerCell cell = cellMap.get(cellNumber);
		cell.addCustomAnalyzer(customAnalyzer, repository, sessionName);
	}

	public void setLinkCell() {
		linkCell.configure(this, "linkCell", linkAnalyzer);
		cellMap.put(1, linkCell);
	}

	private void setNetworkCell() {
		networkCell.configure(this, "networkCell", networkAnalyzer);
		cellMap.put(2, networkCell);
	}

	private void setTransportCell() {
		transportCell.configure(this, "transportCell", transportAnalyzer);
		cellMap.put(3, transportCell);
	}

	public void connectCells(Map<String, Set<String>> protocolGraph) {
		for (Entry<String, Set<String>> node : protocolGraph.entrySet()) {
			AnalyzerCell cell = cellMap.get(protocol.getCellNumber(node
					.getKey()));
			Set<String> toNodes = node.getValue();
			for (String protocolNode : toNodes) {
				AnalyzerCell destinationCell = cellMap.get(protocol
						.getCellNumber(protocolNode));
				cell.configureDestinationStageMap(protocolNode, destinationCell);
			}
		}
	}

	public void incrementPacketProcessedCount() {
		this.packetProcessedCount++;
		if (packetReadCount == packetProcessedCount) {
			this.endSession();
		}
	}

	private void endSession() {
		log.info("Ending " + this.sessionName + "...");
		factory.getEventBus(this.controllerBus).post(new EndAnalysisEvent(this.metrics));
		executorService.shutdown();
		/* repository.terminate(); */
		log.info("Session ended!");
		repository.isFinished();
	}

	public String getSessionName() {
		return sessionName;
	}

	public PcapAnalyzer getPcapAnalyzer() {
		return pcapAnalyzer;
	}

}
