package config.in.ac.bits.protocolanalyzer.analyzer;

import in.ac.bits.protocolanalyzer.analyzer.PerformanceMetrics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PerformanceMetricsConfig {
	@Bean
	public PerformanceMetrics getSampleService() {
		return new PerformanceMetrics();
	}
}