package com.resto.image.util;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import au.com.bytecode.opencsv.CSVWriter;


/**
 * 
 * @author kkanaparthi
 */
public class ImageCrawlerPropertyUtil {

	private static Properties properties = new Properties();
	private static final Logger log = Logger
			.getLogger(ImageCrawlerPropertyUtil.class.getName());

	/**
	 * This method Calculates the Total Time Taken to Execute ImageCrawler
	 * @param startTime
	 * @param endTime
	 * @return Returns the Metrics to run the Crawler
	 */
	public static String calculateMetrics(long startTime, long endTime) {
		String totalTimeStr = null;
		long diff = endTime-startTime;
		long diffHours = diff/(60 * 60 * 1000) % 24;
		long diffMinutes = diff/(60 * 1000) % 60;
		long diffSeconds = diff/1000 % 60;
		totalTimeStr = " Hours "+diffHours+" Minutes "+diffMinutes+" Seconds "+diffSeconds;
		return totalTimeStr;
	}
	
	/**
	 * This method finds out if the given pageUrl is a ProductPage
	 * @return productPage
	 */
	public static boolean isProductPage(String pageUrl) {
		boolean productPage = false;
		if(pageUrl.contains(ImageCrawlerConstants.PRODUCT_PAGE_URL_SNIPPET)) {
			productPage = true;
		}
		return productPage;
	}
	
	
	/**
	 * This method gets the productName from the pageUrl of a ProductPage
	 * @return productName
	 */
	public static String getProductName(String pageUrl) {
		Document document = getDocumentForPage(pageUrl);
		String productName = null;
		if(ImageCrawlerPropertyUtil.isValidObject(document)) {
			productName = document.title();
		}
		return productName;
	}
	
	
	/**
	 * This method gets the productId from the pageUrl of a ProductPage
	 * @return productId
	 */
	public static String getProductId(String pageUrl,String itemPattern) {
		String productId = null;
		Pattern pattern = Pattern.compile(itemPattern);
		Matcher matcher = pattern.matcher(pageUrl);
		if (matcher!=null && matcher.find()) {
			productId = matcher.group(1);
		}
		return productId;
	}
	
	

	
	/**
	 * 
	 * @return
	 */
	public static List<String> getListFromString(String excludeImages,
			String separator) {
		List<String> excludeImageLinksList = Arrays.asList(excludeImages
				.split(separator));
		return excludeImageLinksList;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isProductAvailable(String pageUrl) {
		Document document = getDocumentForPage(pageUrl);
		if (document != null
				&& ((document.getElementsContainingText(
						ImageCrawlerConstants.PRODCUT_NOT_AVAILABLE).size() > 0) || (document.getElementsContainingText(
								ImageCrawlerConstants.PRODCUT_NO_LONGER_AVAILABLE_MSG1).size() > 0) || 
								 (document.getElementsContainingText(
											ImageCrawlerConstants.PRODCUT_NO_LONGER_AVAILABLE_MSG2).size() > 0))) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param scanPage
	 * @return
	 */
	public static Document getDocumentForPage(String scanPage) {
		Document doc = null;
		try {
			if (scanPage != null && !scanPage.isEmpty()) {
				doc = Jsoup.connect(scanPage).ignoreContentType(true)
						.timeout(500 * 1000).get();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return doc;
	}

	/**
	 * This method checks if the given RepositoryItem List is Valid/Not
	 * 
	 * @param inList
	 */
	public static boolean isValidList(@SuppressWarnings("rawtypes") List inList) {
		if (inList != null && inList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method checks if the given RepositoryItem List is Valid/Not
	 * 
	 * @param inList
	 */
	public static boolean isValidCollection(
			@SuppressWarnings("rawtypes") Collection inCollection) {
		if (inCollection != null && inCollection.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method checks if the given RepositoryItem List is Valid/Not
	 * 
	 * @param inList
	 */
	public static boolean isValidMap(@SuppressWarnings("rawtypes") Map inMap) {
		if (inMap != null && inMap.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method checks if the given String is Valid/Not
	 * 
	 * @param inArray
	 * @return
	 */
	public static boolean isValidString(String inString) {
		if (inString != null && !inString.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method checks if the given String is Valid/Not
	 * 
	 * @param inArray
	 * @return
	 */
	public static boolean isValidObject(Object inObject) {
		if (inObject != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static int getIntProperty(String key) {
		return getIntProperty(key, 0);
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getIntProperty(String key, int defaultValue) {
		try {
			String value = properties.getProperty(key);
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			log.error(nfe);
			return 0;
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static boolean getBooleanProperty(String key) {
		return getBooleanProperty(key, false);
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBooleanProperty(String key, boolean defaultValue) {
		try {
			String value = properties.getProperty(key);
			return Boolean.parseBoolean(value);
		} catch (Exception nfe) {
			log.error(nfe);
			return false;
		}
	}

	/**
     * 
     */
	static {
		try {
			String propertyFilePath = "imageCrawler.properties";
			properties.load(new FileInputStream(propertyFilePath));
			if (log.isDebugEnabled()) {
				log.debug("Loaded properties from " + propertyFilePath);
			}
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(
					"Unable to find property file location.", e);
		}
	}
	
	/**
	 * This method Closes the CSV Writer Stream.
	 * @param writer
	 */
	public static void closeResources(List<Closeable>  writers) {
		try {
			if(ImageCrawlerPropertyUtil.isValidCollection(writers)) {
				for(Closeable writer : writers) {
					if(writer!=null) {
						writer.close();
					}
				}
			}
		} catch (IOException e) {
			System.err.println("The IOException while Closing to CSVfile/ Writer  :" + e.getMessage());
		}
			if(log.isDebugEnabled()) {
				log.debug("The System Streams/Resources are Closed Successfully");
			}
	}
	
	/**
	 * THis method creates a CSV file for Logging the Product Details.
	 */
	public static List<Closeable> createCSVWriter() {
			FileWriter fw = null;
		    CSVWriter writer = null;
	        List<Closeable> writers = new ArrayList<Closeable>();

		    try {
		    	fw = new FileWriter(ImageCrawlerConstants.PRODUCT_EXPORT_CSV_NAME);
		        writer = new CSVWriter(fw);
		        writers.add(fw);
		        writers.add(writer);
		    //Write header
		    String [] header = { "PRODUCT_ID" , "PRODUCT_URL", "PRODUCT_NAME","IMAGE_URL" };
		    writer.writeNext(header);
		     } catch(IOException ex){
		    	//	    	log.error("The IOException while writing to CSV file :" + ex.getMessage());	
		    	System.err.println("The IOException while writing to CSV file :" + ex.getMessage());
	    	}
		    try {
		    	if(fw!=null) {
		    		fw.flush();
		    	}
		} 	catch(IOException ex){
	//    	//	    	log.error("The IOException while writing to CSV file :" + ex.getMessage());	
			System.err.println("The IOException while writing to CSV file :" + ex.getMessage());
	//    	}
		}
		    return writers;
	}
	
	public static void main(String a[]) {
//		String mydata = "http://www.restorationhardware.com/catalog/product/product.jsp?productId=prod2140104&categoryId=cat1990057&src=rel";
//		boolean isProductPage = isProductPage(mydata);
////		mydata = "http://www.restorationhardware.com/catalog/product/product.jsp?productId=prod2481080&categoryId=cat2280008";
//		mydata ="http://www.restorationhardware.com/catalog/product/product.jsp?productId=prod2461207&categoryId=cat2390400&src=rel";
//		Pattern pattern = Pattern.compile(ImageCrawlerConstants.PRODUCT_ID_PATTERN);
//		Matcher matcher = pattern.matcher(mydata);
//		String productId = null;
//		String productName = null;
//		if (matcher.find())
//		{
//			productId =  matcher.group(1);
//		    System.out.println("productId : " + productId);
//		}
//			productName  = getProductName(mydata);
//		    System.out.println("productName : " + productName);
//
//		    
//		 System.out.println("isProductPage : " + isProductPage);		
	}
}
