package in.ac.bits.protocolanalyzer.analyzer.event;

import lombok.Getter;

@Getter
public class SaveRepoEndEvent {

	private long time;

	public SaveRepoEndEvent(long time) {
		this.time = time;
	}
}