package com.ij.absee.domain;

import java.util.Map;

import com.ij.absee.util.Stats;

public class ExperimentResults {

	private Map<Alternative, Double> zScores;
	private Map<Alternative, Stats> stats;
	
	public Map<Alternative, Double> getzScores() {
		return zScores;
	}
	public void setzScores(Map<Alternative, Double> zScores) {
		this.zScores = zScores;
	}
	public Map<Alternative, Stats> getStats() {
		return stats;
	}
	public void setStats(Map<Alternative, Stats> stats) {
		this.stats = stats;
	}
	
	
}
