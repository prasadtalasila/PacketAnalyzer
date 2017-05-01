package in.ac.bits.protocolanalyzer.persistence.repository;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import in.ac.bits.protocolanalyzer.analyzer.event.BucketLimitEvent;

@Component
@Scope("prototype")
@Log4j
public class SaveRepository implements Runnable {

	@Autowired
	private ElasticsearchTemplate template;

	private ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets;

	private boolean isRunning = false;

	private EventBus eventBus;

	private int lowWaterMark;

	public boolean isRunning() {
		return isRunning;
	}

	public void configure(EventBus eventBus) {
		buckets = new ConcurrentLinkedQueue<ArrayList<IndexQuery>>();
		this.eventBus = eventBus;
		try {
			Context ctx = new InitialContext();
			Context env = (Context) ctx.lookup("java:comp/env");
			lowWaterMark = Integer.parseInt((String) env.lookup("lowWaterMark"));
			log.info("LOW WATER MARK READ FROM FILE IS: " + lowWaterMark);
		} catch (NamingException e) {
			log.info("EXCEPTION IN READING FROM CONFIG FILE");
			lowWaterMark = 3;
		}
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
			log.info(
					"SaveRepository started at " + System.currentTimeMillis() + " with bucket size: " + buckets.size());
			template.bulkIndex(buckets.poll()); // blocking call
			log.info("SaveRepository finished at " + System.currentTimeMillis());

			if (buckets.size() <= lowWaterMark) {
				this.publishLow();
			}
		}
		isRunning = false;
	}

	private void publishLow() {
		eventBus.post(new BucketLimitEvent("GO"));
	}
}
