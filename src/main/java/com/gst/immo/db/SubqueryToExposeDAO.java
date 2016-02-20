package com.gst.immo.db;
// Generated Feb 20, 2016 1:55:06 PM by Hibernate Tools 4.3.1.Final

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Home object for domain model class SubqueryToExpose.
 * 
 * @see com.gst.immo.db.SubqueryToExpose
 * @author Hibernate Tools
 */
public class SubqueryToExposeDAO {

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void persist(SubqueryToExpose subqueryToExpose) {
		Session session = this.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.persist(subqueryToExpose);
		tx.commit();
		session.close();
	}

}
