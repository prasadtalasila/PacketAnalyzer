package config.in.ac.bits.protocolanalyzer.persistence.repository;

import java.util.ArrayList;
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
	public Context ctx(){
		try {
			return new InitialContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Bean
	public ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets(){
		return new ConcurrentLinkedQueue<ArrayList<IndexQuery>>();
	}
	
	

}