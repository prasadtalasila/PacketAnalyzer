package in.ac.bits.protocolanalyzer.mvc.controller;

import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.WebApplicationContext;

import in.ac.bits.protocolanalyzer.analyzer.Session;
import in.ac.bits.protocolanalyzer.protocol.Protocol;
import in.ac.bits.protocolanalyzer.protocol.ProtocolChecker;
import in.ac.bits.protocolanalyzer.protocol.ProtocolGraphParser;

@Component
@Scope("prototype")
@Qualifier("Experiment")
@Getter
@Setter
@Log4j
public class Experiment {

	@Autowired
	private WebApplicationContext context;

	private Session session;

	private ProtocolGraphParser graphParser;

	private String pcapPath;

	private String protocolGraphStr;

	@Autowired
	private Protocol protocol;

	@Autowired
	private ProtocolChecker checker;

	public void init(String pcapPath, String protocolGraphStr) {
		this.pcapPath = pcapPath;
		this.protocolGraphStr = protocolGraphStr;
	}

	public String analyze() {
		// Initializing session and protocol
		//log.info("Starting to analyze ..........");
		init(pcapPath);
		graphParser = context.getBean(ProtocolGraphParser.class);
		graphParser.configureSession(session, protocolGraphStr);
		log.info("Successfully completed session configuration!!");

		long readCount = session.startExperiment();
		JSONObject expStatus = new JSONObject();
		expStatus.put("status", "success");
		expStatus.put("sessionName", session.getSessionName());
		expStatus.put("packetCount", readCount);
		return expStatus.toString();
	}

	/*
	 * later this method can be converted to an API.
	 */
	private void init(String pcapPath) {
		this.session = context.getBean(Session.class);
		Random rand = new Random();
		session.init("session_" + rand.nextInt(), pcapPath);
		log.info("Session init complete!!");
		protocol.init();
		checker.checkNAdd();
		log.info("Successfully completed init method in session controller!!");
	}

}
