package com.gst.immo.db;
// Generated Feb 20, 2016 1:55:06 PM by Hibernate Tools 4.3.1.Final

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.gst.immo.utils.functions.CheckedConsumer;

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

	public List<ExposeBase> list() {
		Session session = this.sessionFactory.openSession();
		@SuppressWarnings("unchecked")
		List<ExposeBase> list = session.createCriteria(ExposeBase.class).list();
		session.close();
		return list;
	}

	public void forEach(CheckedConsumer<ExposeBase> action) throws IOException {
		Session session = this.sessionFactory.openSession();
		@SuppressWarnings("unchecked")
		Iterator<ExposeBase> iterator = session.createQuery("from " + ExposeBase.class.getName() + " e").iterate();
		while (iterator.hasNext()) {
			ExposeBase next = iterator.next();
			action.accept(next);
		}
	}
	// public void forEach() {
	// Session session = this.sessionFactory.openSession();
	// String sql = "SELECT e.expose_id, e.update_time, e.html_page FROM " +
	// ExposeBase.class.getName() + " e";
	// org.hibernate.Query query = session.createQuery(sql);
	// query.setReadOnly(true);
	// query.setFetchSize(Integer.MIN_VALUE);
	// ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
	// while (results.next()) {
	// Object row = results.get();
	// // process row then release reference
	// // you may need to flush() as well
	// }
	// results.close();
	// }

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
