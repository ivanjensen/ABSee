package com.ij.absee.dao;

import com.ij.absee.domain.Experiment;

public interface ExperimentDao {

	public Experiment findOrCreateByTestName(final String testName);

	public void incrementParticipation(final String testName, final String viewedAlternative);
	public void incrementConversion(final String testName, final String viewedAlternative);

	public void beginTransaction();
	
	public void commit();
	
	public void rollback();
	
	public Experiment update(final Experiment experiment);

	public boolean testExists(String string);

}
