package com.cloudcog.images.resize.worker;

import java.io.File;
import java.util.concurrent.ExecutorService;

import com.cloudcog.images.resize.FolderWatcher;
import com.cloudcog.images.resize.utils.FileUtils;

public class WorkersFactory {

	private ExecutorService threadExecutor;
	private FolderWatcher folderWatcher;
	
	public WorkersFactory(ExecutorService threadExecutor, FolderWatcher folderWatcher) {
		this.threadExecutor = threadExecutor;
		this.folderWatcher = folderWatcher;
	}
	
	public void startFileWorker(File file) {
		if(FileUtils.isFileImage(file.getName())) {
			threadExecutor.execute(new ResizeWorker(file));
		} else if (FileUtils.isFileZip(file.getName())) {
    		threadExecutor.execute(new UnzipWorker(file));
    		
    	} else if (file.isDirectory()) {
    		folderWatcher.watchFolder(file);
    	}
	}
}
