package com.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.sql.DataSource;

public class StudentDBUtil {
	private DataSource datasource;
	public StudentDBUtil(DataSource theDataSource){
		datasource=theDataSource;
	}
	public List<Student> getStudents() throws Exception{
		List<Student> students=new ArrayList<>();
		Connection myConn=null;
		Statement myStmt=null;
		ResultSet rslt=null;
		
		try {
			//get a connection
			myConn=datasource.getConnection();
			//create a SQL statement
			String sql="select * from student order by last_name";
			myStmt=myConn.createStatement();
			//execute query
			rslt=myStmt.executeQuery(sql);
			//process the result set
			while(rslt.next()) {
				//retrieve data from result set row
				int id=rslt.getInt("id");
				String firstName=rslt.getString("first_name");
				String lastName=rslt.getString("last_name");
				String email=rslt.getString("email");
				//create new student object
				Student tempStudent=new Student(id, firstName, lastName, email);
				//add it to list of students
				students.add(tempStudent);
			}
			return students;	
		}
		finally {
			//close the JDBC object
			close(myConn, myStmt, rslt);
		}
	}
	private void close(Connection myConn, Statement myStmt, ResultSet rslt) {
		try {
			if(rslt!=null) {
				rslt.close();
			}
			if(myStmt!=null) {
				myStmt.close();
			}
			if(myConn!=null) {
				myConn.close(); //doesn't really close the connection, just puts it back in connection pool
			}
				
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	public void addStudent(Student theStudent) throws Exception {
		Connection myConn=null;
		PreparedStatement myStmt=null;
		try {
			//get db connection
			myConn=datasource.getConnection();
			//create sql for insert
			String sql="insert into student "
					+ "(first_name, last_name, email)"
					+ "values(?, ?, ?)";
			myStmt=myConn.prepareStatement(sql);
			//set the param values for the student
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			
			//execute sql insert
			myStmt.execute();
		}
		finally {
			//clean up JDBC objects
			close(myConn, myStmt, null);
		}
		
	}
	public Student getStudent(String theStudentId) throws Exception {
		Student theStudent=null;
		Connection myConn=null;
		PreparedStatement myStmt=null;
		ResultSet myRs=null;
		int studentId;
		
		try {
			//convert student id to int
			studentId=Integer.parseInt(theStudentId);
			//get connection to database
			myConn=datasource.getConnection();
			
			//create sql to get selected student
			String sql="select * from student where id=?";
			//create prepared statement
			myStmt=myConn.prepareStatement(sql);
			//set params
			myStmt.setInt(1, studentId);
			//execute statement
			myRs=myStmt.executeQuery();
			//retrieve data from result set row
			if(myRs.next()) {
				String firstName=myRs.getString("first_name");
				String lastName=myRs.getString("last_name");
				String email=myRs.getString("email");

				//use the studentId during construction
				theStudent=new Student(studentId, firstName, lastName, email);
			}
			else {
				throw new Exception("Could not find student id: " + studentId);
			}
		}
		finally {
			//clean up JDBC objects
			close(myConn, myStmt, myRs);
		}
		return theStudent;
	}
	public void updateStudent(Student theStudent) throws Exception {
		Connection myConn=null;
		PreparedStatement myStmt=null;
		
		try {
			//get db connection
			myConn=datasource.getConnection();
			//create SQL update statement
			String sql="update student "
					+ "set first_name=?, last_name=?, email=? "
					+"where id=?";
			//prepare statement
			myStmt=myConn.prepareStatement(sql);
			
			//set params
			myStmt.setString(1,theStudent.getFirstName());
			myStmt.setString(2,theStudent.getLastName());
			myStmt.setString(3,theStudent.getEmail());
			myStmt.setInt(4,theStudent.getId());
			
			//execute SQL statement
			myStmt.execute();
		}
		finally {
			//clean up JDBC objects
			close(myConn, myStmt, null);
		}
	}
	public void deleteStudent(String theStudentId)throws Exception {
		Connection myConn=null;
		PreparedStatement myStmt=null;
		try {
			//convert student id to int
			int studentId=Integer.parseInt(theStudentId);
			//get connection to database
			myConn=datasource.getConnection();
			//create sql to delete student
			String sql="delete from student where id=?";
			//prepare statement
			myStmt=myConn.prepareStatement(sql);
			//set params
			myStmt.setInt(1, studentId);
			//execute sql statement
			myStmt.execute();
		}
		finally {
			//clean up JDBC objects
			close(myConn, myStmt, null);
		}
	}
}
