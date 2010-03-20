package com.ij.absee.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public String md5HexString(final String value) {
		MessageDigest digest = null;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte [] digestBytes = digest.digest(value.getBytes());
		StringBuffer hexString = new StringBuffer();
		for (int i = 0;i < digestBytes.length; i++) {
		    hexString.append(Integer.toHexString(0xFF & digestBytes[i]));
		}
		return hexString.toString();
	}
	
	public BigInteger md5Integer(final String value) {
		return new BigInteger(md5HexString(value), 16);
	}
}
