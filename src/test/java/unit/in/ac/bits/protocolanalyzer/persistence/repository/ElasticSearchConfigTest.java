package unit.in.ac.bits.protocolanalyzer.persistence.repository;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.CoreMatchers.notNullValue;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import in.ac.bits.protocolanalyzer.persistence.repository.ElasticSearchConfig;
import unit.config.in.ac.bits.protocolanalyzer.persistence.repository.ElasticSearchConfigTestConfig;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ElasticSearchConfigTestConfig.class, loader = AnnotationConfigContextLoader.class)
public class ElasticSearchConfigTest{

		@Autowired 
		Environment env;
	
		@Autowired
		public ElasticSearchConfig elasticSearchConfig;
		
		@Before
		public void setup() throws Exception {
			MockitoAnnotations.initMocks(this);}

		@Test
		public void wiringTest() {
			assertThat(elasticSearchConfig, is(notNullValue()));
		}

		@Test
		public void testElasticsearchTemplate() {
			ElasticsearchOperations e = elasticSearchConfig.elasticsearchTemplate(env);	
			assertThat(e, is(notNullValue()));
		}
}
