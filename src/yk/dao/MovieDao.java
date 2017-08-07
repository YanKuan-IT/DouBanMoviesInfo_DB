package yk.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import yk.entity.Movie;
import yk.util.JdbcUtils;

public class MovieDao {
	
	public void save(Movie movie){
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = JdbcUtils.getConnection();
			
			String sql = " INSERT INTO movie(NAME,TYPES,release_date,score,movieUrl,is_playable) VALUE( ?,?,?,?,?,? ) ";
			
			statement = connection.prepareStatement(sql);
			
			statement.setString(1, movie.getName());
			statement.setString(2, movie.getTypes());
			statement.setString(3, movie.getRelease_date());
			statement.setFloat(4, movie.getScore());
			statement.setString(5, movie.getMovieUrl());
			statement.setString(6, movie.getIs_playable());
			
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				connection.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	public void save(List<Movie> listMovie){
		Connection connection = null;
		PreparedStatement statement = null;
		
		connection = JdbcUtils.getConnection();

		try {
			int i = 1;
			for(Movie movie : listMovie){
				System.out.println("正在插入第"+(i++)+"条数据到数据库ing...");
				String sql = " INSERT INTO movie(NAME,TYPES,release_date,score,movieUrl,is_playable) VALUE( ?,?,?,?,?,? ) ";
				
				statement = connection.prepareStatement(sql);
				
				statement.setString(1, movie.getName());
				statement.setString(2, movie.getTypes());
				statement.setString(3, movie.getRelease_date());
				statement.setFloat(4, movie.getScore());
				statement.setString(5, movie.getMovieUrl());
				statement.setString(6, movie.getIs_playable());
				
				statement.execute();
			}
			System.out.println("保存数据完成");
		} catch (SQLException e) {
			System.out.println("保存数据出现错误 MovieDao error");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				connection.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	public List<Movie> findAll(){
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try {
			connection = JdbcUtils.getConnection();
			
			String sql = " select * from movie ";
			
			statement = connection.prepareStatement(sql);
			
			resultSet = statement.executeQuery();

			List<Movie> list = new ArrayList<Movie>();
			while (resultSet.next()) {
				Movie movie = new Movie();
				
				movie.setId(resultSet.getInt("id"));
				movie.setName(resultSet.getString("name"));
				movie.setTypes(resultSet.getString("types"));
				movie.setRelease_date(resultSet.getString("release_date"));
				movie.setScore(resultSet.getFloat("score"));
				movie.setMovieUrl(resultSet.getString("movieUrl"));
				movie.setIs_playable(resultSet.getString("is_playable"));
				
				list.add(movie);
			}
			
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				connection.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	public Movie findById(int id) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		
		try {
			conn = JdbcUtils.getConnection();
			String sql = "select * from movie where id=? ";
			stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, id);
			
			resultSet = stmt.executeQuery();
			Movie movie = new Movie();
			while(resultSet.next()){
				movie.setId(resultSet.getInt("id"));
				movie.setName(resultSet.getString("name"));
				movie.setTypes(resultSet.getString("types"));
				movie.setRelease_date(resultSet.getString("release_date"));
				movie.setScore(resultSet.getFloat("score"));
				movie.setMovieUrl(resultSet.getString("movieUrl"));
				movie.setIs_playable(resultSet.getString("is_playable"));
			}
			return movie;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			try {
				resultSet.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	
	
}
