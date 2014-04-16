package com.resto.image;
/*package com.resto.image;
 *//**
 * 
 */
/*
 //import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.util.ArrayList;
 //import java.util.Calendar;
 import java.util.HashSet;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.BlockingQueue;
 import java.util.concurrent.LinkedBlockingQueue;

 //import org.apache.commons.io.FileUtils;
 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.select.Elements;
 //import org.openqa.selenium.OutputType;
 //import org.openqa.selenium.TakesScreenshot;
 //import org.openqa.selenium.WebDriver;
 import org.openqa.selenium.WebDriverException;
 //import org.openqa.selenium.firefox.FirefoxDriver;
 import org.apache.log4j.Logger;

 import com.resto.image.util.CrawlerPropertyUtil;

 *//**
 * @author kkanaparthi
 * 
 */
/*

 public class ImageTestDFSThreadsSeleniumVersion {

 private static BlockingQueue<Map<String, Set<String>>> queue = new LinkedBlockingQueue<Map<String, Set<String>>>();
 private static final Logger log = Logger.getLogger(ImageTestDFSThreads11102013.class
 .getName());
 private static String startPage = CrawlerPropertyUtil.getProperty("siteToCrawl"); 

 *//**
 * 
 */
/*
 public ImageTestDFSThreadsSeleniumVersion() {
 // TODO Auto-generated constructor stub
 }

 *//**
 * @param args
 * @throws IOException
 */
/*
 public static void main(String[] args) throws IOException {
 // TODO Auto-generated method stub
 ScannerThread prod = new ScannerThread(queue,startPage,log);
 ConsumerThread cons = new ConsumerThread(queue, log);
 Thread prodThread = new Thread(prod,"SCANNER-THREAD");
 Thread consThread = new Thread(cons,"CONSUMER-THREAD");

 prodThread.setName("ImageProducerThread");
 consThread.setName("ImageConsumerThread");
 prodThread.start();
 consThread.start();
 }
 }	



 *//**
 * 
 * @author kkanaparthi
 * This class acts as a Producer
 */
/*
 class ScannerThread11102013 implements Runnable {

 //String hyperLink;
 BlockingQueue<Map<String, Set<String>>> scannerQueue;
 //	private  String  startPage = "http://rh.com"; 
 private String startPage = CrawlerPropertyUtil.getProperty("siteToCrawl"); 
 private static final String homePage = CrawlerPropertyUtil.getProperty("homePage"); 

 private static int linksInnerCount = 0;
 private static List<String> processedHyperLinksList = new ArrayList<String>();
 private final Logger log;
 //	private static final WebDriver driver = new FirefoxDriver();

 *//**
 * 
 * @param consumerQueue
 */
/*
 ScannerThread11102013(BlockingQueue<Map<String, Set<String>>> scannerQueue,String startPage, Logger log) {
 this.scannerQueue = scannerQueue;
 this.log = log;
 this.startPage=startPage;
 }

 @Override
 public void run() {
 try {
 scanPages( startPage,log);
 } catch (IOException e) {
 // TODO Auto-generated catch block
 log.error("The Exception in the Scanner Thread is : "+ e.getMessage());
 }
 }


 *//**
 * @throws IOException
 * 
 */
