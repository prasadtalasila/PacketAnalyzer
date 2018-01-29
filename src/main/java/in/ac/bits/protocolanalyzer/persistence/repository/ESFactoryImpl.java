package in.ac.bits.protocolanalyzer.persistence.repository;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ESFactoryImpl implements ESFactory {

	@Override
	public ImmutableSettings.Builder settingsBuilder() {
		ImmutableSettings.Builder settingBuilder = ImmutableSettings
                .settingsBuilder();
		return settingBuilder;
	}

	@Override
	public NodeBuilder nodeBuilder() {
		NodeBuilder nodebuilder = NodeBuilder.nodeBuilder();
		return nodebuilder;
	}

}
