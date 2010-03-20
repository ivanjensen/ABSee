package com.ij.absee;

import java.util.Map;

import com.ij.absee.domain.Alternatives;
import com.ij.absee.domain.impl.StringArrayAlternatives;
import com.ij.absee.domain.impl.WeightedAlternatives;

/**
 * The entry point for the ABSee framework.  Typical use:
 * setup your test by calling the {@link #test(String, Object, Map)} method
 * later convert {@link #conversion(String)} to convert the user in that test.
 */
public class ABSee {

	private Experiments experiments;
	private final IdentitySource identitySource;

	/**
	 * Create an instance backed by the specified experiments implementation and using the
	 * specified identity source for finding the user who will participate in tests.
	 * @param experiments the experiments implementation to use.
	 * @param identitySource the source for user identities.
	 */
	public ABSee(final Experiments experiments, final IdentitySource identitySource) {
		this.experiments = experiments;
		this.identitySource = identitySource;
	}

	/**
	 * Register a user as participating in a specific test, returning the alternative to use for the user.
	 * The user's identity is returned from the injected identity source.
	 * @param testName the name of the test - will be created if it doesn't exist.
	 * @param alternatives the alternatives to select from in this test.
	 * @param options configuration options for this test.
	 * @return the alternative to use for the user in the specified test.
	 */
	public String test(final String testName, Object alternatives, final Map<String, String> options) {

		
		if (experiments.testEnded(testName)) {
			return experiments.alternativeThatWon(testName);
		}
		
		if (!experiments.exists(testName)) {
			String conversionName = testName;
			if (options != null && options.containsKey("conversion")) {
				conversionName = options.get("conversion");
			}
			experiments.startExperiment(testName, parseAlternatives(testName, alternatives), conversionName);
		}
		
		String choice = experiments.findAlternativeForUser(testName, identitySource.get());
		experiments.recordParticipation(testName, identitySource.get());
		return choice;
	}
	
	private Alternatives parseAlternatives(String testName, Object alternatives) {
		if (alternatives instanceof String[]) {
			return new StringArrayAlternatives((String []) alternatives);
		} else if (alternatives instanceof Integer) {
			int limit = (Integer) alternatives;
			String [] expandedAlternatives = new String [limit];
			for (int x = 1; x <= limit; x++) {
				expandedAlternatives[x - 1] = String.valueOf(x);
			}			
			return new StringArrayAlternatives(expandedAlternatives);
		} else if (alternatives instanceof String) {
			return new WeightedAlternatives((String) alternatives);
		}
		throw new RuntimeException("Supplied alternatives not supported for test " + testName);
	}

	/**
	 * Scores a conversion for a single conversion name.
	 * @param conversionName the conversion that has occurred.
	 */
	public void conversion(final String conversionName) {
		experiments.recordConversion(conversionName, identitySource.get());
	}
	
	/**
	 * Convenience for getting boolean either/or option.
	 * @param testName the test name
	 * @return true or false
	 */
	public boolean flip(final String testName) {
		return Boolean.parseBoolean(test(testName, new String[] {"false", "true"}, null));
	}

	/**
	 * End the specified test selecting the alternative to show from now on.
	 * @param testName the name of the test to end.
	 * @param finalAlternative the alternative to select for the test.
	 */
	public void endTest(String testName, String finalAlternative) {
		experiments.endExperiment(testName, finalAlternative);
	}

}
