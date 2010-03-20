package com.ij.absee.util;

public enum Stats {

	NOT_APPLICABLE(2.00, 0, "N/A", "not applicable"),
	NOT_CONFIDENT(1.00, 0, "0%", "not confident"),
	FAIRLY_CONFIDENT(0.10, 1.29, "90%", "fairly confident"),
	CONFIDENT(0.05, 1.65, "95%", "confident"),
	VERY_CONFIDENT(0.01, 2.33, "99%", "very confident"),
	EXTREMELY_CONFIDENT(0.0001, 3.08, "99.9%", "extremely confident"); 

	private final double value;
	private String percentage;
	private final String description;
	private final double zScore;

	private Stats(final double value, final double zScore, final String percentage, final String description) {
		this.value = value;
		this.zScore = zScore;
		this.percentage = percentage;
		this.description = description;
		
	}

	public double getValue() {
		return value;
	}

	public String getPercentage() {
		return percentage;
	}

	public String getDescription() {
		return description;
	}

	public double getZScore() {
		return zScore;
	}
	
	
}
