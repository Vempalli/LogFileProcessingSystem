package com.LogicMonitor.Process;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Runner implements Runnable {

	private String file;
	private int count = 0;
	
	public Runner(String file2) {
		this.file = file2;
	}
	
	@Override
	public void run() {
		try {
			count = countLines(file);
		} catch (IOException e) {
			System.out.println("Exception "+ e + " in Runner.run()");
		}
        System.out.println("In file "+file+" there are "+count + " lines and is executed by "+Thread.currentThread().getName());
        //TODO: Put this value in to a Hashmap sorted on filename - TreeMap
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
    
    public static void main(String[] args) {
    	//TODO: read number of threads from user
    	ExecutorService executor = Executors.newFixedThreadPool(2);
    	//TODO: create property for pathname
    	String pathname = "logFiles/logtest.2014­-07-­0";
    	//TODO: Get count of number of files and loop on them
    	for(int i = 1; i < 4; i++) {
            executor.submit(new Runner((pathname+i+".log")));
        }
        executor.shutdown();
        //At this point we have submitted all the tasks
        
        //Do not go to next line until all the code is executed.
        //To make sure that everything is executed -> say await 1 day.. this gives enough time to execute everything
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
        	System.out.println("Exception "+ e + " in CountLines.main()");
        }
        System.out.println("Computed Line numbers for each file");
    }

}