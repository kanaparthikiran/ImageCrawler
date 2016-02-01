/**
 * 
 */
package com.resto.image.util;


/**
 * This class contains various constants used for ImageCrawler
 * @author kkanaparthi
 * 
 */
public interface ImageCrawlerConstants {

	public static final String PRODCUT_NOT_AVAILABLE = "Product Not Available";
	public static final String PRODCUT_NO_LONGER_AVAILABLE_MSG1="- No Longer Available -";
	public static final String PRODCUT_NO_LONGER_AVAILABLE_MSG2 = "We're sorry. This product is no longer available";
	public static final String MD5_EQUAL = "Both The MD5 are EQUAL";
	public static final String MD5_NOT_EQUAL = "Both The MD5 are NOT EQUAL";
	public static final String EMAIL_IMAGES_MISSING_ON_PAGE = "Images Missing in the Domain on the Page";
	public static final String EMAIL_IMAGE_URL = " and Image URL is ";
	
	public static final String PRODUCT_PAGE_URL_SNIPPET="/catalog/product/product.jsp?productId=prod";
	public static final String PRODUCT_ID_PATTERN="productId=(.*?)&";
	public static final String PRODUCT_EXPORT_CSV_NAME="prodDetailsFor.csv";
	
}
