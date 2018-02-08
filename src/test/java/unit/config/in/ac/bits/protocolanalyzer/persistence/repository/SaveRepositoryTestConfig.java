package unit.config.in.ac.bits.protocolanalyzer.persistence.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import in.ac.bits.protocolanalyzer.persistence.repository.SaveRepository;

@Configuration
@ComponentScan("in.ac.bits.protocolanalyzer.persistence.repository")
public class SaveRepositoryTestConfig {
	@Bean
	public SaveRepository saveRepo() {
		return new SaveRepository();
	}
	
}