package unit.config.in.ac.bits.protocolanalyzer.persistence.repository;

import in.ac.bits.protocolanalyzer.persistence.repository.SaveRepository;

import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

@Configuration
public class SaveRepositoryTestConfig {
	@Mock
	public ElasticsearchTemplate template;

	@Bean
	public SaveRepository saveRepo() {
		return new SaveRepository();
	}

	@Bean
	public ElasticsearchTemplate template() {
		return template;
	}

}