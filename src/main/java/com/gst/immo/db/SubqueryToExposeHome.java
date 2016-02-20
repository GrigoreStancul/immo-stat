package com.gst.immo.db;
// Generated Feb 20, 2016 1:55:06 PM by Hibernate Tools 4.3.1.Final

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class SubqueryToExpose.
 * @see com.gst.immo.db.SubqueryToExpose
 * @author Hibernate Tools
 */
public class SubqueryToExposeHome {

	private static final Log log = LogFactory.getLog(SubqueryToExposeHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(SubqueryToExpose transientInstance) {
		log.debug("persisting SubqueryToExpose instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(SubqueryToExpose persistentInstance) {
		log.debug("removing SubqueryToExpose instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public SubqueryToExpose merge(SubqueryToExpose detachedInstance) {
		log.debug("merging SubqueryToExpose instance");
		try {
			SubqueryToExpose result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public SubqueryToExpose findById(int id) {
		log.debug("getting SubqueryToExpose instance with id: " + id);
		try {
			SubqueryToExpose instance = entityManager.find(SubqueryToExpose.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
