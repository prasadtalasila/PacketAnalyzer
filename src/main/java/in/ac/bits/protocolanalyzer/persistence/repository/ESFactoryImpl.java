package in.ac.bits.protocolanalyzer.persistence.repository;


import org.elasticsearch.common.settings.ImmutableSettings;
import org.springframework.core.env.Environment;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class ESFactoryImpl implements ESFactory {

	@Autowired
	private Environment environment;
	
	@Override
	public ImmutableSettings.Builder settingsBuilder(Environment env) {
		String clusterName = env
                .getProperty("elasticsearch.cluster.name");
        String nodeName = env.getProperty("elasticsearch.node.name");
        String corsEnabled = environment
                .getProperty("elasticsearch.http.cors.enabled");
        String allowOrigin = env
                .getProperty("elasticsearch.http.cors.allow-origin");
        String allowMethods = env
                .getProperty("elasticsearch.http.cors.allow-methods");
        String allowHeaders = env
                .getProperty("elasticsearch.http.cors.allow-headers");
	String dataPath = env
		.getProperty("elasticsearch.path.data");
	String logPath = env
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
