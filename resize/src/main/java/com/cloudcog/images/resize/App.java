package com.cloudcog.images.resize;

import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudcog.images.resize.config.Config;
import com.cloudcog.images.resize.config.ConfigChecker;

/**
 * 
 * @author mpahic
 *
 */
public class App 
{
	static Logger log = LoggerFactory.getLogger(App.class);
	
    public static void main( String[] args ) throws IOException, InterruptedException
    {
        Config.constructProperties();
		runConfigChecker();

        ExecutorService threadExecutor = Executors.newFixedThreadPool(Config.getMaxThreads());
        
        FolderWatcher folderWatcher = new FolderWatcher(Config.getInputFolder(), threadExecutor);
        Thread th = new Thread(folderWatcher);
        th.start();
        
    	log.info("Application started and running!");
    	th.join();

    }

	private static void runConfigChecker() {
		Timer timer = new Timer();
        ConfigChecker configChecker = new ConfigChecker();
        timer.schedule(configChecker, 600000, 600000);
	}
}
