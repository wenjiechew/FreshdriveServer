package nUtillities;

import org.apache.log4j.Logger;

public class Log {
	
final static Logger logger = Logger.getLogger(Log.class);
	
	public void callMeInAppInfo(String parameter) {
		logger.info("This is info : " + parameter);
		if (logger.isInfoEnabled()) {
			logger.info("This is info : " + parameter);
		}
	}
	
	public void callMeInAppDebug(String parameter) 
	{	logger.debug("This is Debug : " + parameter);
		if (logger.isDebugEnabled()) {
			logger.info("This is Debug : " + parameter);
		}
	}
	
	public void log(String msg)
	{
		logger.debug(msg);
	}

}