/*
 public  void scanPages(String scanPage,Logger log) throws IOException {
 // for(Element element : hyperLinks) {
 log.info("Page that is going to Scanned is " + scanPage);

 //	  driver.get(scanPage); //log.info("The Page Source is "+
 // driver.getPageSource(); 
 //	  String pageSource = driver.getPageSource();
 //	  log.info("The Page Title is :"+ linksInnerCount+ driver.getTitle());
 Document doc =
 Jsoup.parse(pageSource);
 try {
 Document doc = null;
 Elements imagesOnThisPage = null;

 if (scanPage != null && !scanPage.isEmpty()) {
 doc = Jsoup.connect(scanPage).ignoreContentType(true)
 .timeout(500 * 1000).get();
 imagesOnThisPage = doc.getElementsByTag("img");


 //Mark it as scanned/parsed
 processedHyperLinksList.add(scanPage);

 Set<String> imagesSet = new HashSet<String>();
 Map<String, Set<String>> hyperLinksMapIn = new LinkedHashMap<String, Set<String>>();
 List<String> linksAttribsList = new ArrayList<String>();

 //		if(!processedHyperLinksList.contains(scanPage)) {
 imagesSet = collectImageLinks(imagesOnThisPage,log);

 // Store the PageURL, and the imageSet for the Page
 hyperLinksMapIn.put(scanPage, imagesSet);
 linksInnerCount++;

 scannerQueue.put(hyperLinksMapIn);
 //	linkProcessor(hyperLinksMapIn);

 if (imagesSet != null && imagesSet.size() > 0) {
 log.info("Images Size And the Images Are  " + imagesSet.size());
 }

 //Get all Adjascent hyperLinks
 Elements links = doc.getElementsByTag("a");

 if (links != null && !links.isEmpty()) {
 for (Element link : links) {
 linksAttribsList.add(link.toString());
 }
 }
 for (String linkStr : linksAttribsList) {
 if(!processedHyperLinksList.contains(linkStr)) {
 String hyperLink = getHyperLinkFromTag(linkStr, homePage,log);
 log.info("Hyperlink that is going to be Clicked is "
 + hyperLink);
 processedHyperLinksList.add(linkStr);
 scanPages(hyperLink,log);
 } else {
 if(log.isDebugEnabled()) {
 log.debug("The element is added as backEdge, and will not be parsed now");
 }
 }
 }
 } else {
 if(log.isDebugEnabled()) {
 log.debug("Page that is Skipped is " + scanPage);
 }
 } 
 } catch (Exception ex) {
 ex.printStackTrace();

 }

 }



 *//**
 * 
 * @param element
 * @param homePage
 * @return
 */
/*
 public static String getHyperLinkFromTag(String element, String homePage,Logger log) {
 int count = 0;
 String completeUrl = "";
 try {
 log.info("The Link is " + element);
 String hLink = element;
 if (hLink != null && !hLink.isEmpty() && hLink.contains("href")
 && !hLink.contains("@") && !hLink.contains("faebook")
 && !hLink.contains("twitter")
 && !hLink.contains("googleads")&&!hLink.equals("/sitemap.jsp")) {

 if (!hLink.contains("http")) {
 log.info("The  hLink is " + (hLink));
 String subStr = hLink
 .substring(hLink.indexOf("href=\"") + 6);

 log.info("subStr is " + subStr);

 String hLinkSuffix = subStr.substring(0,
 subStr.indexOf("\""));
 log.info("The hLinkSuffix  after update is "
 + (hLinkSuffix));
 completeUrl = (homePage + hLinkSuffix);
 log.info("The complete Link is " + completeUrl);
 } else {

 // Avoid Crawling Outside URLs
 if (!hLink.contains("www")
 && hLink.contains("rhbabyandchild.com")
 && !hLink.contains("apple.com")
 && !hLink.contains("google.com")) {
 // && !hLink.contains("restorationhardware.com") &&
 // !hLink.contains("rh.com")) {
 log.info("The  hLink is " + (hLink));
 String subStr = hLink.substring(hLink.indexOf("http"));
 String hLinkSuffix = subStr.substring(
 subStr.indexOf("http"), subStr.indexOf("\""));

 // String hLinkSuffix =
 // hLink.substring((hLink.indexOf("http")),
 // hLink.indexOf("http","\""));
 log.info("The complete Link after update is "
 + (hLinkSuffix));
 completeUrl = hLinkSuffix;
 }

 }
 }
 log.info("Clicking the Complete URL number " + (count++) + " "
 + completeUrl);

 } catch (Exception ex) {
 log.error("Exception While collecting HyperLinks from anchor Elements of the Document "+ex.getMessage());
 }
 return completeUrl;
 }


 *//**
 * 
 * @return
 */
