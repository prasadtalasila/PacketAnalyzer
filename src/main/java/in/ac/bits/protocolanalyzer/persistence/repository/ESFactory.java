package in.ac.bits.protocolanalyzer.persistence.repository;

import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.stereotype.Component;

@Component
public interface ESFactory {
	public Builder settingsBuilder();
	public NodeBuilder nodeBuilder(Builder settingsBuilder);
}
