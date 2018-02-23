package config.in.ac.bits.protocolanalyzer.persistence.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

@Configuration
@Log4j
public class SaveRepositoryConfig {
	private static final String LWM = "lowWaterMark";
	private static final String AO = "analysisOnly";

	@Bean
	public ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets() {
		return new ConcurrentLinkedQueue<ArrayList<IndexQuery>>();
	}

	@Bean
	public Runtime runtime() {
		return Runtime.getRuntime();
	}

	/**
	 * Returns a HashMap. It contains values about lowWaterMark, analysisOnly that
	 * are read from the Java Environment and also a flag to check if there was an
	 * error while reading these values from the environment.
	 */
	@Bean
	public HashMap<String, String> envProperties() {
		HashMap<String, String> envProperties = new HashMap<>();
		try {
			Context ctx = new InitialContext();
			Context env = (Context) ctx.lookup("java:comp/env");
			envProperties.put(LWM, (String) env.lookup(LWM));
			envProperties.put(AO, (String) env.lookup(AO));
		} catch (NamingException e) {
			envProperties.put(LWM, "3");
			envProperties.put(AO, "true");
			log.info("Have not received the config values;"
					+ "Using the default values of"
					+ " analysisOnly = true and lowWaterMark = 3");
		}
		return envProperties;
	}

}