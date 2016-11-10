import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import nFileHandler.ExpiryFileRemove;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class StartupInit
 *
 */
@WebListener
public class StartupInit implements ServletContextListener {
	private long initialDelay;
	private static final long PERIOD = 86400000L;
	final Date midnight = new Date();
	
	ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    /**
     * Default constructor. 
     */
    public StartupInit() {
    	midnight.setHours(24);
    	midnight.setMinutes(0);
    	midnight.setSeconds(0);
    	
    	initialDelay = new Date( midnight.getTime() - System.currentTimeMillis() ).getTime();
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
        
    	executorService.shutdown();
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
         executorService.scheduleAtFixedRate(() -> {
 			
 			ExpiryFileRemove.checkFileExpire();		
 			
 		},0 , 60000L, TimeUnit.MILLISECONDS);
 		
// 		executorService.scheduleAtFixedRate(() -> {
// 			
// 			ExpiryFileRemove.checkFileExpire();		
// 			
// 		},initialDelay , PERIOD, TimeUnit.MILLISECONDS);
    }
	
}
