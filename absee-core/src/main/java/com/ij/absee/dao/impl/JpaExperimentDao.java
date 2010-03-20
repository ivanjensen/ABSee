package com.ij.absee.dao.impl;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.ij.absee.dao.ExperimentDao;
import com.ij.absee.domain.Experiment;
import com.ij.absee.domain.impl.ExperimentImpl;
import com.ij.absee.util.MD5;

public class JpaExperimentDao implements ExperimentDao {

	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("absee", new HashMap<String, String>());

	private ThreadLocal<EntityManager> entityManager = new ThreadLocal<EntityManager>();
	public void beginTransaction() {
		entityManager.set(emf.createEntityManager());
		entityManager.get().getTransaction().begin();
	}
	
	public void commit() {
		EntityTransaction tx = entityManager.get().getTransaction();
		entityManager.remove();
		tx.commit();
	}
	
	public void rollback() {
		EntityTransaction tx = entityManager.get().getTransaction();
		entityManager.remove();
		tx.rollback();
	}
	
	public Experiment update(Experiment experiment) {
		return entityManager.get().merge(experiment);
	}
	
	@SuppressWarnings("unchecked")
	public Experiment findOrCreateByTestName(String testName) {
	    List<Experiment> experiments = entityManager.get().createQuery("select e from ExperimentImpl e where e.testName = ?").setParameter(1, testName).getResultList();
	    if (!experiments.isEmpty()) {
	    	return experiments.get(0);
	    }
	    	
	    ExperimentImpl newExperiment = new ExperimentImpl();
	    newExperiment.setName(testName);
	    entityManager.get().persist(newExperiment);
	    return newExperiment;
	}
	
	@SuppressWarnings("unchecked")	
	public boolean testExists(String testName) {
	    List<Long> experimentCount = entityManager.get().createQuery("select COUNT(e) from ExperimentImpl e where e.testName = ?").setParameter(1, testName).getResultList();
	    return experimentCount.get(0) > 0;
	}
	
	public void incrementParticipation(String testName, String viewedAlternative) {
		String lookup = alternativeLookupHash(testName, viewedAlternative);
		entityManager.get().createQuery("update AlternativeImpl a set a.participantCount = a.participantCount + 1 where a.lookup = ?").setParameter(1, lookup).executeUpdate();	
	}

	public void incrementConversion(String testName, String viewedAlternative) {
		String lookup = alternativeLookupHash(testName, viewedAlternative);
		entityManager.get().createQuery("update AlternativeImpl a set a.conversionCount = a.conversionCount + 1 where a.lookup = ?").setParameter(1, lookup).executeUpdate();	
	}

	private String alternativeLookupHash(final String testName, final String viewedAlternative) {
		return new MD5().md5HexString(testName + viewedAlternative);
	}
	
}
