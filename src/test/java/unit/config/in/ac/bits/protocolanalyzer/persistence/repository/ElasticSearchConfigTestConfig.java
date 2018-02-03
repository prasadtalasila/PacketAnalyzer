package unit.config.in.ac.bits.protocolanalyzer.persistence.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import in.ac.bits.protocolanalyzer.persistence.repository.ESFactory;
import in.ac.bits.protocolanalyzer.persistence.repository.ESFactoryImpl;
import in.ac.bits.protocolanalyzer.persistence.repository.ElasticSearchConfig;

@Configuration
@PropertySource("classpath:META-INF/elasticsearch.properties")
public class ElasticSearchConfigTestConfig {
	@Bean
	public ElasticSearchConfig elasticsearchConfig() {
		return new ElasticSearchConfig();
	}
	
	@Bean
	public ESFactory esFactoryImpl() {
		return new ESFactoryImpl();
	}
}
