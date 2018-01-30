package unit.in.ac.bits.protocolanalyzer.persistence.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;


import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.google.common.eventbus.EventBus;

import in.ac.bits.protocolanalyzer.analyzer.event.EndAnalysisEvent;
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
	public ArrayList<IndexQuery> listIndexQuery;
	
	@Mock
	public EndAnalysisEvent event;
	
	
	public long bytes = 123312312122L;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		saveRepo.configure(bus);
		assertThat(saveRepo.getLowWaterMark(), is(notNullValue(Integer.class)));
		assertThat(saveRepo.getLowWaterMark(), isA(Integer.class));
		
	}

	@Test
	public void wiringTest() {
		assertNotNull(saveRepo);
	}
	
	@Test
	public void isRunningTest() {
		assertEquals(false, saveRepo.isRunning());
	}

	@Test
	public void bytesToMegabytesTest() {
		assertEquals(bytes/(1024L*1024L), SaveRepository.bytesToMegabytes(bytes));
	}

	@Test
	public void bucketSizeTest() {
		assertEquals(0, saveRepo.getBucketSize());
		saveRepo.setBucket(listIndexQuery);
		assertEquals(1, saveRepo.getBucketSize());
		saveRepo.setBucket(listIndexQuery);
		assertEquals(2, saveRepo.getBucketSize());
	}
	
	@Test
	public void runTest() {
		saveRepo.setBucket(listIndexQuery);
		assertThat(saveRepo.getBuckets(), is(notNullValue(ConcurrentLinkedQueue.class)));
		assertThat(saveRepo.getBuckets(), isA(ConcurrentLinkedQueue.class));
		
		saveRepo.setAnalysisOnly(false);
		//saveRepo.run();
		
		saveRepo.setAnalysisOnly(true);
		saveRepo.run();
		assertEquals(false, saveRepo.isRunning());
		
	}
	
	@Test
	public void endTest() {
		saveRepo.end(event);
		assertThat(saveRepo.isAnalysisRunning(), equalTo(false));
	}
	
}