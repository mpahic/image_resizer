package com.cloudcog.images.resize;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudcog.images.resize.worker.WorkersFactory;
import com.sun.nio.file.ExtendedWatchEventModifier;

/**
 * 
 * @author mpahic
 *
 */
public class FolderWatcher implements Runnable {

	static Logger log = LoggerFactory.getLogger(FolderWatcher.class);
	
	private WatchService myWatcher;
	private WorkersFactory factory;

	private File inputFolder;
	
    public FolderWatcher(String folderpath, ExecutorService threadExecutor) throws IOException {
    	
        this.myWatcher = FileSystems.getDefault().newWatchService();
		this.factory = new WorkersFactory(threadExecutor, this);
        this.inputFolder = new File(folderpath);

        if(inputFolder.exists()) {
	        watchFolder(inputFolder);
	        processExisting(inputFolder);
        }
    }
    
    public void processExisting(File file) {
    	File[] children = file.listFiles();
    	for(int i = 0; i<children.length; i++) {
    		File child = children[i];
    		if(child.isDirectory()) {
    			processExisting(child);
    		} else {
    			factory.startFileWorker(child);
    		}
    	}
	}

	public void watchFolder(File file) {
        try {
        	if(file.isDirectory()) {
	            Path inputFolderPath = file.toPath();
        		WatchEvent.Kind<?>[] standardEvents = {StandardWatchEventKinds.ENTRY_CREATE,};
                inputFolderPath.register(this.myWatcher, standardEvents, ExtendedWatchEventModifier.FILE_TREE);
        	}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void run() {
        try {
            WatchKey key = myWatcher.take();
            while(key != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                	log.debug("Received "+event.kind()+" event for file: "+event.context()+"\n");
                	Path filePath = (Path) event.context();
                	File file = new File(this.inputFolder.getName() + File.separator + filePath.toString());
            		factory.startFileWorker(file);
                }
                key.reset();
                key = myWatcher.take();
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        log.info("Stopping watch thread");
    }
}
