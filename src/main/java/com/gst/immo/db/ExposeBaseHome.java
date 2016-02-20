package com.gst.immo.db;
// Generated Feb 20, 2016 1:55:06 PM by Hibernate Tools 4.3.1.Final

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class ExposeBase.
 * @see com.gst.immo.db.ExposeBase
 * @author Hibernate Tools
 */
public class ExposeBaseHome {

	private static final Log log = LogFactory.getLog(ExposeBaseHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(ExposeBase transientInstance) {
		log.debug("persisting ExposeBase instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(ExposeBase persistentInstance) {
		log.debug("removing ExposeBase instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public ExposeBase merge(ExposeBase detachedInstance) {
		log.debug("merging ExposeBase instance");
		try {
			ExposeBase result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public ExposeBase findById(int id) {
		log.debug("getting ExposeBase instance with id: " + id);
		try {
			ExposeBase instance = entityManager.find(ExposeBase.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
