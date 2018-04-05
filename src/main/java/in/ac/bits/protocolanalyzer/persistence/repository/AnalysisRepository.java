package in.ac.bits.protocolanalyzer.persistence.repository;

import com.google.common.eventbus.EventBus;

import in.ac.bits.protocolanalyzer.analyzer.event.BucketLimitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;

@Component
@ComponentScan("config.in.ac.bits.protocolanalyzer.persistence.repository")
@Scope("prototype")
@Log4j
@Getter
@Setter
public class AnalysisRepository {

	@Autowired
	private SaveRepository saveRepo;

	@Autowired
	private ExecutorService executorService;
	
	@Autowired
	private ArrayBlockingQueue<IndexQuery> queries;
	
	@Autowired
	private Timer pullTimer;
	
	@Autowired
	private ArrayList<IndexQuery> currentBucket;
	
	@Autowired
	private HashMap<String, String> envProperties;
	
	private boolean finished = false;
	
	private int bucketCapacity = 20000;

	private EventBus eventBus;

	private int highWaterMark;

	public void configure(EventBus eventBus) {
		this.eventBus = eventBus;
		saveRepo.configure(eventBus);
		highWaterMark = Integer.parseInt(envProperties.get("highWaterMark"));
		log.info("HIGH WATER MARK READ FROM FILE IS: " + highWaterMark);
	}

	public void save(IndexQuery query) {
		queries.add(query);
	}

	public void start() {
		log.info("Starting analysis repository...");
		TimerTask pull = new TimerTask() {
			
			@Override
			public void run() {
				while (!queries.isEmpty()) {
					int size = currentBucket.size();
					// log.info(">> AnalysisRepository: " +
					// System.currentTimeMillis() + " SIZE: " + size);
					if (size < bucketCapacity) {
						while (!queries.isEmpty() && size < bucketCapacity) {
							currentBucket.add(queries.poll());
							size++;
						}
					} else {
						saveRepo.setBucket(currentBucket);
						log.info(">> Saving bucket in SaveRepository at " + System.currentTimeMillis());
						if (!saveRepo.isRunning()) {
							executorService.execute(saveRepo);
						}
						currentBucket = new ArrayList<IndexQuery>();
						checkBucketLevel();
					}
				}
				if (finished) {
					saveRepo.setBucket(currentBucket);
					log.info(">> Saving bucket in SaveRepository at " + System.currentTimeMillis());
					if (!saveRepo.isRunning()) {
						executorService.execute(saveRepo);
					}
					finished = false;
					checkBucketLevel();
				}
			}
		};
		pullTimer.schedule(pull, 0, 10);
		
	}

	/**
	*	AnalysisRepo repeatedly checks the size of the Queue in SaveRepo everytime a bucket is filled.
	*	If the number of buckets is more than high water-mark. Analysis stops.
	*/
	private void checkBucketLevel() {
		// log.info("BUCKET SIZE: " + saveRepo.getBucketSize() + " ||
		// highWaterMark =" + this.highWaterMark);
		if (saveRepo.getBucketSize() >= highWaterMark) {
			this.publishHigh();
		}
	}

	private void publishHigh() {
		// log.info("Publishing STOP");
		log.info(System.currentTimeMillis());
		eventBus.post(new BucketLimitEvent("STOP"));
	}
}
