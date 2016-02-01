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

import com.resto.image.ImageTestDFSThreads;
import com.resto.image.util.ImageCrawlerPropertyUtil;

/**
 * @author kkanaparthi
 *
 */
public class ImageTestDFSThreadsTest {

	private static final String CLASS_NAME="ImageTestDFSThreadsTest";
	private static final Logger log = Logger
			.getLogger(ImageTestDFSThreadsTest.class.getName());
	private static final String startPageUnitTest = ImageCrawlerPropertyUtil
			.getProperty("siteToCrawlUnitTest");
	private static final String homePageUnitTest = ImageCrawlerPropertyUtil
			.getProperty("homePage");
	private static final long heartBeatCheckTimeUnitTest = ImageCrawlerPropertyUtil.getIntProperty("sleepTime10MinsUnitTest");
	private static final long lastBeatCheckTimeUnitTest = ImageCrawlerPropertyUtil.getIntProperty("sleepTime20MinsUnitTest");

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(log.isInfoEnabled()) {
			log.info("Started setUpBeforeClass()");
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(log.isInfoEnabled()) {
			log.info("STARTED tearDownAfterClass()");
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if(log.isInfoEnabled()) {
			log.info("STARTED setUp()");
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		if(log.isInfoEnabled()) {
			log.info("STARTED tearDown()");
		}
	}


	/**
	 * Test method for {@link com.resto.image.ImageTestDFSThreads#startCrawling()}.
	 */
	@Test
	public final void testStartCrawling() {
		if(log.isInfoEnabled()) {
			log.info("STARTED tearDown()");
		}
		try {
			ImageTestDFSThreads.startCrawling(CLASS_NAME,startPageUnitTest,log,homePageUnitTest,
					heartBeatCheckTimeUnitTest,lastBeatCheckTimeUnitTest);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		if(log.isInfoEnabled()) {
			log.info("Exiting tearDown()");
		}
	}

}
