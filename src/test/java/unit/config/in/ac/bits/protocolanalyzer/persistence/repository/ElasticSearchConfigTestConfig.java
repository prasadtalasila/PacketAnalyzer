package unit.config.in.ac.bits.protocolanalyzer.persistence.repository;

import in.ac.bits.protocolanalyzer.persistence.repository.ElasticSearchConfig;
import in.ac.bits.protocolanalyzer.persistence.repository.SaveRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages= {"in.ac.bits.protocolanalyzer.persistence.repository"},
			   excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
			   value = SaveRepository.class))
@PropertySource("classpath:META-INF/elasticsearch.properties")
public class ElasticSearchConfigTestConfig {
	@Bean
	public ElasticSearchConfig elasticsearchConfig() {
		return new ElasticSearchConfig();
	}
}
