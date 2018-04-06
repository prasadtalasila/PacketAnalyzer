package unit.in.ac.bits.protocolanalyzer.persistence.repository;

import com.google.common.eventbus.EventBus;

import in.ac.bits.protocolanalyzer.analyzer.event.BucketLimitEvent;
import in.ac.bits.protocolanalyzer.persistence.repository.AnalysisRepository;
import in.ac.bits.protocolanalyzer.persistence.repository.SaveRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

@RunWith(MockitoJUnitRunner.class)
public class AnalysisRepositoryTest {
	
	@Mock
	public SaveRepository saveRepo;

	@Mock
	public ExecutorService executorService;
	
	@Spy
	public ArrayBlockingQueue<IndexQuery> queries = new ArrayBlockingQueue<IndexQuery>(10);
	
	@Spy
	public Timer pullTimer;
	
	@Spy
	public ArrayList<IndexQuery> currentBucket = new ArrayList<IndexQuery>(5);
	
	@Mock
	public HashMap<String, String> envProperties;
	
	@InjectMocks
	public AnalysisRepository analRepo;
	
	@Mock
	public EventBus bus;
	
	@Mock
	public IndexQuery query;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void analRepoAutoWiringCheck() {
		assertThat(analRepo, notNullValue());
		assertThat(analRepo.getSaveRepo(), notNullValue());
		assertThat(analRepo.getExecutorService(), notNullValue());
		assertThat(analRepo.getQueries(), notNullValue());
		assertThat(analRepo.getPullTimer(), notNullValue());
		assertThat(analRepo.getCurrentBucket(), notNullValue());
		assertThat(analRepo.getEnvProperties(), notNullValue());
	}
	
	@Test
	public void analRepoConfigurationTest() {
		doNothing().when(saveRepo).configure(bus);
		when(envProperties.get("highWaterMark")).thenReturn("6");
		
		analRepo.configure(bus);
		
		assertThat(analRepo.getEventBus(), equalTo(bus));
		assertThat(analRepo.getHighWaterMark(), equalTo(6));
	}
	
	@Test
	public void saveQueryTest() {
		analRepo.save(query);
		
		assertThat(analRepo.getQueries().peek(), equalTo(query));
	}
	
	@Test
	public void startAnalysisTestWithoutOverFlowWithoutFinished() {
		analRepo.save(query);
		analRepo.save(query);
		analRepo.setBucketCapacity(5);
		analRepo.setFinished(false);
		
		analRepo.start();
		// Ensuring that pull.run has ended
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		
		assertThat(analRepo.getQueries().size(), equalTo(0));
		//Checks for the successful transfer of queries from the queue to the bucket
		assertThat(analRepo.getCurrentBucket().size(), equalTo(2));
		//Checks for finished=false condition
		verify(saveRepo, times(0)).setBucket(currentBucket);
	}
	
	@Test
	public void startAnalysisTestWithOverFlowWIthFinished() {
		doNothing().when(saveRepo).setBucket(currentBucket);
		when(saveRepo.getBucketSize()).thenReturn(3);
		doNothing().when(bus).post(new BucketLimitEvent("STOP"));
		
		analRepo.save(query);
		analRepo.save(query);
		analRepo.save(query);
		analRepo.save(query);
		analRepo.save(query);
		analRepo.save(query);
		analRepo.setBucketCapacity(5);
		analRepo.setHighWaterMark(2);
		analRepo.setFinished(true);
		
		analRepo.start();
		// Ensuring that pull.run has ended
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		
		assertThat(analRepo.getQueries().size(), equalTo(0));
		//Checks if overflow condition with the Bucket's size was taken care of
		assertThat(analRepo.getCurrentBucket().size(), equalTo(1));
		//Checks if the completely filled bucket was passed on to saveRepo
		verify(saveRepo, times(1)).setBucket(currentBucket);
		//Checks if the second bucket was passed on to saveRepo or not
		verify(saveRepo, times(1)).setBucket(analRepo.getCurrentBucket());
	}
	
}
