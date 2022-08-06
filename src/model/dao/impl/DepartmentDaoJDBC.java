package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao{

	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement( // Inserir o codigo SQL
					"INSERT INTO department " +
					"(Name) " +
					"VALUES " +
					"(?)", 
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName()); //Setando dados no Placeholder
			int rowsAffected = st.executeUpdate(); // Executando a tarefa e armazendo o resultado na variavel rowsAffected
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(null);
				}
			} else {
				throw new DbException("Unexpected Error! No rows Affected.");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department obj) {
		
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement(
					"UPDATE department " +
					"SET Name = ? " +
					"WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			st.executeUpdate();
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			
			st.setInt(1, id);
			st.executeUpdate();
			
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Department findById(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT * FROM department WHERE Id = ?"); //Codigo SQL
			
			st.setInt(1, id); // Setando dados no lugar do placeholder
			rs = st.executeQuery(); //O resultado da query é armazenado no rs
			
			if(rs.next()) { //verificando se o rs não está vazio
				Department dep = new Department(); //Instanciando um department para receber o resultado da minha Query
				dep.setId(rs.getInt("Id")); //Setando os valores dos atributos da classe Department, através do GET na Query(rs)
				dep.setName(rs.getString("Name"));
				return dep;
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Department> findAll() {

		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT * FROM department ORDER BY Name");
			rs = st.executeQuery();
			
			List<Department> list = new ArrayList<>(); //Lista criada para armazenar todos valores do FindAll
			
			while(rs.next()) {
				Department dep = new Department();
				dep.setId(rs.getInt("Id"));
				dep.setName(rs.getString("Name"));
				list.add(dep);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}		
	}
}
