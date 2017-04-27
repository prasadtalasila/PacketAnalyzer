package config.in.ac.bits.protocolanalyzer.persistence.repository;

import in.ac.bits.protocolanalyzer.persistence.repository.AnalysisRepository;
import in.ac.bits.protocolanalyzer.persistence.repository.SaveRepository;

import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalysisRepositoryTestConfig {

	@Mock
	public SaveRepository saveRepo;

	@Bean
	public AnalysisRepository analysisRepository() {
		return new AnalysisRepository();
	}

	@Bean
	public SaveRepository saveRepo() {
		return saveRepo;
	}
}
