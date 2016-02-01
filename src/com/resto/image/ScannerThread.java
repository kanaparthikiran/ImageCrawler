/**
 * 
 */
package com.resto.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.resto.image.util.ImageCrawlerPropertyUtil;

/**
 *  This Thread acts as a Producer for producing the list of Images from the WebPages.
 * @author kkanaparthi
 */
public class ScannerThread implements Runnable {

	// String hyperLink;
	BlockingQueue<Map<String, Set<String>>> scannerQueue;
	private String startPage = null;
	private String homePage = null;

	private static int linksInnerCount = 0;
	private static Set<String> processedHyperLinksList = new LinkedHashSet<String>();
	private final Logger log;

	/**
	 * 
	 * @param consumerQueue
	 */
	public ScannerThread(BlockingQueue<Map<String, Set<String>>> scannerQueue,
			String startPage, Logger log,String homePage) {
		this.scannerQueue = scannerQueue;
		this.log = log;
		this.startPage = startPage;
		this.homePage= homePage;
	}
	

	@Override
	public void run() {
		try {
			scanPages(startPage, log);
		} catch (IOException e) {
			log.error("The Exception in the Scanner Thread is : "
					+ e.getMessage());
		}
	}

	/**
	 * This method scans each page and collects the Images from the WebPages.
	 * @throws IOException
	 * 
	 */
	public void scanPages(String scanPage, Logger log) throws IOException {
		log.info("Page that is going to Scanned is " + scanPage);
		try {
			Document doc = null;
			Elements imagesOnThisPage = null;

			if (scanPage != null && !scanPage.isEmpty()) {
				doc = Jsoup.connect(scanPage).ignoreContentType(true)
						.timeout(500 * 1000).get();
				imagesOnThisPage = doc.getElementsByTag("img");

				// Mark it as scanned/parsed
				processedHyperLinksList.add(scanPage);

				Set<String> imagesSet = new HashSet<String>();
				Map<String, Set<String>> hyperLinksMapIn = new LinkedHashMap<String, Set<String>>();
				List<String> linksAttribsList = new ArrayList<String>();

				imagesSet = collectImageLinks(imagesOnThisPage, log);
				if (ImageCrawlerPropertyUtil.isValidCollection(imagesSet)) {
					if (log.isDebugEnabled()) {
						log.debug("Images Size And the Images Are  "
								+ imagesSet.size());
					}
				}
				// Store the PageURL, and the imageSet for the Page
				hyperLinksMapIn.put(scanPage, imagesSet);
				linksInnerCount++;

				scannerQueue.put(hyperLinksMapIn);
				getAdjacentLinks(log, doc, linksAttribsList);
				
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Page that is Skipped is " + scanPage+" and is Null or Empty");
				}
			}
		} catch (Exception ex) {
			log.error("The Exception While Parsing the Pages is :" + ex);
		}
	}


	/**
	 * This method gets the Adjacent Links from each Link
	 * @param log
	 * @param doc
	 * @param linksAttribsList
	 * @throws IOException
	 */
	private void getAdjacentLinks(Logger log, Document doc,
			List<String> linksAttribsList) throws IOException {
		// Get all Adjacent hyperLinks
		Elements links = doc.getElementsByTag("a");

		if (ImageCrawlerPropertyUtil.isValidCollection(links)) {
			for (Element link : links) {
				linksAttribsList.add(link.toString());
			}
		}
		if(ImageCrawlerPropertyUtil.isValidCollection(linksAttribsList)) {
		for (String linkStr : linksAttribsList) {
			if (!processedHyperLinksList.contains(linkStr)) {
				String hyperLink = getHyperLinkFromTag(linkStr,
						homePage, log);
				log.info("Hyperlink that is going to be Clicked is "
						+ hyperLink);
				processedHyperLinksList.add(linkStr);
				scanPages(hyperLink, log);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("The element is added as backEdge, and will not be parsed now"+linkStr);
				}
			}
		}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("The linksSet is Null or Empty");
			}
		}
	}

	
	
	/**
	 * This method gets/derives the hyperlinks from the anchor Tags, and skips navigating to the embedded URLs(SocialNetworkg,Advertising  sites Etc.)
	 * @param element
	 * @param homePage
	 * @return
	 */
	public static String getHyperLinkFromTag(String element, String homePage,
			Logger log) {
		int count = 0;
		String completeUrl = "";
		try {
			if (log.isDebugEnabled()) {
				log.debug("The Link is " + element);
			}
			String hLink = element;
			if (ImageCrawlerPropertyUtil.isValidString(hLink)
					&& hLink.contains("href") && !hLink.contains("@")
					&& !hLink.contains("facebook")
					&& !hLink.contains("twitter")
					&& !hLink.contains("googleads")
					&& !hLink.equals("/sitemap.jsp")) {
				
				if (!hLink.contains("http")) {
					// log.info("The  hLink is " + (hLink));
					String subStr = hLink
							.substring(hLink.indexOf("href=\"") + 6);

					// log.info("subStr is " + subStr);

					String hLinkSuffix = subStr.substring(0,
							subStr.indexOf("\""));
					// log.info("The hLinkSuffix  after update is "
					// + (hLinkSuffix));
					completeUrl = (homePage + hLinkSuffix);
					if (log.isDebugEnabled()) {
						log.debug("The complete Link is " + completeUrl);
					}
				} else {

					// Avoid Crawling Outside URLs
					if (!hLink.contains("www")
							&& hLink.contains("rhbabyandchild.com")
							&& !hLink.contains("apple.com")
							&& !hLink.contains("google.com")) {
						// && !hLink.contains("restorationhardware.com") &&
						// !hLink.contains("rh.com")) {
						if (log.isDebugEnabled()) {
							log.debug("The  hLink is " + (hLink));
						}
						String subStr = hLink.substring(hLink.indexOf("http"));
						String hLinkSuffix = subStr.substring(
								subStr.indexOf("http"), subStr.indexOf("\""));
						if (log.isDebugEnabled()) {
							log.debug("The complete Link after update is "
									+ (hLinkSuffix));
						}
						completeUrl = hLinkSuffix;
					}

				}
			}
			if (log.isDebugEnabled()) {
				log.debug("Clicking the Complete URL number " + (count++) + " "
						+ completeUrl);
			}

		} catch (Exception ex) {
			log.error("Exception While collecting HyperLinks from anchor Elements of the Document "
					+ ex.getMessage());
		}
		return completeUrl;
	}

	/**
	 * This method collects the imageLinks.
	 * @return
	 */
	public  Set<String> collectImageLinks(Elements images, Logger log) {
		// int countImages = 0;
		Set<String> imagesSet = new HashSet<String>();
		if (images != null) {
			for (Element image : images) {
				try {
					String imageLink = image.toString();
					String imageSubStr = "";
					if (imageLink.contains("data-src")) {
						imageSubStr = imageLink.substring(imageLink
								.indexOf("data-src=\"") + 10);
					} else {
						imageSubStr = imageLink.substring(imageLink
								.indexOf("src=\"") + 5);
					}
					// log.info("subStr is " + imageSubStr);

					String imageStringEx = imageSubStr.substring(0,
							imageSubStr.indexOf("\""));
					// log.info("The hLinkSuffix  after update is "
					// + (imageStringEx));
					if (!imageStringEx.contains("googleads")
							&& !imageStringEx.contains("ads.bluelithium.com")
							&& !imageStringEx.contains("secure.adnxs.com")
							&& !imageStringEx.contains("a.tribalfusion.com")
							&& !imageStringEx.contains("sitemap.jsp")) {
						if (!imageStringEx.contains("http")
								&& !imageStringEx.contains(".com")) {
							imageStringEx = homePage + imageStringEx;
						}
					} else {
						imageStringEx = "";
					}
					if (log.isDebugEnabled()) {
						log.debug("The imageStringEx is After All is "
								+ imageStringEx);
					}
					if (imageStringEx != null && !imageStringEx.isEmpty()
							&& imageStringEx.trim().startsWith("http://")) {
						imagesSet.add(imageStringEx);
					}
				} catch (Exception ex) {
					log.error("Exception While collecting ImageLinks from img Elements of the Document "
							+ ex.getMessage());
				}
			}
		}
		return imagesSet;
	}
}

