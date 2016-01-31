package com.resto.image;

/**
 * 
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.apache.log4j.Logger;

import com.resto.image.util.ImageCrawlerConstants;
import com.resto.image.util.ImageCrawlerPropertyUtil;

/**
 * This class calculates the CheckSum of the BlankImages for Comparison with each Image on the Domain.
 * @author kkanaparthi
 * 
 */
public class ImagePatternCheckSum {

	private static final Logger log = Logger
			.getLogger(ImagePatternCheckSum.class.getName());

	/**
	 * 
	 */
	public ImagePatternCheckSum() {
		// TODO Auto-generated constructor stub
	}

	/**
	 *  This method gets the BlankImageCheckSumList.
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static List<String> getBlankImageCheckSumList() throws IOException,
			FileNotFoundException {
		String blankImages = ImageCrawlerPropertyUtil
				.getProperty("blankImagesList");
		List<String> imagesList = new ArrayList<String>();
		imagesList = ImageCrawlerPropertyUtil.getListFromString(blankImages,
				",");
		return ImagePatternCheckSum.getMessageDigest(imagesList);
	}

	/**
	 * This method gets the messageDigest for the Given List of fileNames.
	 * @param fileNames List of fileNames.
	 * @return returns messageDigestList
	 */
	public static List<String> getMessageDigest(List<String> fileNames)
			throws FileNotFoundException, IOException {
		String messageDigest = null;
		List<String> md5SumList = new ArrayList<String>();
		InputStream fis = null;
		try {
			for (String fileName : fileNames) {
				fis = new FileInputStream(fileName);
				MessageDigest md5 = MessageDigest.getInstance("SHA");
				byte[] bytes = new byte[1024];
				int read = 0;
				while ((read = fis.read(bytes)) != -1) {
					md5.update(bytes, 0, read);
				}
				byte[] digestBytes = md5.digest();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < digestBytes.length; i++) {
					sb.append(Integer.toString((digestBytes[i] & 0xff) + 0x100,
							16).substring(1));
				}
				if (log.isDebugEnabled()) {
					log.debug("Adding the List . For blank Image - Digest(in hex format):: "
							+ sb.toString());
				}
				messageDigest = sb.toString();
				md5SumList.add(messageDigest);
			}

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException fne) {
			fne.printStackTrace();
			throw new FileNotFoundException("The SpecifiedImage is Not Found "
					+ fne.getMessage());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new IOException("The IO Exception is is " + ioe.getMessage());
		} finally {
			try {
				if (fis != null) {
					fis.close();
					if (log.isDebugEnabled()) {
						log.debug("Closed the  FileStream after reading the Images");
					}
				}
			} catch (IOException ex) {
				log.error("The Exception while closing the Connection is "
						+ ex.getMessage());
			}

		}
		return md5SumList;
	}

	/**
	 * This method gets the messageDigest for the Given URL
	 * @param file filePath
	 * @return returns the message Digest
	 */
	public static String getMessageDigestForUrl(String fileUrl)
			throws FileNotFoundException, IOException {
		String messageDigest = null;
		if (fileUrl != null && !fileUrl.isEmpty()) {
			try {
				InputStream fis = new URL(fileUrl).openStream();
				MessageDigest md5 = MessageDigest.getInstance("SHA");
				byte[] bytes = new byte[1024];
				int read = 0;
				while ((read = fis.read(bytes)) != -1) {
					md5.update(bytes, 0, read);
				}
				byte[] digestBytes = md5.digest();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < digestBytes.length; i++) {
					sb.append(Integer.toString((digestBytes[i] & 0xff) + 0x100,
							16).substring(1));
				}
				if (log.isDebugEnabled()) {
					log.debug("For Url - Digest(in hex format):: "
							+ sb.toString());
				}
				messageDigest = sb.toString();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException fne) {
				fne.printStackTrace();
				throw new FileNotFoundException(
						"The SpecifiedImage is Not Found " + fne.getMessage());
			} catch (MalformedURLException mfe) {
				throw new MalformedURLException("The MalformedURLException is "
						+ mfe.getMessage());
			} catch (IOException ioe) {
				ioe.printStackTrace();
				throw new IOException("The IO Exception  is "
						+ ioe.getMessage());
			}
		} else {
			messageDigest = "";
		}
		return messageDigest;
	}

	/**
	 * This method finds out if the image is missing/blank.
	 * @return returns true is the image is blank/missing.
	 */
	public static boolean isImageNotFound(List<String> blankImageMD5List,
			String currentImageUrl) throws FileNotFoundException, IOException {
		boolean isBlank = false;
		// List<String> blankImageMD5List = getMessageDigest(blankImagesPath);
		String targetImageMD5 = getMessageDigestForUrl(currentImageUrl);
		if (blankImageMD5List != null && targetImageMD5 != null
				&& (blankImageMD5List.contains(targetImageMD5))) {
			if (log.isDebugEnabled()) {
				log.debug("both The MD5 are Equal ");
			}
			isBlank = true;
		} else {
			if (log.isDebugEnabled()) {
				log.debug("both The MD5 are NOT EQUAL");
			}
		}
		return isBlank;
	}

	/**
	 * main method for  Developer Unit Testing
	 * @param args
	 * @throws EncoderException
	 */
	public static void main(String[] args) throws IOException,
			FileNotFoundException, NoSuchAlgorithmException, EncoderException {
		System.out.println("STARTED THE PROCESS");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> blankImageMD5List = getBlankImageCheckSumList();
		for (String md5Str : blankImageMD5List) {
			log.info("md5String : " + md5Str);
		}
		//System.exit(1);

		String imageUrl = ImageCrawlerPropertyUtil.getProperty("blankImageUrl");

		// URLCodec urlCodec = new org.apache.commons.codec.net.URLCodec();
		// String encodedurl = urlCodec.encode(imageUrl, "UTF-8");

		// String encodedurl = URLEncoder.encode(imageUrl,"UTF-8");
		// log.info("encoded URL is "+ encodedurl);
		String targetImageMD5 = getMessageDigestForUrl(imageUrl);
		boolean isBlank = false;

		if (blankImageMD5List != null && !blankImageMD5List.isEmpty()
				&& targetImageMD5 != null
				&& (blankImageMD5List.contains(targetImageMD5))) {
			log.debug(ImageCrawlerConstants.MD5_EQUAL);
			isBlank = true;
		} else {
			log.debug(ImageCrawlerConstants.MD5_NOT_EQUAL);
		}
		log.debug("isBlank " + isBlank);
		if (isBlank) {
			RHEMailClient.sendEmailMessage(
					"Images Missing in the Domain on the Page",
					"Watch out for An Email for Missing Images");
		}
		
		System.out.println("COMPLETED THE PROCESS");

	}
}
