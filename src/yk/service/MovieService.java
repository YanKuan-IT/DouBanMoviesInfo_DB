package yk.service;

import java.util.List;
import java.util.Map;

import yk.dao.MovieDao;
import yk.entity.Movie;

public class MovieService {
	
	MovieDao movieDao = new MovieDao();
	
	public void save(Movie movie){
		movieDao.save(movie);
	}

	public void save(List<Movie> listMovie){
		movieDao.save(listMovie);
	}

	public List<Movie> findAll(){
		return movieDao.findAll();
	}
	
	public Movie findById(int id) {
		return movieDao.findById(id);
	}
	
	public Map<String,Integer> Count(){
		return movieDao.Count();
	}
	
	public Map<String,Integer> lineChart(){
		return movieDao.lineChart();
	}
	
}
