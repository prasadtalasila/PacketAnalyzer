package in.ac.bits.protocolanalyzer.mvc.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	private  WebApplicationContext context;

	private Session session;

	private ProtocolGraphParser graphParser;

	private String pcapPath;

	private String protocolGraphStr;

	@Autowired
	private Protocol protocol;

	@Autowired
	private ProtocolChecker checker;

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

	private void init(String pcapPath) {
		this.session = context.getBean(Session.class);
		Random rand = new Random();
		session.init("session_" + rand.nextInt(), pcapPath);
		log.info("Session init complete!!");
		protocol.init();
		checker.checkNAdd();
		log.info("Successfully completed init method in session controller!!");
	}
	
	public void checkFileAccess(String path) throws Exception{
		File pcapFile = new File(path);
		if(!(pcapFile.isFile())) {
			throw new Exception("Error in reading file(s) : No such file(s) found");
		}
		if(pcapFile.length()==0) {
			throw new Exception("Error in reading file(s) : Empty file(s)");
		}
		if(!pcapFile.canRead()) {
			throw new Exception("Error in reading file(s) : Access denied to file(s)");
		}
	}
	
	public void init(String pcapPath,String protocolGraphPath) throws Exception  
	{
	   checkFileAccess(protocolGraphPath);
	   protocolGraphStr = new String(Files.readAllBytes(Paths.get(protocolGraphPath)));
	   initWithPcapFileCheck(pcapPath, protocolGraphStr);  
	}
	
	public void initWithPcapFileCheck(String pcapPath,String protocolGraphStr) throws Exception  
	{
	   checkFileAccess(pcapPath);
	   this.pcapPath = pcapPath;
	   this.protocolGraphStr = protocolGraphStr;
       init(pcapPath);
	}
}
