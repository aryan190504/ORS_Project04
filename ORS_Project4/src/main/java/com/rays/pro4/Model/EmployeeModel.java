package com.rays.pro4.Model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.rays.pro4.Bean.EmployeeBean;
import com.rays.pro4.Exception.ApplicationException;
import com.rays.pro4.Exception.DatabaseException;
import com.rays.pro4.Exception.DuplicateRecordException;
import com.rays.pro4.Util.JDBCDataSource;

public class EmployeeModel {
	private static Logger log = Logger.getLogger(StudentModel.class);

	public Integer nextPK() throws DatabaseException {

		log.debug("Model nextPK Started");
		Connection conn = null;
		int pk = 0;

		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("select max(ID) FROM st_employee");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				pk = rs.getInt(1);
			}
			rs.close();

		} catch (Exception e) {
			log.error("Database Exception .....", e);
			throw new DatabaseException("Exception :Exception in getting PK");

		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model nextPk End");
		return pk + 1;

	}

	public long add(EmployeeBean bean) throws ApplicationException, DuplicateRecordException, Exception {
		log.debug("Model add Started");
		System.out.println("add started");

		Connection conn = null;

		EmployeeBean duplicateName = findByName(bean.getFullName());
		int pk = nextPK();
		if (duplicateName != null) {
			throw new DuplicateRecordException("Name already exists");
		}

		try {
			conn = JDBCDataSource.getConnection();
			pk = nextPK();

			System.out.println(pk + " in ModelJDBC");
			conn.setAutoCommit(false); // Begin transaction
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ST_EMPLOYEE VALUES(?,?,?,?,?,?,?,?,?,?)");
			pstmt.setInt(1, pk);
			pstmt.setString(2, bean.getFullName());
			pstmt.setString(3, bean.getUserName());
			pstmt.setString(4, bean.getPassword());
			
			pstmt.setDate(5, new java.sql.Date(bean.getBirthDate().getTime()));
			System.out.println("employee model");
			pstmt.setString(6, bean.getNumber());
			pstmt.setString(7, bean.getCreatedBy());
			pstmt.setString(8, bean.getModifiedBy());
			pstmt.setTimestamp(9, bean.getCreatedDatetime());
			pstmt.setTimestamp(10, bean.getModifiedDatetime());
			int i = pstmt.executeUpdate();

			conn.commit(); // End transaction
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Database Exception..", e);
			try {
				conn.rollback();

			} catch (Exception ex) {
				ex.printStackTrace();
				throw new ApplicationException("Exception : add rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception : Exception in add Student");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model add End");
		return pk;
	}

	public void delete(EmployeeBean bean) throws ApplicationException {

		log.debug("Model delete Started");
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement pstmt = conn.prepareStatement("DELETE FROM st_employee WHERE ID=?");
			pstmt.setLong(1, bean.getId());
			pstmt.executeUpdate();
			conn.commit();
			pstmt.close();
		} catch (Exception e) {
			log.error("Database Exception.." + e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				throw new ApplicationException("Exception : delete rollback exception  " + ex.getMessage());
			}
			throw new ApplicationException("Exception : Exception in delete Student");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model delete End");
	}

	public EmployeeBean findByName(String name) throws ApplicationException {
		log.debug("Model findBy Name Started");
		StringBuffer sql = new StringBuffer("SELECT * FROM st_employee WHERE name=?");
		EmployeeBean bean = null;
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new EmployeeBean();
				bean.setId(rs.getLong(1));
				bean.setFullName(rs.getString(2));
				bean.setUserName(rs.getString(3));
				bean.setPassword(rs.getString(4));
				bean.setBirthDate(rs.getDate(5));
				bean.setNumber(rs.getString(6));
				bean.setCreatedBy(rs.getString(7));
				bean.setModifiedBy(rs.getString(8));
				bean.setCreatedDatetime(rs.getTimestamp(9));
				bean.setModifiedDatetime(rs.getTimestamp(10));

			}
			rs.close();
		} catch (Exception e) {
			log.error("Database Exception..", e);
			// throw new ApplicationException("Exception : Exception in getting User by
			// Email");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model findBy Name End");
		return bean;
	}

	public EmployeeBean findByPK(long pk) throws ApplicationException {
		log.debug("Model findByPK Started");
		StringBuffer sql = new StringBuffer("SELECT * FROM st_employee WHERE ID=?");
		EmployeeBean bean = null;
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, pk);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new EmployeeBean();
				bean.setId(rs.getLong(1));
				bean.setFullName(rs.getString(2));
				bean.setUserName(rs.getString(3));
				bean.setPassword(rs.getString(4));
				bean.setBirthDate(rs.getDate(5));
				bean.setNumber(rs.getString(6));
				bean.setCreatedBy(rs.getString(7));
				bean.setModifiedBy(rs.getString(8));
				bean.setCreatedDatetime(rs.getTimestamp(9));
				bean.setModifiedDatetime(rs.getTimestamp(10));

			}
			rs.close();
		} catch (Exception e) {
			log.error("Database Exception..", e);
			throw new ApplicationException("Exception : Exception in getting User by pk");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model findByPK End");
		return bean;
	}

	public void Update(EmployeeBean bean) throws ApplicationException, DuplicateRecordException {
		log.debug("Model Update Started");
		Connection conn = null;
		EmployeeBean beanExist = findByName(bean.getFullName());

		if (beanExist != null && beanExist.getId() != bean.getId()) {
			throw new DuplicateRecordException("Email Id is already exist");

		}

		try {
			conn = JDBCDataSource.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement pstmt = conn.prepareStatement(
					"UPDATE st_employee SET NAME=?,CATEGORY=?,SUPLIER_DATE=?,PAYMENT=?,CREATED_BY=?,MODIFIED_BY=?,CREATED_DATETIME=?,MODIFIED_DATETIME=? WHERE ID=?");

			pstmt.setString(1, bean.getFullName());
			pstmt.setString(2, bean.getUserName());
			pstmt.setString(3, bean.getPassword());
			pstmt.setDate(4, new java.sql.Date(bean.getBirthDate().getTime()));
			pstmt.setString(5, bean.getNumber());
			pstmt.setString(6, bean.getCreatedBy());
			pstmt.setString(7, bean.getModifiedBy());
			pstmt.setTimestamp(8, bean.getCreatedDatetime());
			pstmt.setTimestamp(9, bean.getModifiedDatetime());
			pstmt.setLong(10, bean.getId());

			pstmt.executeUpdate();
			conn.commit();
			pstmt.close();

		} catch (Exception e) {
			log.error("Database Exception....", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				// throw new ApplicationException("Exception : Delete rollback
				// exception"+ex.getMessage());
			}
			// throw new ApplicationException("Exception in updating Student");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model update End");
	}

	public List search(EmployeeBean bean) throws ApplicationException {
		return search(bean, 0, 0);
	}

	public List search(EmployeeBean bean, int pageNo, int pageSize) throws ApplicationException {
		log.debug("Model search Started");
		StringBuffer sql = new StringBuffer("SELECT * FROM st_employee WHERE 1=1");

		if (bean != null) {
			if (bean.getId() > 0) {
				sql.append(" AND id = " + bean.getId());
			}
			if (bean.getFullName() != null && bean.getFullName().length() > 0) {
				sql.append(" AND name like '" + bean.getFullName() + "%'");
			}

			if (bean.getBirthDate() != null && bean.getBirthDate().getDate() > 0) {
				sql.append(" AND supplier_date = " + bean.getBirthDate());
			}

		}

		if (pageSize > 0) {

			pageNo = (pageNo - 1) * pageSize;

			sql.append(" Limit " + pageNo + ", " + pageSize);
			// sql.append(" limit " + pageNo + "," + pageSize);
		}

		ArrayList list = new ArrayList();
		Connection conn = null;
		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bean = new EmployeeBean();
				bean.setId(rs.getLong(1));
				bean.setFullName(rs.getString(2));
				bean.setUserName(rs.getString(3));
				bean.setPassword(rs.getString(4));
				bean.setBirthDate(rs.getDate(5));
				bean.setNumber(rs.getString(6));
				bean.setCreatedBy(rs.getString(7));
				bean.setModifiedBy(rs.getString(8));
				bean.setCreatedDatetime(rs.getTimestamp(9));
				bean.setModifiedDatetime(rs.getTimestamp(10));
				list.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			log.error("Database Exception..", e);
			throw new ApplicationException("Exception : Exception in search Student");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}

		log.debug("Model search End");
		return list;
	}

	public List list() throws ApplicationException {
		return list(0, 0);
	}

	public List list(int pageNo, int pageSize) throws ApplicationException {
		log.debug("Model list Started");
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer("select * from st_employee");

		if (pageSize > 0) {
			pageNo = (pageNo - 1) * pageSize;
			sql.append(" limit " + pageNo + "," + pageSize);

		}

		Connection conn = null;

		try {
			conn = JDBCDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				EmployeeBean bean = new EmployeeBean();
				bean.setId(rs.getLong(1));
				bean.setFullName(rs.getString(2));
				bean.setUserName(rs.getString(3));
				bean.setPassword(rs.getString(4));
				bean.setBirthDate(rs.getDate(5));
				bean.setNumber(rs.getString(6));
				bean.setCreatedBy(rs.getString(7));
				bean.setModifiedBy(rs.getString(8));
				bean.setCreatedDatetime(rs.getTimestamp(9));
				bean.setModifiedDatetime(rs.getTimestamp(10));
				list.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			log.error("Database Exception...", e);
			throw new ApplicationException("Exception : Exception in getting list of Student");
		} finally {
			JDBCDataSource.closeConnection(conn);
		}
		log.debug("Model list End");
		return list;
	}

}
