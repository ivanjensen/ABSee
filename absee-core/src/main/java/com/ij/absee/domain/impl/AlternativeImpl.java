package com.ij.absee.domain.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;


import com.ij.absee.domain.Alternative;
import com.ij.absee.domain.Experiment;
import com.ij.absee.util.MD5;

@Entity
@Table(name="AB_ALTERNATIVE")
@org.hibernate.annotations.Table(appliesTo="AB_ALTERNATIVE", indexes={@Index(name="AB_ALTERNATIVE_LOOKUP", columnNames="lookup")})
public class AlternativeImpl implements Alternative {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long id;
	
	private String content;
	
	@SuppressWarnings("unused")  // Used by JPA
	@ManyToOne(targetEntity=ExperimentImpl.class)
	@ForeignKey(name="AB_ALT_EXP_FK")
	private Experiment experiment;
    
    private int participantCount;
	
	private int conversionCount;

	@SuppressWarnings("unused")  // Used by JPA
	@Column(length=32)
	private String lookup;

	private double weight;
	
	public AlternativeImpl() {
	}
	
	public AlternativeImpl(final Experiment experiment, final String testAlternative, final double weight) {
		this.experiment = experiment;
		this.content = testAlternative;
		this.weight = weight;
		this.lookup = new MD5().md5HexString(experiment.getName() + testAlternative);
	}
	
	public void finish() {
		lookup = "Experiment completed." + id;
	}

	public String getContent() {
		return content;
	}
	
	public void incrementParticipants() {
		participantCount++;
	}
	
	public void incrementConversions() {
		conversionCount++;
	}
	
	public int getConversionCount() {
		return conversionCount;
	}
	
	public int getParticipantCount() {
		return participantCount;
	}
	
	public double conversionRate() {
		return 1.0 * conversionCount / participantCount;
	}
	
	public double getWeight() {
		return weight;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AlternativeImpl)){
			return false;
		}
		return id == ((AlternativeImpl) obj).id;
	}
	
	@Override
	public int hashCode() {
		return 37 * (int)id;
	}
}
