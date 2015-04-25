package com.LogicMonitor.Process;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Runner implements Runnable {
	
	public static Map<String,Integer> lineCountMap = new TreeMap<String, Integer>();
	private String file;
	private int count = 0;
	
	public Runner(String file) {
		this.file = file;
	}
	
	@Override
	public void run() {
		try {
			count = countLines(file);
		} catch (IOException e) {
			System.out.println("Exception "+ e + " in Runner.run()");
		}
		//count number of lines in each file and add to map
        lineCountMap.put(file, count);
    }
	
	//Count the number of lines in each text file
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] chars = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(chars)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (chars[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 0 : count+1;
	    } finally {
	        is.close();
	    }
	}
}

public class CountLines {
    
    public static Map<String, Integer> execute(int thread_count) {
    	//Use thread pooling to assign required number of threads
    	ExecutorService executor = Executors.newFixedThreadPool(thread_count);
    	File folder = new File("logFiles/");
    	for (final File fileEntry : folder.listFiles()) {
    		 executor.submit(new Runner((fileEntry.getAbsolutePath())));
    	}
        executor.shutdown();
        //At this point we have submitted all the tasks
        //Do not return map until line numbers is counted for all the files
        //To make sure that everything is executed -> use await 1 day.. this gives enough time to execute everything
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
        	System.out.println("Exception "+ e + " in CountLines.execute()");
        }
        return Runner.lineCountMap;
    }

}