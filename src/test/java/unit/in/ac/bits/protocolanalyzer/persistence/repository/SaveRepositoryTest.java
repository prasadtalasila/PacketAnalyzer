package unit.in.ac.bits.protocolanalyzer.persistence.repository;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.google.common.eventbus.EventBus;

import in.ac.bits.protocolanalyzer.persistence.repository.SaveRepository;
import unit.config.in.ac.bits.protocolanalyzer.persistence.repository.SaveRepositoryTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SaveRepositoryTestConfig.class, loader = AnnotationConfigContextLoader.class)
public class SaveRepositoryTest {
	@Autowired
	public SaveRepository saveRepo;

	@Mock
	public EventBus bus;

	@Mock
	public ElasticsearchTemplate template;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		saveRepo.configure(bus);
	}

	@Test
	public void wiringTest() {
		assertThat(saveRepo,is(notNullValue()));
	}

	@Test
	public void testConfiguration() {
		assertThat(0, equalTo(saveRepo.getBucketSize()));
		ArrayList<IndexQuery> l = new ArrayList<IndexQuery>();
		saveRepo.setBucket(l);
		assertThat(1,equalTo(saveRepo.getBucketSize()));
		saveRepo.setBucket(l);
		assertThat(2,equalTo(saveRepo.getBucketSize()));
	}
}