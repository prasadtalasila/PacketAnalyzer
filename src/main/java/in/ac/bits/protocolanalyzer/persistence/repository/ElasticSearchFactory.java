package in.ac.bits.protocolanalyzer.persistence.repository;

import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.stereotype.Component;

@Component
public interface ElasticSearchFactory {
	
	public NodeBuilder nodeBuilder(Builder settingsBuilder);
	
	public Builder settingsBuilder(org.springframework.core.env.Environment env);
}
