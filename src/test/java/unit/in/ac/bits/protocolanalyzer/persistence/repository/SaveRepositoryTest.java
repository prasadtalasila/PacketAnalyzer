package unit.in.ac.bits.protocolanalyzer.persistence.repository;

import com.google.common.eventbus.EventBus;

import in.ac.bits.protocolanalyzer.analyzer.event.EndAnalysisEvent;
import in.ac.bits.protocolanalyzer.persistence.repository.SaveRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

@RunWith(MockitoJUnitRunner.class)
public class SaveRepositoryTest {

	@Mock
	public ElasticsearchTemplate template;

	@Spy
	public ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets;

	@Mock
	public Runtime runtime;

	@Mock
	public HashMap<String, String> envProperties;

	@InjectMocks
	public SaveRepository saveRepo;

	@Mock
	public EventBus bus;

	@Mock
	public ArrayList<IndexQuery> listIndexQuery;

	@Mock
	public EndAnalysisEvent event;

	public long bytes = 123312312122L;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void saveRepoAutoWiringCheck() {
		assertThat(saveRepo, notNullValue());
		assertThat(saveRepo.getTemplate(), notNullValue());
		assertThat(saveRepo.getBuckets(), notNullValue());
		assertThat(saveRepo.getRuntime(), notNullValue());
		assertThat(saveRepo.getEnvProperties(), notNullValue());
	}

	@Test
	public void saveRepoConfigurationCheck() {
		doNothing().when(bus).register(saveRepo);
		when(envProperties.get("lowWaterMark")).thenReturn("2");
		when(envProperties.get("analysisOnly")).thenReturn("true");

		saveRepo.configure(bus);
		
		verify(bus).register(saveRepo);
		verify(envProperties).get("lowWaterMark");
		verify(envProperties).get("analysisOnly");
		assertThat(saveRepo.getLowWaterMark(), equalTo(2));
		assertThat(saveRepo.isAnalysisOnly(), equalTo(true));

	}

	@Test
	public void memSizeConversionCheck() {
		assertThat(SaveRepository.bytesToMegabytes(bytes),equalTo(bytes / (1024L * 1024L)));
	}

	@Test
	public void runTest() {
		saveRepo.setBucket(listIndexQuery);
		saveRepo.setBucket(listIndexQuery);
		saveRepo.setBucket(listIndexQuery);
		saveRepo.setAnalysisOnly(true);
		saveRepo.setAnalysisRunning(false);
		saveRepo.setLowWaterMark(2);

		when(runtime.totalMemory()).thenReturn(10L);
		when(runtime.freeMemory()).thenReturn(1L);

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<?> future = executorService.submit(saveRepo);
		try {
			// Ensuring that saveRepo.run has ended
			if (future.get() == null) {
				verify(buckets, times(4)).isEmpty();
				verify(runtime, times(3)).totalMemory();
				verify(runtime, times(3)).freeMemory();
				verify(buckets, times(3)).poll();
				verify(buckets, times(9)).size();

				assertThat(saveRepo.getMemory(), equalTo(9L));
				assertThat(saveRepo.isRunning(), equalTo(false));

			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		executorService.shutdown();
	}

	@Test
	public void terminateSaveTest() {
		saveRepo.end(event);
		assertThat(saveRepo.isRunning(), equalTo(false));
	}

	@Test
	public void accessBucketSizeTest() {
		saveRepo.setBucket(listIndexQuery);
		saveRepo.setBucket(listIndexQuery);
		saveRepo.setBucket(listIndexQuery);

		assertThat(saveRepo.getBucketSize(), equalTo(3));
	}

}