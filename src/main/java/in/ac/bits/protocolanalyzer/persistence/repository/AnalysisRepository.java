package in.ac.bits.protocolanalyzer.persistence.repository;

import com.google.common.eventbus.EventBus;

import in.ac.bits.protocolanalyzer.analyzer.event.BucketLimitEvent;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
@Log4j
public class AnalysisRepository {

	@Autowired
	private SaveRepository saveRepo;

	private ExecutorService executorService;
	private Queue<IndexQuery> queries;

	private Timer pullTimer;
	private boolean isFinished = false;

	private ArrayList<IndexQuery> currentBucket;
	private int bucketCapacity = 20000;

	private EventBus eventBus;

	private int highWaterMark;

	/**
	   * Configures the instance according to the parameters.
	   * @param {eventBus}
	   */
	public void configure(EventBus eventBus) {
		this.queries = new ArrayBlockingQueue<>(100000);
		executorService = Executors.newFixedThreadPool(2);
		currentBucket = new ArrayList<IndexQuery>();
		pullTimer = new Timer("pullTimer");
		this.eventBus = eventBus;
		saveRepo.configure(eventBus);
		try {
			Context ctx = new InitialContext();
			Context env = (Context) ctx.lookup("java:comp/env");
			highWaterMark = Integer.parseInt((String) env.lookup("highWaterMark"));
			log.info("HIGH WATER MARK READ FROM FILE IS: " + highWaterMark);
		} catch (NamingException e) {
			log.info("EXCEPTION IN READING FROM CONFIG FILE");
			highWaterMark = 5;
		}
	}

	public void isFinished() {
		this.isFinished = true;
	}

	public void save(IndexQuery query) {
		queries.add(query);
	}

	/**
	*	Starts the analysis repository. 
	*/
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
						while (!queries.isEmpty()
								&& size < bucketCapacity) {
							currentBucket.add(queries.poll());
							size++;
						}
					} else {
						saveRepo.setBucket(currentBucket);
						log.info(">> Saving bucket in SaveRepository at " 
						+ System.currentTimeMillis());
						if (!saveRepo.isRunning()) {
							executorService.execute(saveRepo);
						}
						currentBucket = new ArrayList<IndexQuery>();
						checkBucketLevel();
					}
				}
				if (isFinished) {
					saveRepo.setBucket(currentBucket);
					log.info(">> Saving bucket in SaveRepository at " + System.currentTimeMillis());
					if (!saveRepo.isRunning()) {
						executorService.execute(saveRepo);
					}
					isFinished = false;
					checkBucketLevel();
				}
			}
		};
		pullTimer.schedule(pull, 0, 10);
	}

	/**
	*	AnalysisRepo repeatedly checks the size 
	*   of the Queue in SaveRepo everytime a bucket is filled.
	*	If the number of buckets is more
	*   than high water-mark. Analysis stops.
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
