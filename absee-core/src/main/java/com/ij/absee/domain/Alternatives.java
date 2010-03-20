/**
 * 
 */
package com.ij.absee.domain;

import java.io.Serializable;
import java.math.BigInteger;

import com.ij.absee.util.MD5;

public abstract class Alternatives implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final MD5 md5 = new MD5();
	
	public abstract String selectForUser(String testName, String identity);

	protected int moduloChoice(final String testAndIdentity, final int choicesCount) {
		return (int) md5.md5Integer(testAndIdentity).mod(BigInteger.valueOf(choicesCount)).intValue();
	}

	public abstract double getWeight(String alternative);
	
}