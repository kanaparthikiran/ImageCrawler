package com.resto.image.util;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 
 * @author kkanaparthi
 */
public class ImageCrawlerPropertyUtil {

	private static Properties properties = new Properties();
	private static final Logger log = Logger
			.getLogger(ImageCrawlerPropertyUtil.class.getName());

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
}
