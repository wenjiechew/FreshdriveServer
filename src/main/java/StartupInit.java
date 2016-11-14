import java.util.Calendar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import nConstants.Constants;
import nFileHandler.ExpiryFileRemove;

/**
 * This Class purpose to create a schedule on Server Application Startup
 * Which Initializes a Executor Service to execute a Task to delete from from file server and delete data from DB
 * 
 * Application Life cycle Listener implementation class StartupInit
 */
@WebListener
public class StartupInit implements ServletContextListener {
	private long initialDelay;
	private static final long PERIOD = 86400000L;
	Calendar time = Calendar.getInstance();
	
	
	ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    /**
     * Here we Set the initial delay to the end of the day, (24 Hours)
     * as we want the schedule to execute the task at the first hour of the day
     * Default constructor. 
     */
    public StartupInit() {
    	time.set(Calendar.HOUR_OF_DAY, 23); 
    	time.set(Calendar.MINUTE, 59);
    	time.set(Calendar.SECOND, 60);

    	initialDelay = time.getTimeInMillis() - System.currentTimeMillis();
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
        
    	executorService.shutdown();
    }

	/**
	 * 
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	Constants.setCurrentPath( sce.getServletContext().getRealPath("/") );
    	
        executorService.scheduleAtFixedRate(() -> {
 			ExpiryFileRemove.deleteFileExpired();;		
 			
 		},0 , 300000L, TimeUnit.MILLISECONDS);
 		
// 		executorService.scheduleAtFixedRate(() -> {
// 			
// 			ExpiryFileRemove.checkFileExpire();		
// 			
// 		},initialDelay , PERIOD, TimeUnit.MILLISECONDS);
    }
	
}
