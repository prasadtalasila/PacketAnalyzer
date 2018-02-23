package in.ac.bits.protocolanalyzer.persistence.repository;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import in.ac.bits.protocolanalyzer.analyzer.event.BucketLimitEvent;
import in.ac.bits.protocolanalyzer.analyzer.event.EndAnalysisEvent;
import in.ac.bits.protocolanalyzer.analyzer.event.SaveRepoEndEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.Data;
import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;

@Component
@ComponentScan("config.in.ac.bits.protocolanalyzer.persistence.repository")
@Scope("prototype")
@Log4j
@Data
public class SaveRepository implements Runnable {

	@Autowired
	private ElasticsearchTemplate template;

	@Autowired
	private ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets;

	@Autowired
	private Runtime runtime;

	@Autowired
	private HashMap<String, String> envProperties;

	private int lowWaterMark;

	private boolean analysisOnly;

	private long memory;

	private boolean isRunning = false;

	private boolean analysisRunning = true;

	private EventBus eventBus;

	private static final long MEGABYTE = 1024L * 1024L;

	public void configure(EventBus eventBus) {
		this.eventBus = eventBus;
		this.eventBus.register(this);
		lowWaterMark = Integer.parseInt(envProperties.get("lowWaterMark"));
		log.info("LOW WATER MARK READ FROM FILE IS: " + lowWaterMark);
		analysisOnly = Boolean.parseBoolean(envProperties.get("analysisOnly"));
		log.info("Perform only analysis: " + analysisOnly);
	}

	public void setBucket(ArrayList<IndexQuery> bucket) {
		buckets.add(bucket);
	}

	public int getBucketSize() {
		return this.buckets.size();
	}

	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	@Override
	public void run() {
		this.isRunning = true;
		while (!buckets.isEmpty()) {
			memory = runtime.totalMemory() - runtime.freeMemory();
			log.info("Used memory is bytes: " + memory);
			log.info(System.currentTimeMillis() + " Used memory is megabytes: "
					+ bytesToMegabytes(memory));
			log.info("SaveRepository started at " + System.currentTimeMillis()
					+ " with bucket size: " + buckets.size());

			if (analysisOnly) {
				log.info("Not saving ... but polling");
				buckets.poll();
			} else {
				template.bulkIndex(buckets.poll()); // blocking call
			}

			log.info("SaveRepository finished at " + System.currentTimeMillis());

			if (buckets.size() == 0 && !analysisRunning) {
				this.publishEndOfSave(System.currentTimeMillis());
			}

			if (buckets.size() <= lowWaterMark) {
				this.publishLow();
			}
		}
		isRunning = false;
	}

	/**
	 * Since AnalysisRepository is blocked when SaveRepository is running, this
	 * thread itself ensures that analysis will resume when low water-mark is
	 * reached.
	 */
	private void publishLow() {
		log.info(System.currentTimeMillis());
		eventBus.post(new BucketLimitEvent("GO"));
	}

	@Subscribe
	public void end(EndAnalysisEvent event) {
		// log.info("Save repo received signal that analysis has ended");
		analysisRunning = false;
	}

	private void publishEndOfSave(long time) {
		// log.info("Publishing end of Save Repository");
		eventBus.post(new SaveRepoEndEvent(time));
	}
}