package in.ac.bits.protocolanalyzer.persistence.repository;


import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "in.ac.bits.protocolanalyzer.persistence.repository")
/*@PropertySource(value = "classpath:META-INF/elasticsearch.properties")*/
public class ElasticsearchConfig {

    /*@Resource
    private Environment environment;*/

    @Autowired
    ESFactory esFactoryImpl;
    
    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
      /*  ImmutableSettings.Builder settingsBuilder = ImmutableSettings
                .settingsBuilder().put("cluster.name", clusterName)
                .put("node.name", nodeName).put("node.data", true)
		.put("path.data", dataPath).put("path.logs", logPath)
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
                .put("http.cors.enabled",
                        Boolean.valueOf(corsEnabled).booleanValue())
                .put("http.cors.allow-origin", allowOrigin)
                .put("http.cors.allow-methods", allowMethods)
                .put("http.cors.allow-headers", allowHeaders);
        NodeBuilder builder = NodeBuilder.nodeBuilder().local(true)
                .settings(settingsBuilder.build());*/
	ImmutableSettings.Builder settingsBuilder = esFactoryImpl.settingsBuilder();
	/*.put("cluster.name", clusterName)
            .put("node.name", nodeName).put("node.data", true)
	.put("path.data", dataPath).put("path.logs", logPath)
            .put("index.number_of_shards", 1)
            .put("index.number_of_replicas", 0)
            .put("http.cors.enabled",
                    Boolean.valueOf(corsEnabled).booleanValue())
            .put("http.cors.allow-origin", allowOrigin)
            .put("http.cors.allow-methods", allowMethods)
            .put("http.cors.allow-headers", allowHeaders);*/
	NodeBuilder builder = esFactoryImpl.nodeBuilder(settingsBuilder);
        return new ElasticsearchTemplate(builder.node().client());
    }

}
