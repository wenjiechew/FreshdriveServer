package nUtillities;

import org.apache.log4j.Logger;

public class Log {
	
final static Logger logger = Logger.getLogger(Log.class);
	
	public void log(String msg)
	{
		logger.info(msg);
	}
	
	public void warn(String msg)
	{
		logger.warn(msg);
	}

}
