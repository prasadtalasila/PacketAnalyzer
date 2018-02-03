package in.ac.bits.protocolanalyzer.persistence.repository;


import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
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
public class ElasticSearchConfig {
    
	@Autowired
    ESFactory esFactoryImpl;
    
    @Bean
    public ElasticsearchOperations elasticsearchTemplate(Environment env) {
		ImmutableSettings.Builder settingsBuilder = esFactoryImpl.settingsBuilder(env);
		NodeBuilder builder = esFactoryImpl.nodeBuilder(settingsBuilder);
	    return new ElasticsearchTemplate(builder.node().client());
    }

}
