package in.ac.bits.protocolanalyzer.persistence.repository;

import javax.annotation.Resource;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@EnableElasticsearchRepositories(basePackages = "in.ac.bits.protocolanalyzer.persistence.repository")
@PropertySource(value = "classpath:META-INF/elasticsearch.properties")
public class ESFactoryImpl implements ESFactory {

	@Resource
    private Environment environment;
	
	@Override
	public ImmutableSettings.Builder settingsBuilder() {
		String clusterName = environment
                .getProperty("elasticsearch.cluster.name");
        String nodeName = environment.getProperty("elasticsearch.node.name");
        String corsEnabled = environment
                .getProperty("elasticsearch.http.cors.enabled");
        String allowOrigin = environment
                .getProperty("elasticsearch.http.cors.allow-origin");
        String allowMethods = environment
                .getProperty("elasticsearch.http.cors.allow-methods");
        String allowHeaders = environment
                .getProperty("elasticsearch.http.cors.allow-headers");
	String dataPath = environment
		.getProperty("elasticsearch.path.data");
	String logPath = environment
		.getProperty("elasticsearch.path.logs");
		ImmutableSettings.Builder settingBuilder = ImmutableSettings
                .settingsBuilder().put("cluster.name",clusterName)
                .put("node.name", nodeName).put("node.data", true)
            	.put("path.data", dataPath).put("path.logs", logPath)
                        .put("index.number_of_shards", 1)
                        .put("index.number_of_replicas", 0)
                        .put("http.cors.enabled",
                                Boolean.valueOf(corsEnabled).booleanValue())
                        .put("http.cors.allow-origin", allowOrigin)
                        .put("http.cors.allow-methods", allowMethods)
                        .put("http.cors.allow-headers", allowHeaders);
		return settingBuilder;
	}

	@Override
	public NodeBuilder nodeBuilder(ImmutableSettings.Builder settingsBuilder) {
		NodeBuilder nodebuilder = NodeBuilder.nodeBuilder().local(true)
				.settings(settingsBuilder.build());
		return nodebuilder;
	}


}
