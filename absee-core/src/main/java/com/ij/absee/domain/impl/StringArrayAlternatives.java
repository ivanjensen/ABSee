package com.ij.absee.domain.impl;


import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import com.ij.absee.domain.Alternatives;

public class StringArrayAlternatives extends Alternatives implements Iterable<String> {

	/** Serialization id */
	private static final long serialVersionUID = 1L;
	
	private String [] values;
	
	public StringArrayAlternatives(String[] alternatives) {
		values = alternatives;
	}

	public String selectForUser(String testName, String identity) {
		return values[moduloChoice(testName + identity, values.length)];
	}

	public Iterator<String> iterator() {
		return Collections.unmodifiableList(Arrays.asList(values)).iterator();
	}

	@Override
	public double getWeight(String alternative) {
		return 1 / values.length;
	}
}