/*
 public static Set<String> collectImageLinks(Elements images,Logger log) {
 //	int countImages = 0;
 Set<String> imagesSet = new HashSet<String>();
 if (images != null) {
 for (Element image : images) {
 try {
 String imageLink = image.toString();
 String imageSubStr ="";
 if(imageLink.contains("data-src")) {
 imageSubStr = imageLink.substring(imageLink
 .indexOf("data-src=\"") + 10);
 } else {
 imageSubStr = imageLink.substring(imageLink
 .indexOf("src=\"") + 5);
 }
 log.info("subStr is " + imageSubStr);

 String imageStringEx = imageSubStr.substring(0,
 imageSubStr.indexOf("\""));
 log.info("The hLinkSuffix  after update is "
 + (imageStringEx));
 if (!imageStringEx.contains("googleads") && !imageStringEx.contains("ads.bluelithium.com")
 &&!imageStringEx.contains("secure.adnxs.com") &&!imageStringEx.contains("a.tribalfusion.com") &&
 !imageStringEx.contains("sitemap.jsp")) {
 if (!imageStringEx.contains("http")
 && !imageStringEx.contains(".com") ) {
 imageStringEx = homePage + imageStringEx;
 }
 } else {
 imageStringEx = "";
 }
 log.info("The imageStringEx is After All is "
 + imageStringEx);
 if(imageStringEx!=null && !imageStringEx.isEmpty() && imageStringEx.trim().startsWith("http://")) {
 imagesSet.add(imageStringEx);
 }
 } catch (Exception ex) {
 //	failedURLSet.add(webDriverEx.toString());
 // takeScreenshot(driver, new RuntimeException(""),
 // countImages+"");
 log.error("Exception While collecting ImageLinks from img Elements of the Document "+ex.getMessage());
 }
 }
 }
 return imagesSet;
 }
 }

 *//**
 * 
 * @author kkanaparthi
 *
 */
/*
 class ConsumerThread11102013 implements Runnable {

 BlockingQueue<Map<String, Set<String>>> consumerQueue;
 private  final Logger log;
 //	private  final WebDriver driver = new FirefoxDriver();
 private  static Set<String> failedURLSet = new HashSet<String>();


 *//**
 * 
 * @param consumerQueue
 */
/*
 ConsumerThread11102013(BlockingQueue<Map<String, Set<String>>> consumerQueue,Logger log) {
 this.consumerQueue = consumerQueue;
 this.log= log;
 }


 @Override
 public void run() {
 // TODO Auto-generated method stub
 while(true) {
 try {
 Map<String, Set<String>> hyperLinksMapLcl = consumerQueue.take();

 if (hyperLinksMapLcl != null && !hyperLinksMapLcl.isEmpty()) {
 Set<String> hyperLinksKeySet = hyperLinksMapLcl.keySet();

 for (String hyperLinkElem : hyperLinksKeySet) {
 log.info("hyperLinkElem :" + hyperLinkElem);
 Set<String> imagesSet = hyperLinksMapLcl.get(hyperLinkElem);

 if (imagesSet != null && !imagesSet.isEmpty()) {
 for (String imageElem : imagesSet) {
 log.info("The Images are " + imageElem);
 validateImage(hyperLinkElem, imageElem);
 }
 }
 }
 }
 } catch(InterruptedException iex) {
 log.error("The Exception while Processing the Images is " +iex.getMessage());
 }
 log.info("*****Done Processing the hyperLinks******");
 }

 }

 *//**
 * This Method Validates the Images that were captured from all the
 * navigated Pages
 */
