package com.LogicMonitor.Process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class PrependLineNumber implements Runnable{
	
	private String filePath;
	private int startingNumber;
	
	
	public PrependLineNumber(String filePath, int startingNumber) {
		this.filePath = filePath;
		this.startingNumber = startingNumber;
	}

	@Override
	public void run() {
        String line = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            bufferedWriter = new BufferedWriter(new FileWriter(filePath));
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() > 0){
                	line = startingNumber + line;
                }
                bufferedWriter.write(line+"\n");
                startingNumber++;
             }
            bufferedReader.close();
            bufferedWriter.close();
        }
        catch(Exception e){
        	System.out.println("Exception "+e +" occured in PrependLineNumber.run()");
        }
	}
}

public class AddLineNumbers {
	public static void modifyFiles(Map<String, Integer> firstLineNumerMap, int thread_count){
		ExecutorService executor = Executors.newFixedThreadPool(thread_count);
    	for(Map.Entry<String, Integer> entry: firstLineNumerMap.entrySet()){
			String absolutePath = entry.getKey();
			int startAt = entry.getValue();
			executor.submit(new PrependLineNumber(absolutePath,startAt));
		}
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
        	System.out.println("Exception "+ e + " in AddLineNumbers.modifyFiles()");
        }
        System.out.println("Added line numbers to all the files");
	}

}
