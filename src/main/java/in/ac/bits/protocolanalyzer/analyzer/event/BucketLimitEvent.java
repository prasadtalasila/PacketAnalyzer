package in.ac.bits.protocolanalyzer.analyzer.event;

import lombok.Getter;

@Getter
public class BucketLimitEvent {

	private String status;

	public BucketLimitEvent(String s) {
		this.status = s.trim();
	}

}