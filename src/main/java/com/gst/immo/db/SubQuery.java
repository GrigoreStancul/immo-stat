package com.gst.immo.db;
// Generated Feb 20, 2016 1:55:06 PM by Hibernate Tools 4.3.1.Final

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * SubQuery generated by hbm2java
 */
@Entity
@Table(name = "sub_query", catalog = "immo")
public class SubQuery implements java.io.Serializable {

	private int subQueryId;
	private Query query;
	private Date dateTime;
	private String subQuery;
	private Set<SubqueryToExpose> subqueryToExposes = new HashSet<SubqueryToExpose>(0);

	public SubQuery() {
	}

	public SubQuery(Query query, Date dateTime) {
		this.query = query;
		this.dateTime = dateTime;
	}

	public SubQuery(Query query, Date dateTime, String subQuery, Set<SubqueryToExpose> subqueryToExposes) {
		this.query = query;
		this.dateTime = dateTime;
		this.subQuery = subQuery;
		this.subqueryToExposes = subqueryToExposes;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "sub_query_id", unique = true, nullable = false)
	public int getSubQueryId() {
		return this.subQueryId;
	}

	public void setSubQueryId(int subQueryId) {
		this.subQueryId = subQueryId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_query_id", nullable = false)
	public Query getQuery() {
		return this.query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_time", nullable = false, insertable = false, length = 19)
	public Date getDateTime() {
		return this.dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	@Column(name = "sub_query", length = 45)
	public String getSubQuery() {
		return this.subQuery;
	}

	public void setSubQuery(String subQuery) {
		this.subQuery = subQuery;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "subQuery")
	public Set<SubqueryToExpose> getSubqueryToExposes() {
		return this.subqueryToExposes;
	}

	public void setSubqueryToExposes(Set<SubqueryToExpose> subqueryToExposes) {
		this.subqueryToExposes = subqueryToExposes;
	}

}