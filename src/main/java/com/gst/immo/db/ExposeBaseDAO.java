package com.gst.immo.db;
// Generated Feb 20, 2016 1:55:06 PM by Hibernate Tools 4.3.1.Final

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Home object for domain model class ExposeBase.
 * 
 * @see com.gst.immo.db.ExposeBase
 * @author Hibernate Tools
 */
public class ExposeBaseDAO {

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void persist(ExposeBase exposeBase) {
		Session session = this.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.persist(exposeBase);
		tx.commit();
		session.close();
	}

	public ExposeBase get(long exposeBaseId) {
		Session session = this.sessionFactory.openSession();
		ExposeBase exposeBase = session.get(ExposeBase.class, exposeBaseId);
		session.close();
		return exposeBase;
	}

	// private void testHibernate() {
	// SessionFactory factory = HibernateUtils.getSessionFactory();
	// Session session = factory.openSession();
	// session.getTransaction().begin();
	// String sql = "SELECT q.query_id, q.date_time, q.query FROM " +
	// Query.class.getName() + " q";
	// org.hibernate.Query hibQuery = session.createQuery(sql);
	// List<Query> queries = hibQuery.list();
	// queries.forEach(q -> System.out.println(q));
	// session.close();
	// }

}
