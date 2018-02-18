package config.in.ac.bits.protocolanalyzer.persistence.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

@Configuration
public class SaveRepositoryConfig {
	@Bean
	public ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets(){
		return new ConcurrentLinkedQueue<ArrayList<IndexQuery>>();
	}
	
	@Bean
	public Runtime runtime(){
		return Runtime.getRuntime();
	}
	
	@Bean
	public HashMap<String,String> envProperties(){
		HashMap<String,String> envProperties = new HashMap<>();
		try {
			Context ctx = new InitialContext();
			Context env = (Context) ctx.lookup("java:comp/env");
			envProperties.put("lowWaterMark", (String) env.lookup("lowWaterMark"));
			envProperties.put("analysisOnly", (String) env.lookup("analysisOnly"));
			envProperties.put("Error", "false");
		} catch (NamingException e) {
			envProperties.put("lowWaterMark", "3");
			envProperties.put("analysisOnly", "false");
			envProperties.put("Error", "true");
			e.printStackTrace();
		}
		return envProperties;
	}
	
	
}