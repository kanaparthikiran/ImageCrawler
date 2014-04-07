package com.resto.image;

import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * 
 */

/**
 * @author Kiran
 * 
 */
public class MyLogger {

	/**
	 * 
	 */
	public MyLogger() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public static Logger createMyLogFile(String logFile) {
		Logger consumerLogger = Logger.getLogger("MyLog");
		Appender fh;
		try {
			fh = new FileAppender(new SimpleLayout(), logFile);
			consumerLogger.addAppender(fh);
			fh.setLayout(new SimpleLayout());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return consumerLogger;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Logger logger = Logger.getLogger("MyLog");
		Appender fh;
		try {
			fh = new FileAppender(new SimpleLayout(), "MyLogFile.log");
			logger.addAppender(fh);
			fh.setLayout(new SimpleLayout());
			logger.info("My first log");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Hi How r u?");
	}
}
