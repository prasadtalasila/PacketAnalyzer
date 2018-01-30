package unit.in.ac.bits.protocolanalyzer.persistence.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.elasticsearch.node.NodeBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.google.common.eventbus.EventBus;

import in.ac.bits.protocolanalyzer.persistence.repository.ElasticsearchConfig;
import unit.config.in.ac.bits.protocolanalyzer.persistence.repository.ElasticsearchConfigTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ElasticsearchConfigTestConfig.class, loader = AnnotationConfigContextLoader.class)
public class ElasticsearchConfigTest{
		@InjectMocks
		@Autowired
		public ElasticsearchConfig elasticSearchConfig;


		@Mock
	    private Environment environment;
		
		@Mock
		public EventBus bus;

		@Mock
		public ElasticsearchTemplate template;

		@Before
		public void setup() throws Exception {
			MockitoAnnotations.initMocks(this);
		}

		@Test
		public void wiringTest() {
			assertThat(elasticSearchConfig, is(notNullValue()));
		}

		@Test
		public void testElasticsearchTemplate() {
	       /* when(this.environment.getProperty("elasticsearch.cluster.name")).thenReturn("testClusterName");
	        when(this.environment.getProperty("elasticsearch.node.name")).thenReturn("testNodeName");
	        when(this.environment.getProperty("elasticsearch.http.cors.enabled")).thenReturn("testCorsEnabled");
	        when(this.environment.getProperty("elasticsearch.http.cors.allow-origin")).thenReturn("testCorsAllowOrigin");
	        when(this.environment.getProperty("elasticsearch.http.cors.allow-methods")).thenReturn("testCorsAllowMethods");
	        when(this.environment.getProperty("elasticsearch.http.cors.allow-headers")).thenReturn("testCorsAllowHeaders");
	        when(this.environment.getProperty("elasticsearch.path.data")).thenReturn("testPathData");
	        when(this.environment.getProperty("elasticsearch.path.logs")).thenReturn("testPathLogs");*/
	        ElasticsearchOperations testTemplate = elasticSearchConfig.elasticsearchTemplate();
	        verify(environment, times(1)).getProperty("elasticsearch.cluster.name");
	        verify(environment, times(1)).getProperty("elasticsearch.node.name");
	        verify(environment, times(1)).getProperty("elasticsearch.http.cors.enabled");
	        verify(environment, times(1)).getProperty("elasticsearch.http.cors.allow-origin");
	        verify(environment, times(1)).getProperty("elasticsearch.http.cors.allow-headers");	        
		}
}
