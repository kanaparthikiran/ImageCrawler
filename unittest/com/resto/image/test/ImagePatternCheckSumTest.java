/**
 * 
 */
package com.resto.image.test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.resto.image.ImagePatternCheckSum;
import com.resto.image.util.ImageCrawlerPropertyUtil;

/**
 * Unit Test class for ImagePatternCheckSum class
 * @author kkanaparthi
 *
 */
public class ImagePatternCheckSumTest {
	
	private static final String CLASS_NAME="ImagePatternCheckSumTest";
	private static final Logger log = Logger
			.getLogger(CLASS_NAME);
	ImagePatternCheckSum imageCheckSum = null;
	private static final String blankImageUrlUnitTest = ImageCrawlerPropertyUtil.getProperty("blankImageUrlUnitTest");
	private static final String nonBlankImageUrlUnitTest = ImageCrawlerPropertyUtil.getProperty("nonBlankImageUrlUnitTest");

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(log.isInfoEnabled()) {
			log.info("Started setUpBeforeClass");
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(log.isInfoEnabled()) {
			log.info("Started tearDownAfterClass");
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if(log.isInfoEnabled()) {
			log.info("Started setUp");
		}
		imageCheckSum =  new ImagePatternCheckSum();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		if(log.isInfoEnabled()) {
			log.info("Started tearDown");
		}
	}

	/**
	 * Test method for {@link com.resto.image.ImagePatternCheckSum#isImageMissing(java.lang.String)}.
	 */
	@Test
	public final void testIsImageMissing() {
		if(log.isInfoEnabled()) {
			log.info("Started testIsImageMissing()");
		}
		boolean isMissing = false;
		try {
			isMissing = imageCheckSum.isImageMissing(blankImageUrlUnitTest);
			org.junit.Assert.assertTrue(isMissing);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(log.isInfoEnabled()) {
			log.info("Exiting testIsImageMissing()");
		}

	}

	/**
	 * Test method for {@link com.resto.image.ImagePatternCheckSum#isImageMissing(java.lang.String)}.
	 */
	@Test
	public final void testIsImageMissingFalse() {
		if(log.isInfoEnabled()) {
			log.info("Started testIsImageMissingFalse()");
		}
		boolean isMissing = false;
		try {
			isMissing = imageCheckSum.isImageMissing(nonBlankImageUrlUnitTest);
			org.junit.Assert.assertFalse(isMissing);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(log.isInfoEnabled()) {
			log.info("Exiting testIsImageMissingFalse()");
		}
	}
}
