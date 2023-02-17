package concurrentspider;

/**
 * The class that implements the Runnable interface is the one
 * that defines what is to be done by each 'thread' running concurrently.
 * In our case, grabbing pages from the 'work' queue and processing them.
 *
 * @author shoop, jackson
 *
 */
public class ConcurrentSpider implements Runnable {
    private String beginningURL = null;
    /**
     * Helps download and parse the web pages.
     */
    private HttpHelper helper = new HttpHelper();
    /**
     * Maximum number of urls that should be scraped.
     */
    private int maxUrls;   // you can experiment with this value by changing what is passed into the constructor

    private int urlCount = 0;


    // To continue the 'pattern' from RunThreadedSpider, have this shared data
    // be passed into the constructor from the single place where it was originally created.
    private SharedSpiderData sharedData;

    /**
     * Create a new spider with access to the shared data by passing it a reference.
     * This constructor could be called many times.
     * @param data
     */
    public ConcurrentSpider(SharedSpiderData data, int maxUrls) {
        sharedData = data;
        this.maxUrls = maxUrls;
    }
    /**
     * Create a new spider with access to the shared data by passing it a reference.
     * Also give it the starting point to begin scraping.  This constructor should be called
     * once for the single thread that will start the process at the beginning URL.
     * @param data
     * @param startURL
     */
    public ConcurrentSpider(SharedSpiderData data, int maxUrls, String startURL) {
        sharedData = data;
        this.maxUrls = maxUrls;
        beginningURL = startURL;
    }

    /**
     * The method that is executed when you 'start()' a thread with this class.
     * Thus, the thread behavior is here in this run method.
     */
    public void run() {
        // To get things started, we need one thread to put the starting point
        // URL onto the work queue.
        if (beginningURL != null) {
            try {
                sharedData.getWork().put(beginningURL); // We call put here because it will block, i.e. wait, if the queue is full until it can actually add.
            } catch (InterruptedException e) {
                // catch errors that can occur from the 'put' to the shared queue
                System.out.println("Error putting data into work queue");
                e.printStackTrace();
            }
        }

        while (urlCount <= maxUrls) {  // each thread does a certain amount of 'work'
            String url = null;
            //TODO: take the next url from the work queue (make sure to try/catch any possible exceptions).
            //      If it has not already been finished, process the page.
            //      After processing, mark it as finished.
            
                
            
            
            

            
            


            urlCount++;
        }
        System.err.println("ConcurrentSpider done with URLs");
    }

    /**
     * Retrieves content from a url and processes that content.
     * @param url
     */
    public void processPage(String url) {

        for (String url2 : helper.extractLinks(url)) {
            //System.out.println("next URL on " + url + " : " + url2);
            
            // TODO: process the page here
            // For each link increment the count for the link.
            // Queue up the link for future scraping if it has not already been finished.
            // Hint: Remember that calling add or offer won't have the correct behavior we want with a blocking queue.
            // If the queue is full, we want it to wait until we can actually put the url into the queue.
        
           
        }
    }


}
