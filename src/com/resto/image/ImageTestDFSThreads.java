package com.resto.image;

/**
 * 
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.resto.image.util.ImageCrawlerConstants;
import com.resto.image.util.ImageCrawlerPropertyUtil;

/**
 * @author kkanaparthi
 * 
 */

public class ImageTestDFSThreads {

	private static BlockingQueue<Map<String, Set<String>>> queue = new LinkedBlockingQueue<Map<String, Set<String>>>();
	private static final Logger log = Logger
			.getLogger(ImageTestDFSThreads.class.getName());
	private static String startPage = ImageCrawlerPropertyUtil
			.getProperty("siteToCrawl");
	private static int termCounter = 0;
	private static long sleepTime10Mins=600000;
	private static long sleepTime20Mins=1200000;

	/**
	 * 
	 */
	public ImageTestDFSThreads() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		List<String> blankImageCheckSumList = ImagePatternCheckSum
				.getBlankImageCheckSumList();
		ScannerThread prod = new ScannerThread(queue, startPage, log);
		ConsumerThread cons = new ConsumerThread(queue, blankImageCheckSumList,
				log);
		Thread prodThread = new Thread(prod);
		Thread consThread = new Thread(cons);

		prodThread.setName("ImageProducerThread");
		consThread.setName("ImageConsumerThread");

		prodThread.start();
		consThread.start();

		shutDownThreads();
	}

	
	/**
	 * @throws InterruptedException
	 */
	private static void shutDownThreads() throws InterruptedException {
		while(true) {
			Thread.sleep(sleepTime10Mins);
			Map<String, Set<String>> queueElem = queue.peek();
			if(queueElem==null) {
				termCounter++;
				log.error("termCounter in MAIN as Queue Returned NULL : "+termCounter);
				Thread.sleep(sleepTime20Mins);
			}
			if(termCounter>=3) {
				log.error("TERMINAL COUNT REACHED, MAIN is GOING TO Interrupt , After 1 HOUR");
				System.exit(1);
			}
		}
	}
}

/**
 * 
 * @author kkanaparthi This class acts as a Producer
 */
class ScannerThread implements Runnable {

	// String hyperLink;
	BlockingQueue<Map<String, Set<String>>> scannerQueue;
	private String startPage = ImageCrawlerPropertyUtil
			.getProperty("siteToCrawl");
	private static final String homePage = ImageCrawlerPropertyUtil
			.getProperty("homePage");

	private static int linksInnerCount = 0;
	private static Set<String> processedHyperLinksList = new LinkedHashSet<String>();
	private final Logger log;

	/**
	 * 
	 * @param consumerQueue
	 */
	ScannerThread(BlockingQueue<Map<String, Set<String>>> scannerQueue,
			String startPage, Logger log) {
		this.scannerQueue = scannerQueue;
		this.log = log;
		this.startPage = startPage;
	}

	@Override
	public void run() {
		try {
			scanPages(startPage, log);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("The Exception in the Scanner Thread is : "
					+ e.getMessage());
		}
	}

