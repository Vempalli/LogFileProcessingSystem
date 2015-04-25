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
        String content = "";
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            content = readContent(bufferedReader,line);
            content = content.substring(0,content.lastIndexOf('\n'));
            bufferedReader.close();
            bufferedWriter = new BufferedWriter(new FileWriter(filePath));
            writeContent(content,bufferedWriter);
            bufferedWriter.close();
        }
        catch(Exception e){
        	System.out.println("Exception "+e +" occured in PrependLineNumber.run()");
        }
	}

	private void writeContent(String content, BufferedWriter bufferedWriter) {
		try{
			bufferedWriter.write(content);
		}
		catch(Exception e){
			System.out.println("Exception "+ e +" in PrependLineNumber.writeContent()");
		}
	}

	private String readContent(BufferedReader bufferedReader, String line) {
		 String content = "";
		 try{
			 while ((line = bufferedReader.readLine()) != null) {
				 line = startingNumber + ". "+ line;
				 content = content + line + '\n';
				 startingNumber++;
			 }
		 }
		 catch(Exception e){
			 System.out.println("Exception " + e + " at PrependLineNumber.readContent()");
		 }
		 return content;
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
	}

}
