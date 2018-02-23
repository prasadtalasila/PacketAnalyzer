package integration.config.in.ac.bits.protocolanalyzer.mvc.model;

import in.ac.bits.protocolanalyzer.analyzer.AnalyzerCell;
import in.ac.bits.protocolanalyzer.analyzer.EventBusFactory;
import in.ac.bits.protocolanalyzer.analyzer.PcapAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.PerformanceMetrics;
import in.ac.bits.protocolanalyzer.analyzer.Session;
import in.ac.bits.protocolanalyzer.analyzer.link.EthernetAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.link.LinkAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.network.IPv4Analyzer;
import in.ac.bits.protocolanalyzer.analyzer.network.IPv6Analyzer;
import in.ac.bits.protocolanalyzer.analyzer.network.NetworkAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.transport.IcmpAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.transport.TcpAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.transport.TransportAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.transport.UdpAnalyzer;
import in.ac.bits.protocolanalyzer.mvc.model.Experiment;
import in.ac.bits.protocolanalyzer.persistence.repository.AnalysisRepository;
import in.ac.bits.protocolanalyzer.persistence.repository.ElasticSearchConfig;
import in.ac.bits.protocolanalyzer.persistence.repository.ElasticSearchFactoryImpl;
import in.ac.bits.protocolanalyzer.persistence.repository.SaveRepository;
import in.ac.bits.protocolanalyzer.protocol.Protocol;
import in.ac.bits.protocolanalyzer.protocol.ProtocolChecker;
import in.ac.bits.protocolanalyzer.protocol.ProtocolGraph;
import in.ac.bits.protocolanalyzer.protocol.ProtocolGraphParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

@Configuration
@PropertySource("classpath:META-INF/elasticsearch.properties")
public class ExperimentTestConfig {
	
	@Autowired 
	Environment env;
	
	@Bean
	public ElasticSearchFactoryImpl getElasticSearchFactoryImpl() {
		return new ElasticSearchFactoryImpl();
	}

	@Bean
	public ElasticSearchConfig getElasticSearchConfig() {
		return new ElasticSearchConfig();
	}
	
	@Bean
	public Experiment getSampleExperiment() {
		return new Experiment();
	}
	
	@Bean
	public Session getSampleSession() {
		return new Session();
	}
	
	@Bean
	public PcapAnalyzer getSamplePcapAnalyzer() {
		return new PcapAnalyzer();
	}
	
	@Bean
	public PerformanceMetrics getSamplePerformanceMetrics() {
		return new PerformanceMetrics();
	}
	
	@Bean
	public AnalyzerCell getSampleAnalyzerCell() {
		return new AnalyzerCell();
	}
	
	@Bean
	public EventBusFactory getSampleEventBusFactory() {
		return new EventBusFactory();
	}
	
	@Bean
	public LinkAnalyzer getSampleLinkAnalyzer() {
		return new LinkAnalyzer();
	}
	
	@Bean
	public NetworkAnalyzer getSampleNetworkAnalyzer() {
		return new NetworkAnalyzer();
	}
	
	@Bean
	public TransportAnalyzer getSampleTransportAnalyzer() {
		return new TransportAnalyzer();
	}
	
	@Bean
	public Protocol getSampleProtocol() {
		return new Protocol();
	}
	
	@Bean
	public SaveRepository getSampleSaveRepo() {
		return new SaveRepository();
	}
	
	@Bean
	public AnalysisRepository getSampleAnalysisRepository() {
		return new AnalysisRepository();
	}
	
	@Bean
	public ElasticsearchTemplate getSampleElasticsearchTemplate() {
		return (ElasticsearchTemplate) getElasticSearchConfig().elasticsearchTemplate(env);
	}
	
	@Bean
	public ProtocolChecker getSampleProtocolChecker() {
		return new ProtocolChecker();
	}
	
	@Bean
	public IPv4Analyzer getSampleIPv4Analyzer() {
		return new IPv4Analyzer();
	}
	
	@Bean
	public EthernetAnalyzer getSampleEthernetAnalyzer() {
		return new EthernetAnalyzer();
	}
	
	@Bean
	public IPv6Analyzer getSampleIPv6Analyzer() {
		return new IPv6Analyzer();
	}
	
	@Bean
	public TcpAnalyzer getSampleTcpAnalyzer() {
		return new TcpAnalyzer();
	}
	
	@Bean
	public UdpAnalyzer getSampleUdpAnalyzer() {
		return new UdpAnalyzer();
	}
	
	@Bean
	public IcmpAnalyzer getSampleIcmpAnalyzer() {
		return new IcmpAnalyzer();
	}
	
	@Bean
	public ProtocolGraphParser getSampleProtocolGraphParser() {
		return new ProtocolGraphParser();
	}
	
	@Bean
	public ProtocolGraph getSampleProtocolGraph() {
		return new ProtocolGraph();
	}
	
	@Bean
	public ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets(){
		return new ConcurrentLinkedQueue<ArrayList<IndexQuery>>();
	}
	
	@Bean
	public Runtime runtime(){
		return Runtime.getRuntime();
	}
	
	/**
	*	Returns a HashMap. It contains values about lowWaterMark, analysisOnly that are
	*	read from the Java Environment and also a flag to check if there was an error
	* 	while reading these values from the environment.
	*/
	@Bean
	public HashMap<String,String> envProperties(){
		HashMap<String,String> envProperties = new HashMap<>();
		envProperties.put("lowWaterMark", "2");
		envProperties.put("analysisOnly", "true");
		return envProperties;
	}
}
