package yk.entity;

public class Movie {
	
	private Integer id;
	private String name;
	private String types;
	private String release_date;
	private float score;
	private String movieUrl;
	private String is_playable;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public String getRelease_date() {
		return release_date;
	}
	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public String getMovieUrl() {
		return movieUrl;
	}
	public void setMovieUrl(String movieUrl) {
		this.movieUrl = movieUrl;
	}
	public String getIs_playable() {
		return is_playable;
	}
	public void setIs_playable(String is_playable) {
		this.is_playable = is_playable;
	}
	@Override
	public String toString() {
		return "Movie [id=" + id + ", name=" + name + ", types=" + types + ", release_date=" + release_date + ", score="
				+ score + ", movieUrl=" + movieUrl + ", is_playable=" + is_playable + "]";
	}
	
	
	
}
