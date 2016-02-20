package com.gst.immo.db;

public class ImmoDAO {

	private QueryDAO _queryDAO;
	private SubQueryDAO _subQueryDAO;
	private ExposeBaseDAO _exposeBaseDAO;
	private SubqueryToExposeDAO _subqueryToExposeDAO;

	public QueryDAO getQueryDAO() {
		return _queryDAO;
	}

	public void setQueryDAO(QueryDAO queryDAO) {
		_queryDAO = queryDAO;
	}

	public SubQueryDAO getSubQueryDAO() {
		return _subQueryDAO;
	}

	public void setSubQueryDAO(SubQueryDAO subQueryDAO) {
		_subQueryDAO = subQueryDAO;
	}

	public ExposeBaseDAO getExposeBaseDAO() {
		return _exposeBaseDAO;
	}

	public void setExposeBaseDAO(ExposeBaseDAO exposeBaseDAO) {
		_exposeBaseDAO = exposeBaseDAO;
	}

	public SubqueryToExposeDAO getSubqueryToExposeDAO() {
		return _subqueryToExposeDAO;
	}

	public void setSubqueryToExposeDAO(SubqueryToExposeDAO subqueryToExposeDAO) {
		_subqueryToExposeDAO = subqueryToExposeDAO;
	}

}
