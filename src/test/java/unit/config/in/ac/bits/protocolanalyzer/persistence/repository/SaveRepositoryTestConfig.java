package unit.config.in.ac.bits.protocolanalyzer.persistence.repository;

import in.ac.bits.protocolanalyzer.persistence.repository.SaveRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("in.ac.bits.protocolanalyzer.persistence.repository")
public class SaveRepositoryTestConfig {
	@Bean
	public SaveRepository saveRepo() {
		return new SaveRepository();
	}

	
	

}