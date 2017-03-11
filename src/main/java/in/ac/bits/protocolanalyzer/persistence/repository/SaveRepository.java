package in.ac.bits.protocolanalyzer.persistence.repository;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Log4j
public class SaveRepository implements Runnable {

	@Autowired
	private ElasticsearchTemplate template;

	private ConcurrentLinkedQueue<ArrayList<IndexQuery>> buckets;

	private boolean isRunning = false;

	public boolean isRunning() {
		return isRunning;
	}

	public void configure() {
		buckets = new ConcurrentLinkedQueue<ArrayList<IndexQuery>>();
	}

	public void setBucket(ArrayList<IndexQuery> bucket) {
		buckets.add(bucket);
	}

	@Override
	public void run() {
		this.isRunning = true;
		while (!buckets.isEmpty()) {
			log.info("SaveRepository started at " + System.currentTimeMillis() + " with bucket size: " + buckets.size());
			template.bulkIndex(buckets.poll());
			log.info("SaveRepository finished at " + System.currentTimeMillis());
		}
		isRunning = false;
	}

}
