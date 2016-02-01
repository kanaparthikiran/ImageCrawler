/**
 * 
 */
package com.resto.image;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

import com.resto.image.beans.ProductDetails;
import com.resto.image.util.ImageCrawlerConstants;
import com.resto.image.util.ImageCrawlerPropertyUtil;


/**
 * This Thread acts as a Consumer for consuming the list of 
 * Images from the WebPages, and process them.
 * @author kkanaparthi
 * 
 */

public class ConsumerThread implements Runnable {

		BlockingQueue<Map<String, Set<String>>> consumerQueue;
		private final Logger log;
		private static Set<String> failedURLSet = new HashSet<String>();
		private List<String> blankImageCheckSumList = null;
		private CSVWriter csvWriter;
		/**
		 * 
		 * @param consumerQueue
		 */
		public ConsumerThread(BlockingQueue<Map<String, Set<String>>> consumerQueue,
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
//			    FileWriter fw = null;
//			    CSVWriter writer = null;
//			    try {
//			    	fw = new FileWriter(ImageCrawlerConstants.PRODUCT_EXPORT_CSV_NAME);
//			        writer = new CSVWriter(fw);
//			    //Write header
//			    String [] header = { "PRODUCT_ID" , "PRODUCT_URL", "PRODUCT_NAME","IMAGE_URL" };
//			    writer.writeNext(header);
			    //Write data
			    String [] data;
			    for (ProductDetails productDetailsItem : productDetails) {
			      data = new String [] { 
			    		  productDetailsItem.getProductId(), productDetailsItem.getProductUrl()
			    		  , productDetailsItem.getProductName(),productDetailsItem.getImageUrl() };
			      writer.writeNext(data);
			      System.out.println("Written a Record "+ productDetails.toString());
//			      if(log.isDebugEnabled()) {
//			    	  log.debug("Done Logging the Product Record " +productDetailsItem.getProductName());
//			      }
			    }
//			    } catch(IOException ex){
//		//	    	log.error("The IOException while writing to CSV file :" + ex.getMessage());	
//			    	System.err.println("The IOException while writing to CSV file :" + ex.getMessage());
//			    	}
//			    try {
//			    	if(writer!=null && fw!=null) {
//			    		writer.flush();
//			    		fw.flush();
////			    		writer.close();
////			    		fw.close();
//			    	}
//				} catch (IOException e) {
//					System.err.println("The IOException while writing to CSV file :" + e.getMessage());
//			//		log.error("The IOException while writing to CSV file :" + e.getMessage());
//				}
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
				RHEMailClient.sendEmailMessage(
						"FileNotFoundException for the Link/Image", hyperLinkElem
								+ " and Image URL is " + imageStringEx);
			} catch (IOException ioe) {
				failedURLSet.add(hyperLinkElem);
				log.error("The Exception while validating the Image - IOException : "
						+ ioe.getMessage());
			}
			if (isImageNotFound) {
				failedURLSet.add(hyperLinkElem);
				log.error("The Image is Not Found......and is Blank at the Page "
						+ hyperLinkElem);
				RHEMailClient.sendEmailMessage(
						ImageCrawlerConstants.EMAIL_IMAGES_MISSING_ON_PAGE,
						hyperLinkElem + ImageCrawlerConstants.EMAIL_IMAGE_URL
						+ imageStringEx);
			}
		}

}
