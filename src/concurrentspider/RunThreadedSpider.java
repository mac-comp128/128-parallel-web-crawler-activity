package concurrentspider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This contains main and the declarations of the data structures to be shared
 * by the 'Runnable' threads that get started.
 *
 * @author shoop, jackson
 *
 */
public class RunThreadedSpider {
	private static final String BEGINNING_URL = "https://www.macalester.edu";
	private static final int NUM_THREADS = 8;

	private static final int MAX_URLS = 64; // This number gets divided by the number of threads you have.


	/**
	 * Run the concurrent spider program.
	 * @param args
	 */
	public static void main(String [] args) {
		ConcurrentAllUrlsCounter urlCounts = runSpider(NUM_THREADS);
		printURLCounts(urlCounts);
	}

	/**
	 * Creates and runs numOfThreads spider objects that run concurrently on different threads
	 * @param numberOfThreads
	 * @return
	 */
	public static ConcurrentAllUrlsCounter runSpider(int numberOfThreads){
		// The pattern in these threaded shared memory programs is to declare the
		// shared data structures here in the 'main' class first, then pass them
		// into the constructor of the 'Runnable' for each new thread started.
		// In this case, we have a helper class containing the shared data.
		SharedSpiderData sharedData = new SharedSpiderData();

		int maxUrlsPerThread = MAX_URLS/numberOfThreads;
		int remainder = MAX_URLS % numberOfThreads;

		//launch the threads that will do the work in parallel
		Thread threads[] = new Thread[numberOfThreads];
		for (int i=0; i < numberOfThreads; i++ ) {
            int urlsForThread = maxUrlsPerThread;
            if (i==0){
                urlsForThread+=remainder;
            }
            threads[i] = startThread(i, urlsForThread, sharedData);
			System.out.println("Thread" + i + " started");
		}

		// wait until all threads complete
		for (int i=0; i < numberOfThreads; i++ ) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return sharedData.getUrlCounter();
	}


	/**
	 * Start a new ConcurrentSpider to process a certain number of URLS.
	 * @param threadNum
	 * @return
	 */
	public static Thread startThread(int threadNum, int urlsPerThread, SharedSpiderData sharedData) {
		Thread t;
		if (threadNum != 0) {
			t = new Thread(new ConcurrentSpider(sharedData, urlsPerThread));
		} else {
			t = new Thread(new ConcurrentSpider(sharedData, urlsPerThread, BEGINNING_URL));
		}
		t.start(); // Calls back to the run method in ConcurrentSpider class
		return t;
	}

	/**
	 * For each of the urls encountered, this prints the number of incoming links.
	 * @param urlCounts
	 */
	public static void printURLCounts (ConcurrentAllUrlsCounter urlCounts){
		//count up the words found
		UrlCount[] counts = urlCounts.getCounts();
		Arrays.sort(counts);
		for (UrlCount urlCount : counts) {
			System.out.println("url " + urlCount.getUrl() + " found " + urlCount.getCount() + " times.");
		}
	}
}

