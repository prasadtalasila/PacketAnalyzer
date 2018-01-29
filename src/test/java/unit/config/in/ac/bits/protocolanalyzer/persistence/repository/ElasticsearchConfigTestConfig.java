package unit.config.in.ac.bits.protocolanalyzer.persistence.repository;

import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import in.ac.bits.protocolanalyzer.persistence.repository.ESFactory;
import in.ac.bits.protocolanalyzer.persistence.repository.ESFactoryImpl;
import in.ac.bits.protocolanalyzer.persistence.repository.ElasticsearchConfig;

@Configuration
@ComponentScan
public class ElasticsearchConfigTestConfig {
	@Mock
	public ElasticsearchTemplate template;
	
	@Mock
    private Environment environment;

	@Bean
	public ElasticsearchConfig elasticsearchConfig() {
		return new ElasticsearchConfig();
	}
	
	@Bean
	public ESFactory esFactoryImpl() {
		return new ESFactoryImpl();
	}

	@Bean
	public ElasticsearchTemplate template() {
		return template;
	}
}
