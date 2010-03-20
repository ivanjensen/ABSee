package com.ij.absee.domain;

public interface Alternative {

	public String getContent();

	public void incrementParticipants();
	
	public void incrementConversions();

	public int getParticipantCount();
	
	public int getConversionCount();

	public void finish();

	public double conversionRate();
	
	public double getWeight();
}
