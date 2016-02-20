package com.gst.immo.db;
// Generated Feb 20, 2016 1:55:06 PM by Hibernate Tools 4.3.1.Final

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class SubQuery.
 * @see com.gst.immo.db.SubQuery
 * @author Hibernate Tools
 */
public class SubQueryHome {

	private static final Log log = LogFactory.getLog(SubQueryHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(SubQuery transientInstance) {
		log.debug("persisting SubQuery instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(SubQuery persistentInstance) {
		log.debug("removing SubQuery instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public SubQuery merge(SubQuery detachedInstance) {
		log.debug("merging SubQuery instance");
		try {
			SubQuery result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public SubQuery findById(int id) {
		log.debug("getting SubQuery instance with id: " + id);
		try {
			SubQuery instance = entityManager.find(SubQuery.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
