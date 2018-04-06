package config.in.ac.bits.protocolanalyzer.persistence.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

@Configuration
@Log4j
public class AnalysisRepositoryConfig {
	private static final String HWM = "highWaterMark";
	
	@Bean
	public ExecutorService executorService() {
		return Executors.newFixedThreadPool(2);
	}
	
	@Bean
	public ArrayBlockingQueue<IndexQuery> queries(){
		return new ArrayBlockingQueue<IndexQuery>(100000);
	}
	
	@Bean
	public Timer pullTimer() {
		return new Timer("pullTimer");
	}
	
	@Bean
	public ArrayList<IndexQuery> currentBucket() {
		return new ArrayList<IndexQuery>();
	}
	
	/**
	 * Returns a HashMap. It contains values about highWaterMark that
	 * is read from the Java Environment and also a flag to check if there was an
	 * error while reading these values from the environment.
	 */
	@Bean
	public HashMap<String, String> envProperties() {
		HashMap<String, String> envProperties = new HashMap<>();
		try {
			Context ctx = new InitialContext();
			Context env = (Context) ctx.lookup("java:comp/env");
			envProperties.put(HWM, (String) env.lookup(HWM));
		} catch (NamingException e) {
			envProperties.put(HWM, "5");
			log.info("Have not received the config values;"
					+ "Using the default value of"
					+ " highWaterMark = 5");
		}
		return envProperties;
	}
}