	/**
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
			log.error("The Exception is :" + ex);
			ex.printStackTrace();

		}
	}


	/**
	 * @param log
	 * @param doc
	 * @param linksAttribsList
	 * @throws IOException
	 */
	private void getAdjacentLinks(Logger log, Document doc,
			List<String> linksAttribsList) throws IOException {
		// Get all Adjascent hyperLinks
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
	 * 
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
	 * 
	 * @return
	 */
	public static Set<String> collectImageLinks(Elements images, Logger log) {
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

/**
 * 
 * @author kkanaparthi
 * 
 */
class ConsumerThread implements Runnable {

	BlockingQueue<Map<String, Set<String>>> consumerQueue;
	private final Logger log;
	private static Set<String> failedURLSet = new HashSet<String>();
	private List<String> blankImageCheckSumList = null;
	/**
	 * 
	 * @param consumerQueue
	 */
	ConsumerThread(BlockingQueue<Map<String, Set<String>>> consumerQueue,
			List<String> blankImageCheckSumList, Logger log) {
		this.consumerQueue = consumerQueue;
		this.log = log;
		this.blankImageCheckSumList = blankImageCheckSumList;
	}

	@Override
	public void run() {
		
		// TODO Auto-generated method stub
		while (true) {
			try {
				Map<String, Set<String>> hyperLinksMapLcl = consumerQueue
						.take();
				
				if (ImageCrawlerPropertyUtil.isValidMap(hyperLinksMapLcl)) {
					Set<String> hyperLinksKeySet = hyperLinksMapLcl.keySet();

					for (String hyperLinkElem : hyperLinksKeySet) {
						if (log.isDebugEnabled()) {
							log.debug("hyperLinkElem :" + hyperLinkElem);
						}
						Set<String> imagesSet = hyperLinksMapLcl
								.get(hyperLinkElem);

						if (ImageCrawlerPropertyUtil
								.isValidCollection(imagesSet)) {
							for (String imageElem : imagesSet) {
								if (log.isDebugEnabled()) {
									log.debug("The Image under comparison is " + imageElem);
								}
								validateImage(hyperLinkElem, imageElem,
										blankImageCheckSumList);
							}
						}
					}
				} 
			} catch (InterruptedException iex) {
				log.error("The Exception while Processing the Images is "
						+ iex.getMessage());
			}
			if (log.isDebugEnabled()) {
				log.debug("*****Done Processing the hyperLinks******");
			}
		}

	}

	
	/**
	 * 
	 * @param scannerQueue
	 * @param termCounter
	 */
	/*private void interruptConsumer(BlockingQueue<Map<String, Set<String>>> scannerQueue,int termCounter, long currentTime) {
		if (log.isDebugEnabled()) {
			log.debug("currentTime  " + currentTime+" recheckTime "+recheckTime+" Local Current Time is "+System.currentTimeMillis());
		}
		if(recheckTime<=System.currentTimeMillis()) {
			if (log.isDebugEnabled()) {
				log.debug("Waiting done, now Comparing both the Times,  checking Scannner Queue Size...currentTime  " + currentTime+" recheckTime "+recheckTime);
			}
			if(scannerQueue.size()==0) {
				termCounter++;
				if (log.isDebugEnabled()) {
					log.debug("The termCounter is " + termCounter);
				}	
				if(termCounter==10) {
					if (log.isDebugEnabled()) {
						log.debug("Going to Interrupt the Current Thread, and Hence Exiting the Consumer Thread " + termCounter);
					}
					try {
						Thread.currentThread().interrupt();
						throw new InterruptedException("Consumer Thread is Interrupted, Hence Terminating the Thread");
					} catch(InterruptedException conInEx) {
							log.error("Interrupted the Current Thread, and Hence Exiting the Consumer Thread " + termCounter);
					}
				}
			}
		}
	}*/
	
	
	/**
	 * This Method Validates the Images that were captured from all the
	 * navigated Pages
	 */
	public void validateImage(String hyperLinkElem, String imageStringEx,
			List<String> blankImageCheckSumList) {
		int countImages = 0;
		// try {
		boolean isImageNotFound = false;
		try {
			if (log.isDebugEnabled()) {
				log.debug("Here is Client waiting and for the Hyperlink "
						+ hyperLinkElem);
				log.debug("Now Doing the Comparison............");
			}
			isImageNotFound = ImagePatternCheckSum.isImageNotFound(
					blankImageCheckSumList, imageStringEx);

			if (isImageNotFound
					&& ImageCrawlerPropertyUtil
							.isProductAvailable(hyperLinkElem)) {
			isImageNotFound = true;
			} else {
				//Reset to false, to avoid other true condition, which is just based on the checksum matching.
				isImageNotFound = false;
			}

		} catch (FileNotFoundException fne) {
			failedURLSet.add(hyperLinkElem);
			log.error("The Exception while validating the Image - FileNotFound "
					+ fne.getMessage());
			RHEMailClientUnAuth.sendEmailMessage(
					"FileNotFoundException for the Link/Image", hyperLinkElem
							+ " and Image URL is " + imageStringEx);
		} catch (IOException ioe) {
			failedURLSet.add(hyperLinkElem);
			log.error("The Exception while validating the Image - IOException :"
					+ ioe.getMessage());
		}
		if (isImageNotFound) {
			failedURLSet.add(hyperLinkElem);
			log.error("The Image is Not Found......and is Blank at the Page"
					+ hyperLinkElem);
			RHEMailClientUnAuth.sendEmailMessage(
					ImageCrawlerConstants.EMAIL_IMAGES_MISSING_ON_PAGE,
					hyperLinkElem + ImageCrawlerConstants.EMAIL_IMAGE_URL
					+ imageStringEx);
		}
	}
}
