package com.resto.image;

/**
 * 
 */
import java.io.Closeable;
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

import au.com.bytecode.opencsv.CSVWriter;

import com.resto.image.beans.ProductDetails;
import com.resto.image.util.ImageCrawlerConstants;
import com.resto.image.util.ImageCrawlerPropertyUtil;


/**
 * This is the main class to start ImageCrawler Application.
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
	private static final long startTime = System.currentTimeMillis();

	
	/**
	 * 
	 */
	public ImageTestDFSThreads() {
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {

		List<String> blankImageCheckSumList = ImagePatternCheckSum
				.getBlankImageCheckSumList();
		ScannerThread prod = new ScannerThread(queue, startPage, log);
		List<Closeable> writers = ImageCrawlerPropertyUtil.createCSVWriter();
		CSVWriter csvWriter = (CSVWriter) writers.get(1);
		ConsumerThread cons = new ConsumerThread(queue, blankImageCheckSumList,csvWriter,log);

		Thread prodThread = new Thread(prod);
		Thread consThread = new Thread(cons);
		prodThread.setName("ImageProducerThread");
		consThread.setName("ImageConsumerThread");

		prodThread.start();
		consThread.start();
		shutDownThreads(prodThread,consThread,writers);
	}

		
		/**
		 * This method shutsDown All the Threads/VM.
		 * @throws InterruptedException
		 */
		private static void shutDownThreads(Thread prodThread,Thread consumerThread,List<Closeable >writers) throws InterruptedException {
			while(true) {
				Thread.sleep(sleepTime10Mins);
				log.error("HEART BEAT Check: in shutDownThreads-> prodThread Status ALIVE Status is "+prodThread.isAlive()
					+" consumerThread Status ALIVE Status is "+consumerThread.isAlive());
	
				if(!prodThread.isAlive()) {
					Map<String, Set<String>> queueElem = queue.peek();
					if(queueElem==null) { 
						termCounter++;
						log.error("Queue Returned NULL in MAIN termCounter  : "+termCounter+" prodThread Status ALIVE Status is "+prodThread.isAlive());
						Thread.sleep(sleepTime20Mins);
				}
				if(termCounter>=3) {
					log.error("TERMINAL COUNT REACHED, MAIN is GOING TO Interrupt the Threads ");
					log.error("The Total Time to Crawl the Site is :  " + ImageCrawlerPropertyUtil.calculateMetrics(startTime, System.currentTimeMillis()));
					
					ImageCrawlerPropertyUtil.closeResources(writers);
					
					System.exit(0);
				}
			}
		}
	}
	
}

/**
 *  This Thread acts as a Producer for producing the list of Images from the WebPages.
 * @author kkanaparthi
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
 * This Thread acts as a Consumer for consuming the list of Images from the WebPages, and process them.
 * @author kkanaparthi
 * 
 */
class ConsumerThread implements Runnable {

	BlockingQueue<Map<String, Set<String>>> consumerQueue;
	private final Logger log;
	private static Set<String> failedURLSet = new HashSet<String>();
	private List<String> blankImageCheckSumList = null;
	private CSVWriter csvWriter;
	/**
	 * 
	 * @param consumerQueue
	 */
	ConsumerThread(BlockingQueue<Map<String, Set<String>>> consumerQueue,
			List<String> blankImageCheckSumList, CSVWriter csvWriter,Logger log) {
		this.consumerQueue = consumerQueue;
		this.log = log;
		this.blankImageCheckSumList = blankImageCheckSumList;
		this.csvWriter = csvWriter;
	}

	@Override
	public void run() {
		
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
							if(ImageCrawlerPropertyUtil.isProductPage(hyperLinkElem)) {
									if (log.isDebugEnabled()) {
										log.debug("Product Page is Found, URL, and it is productPage******** " + hyperLinkElem);
									}
									ProductDetails productDetails = createProductDetails(hyperLinkElem, imageElem);
									List<ProductDetails> prdList = new ArrayList<ProductDetails>();
									prdList.add(productDetails);
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
	
	}*/
	
	/**
	 * This method gets the ProductDtails Bean from the given pageUrl
	 * @param pageUrl
	 * @return
	 */
	public ProductDetails  createProductDetails(String pageUrl,String imageUrl) {
		String productId = ImageCrawlerPropertyUtil.getProductId(pageUrl,ImageCrawlerConstants.PRODUCT_ID_PATTERN);
		String productName = ImageCrawlerPropertyUtil.getProductName(pageUrl);
		ProductDetails productDetails = new ProductDetails(productId, pageUrl, productName, imageUrl);
		return productDetails;
	}
	
	

	/**
	 * This method logs the ProductDtails to XLS/CSV
	 * @param pageUrl
	 * @return
	 * @throws IOException 
	 */
	public void logProductDetails(List<ProductDetails> productDetails,CSVWriter writer)  {
		//    StringWriter sw = new StringWriter();
//		    FileWriter fw = null;
//		    CSVWriter writer = null;
//		    try {
//		    	fw = new FileWriter(ImageCrawlerConstants.PRODUCT_EXPORT_CSV_NAME);
//		        writer = new CSVWriter(fw);
//		    //Write header
//		    String [] header = { "PRODUCT_ID" , "PRODUCT_URL", "PRODUCT_NAME","IMAGE_URL" };
//		    writer.writeNext(header);
		    //Write data
		    String [] data;
		    for (ProductDetails productDetailsItem : productDetails) {
		      data = new String [] { 
		    		  productDetailsItem.getProductId(), productDetailsItem.getProductUrl()
		    		  , productDetailsItem.getProductName(),productDetailsItem.getImageUrl() };
		      writer.writeNext(data);
		      System.out.println("Written a Record "+ productDetails.toString());
//		      if(log.isDebugEnabled()) {
//		    	  log.debug("Done Logging the Product Record " +productDetailsItem.getProductName());
//		      }
		    }
//		    } catch(IOException ex){
//	//	    	log.error("The IOException while writing to CSV file :" + ex.getMessage());	
//		    	System.err.println("The IOException while writing to CSV file :" + ex.getMessage());
//		    	}
//		    try {
//		    	if(writer!=null && fw!=null) {
//		    		writer.flush();
//		    		fw.flush();
////		    		writer.close();
////		    		fw.close();
//		    	}
//			} catch (IOException e) {
//				System.err.println("The IOException while writing to CSV file :" + e.getMessage());
//		//		log.error("The IOException while writing to CSV file :" + e.getMessage());
//			}
	}


	
	
	/**
	 * This Method Validates the Images that were captured from all the
	 * navigated Pages, and sends out email in case of any image is missing/blank.
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
