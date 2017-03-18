package in.ac.bits.protocolanalyzer.persistence.repository;


import in.ac.bits.protocolanalyzer.analyzer.event.BucketLimitEvent;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;

@Component
@Scope("prototype")
@Log4j
public class SaveRepository implements Runnable {

	@Autowired
	private ElasticsearchTemplate template;

	private ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets;

	private boolean isRunning = false;

	private EventBus eventBus;

	public boolean isRunning() {
		return isRunning;
	}

	public void configure(EventBus eventBus) {
		buckets = new ConcurrentLinkedQueue<ArrayList<IndexQuery>>();
		this.eventBus = eventBus;
	}

	public void setBucket(ArrayList<IndexQuery> bucket) {
		buckets.add(bucket);
	}

	public int getBucketSize() {
		return this.buckets.size();
	}

	@Override
	public void run() {
		this.isRunning = true;
		while (!buckets.isEmpty()) {
			log.info("SaveRepository started at " + System.currentTimeMillis() + " with bucket size: " + buckets.size());
			template.bulkIndex(buckets.poll()); //blocking call
			log.info("SaveRepository finished at " + System.currentTimeMillis());
			
			if ( buckets.size() <= 3 ) {
				this.publishLow();
			}
			
		}
		isRunning = false;
	}

	/**
	*	Since AnalysisRepository is blocked when SaveRepository is running, this thread itself ensures that
	*	analysis will resume when low water-mark is reached.
	*/
    private void publishLow() {
        eventBus.post(new BucketLimitEvent("GO"));
    }
	
}
