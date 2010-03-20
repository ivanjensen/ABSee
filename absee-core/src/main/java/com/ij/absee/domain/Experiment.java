package com.ij.absee.domain;

import java.util.List;

public interface Experiment {

	String getName();
	
	String getStatus();

	public void start();
	
	public void finish();
	
	public List<Alternative> getAlternatives();

	int participantCount();

	int conversionCount();

	Alternative bestAlternative();
	
	public void removeAlternatives();

	void setAlternatives(Alternatives alternatives);

	ExperimentResults results();

}
