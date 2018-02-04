package unit.config.in.ac.bits.protocolanalyzer.persistence.repository;

import in.ac.bits.protocolanalyzer.persistence.repository.ElasticSearchConfig;
import in.ac.bits.protocolanalyzer.persistence.repository.ElasticSearchFactory;
import in.ac.bits.protocolanalyzer.persistence.repository.ElasticSearchFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:META-INF/elasticsearch.properties")
public class ElasticSearchConfigTestConfig {
	@Bean
	public ElasticSearchConfig elasticsearchConfig() {
		return new ElasticSearchConfig();
	}
	
	@Bean
	public ElasticSearchFactory esFactoryImpl() {
		return new ElasticSearchFactoryImpl();
	}
}
