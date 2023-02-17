package concurrentspider;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import concurrentspider.*;

/**
 * @author shilad
 *
 */
public class TestSpider  {
	private static final String BEGINNING_URL = "https://www.macalester.edu";
	/**
	 * Test the processPage method of the spider.
	 */
    @Test
	public void testProcessPage() {
		SharedSpiderData sharedData = new SharedSpiderData();
		ConcurrentSpider spider = new ConcurrentSpider(sharedData, 10);
		spider.processPage(BEGINNING_URL);
		
		assertTrue(sharedData.getWork().size() > 6);

		// nothing in the work queue should already be finished
		for(String url : sharedData.getWork()){
			assertFalse(sharedData.getFinished().contains(url));
		}
		
		int i = 0;
		for (UrlCount urlCount : sharedData.getUrlCounter().getCounts()) {
			i += urlCount.getCount();
		}
		assertTrue(i >= 10);
	}

	
	

}
