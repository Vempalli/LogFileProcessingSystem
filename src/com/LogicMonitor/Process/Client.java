package com.LogicMonitor.Process;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Client {

	public static void main(String[] args) {
		System.out.println("Enter number of threads to work");
		Scanner in = new Scanner(System.in);
		int thread_count = in.nextInt();
		Map<String, Integer>  lineCountMap = new TreeMap<String, Integer>();
		lineCountMap = CountLines.execute(thread_count);
		for(Map.Entry<String, Integer> entry: lineCountMap.entrySet()){
			System.out.println("Key: "+ entry.getKey() + " value: "+ entry.getValue());
		}
	}

}
