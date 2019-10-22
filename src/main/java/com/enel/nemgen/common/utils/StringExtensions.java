package com.enel.nemgen.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringExtensions {
	
	public static boolean IsNullOrEmpty(String s) {
    	if(s == null) { return true; }
    	if(s.isEmpty()){ return true; }
    	return false;
    }
	
	public static boolean IsNullOrWhitespace(String s) {
    	if(s == null) { return true; }
    	return IsNullOrEmpty(s.trim());
    }
	
	public static String TrimmedOrEmpty(String s) {
    	if(s == null) { return ""; }
    	return s.trim();
    }
	
	public static String ToLowerTrimmed(String s) {
    	if(s == null) { return null; }
    	return s.trim().toLowerCase();
    }
	
	public static String ToUpperTrimmed(String s) {
    	if(s == null) { return null; }
    	return s.trim().toUpperCase();
    }
	
	public static String ToLowerTrimmedOrEmpty(String s) {
    	if(s == null) { return ""; }
    	return ToLowerTrimmed(s);
    }
	
	public static String ToUpperTrimmedOrEmpty(String s) {
    	if(s == null) { return ""; }
    	return ToUpperTrimmed(s);
    }
	
	public static <T> String listToCsv(List<T> items) {
		return listToDelimitedString(items, ",");
	}
	
	public static <T> String listToDelimitedString(List<T> items, String delimiter) {
    	if(items == null) { return ""; }
    	String csv = "";
    	for(T item : items) {
    		csv += (csv.isEmpty() ? String.valueOf(item) : (delimiter+String.valueOf(item)));
    	}
    	return csv;
    }
	
	public static List<String> splitCsvText(String text){
		return splitDelimitedText(text, ",");
	}
	
	public static List<String> splitDelimitedText(String text, String delimiter){
		if(text == null || text.trim().length() == 0) {
			return new ArrayList<String>();
		}
		return Arrays.asList(text.split(delimiter));
	}
	
	public static List<String> getList(String... args) {
		List<String> list = new ArrayList<>();
		for (String arg : args) {
			list.add(arg);
		}
		return list;
	}
}
