package in.ac.bits.protocolanalyzer.persistence.repository;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
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
@ComponentScan("config.in.ac.bits.protocolanalyzer.persistence.repository")
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
	
	//eventBus, getting initialized through a setter-method, DI is managed/
	public void configure(EventBus eventBus) {
		setBuckets(new ConcurrentLinkedQueue<ArrayList<IndexQuery>>());
		this.eventBus = eventBus;
		this.eventBus.register(this);
		try {
			Context ctx = new InitialContext();
			Context env = (Context) ctx.lookup("java:comp/env");
			setLowWaterMark(Integer.parseInt((String) env.lookup("lowWaterMark")));
			log.info("LOW WATER MARK READ FROM FILE IS: " + getLowWaterMark());
		} catch (NamingException e) {
			log.info("EXCEPTION IN READING FROM CONFIG FILE");
			setLowWaterMark(3);
		}
		//Set the value of the analysisOnly
		try {
			Context ctx = new InitialContext();
			Context env = (Context) ctx.lookup("java:comp/env");
			if (((String) env.lookup("analysisOnly")).equals("true")) {
				setAnalysisOnly(true);
			} else {
				setAnalysisOnly(false);
			}
			log.info("Perform only analysis: " + isAnalysisOnly());
		} catch (NamingException e) {
			log.info("EXCEPTION IN READING FROM CONFIG FILE FOR analysisOnly .. setting false by default");
			setAnalysisOnly(false);
		}
	}

	public void setBucket(ArrayList<IndexQuery> bucket) {
		getBuckets().add(bucket);
	}

	public int getBucketSize() {
		return this.getBuckets().size();
	}

	private static final long MEGABYTE = 1024L * 1024L;

	public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
	}

	@Override
	public void run() {
		this.isRunning = true;
		while (!getBuckets().isEmpty()) {

			// Get the Java runtime
            Runtime runtime = Runtime.getRuntime();
            // Run the garbage collector
            //runtime.gc();
            // Calculate the used memory
            long memory = runtime.totalMemory() - runtime.freeMemory();
            log.info("Used memory is bytes: " + memory);
            log.info(System.currentTimeMillis() + " Used memory is megabytes: "+ bytesToMegabytes(memory));

			log.info("SaveRepository started at " + System.currentTimeMillis() + " with bucket size: " + getBuckets().size());

			if ( isAnalysisOnly() ) {
				log.info("Not saving ... but polling");
				getBuckets().poll();
			} else {
				template.bulkIndex(getBuckets().poll()); // blocking call
			}
			log.info("SaveRepository finished at " + System.currentTimeMillis());

			if ( getBuckets().size() == 0 && !isAnalysisRunning() ) {
				this.publishEndOfSave(System.currentTimeMillis());
			}

			if (getBuckets().size() <= getLowWaterMark()) {
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
		log.info(System.currentTimeMillis());
		eventBus.post(new BucketLimitEvent("GO"));
	}

	@Subscribe
	public void end(EndAnalysisEvent event) {
		//log.info("Save repo received signal that analysis has ended");
		setAnalysisRunning(false);
	}

	private void publishEndOfSave(long time) {
		//log.info("Publishing end of Save Repository");
		eventBus.post(new SaveRepoEndEvent(time));
	}

	public int getLowWaterMark() {
		return lowWaterMark;
	}

	public void setLowWaterMark(int lowWaterMark) {
		this.lowWaterMark = lowWaterMark;
	}

	public boolean isAnalysisOnly() {
		return analysisOnly;
	}

	public void setAnalysisOnly(boolean analysisOnly) {
		this.analysisOnly = analysisOnly;
	}

	public ConcurrentLinkedQueue<ArrayList<IndexQuery>> getBuckets() {
		return buckets;
	}

	public void setBuckets(ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets) {
		this.buckets = buckets;
	}

	public boolean isAnalysisRunning() {
		return analysisRunning;
	}

	public void setAnalysisRunning(boolean analysisRunning) {
		this.analysisRunning = analysisRunning;
	}
}