/*
 public  void validateImage(String hyperLinkElem, String imageStringEx) {
 int countImages = 0;
 try {
 boolean isImageNotFound = false;
 try {
 log.debug("Here is Client waiting and for the Hyperlink "+ hyperLinkElem);
 try {
 Thread.sleep(1000); 
 } catch(InterruptedException iex) {
 log.equals("The Error While Waiting in Consumer is "+iex.getMessage());
 }
 //			driver.get(imageStringEx);
 List<String> imagesList = new ArrayList<String>();
 imagesList.add("blankImages/prod690582_S13_cl266255.jpeg");
 imagesList.add("blankImages/prod2810006.jpeg");
 imagesList.add("blankImages/prod2810006.jpeg");
 imagesList.add("blankImages/prod2810008.jpg");
 imagesList.add("blankImages/prod2810009.jpg");
 imagesList.add("blankImages/prod2810004.jpg");
 imagesList.add("blankImages/rhbc_cat358034_feature.jpeg");
 imagesList.add("blankImages/rhbc_cat358034_feature.jpg");
 imagesList.add("blankImages/blank6kb.jpeg");
 imagesList.add("blankImages/blankImage.jpg");
 imagesList.add("blankImages/Blank1.jpeg");
 imagesList.add("blankImages/Blank2.jpeg");
 imagesList.add("blankImages/Blank3.jpeg");
 imagesList.add("blankImages/Blank5.jpeg");
 imagesList.add("blankImages/Blank6.jpg");
 imagesList.add("blankImages/Blank7.jpeg");
 imagesList.add("blankImages/Blank8.jpeg");
 imagesList.add("blankImages/rhbc_cat358034_feature.jpg");

 isImageNotFound = ImagePatternCheckSum
 .isImageNotFound(imagesList
 ,
 imageStringEx);
 log.debug("Now Doing the Comparison............");

 } catch (FileNotFoundException fne) {
 failedURLSet.add(hyperLinkElem);
 takeScreenshot(driver, new RuntimeException(""), countImages
 + "fne");
 log.error("fne-> Screenshot Taken and Stored at " + countImages
 + "fne_");
 log.error("The Exception while validating the Image - FileNotFound "+fne.getMessage());
 RHEMailClient.sendEmailMessage("FileNotFoundException for the Link/Image", hyperLinkElem+" and Image URL is "+imageStringEx);
 } catch (IOException ioe) {
 failedURLSet.add(hyperLinkElem);
 takeScreenshot(driver, new RuntimeException(""), countImages
 + "ioe");
 log.error("ioe->Screenshot Taken and Stored at " + countImages
 + "ioe_");
 //		RHEMailClient.sendEmailMessage("IOException for the Link/Image", hyperLinkElem+" and Image URL is "+imageStringEx);
 log.error("The Exception while validating the Image - IOException :"+ ioe.getMessage());
 }
 if (isImageNotFound) {
 failedURLSet.add(hyperLinkElem);
 log.error("The Image is Not Found......and is Blank at the Page"
 + hyperLinkElem);
 RHEMailClient.sendEmailMessage("Images Missing in the Domain on the Page", hyperLinkElem+" and Image URL is "+imageStringEx);
 takeScreenshot(driver, new RuntimeException(""), countImages
 + "imageNotFound_");
 }
 } catch (WebDriverException webDriverEx) {
 failedURLSet.add(hyperLinkElem);
 takeScreenshot(driver, new RuntimeException(""), countImages
 + "webDriver_");
 log.error("Screenshot Taken and Stored at " + countImages + ".png");
 log.error("The Exception while Validating the Image WebDriverException is :"+ webDriverEx.getMessage() );
 RHEMailClient.sendEmailMessage("Images Missing in the Domain on the Page", hyperLinkElem+" and Image URL is "
 + imageStringEx);
 }
 }


 *//**
 * 
 * @param re
 * @param fileName
 */
/*
 * public void takeScreenshot(WebDriver driver, RuntimeException re, String
 * fileName) { File screenShot = ((TakesScreenshot) driver)
 * .getScreenshotAs(OutputType.FILE); try { FileUtils.copyFile(screenShot, new
 * File(fileName + Calendar.getInstance().getTime() + ".png"));
 * log.info("*************** Screenshot Taken For the Error Page ***********");
 * } catch (IOException ioe) { throw new RuntimeException(ioe.getMessage(),
 * ioe); } } }
 */