package com.ij.absee.domain.impl;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.ij.absee.domain.Alternative;
import com.ij.absee.domain.Alternatives;
import com.ij.absee.domain.Experiment;
import com.ij.absee.domain.ExperimentResults;
import com.ij.absee.util.Stats;

@Entity
@Table(name="AB_EXPERIMENT", uniqueConstraints=@UniqueConstraint(columnNames="TESTNAME"))
public class ExperimentImpl implements Experiment {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(unique=true, nullable=false) 
	private String testName;
	
	private String status = "New";
	
	@OneToMany(cascade=ALL, mappedBy="experiment", targetEntity=AlternativeImpl.class)
	public List<Alternative> alternatives = new ArrayList<Alternative>();
	
	public String getName() {
		return testName;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void start() {
		status = "Live";
	}
	
	public void finish() {
		status = "Ended";
		for (Alternative alternative : alternatives) {
			alternative.finish();
		}
	}
	
	public List<Alternative> getAlternatives() {
		return Collections.unmodifiableList(alternatives);
	}
	
	public void removeAlternatives() {
		alternatives.clear();
	}
	
	
	public void setAlternatives(Alternatives alternatives) {
		if (alternatives instanceof StringArrayAlternatives) {
			StringArrayAlternatives stringAlternatives = (StringArrayAlternatives) alternatives;
			for (String alternative : stringAlternatives) {
				this.alternatives.add(new AlternativeImpl(this, alternative, alternatives.getWeight(alternative)));
			}
		} else if (alternatives instanceof WeightedAlternatives) {
			WeightedAlternatives weightedAlternatives = (WeightedAlternatives) alternatives;
			for (String alternative : weightedAlternatives) {
				this.alternatives.add(new AlternativeImpl(this, alternative, alternatives.getWeight(alternative)));
			}			
		}
	}
	
	public void setName(final String testName) {
		this.testName = testName;
	}
	
	public int conversionCount() {
		int conversions = 0;
		for (Alternative alternative : alternatives) {
			conversions += alternative.getConversionCount();
		}
		return conversions;
	}

	public int participantCount() {
		int participants = 0;
		for (Alternative alternative : alternatives) {
			participants += alternative.getParticipantCount();
		}
		return participants;
	}
	
	public Alternative bestAlternative() {
		Alternative bestAlternative = null;
		for (Alternative alternative : alternatives) {
			if (bestAlternative == null || alternative.conversionRate() > bestAlternative.conversionRate()) {
				bestAlternative = alternative;
			}
		}
		return bestAlternative;
	}
	
	public double zScore(Alternative control, Alternative experiment) {
		
	    if ((control.getParticipantCount() == 0) || (experiment.getParticipantCount() == 0)) {
	      throw new RuntimeException("Can't calculate the z score if either of the alternatives lacks participants.");
	    }

	    double crControl = control.conversionRate();
	    double controlParticipants = control.getParticipantCount();

	    double crExperiment = experiment.conversionRate();
	    double experimentParticipants = experiment.getParticipantCount();

	    double numerator = crExperiment - crControl;   
	    double frac1 = crExperiment * (1 - crExperiment) / experimentParticipants;
	    double frac2 = crControl * (1 - crControl) / controlParticipants;

	    return numerator / Math.sqrt(frac1 + frac2);
	}
	
	public Stats pValue(final double zScore) {
		int index = 0;
		double z = Math.abs(zScore);
		Stats stat = Stats.NOT_CONFIDENT;
		while (index < Stats.values().length) {
			if (z > Stats.values()[index].getZScore()) {
				stat = Stats.values()[index];
			}
			index += 1;
		}
		return stat;
	}

	public ExperimentResults results() {
		ExperimentResults results = new ExperimentResults();
		
		Alternative control = alternatives.get(0);
		List<Alternative> experiments = new ArrayList<Alternative>(alternatives);
		experiments.remove(0);

		Map<Alternative, Double> zScores = new HashMap<Alternative, Double>();
		Map<Alternative, Stats> stats = new HashMap<Alternative, Stats>();
		for (Alternative experiment : experiments) {
			double zScore = zScore(control, experiment);
			zScores.put(experiment, Double.valueOf(zScore));
			stats.put(experiment, pValue(zScore));
		}
		results.setzScores(zScores);
		results.setStats(stats);
			
		return results;
	}
		
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ExperimentImpl)){
			return false;
		}
		return id == ((ExperimentImpl) obj).id;
	}
	
	@Override
	public int hashCode() {
		return 37 * (int)id;
	}

		
}
