

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class StartupInit
 *
 */
@WebListener
public class StartupInit implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public StartupInit() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    	ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
		
		executorService.scheduleAtFixedRate(() -> {
			
			//System.out.println("Hi there at: " + new java.util.Date());
			
			
		}, 0, 1000L, TimeUnit.MILLISECONDS);
		
		
    }
	
}
