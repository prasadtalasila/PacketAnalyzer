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
import com.google.common.eventbus.Subscribe;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import in.ac.bits.protocolanalyzer.analyzer.event.BucketLimitEvent;
import in.ac.bits.protocolanalyzer.analyzer.event.EndAnalysisEvent;
import in.ac.bits.protocolanalyzer.analyzer.event.SaveRepoEndEvent;

@Component
@Scope("prototype")
@Log4j
public class SaveRepository implements Runnable {

	@Autowired
	private ElasticsearchTemplate template;

	private ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets;

	private boolean isRunning = false;

	private boolean analysisRunning = true;

	private EventBus eventBus;

	private int lowWaterMark;

	private boolean analysisOnly;

	public boolean isRunning() {
		return isRunning;
	}

	public void configure(EventBus eventBus) {
		buckets = new ConcurrentLinkedQueue<ArrayList<IndexQuery>>();
		this.eventBus = eventBus;
		this.eventBus.register(this);
		try {
			Context ctx = new InitialContext();
			Context env = (Context) ctx.lookup("java:comp/env");
			lowWaterMark = Integer.parseInt((String) env.lookup("lowWaterMark"));
			log.info("LOW WATER MARK READ FROM FILE IS: " + lowWaterMark);
		} catch (NamingException e) {
			log.info("EXCEPTION IN READING FROM CONFIG FILE");
			lowWaterMark = 3;
		}
		//Set the value of the analysisOnly
		try {
			Context ctx = new InitialContext();
			Context env = (Context) ctx.lookup("java:comp/env");
			if (((String) env.lookup("analysisOnly")).equals("true")) {
				analysisOnly = true;
			} else {
				analysisOnly = false;
			}
			log.info("Perform only analysis: " + analysisOnly);
		} catch (NamingException e) {
			log.info("EXCEPTION IN READING FROM CONFIG FILE FOR analysisOnly .. setting false by default");
			analysisOnly = false;
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
			
			if ( analysisOnly ) {
				log.info("Not saving ... but polling");
				buckets.poll();
			} else {
				template.bulkIndex(buckets.poll()); // blocking call
			}			
			log.info("SaveRepository finished at " + System.currentTimeMillis());

			if ( buckets.size() == 0 && !analysisRunning ) {
				this.publishEndOfSave(System.currentTimeMillis());
			}

			if (buckets.size() <= lowWaterMark) {
				this.publishLow();
			}
		}
		isRunning = false;
	}

	private void publishLow() {
		eventBus.post(new BucketLimitEvent("GO"));
	}

    @Subscribe
	public void end(EndAnalysisEvent event) {
		//log.info("Save repo received signal that analysis has ended");
		analysisRunning = false;
	}

	private void publishEndOfSave(long time) {
		//log.info("Publishing end of Save Repository");
		eventBus.post(new SaveRepoEndEvent(time));
	}
}
