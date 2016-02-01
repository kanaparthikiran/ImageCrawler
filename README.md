ImageCrawler
============

ImageCrawler


This is the ImageCrawler Application Which can be used to Crawl any WebSite, and find out any missing images, text etc.


The ImageCrawler can be initialized in 3 ways.
================================================

1) ImageTestDFSThreads.java is the main class which invokes the ImageCrawler Application.

2) imageCrawler.sh can be invoked from command line with this command.
 $nohup ./imageCrawler.sh &
 This will cause the image Crawler Application to run on the given domain specified in imageCrawler.properties and produce the results. 
 The log file is generated at "ImageCrawler/ImageCrawler.log".
 
3) ImageTestDFSThreadsSeleniumVersion.java is the main class which invokes the ImageCrawler Application, 
   which is a Selenium version. 
 
imageCrawler.properties contains all the properties that are needed to run ImageCrawler with all the features, for JavaMail etc.,

 


Unit Tests
======================
The ImageCrawler Application has Unit Tests, the code for which is present in resto/images/test package.
The UnitTest Suite is called resto/images/test/AllTests.java, which can be executed as JUnit Test.

For the UnitTesting purpose, a sample WebApplication is created which contains both valid and Invalid(blank/target type We want to Find out) Images, and
the pages are linked to each other similar to a real world E-Commerce/Web Application.

Invoking AllTests.java will execute the UnitTests, will crawl the target site and generate the report, and sends emails.

imageCrawler.properties contains UnitTest Properties under #UNIT TEST PROPERTIES section, 
which can be configured to suite UnitTesting
