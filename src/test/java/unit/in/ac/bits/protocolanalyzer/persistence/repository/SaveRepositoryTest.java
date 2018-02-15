package unit.in.ac.bits.protocolanalyzer.persistence.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.naming.Context;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import com.google.common.eventbus.EventBus;

import in.ac.bits.protocolanalyzer.analyzer.event.EndAnalysisEvent;
import in.ac.bits.protocolanalyzer.persistence.repository.SaveRepository;

@RunWith(MockitoJUnitRunner.class)
public class SaveRepositoryTest {
	@Mock
	public ElasticsearchTemplate template;
	
	@Spy
	public ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets;
	
	@Mock
	public Context ctx;
	
	@Mock
	public Runtime runtime;
	
	@InjectMocks
	public SaveRepository saveRepo;
	
	@Mock
	public Context ctxString;

	@Mock
	public EventBus bus;
	
	@Mock
	public ArrayList<IndexQuery> listIndexQuery;
	
	@Mock
	public EndAnalysisEvent event;
	
	
	public long bytes = 123312312122L;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void configureTest() {
		doNothing().when(bus).register(saveRepo);
		try {
			when(ctx.lookup("java:comp/env")).thenReturn(ctxString);
			when(ctxString.lookup("lowWaterMark")).thenReturn("2");
			when(ctxString.lookup("analysisOnly")).thenReturn("true");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
		saveRepo.configure(bus);
		verify(bus).register(saveRepo);
		
		try {
			verify(ctx,times(2)).lookup("java:comp/env");
			verify(ctxString,times(1)).lookup("lowWaterMark");
			
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
		assertThat(saveRepo.getLowWaterMark(), equalTo(2));
		assertThat(saveRepo.isAnalysisOnly(), equalTo(true));
		assertNotNull(saveRepo);
	}
	
	@Test
	public void bytesToMegabytesTest() {
		assertThat(SaveRepository.bytesToMegabytes(bytes), equalTo(bytes/(1024L*1024L)));
	}
	
	
	@Test
	public void runTest() {
		saveRepo.setBucket(listIndexQuery);
		saveRepo.setAnalysisOnly(true);
		saveRepo.analysisRunning = false;
		saveRepo.setLowWaterMark(2);
		
		when(runtime.totalMemory()).thenReturn(10L);
		when(runtime.freeMemory()).thenReturn(1L);
		
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<?> future = executorService.submit(saveRepo);
		try {
			if(future.get()==null) {
				verify(buckets,times(2)).isEmpty();
				verify(runtime,times(1)).totalMemory();
				verify(runtime,times(1)).freeMemory();
				verify(buckets,times(1)).poll();
				verify(buckets,times(3)).size();
				
				assertThat(saveRepo.memory, equalTo(9L));
				assertThat(saveRepo.isRunning(), equalTo(false));
				
			}
		} catch (InterruptedException | ExecutionException e) {
		    e.printStackTrace();
		}
		
		executorService.shutdown();
	}
	
	@Test
	public void endTest() {
		saveRepo.end(event);
		assertThat(saveRepo.analysisRunning, equalTo(false));
	}
	
	@Test
	public void getBucketsSize() {
		saveRepo.setBucket(listIndexQuery);
		saveRepo.setBucket(listIndexQuery);
		saveRepo.setBucket(listIndexQuery);
		
		assertThat(saveRepo.getBucketSize(), equalTo(3));
	}
	
}