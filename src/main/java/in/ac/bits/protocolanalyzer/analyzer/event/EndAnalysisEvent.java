package in.ac.bits.protocolanalyzer.analyzer.event;

import in.ac.bits.protocolanalyzer.analyzer.PerformanceMetrics;

import lombok.Getter;

@Getter
public class EndAnalysisEvent {
	private PerformanceMetrics metrics;

	public EndAnalysisEvent(PerformanceMetrics m) {
		this.metrics = m;
	}
}