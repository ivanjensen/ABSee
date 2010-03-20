package com.ij.absee.domain.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ij.absee.domain.Alternatives;

public class WeightedAlternatives extends Alternatives implements Iterable<String> {

	/** Serialization id. */
	private static final long serialVersionUID = 1L;

	/** Match weighted alternatives: "bob" => 3 */
	private Pattern weightedAlternativePattern = Pattern.compile("['\"](\\w+)[\"'] *=> *(0\\.\\d+) *,{0,1}");

	private Map<Double, String> values = new LinkedHashMap<Double, String>(); 
	
	public WeightedAlternatives(final String definitions) {
		parseWeightedAlternatives(definitions);
	}
	
	@Override
	public String selectForUser(String testName, String identity) {
		int choice = moduloChoice(testName + identity, 1000);
		int offset = 0;
	    for (Entry<Double, String> entry : values.entrySet()) {
	    	offset += entry.getKey() * 1000;
	    	if (choice <= offset) {
	    		return entry.getValue();
	    	}
	    }
	    throw new RuntimeException("Error selecting weighted alternative");
	}
	
	@Override
	public double getWeight(String alternative) {
	    for (Entry<Double, String> entry : values.entrySet()) {
	    	if (alternative.equals(entry.getValue())) {
	    		return entry.getKey();
	    	}
	    }
	    return -1;
	}
	
	public Iterator<String> iterator() {
		return Collections.unmodifiableCollection(values.values()).iterator();
	}
	
	private void parseWeightedAlternatives(final String alternativeString) {
		Matcher matcher = weightedAlternativePattern.matcher(alternativeString);
		while (matcher.find()) {
			String alternative = matcher.group(1);
			Double weight = Double.parseDouble(matcher.group(2));
			for (int x = 0; x < weight; x++) {
				values.put(weight, alternative);
			}
		}
	}
	
}
