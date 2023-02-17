package concurrentspider;

import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.robots.SimpleRobotRules.RobotRulesMode;

public class HttpHelper {

	final String USER_AGENT = "MacalesterComp128/1.0";
	private Map<String, SimpleRobotRules> robotsTxtRules;
	private HttpClient client;

	public HttpHelper(){
		robotsTxtRules = new HashMap<>();
		client = HttpClient.newBuilder()
					.version(Version.HTTP_1_1)
					.followRedirects(Redirect.NORMAL)
					.connectTimeout(Duration.ofSeconds(10))
					.build();
	}

	/**
	 * Returns the contents of a url as a string.
	 * @param urlStr
	 * @return String contents or null in case of an error.
	 */
	public String retrieve(String urlStr) {
		try {
			Document htmlDoc = Jsoup.connect(urlStr).userAgent(USER_AGENT).get();
			return htmlDoc.toString();

		} catch (Exception ex) {
			System.err.println("http fetch of '" + urlStr + "' failed: "+ex.getMessage());
			//ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the web links contained in the website with baseURL.
	 * @param baseUrl
	 * @return
	 */
	public List<String> extractLinks(String baseUrl) {
		try {
			Document htmlDoc = Jsoup.connect(baseUrl).userAgent(USER_AGENT).get();
			Thread.sleep(200); // add a politness delay so we don't overwhelm any servers
			return extractLinks(htmlDoc);

		} catch (Exception ex) {
			System.err.println("http fetch of '" + baseUrl + "' failed: "+ex.getMessage());
			//ex.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**
	 * Returns the web links contained in the html content
	 * @param baseUrl
	 * @param html
	 * @return
	 */
	public List<String> extractLinks(String baseUrl, String html){
		Document htmlDoc = Jsoup.parse(html);
		htmlDoc.setBaseUri(baseUrl);
		return extractLinks(htmlDoc);
	}

	private List<String> extractLinks(Document htmlDoc){
		List<String> links = new ArrayList<>();
		Elements elements = htmlDoc.select("a[href]");
		for(Element elem : elements){
			String url = elem.attr("abs:href");
			if (isCrawlingAllowed(url)){
				links.add(url);
			}
		}

		return links;
	}

	private boolean isCrawlingAllowed(String url){
		try {
			URL urlObj = new URL(url);
			String hostId = urlObj.getProtocol() + "://" + urlObj.getHost()
							+ (urlObj.getPort() > -1 ? ":" + urlObj.getPort() : "");
			SimpleRobotRules rules = robotsTxtRules.get(hostId);
			if (rules == null) {
			
				HttpRequest request = HttpRequest.newBuilder()
					.uri(urlObj.toURI())
					.build();

				HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
		
				if (response.statusCode() == 404) {
					rules = new SimpleRobotRules(RobotRulesMode.ALLOW_ALL);
				} 
				else {
					SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
					rules = robotParser.parseContent(hostId, response.body(),"text/plain", USER_AGENT);
				}
				robotsTxtRules.put(hostId, rules);
			}
			return rules.isAllowed(url);
		}
		catch(Exception e){
			return false;
		}
	}

}
