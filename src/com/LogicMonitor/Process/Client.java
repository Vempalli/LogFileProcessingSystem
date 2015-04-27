package com.LogicMonitor.Process;

import java.io.File;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Client {

	public static void main(String[] args) {
		
		long suggested_threads = suggestNumberOfThreads();
		System.out.println("Depending on your system configuration suggested number of threads to use is " + suggested_threads);
		System.out.println("But you are free to select number of threads. Enter number of threads you want to choose");
		Scanner in = new Scanner(System.in);
		int thread_count = in.nextInt();
		ConcurrentSkipListMap<String, Integer>  lineCountMap = new ConcurrentSkipListMap<String, Integer>();
		//Compute Number of lines in each file
		lineCountMap = CountLines.execute(thread_count);
		//create a map that stores starting line number for each file
		Map<String, Integer>  firstLineNumerMap = new TreeMap<String, Integer>();
		firstLineNumerMap = computeFirstLineOfEachFile(firstLineNumerMap,lineCountMap);
		//add line numbers to the log files
		AddLineNumbers.modifyFiles(firstLineNumerMap,thread_count);
		System.out.println("Line numbers added to all the log files. Please check the log files in logFiles"+ File.separator+" directory");
	}
	/**
	 * Record starting line number of each file in a map
	 * @param firstLineNumerMap
	 * @param lineCountMap
	 * @return
	 */
	private static Map<String, Integer> computeFirstLineOfEachFile(Map<String, Integer> firstLineNumerMap, Map<String, Integer> lineCountMap) {
		int count = 1;
		int index = 0;
		for(Map.Entry<String, Integer> entry: lineCountMap.entrySet()){
			//Starting line = number of lines in previous file + 1
			String key = entry.getKey();
			int value = (index == 0) ? 1:count;
			firstLineNumerMap.put(key, value);
			count = count + entry.getValue();
			index++;
		}
		return firstLineNumerMap;
	}

	/**
	 * This method is implemented to suggest optimal number of threads to choose
	 * Source: http://stackoverflow.com/a/13958877/2833048 
	 * @return
	 */
	private static long suggestNumberOfThreads() {
		//Arithmetic calculation to decide number of threads:
		//Reference: http://stackoverflow.com/a/18366283/2833048
		//http://stackoverflow.com/a/13958877/2833048
		/*
		 N_threads = N_cpu * U_cpu * (1 + W / C)
		 where:
		 N_threads is the optimal number of threads
		 N_cpu is the number of processors
		 U_cpu is the target CPU utilization
		 W / C is the ratio of wait time to compute time (0 for CPU-bound task, maybe 10 or 100 for slow I/O tasks) 
		 */
		long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
		long presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
		//Given constraint: 100 MB memory
		final long Memory_Constraint = 100000000;
		float U_CPU = (float)Memory_Constraint/presumableFreeMemory;
		long N_CPU = Runtime.getRuntime().availableProcessors();
		//taking W/C as 10 in our case
		long N_threads = (long) (N_CPU * U_CPU * (1+10));
		return N_threads;
	}

}
