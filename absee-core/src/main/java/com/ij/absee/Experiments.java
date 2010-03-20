package com.ij.absee;

import com.ij.absee.domain.Alternatives;

public interface Experiments {

	boolean exists(final String testName);
	
	void startExperiment(final String testName, final Alternatives alternatives , final String conversionName);
	
	boolean testEnded(final String testName);
	
	String alternativeThatWon(final String testName);

	void endExperiment(final String testName, final String finalAlternative);

	public void recordParticipation(final String testName, final String identity);

	void recordConversion(String conversionName, String string);

	String findAlternativeForUser(final String testName, final String identity);

}
