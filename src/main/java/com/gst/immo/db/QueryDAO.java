package com.gst.immo.db;
// Generated Feb 20, 2016 1:55:06 PM by Hibernate Tools 4.3.1.Final

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Home object for domain model class Query.
 * 
 * @see com.gst.immo.db.Query
 * @author Hibernate Tools
 */
public class QueryDAO {

	private SessionFactory sessionFactory;

	public void persist(Query query) {
		Session session = this.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.persist(query);
		tx.commit();
		session.close();
	}

	public void remove(Query query) {
		Session session = this.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.delete(query);
		tx.commit();
		session.close();
	}

	public Query findById(int id) {
		Session session = this.sessionFactory.openSession();
		Query query = session.get(Query.class, id);
		session.close();
		return query;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
