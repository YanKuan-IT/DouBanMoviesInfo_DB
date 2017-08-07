package yk.servlet;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import yk.entity.Movie;
import yk.service.MovieService;


@WebServlet("/MovieServlet")
public class MovieServlet extends HttpServlet {
	CacheManager manager;
	Cache cache;
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path = request.getSession().getServletContext().getRealPath("/");
		path = path + "WEB-INF/classes/ehcache.xml"; 
		manager = CacheManager.create(path);
		
		cache = manager.getCache("a");
		
		MovieService movieService = new MovieService();
		
		String method = request.getParameter("method");
		if(method.equals("listAll")){
			List<Movie> allMovies = movieService.findAll();
			request.setAttribute("allMovies", allMovies);
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}else if(method.equals("getData")){
			long startTime = System.currentTimeMillis();
			/**
			 * 获取数据并保存到数据库
			 */
			try {
				getData();
			} catch (Exception e) {
				System.out.println("error");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			long endTime = System.currentTimeMillis();
			System.out.println("共用时："+((endTime-startTime)/1000)+"s");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}
		
		// 关闭当前CacheManager对象
        manager.shutdown();
        // 关闭CacheManager单例实例
        CacheManager.getInstance().shutdown();
	}
	
	private void getData(){
		
		HashMap<String, String> urlandnames = new HashMap<String, String>();
		MovieService movieService = new MovieService();
		// 排行榜页面
		String url = "http://movie.douban.com/chart";
		// 获取分类的所有相对链接和分类名称
		try {
			Document kinds = Jsoup.connect(url)
							  .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36")
							  .timeout(10000)
							  .get();
			Elements elements = kinds.select("#content .types a");
			for(Element element : elements){
				String kindurl = element.attr("href");	// 链接地址
				String name = element.text();			// 类别
				urlandnames.put(kindurl,name);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("获取urlandname出现错误！！");
		}
		//获取所有的key
		Set<String> keySet = urlandnames.keySet();	
		//迭代key值
		Iterator<String> iterator = keySet.iterator();
		List<Movie> allMovies = new ArrayList<Movie>();
		while(iterator.hasNext()){
			// 获取到key值,即url
			String next = iterator.next();
			// 根据某一个类别的链接，获取行对应的电影数据
			List<Movie> listMovie = getMovieInfo(next);
			allMovies.addAll(listMovie);
		}
		int size = allMovies.size();
		System.out.println("共："+size+"条数据············");
		try {
			if(allMovies.size()!=0)
				movieService.save(allMovies);
		} catch (Exception e) {
			System.out.println("保存数据出现错误！ MovieServlet");
			e.printStackTrace();
		}
	}
	/**
	 * 获取种类电影信息，保存到数据库
	 * @param url 某一个种类的链接地址
	 */
	private List<Movie> getMovieInfo(String url){
		String[] tempurl = url.split("&");
		String finalurl = "http://movie.douban.com/j/chart/top_list_count?"+tempurl[1]+"&"+tempurl[2];
		// finalurl ---------http://movie.douban.com/j/chart/top_list_count?type=18&interval_id=100:90
		String document = null;
		try {
			//获取该类别影片的数量total、可在线观看数量playable_count
			document = Jsoup.connect(finalurl).timeout(10000).ignoreContentType(true).execute().body();	
			// document------{"playable_count":18,"total":32,"unwatched_count":32}可在线观看18部，共32部，未观看32部
		} catch (IOException e) {
			e.printStackTrace();
		}

		//json解析器
		JsonParser parser = new JsonParser();
		//获取json对象
		JsonObject jsonObject = (JsonObject) parser.parse(document);
		//将json数据转为int型数据
		int movienum = jsonObject.get("total").getAsInt();
		System.out.println(movienum);//该类型的数量
		String nameurl = "http://movie.douban.com/j/chart/top_list?"+tempurl[1]+"&"+tempurl[2]+"&action=&start=0&limit="+movienum;
		// nameurl-------------http://movie.douban.com/j/chart/top_list?type=18&interval_id=100:90&action=&start=0&limit=32
		FileWriter fw = null;
		String doc = null;
		try {
			//获取该类别的所有影片的信息
			doc = Jsoup.connect(nameurl).timeout(10000).ignoreContentType(true).execute().body();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//将json的一个对象数组解析成JsonElement对象
		JsonElement element = null;
		try {
			//通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
			element = parser.parse(doc);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		JsonArray jsonArray = null;
		if(element.isJsonArray()){
			//JsonElement对象如果是一个数组的话转化成jsonArray
			jsonArray = element.getAsJsonArray();
		}
		
		//遍历json的对象数组
		Iterator it = jsonArray.iterator();
		List<Movie> listMovie = new ArrayList<Movie>();
		while (it.hasNext()) {
			JsonObject e = (JsonObject)it.next();
			//电影名称
			String name = e.get("title").getAsString();
			//豆瓣评分
			float score = e.get("score").getAsFloat();
			//发布时间
			String release_date = e.get("release_date").getAsString();
			//类型
			JsonArray jsonArray2 = e.get("types").getAsJsonArray();
			String types = jsonArray2.toString();
			//链接地址
			String movieUrl = e.get("url").getAsString();
			//是否可以在线播放
			String is_playable = e.get("is_playable").getAsString();
			
			String substring = movieUrl.substring(0, movieUrl.lastIndexOf("/"));
			String keyID = substring.substring(substring.lastIndexOf("/"), substring.length());
			
			if(cache.get(keyID) != null){
				String value = (String) cache.get(keyID).getObjectValue();
				if(!name.equals(value)){
					net.sf.ehcache.Element element2 = new net.sf.ehcache.Element(keyID,name);
					cache.put(element2);
				}else {
//					System.out.println("重复的 movie Info");
					continue;
				}
			}else {
				net.sf.ehcache.Element element2 = new net.sf.ehcache.Element(keyID,name);
				cache.put(element2);
			}
			
			Movie movie = new Movie();
			
			movie.setName(name);
			movie.setTypes(types);
			movie.setRelease_date(release_date);
			movie.setScore(score);
			movie.setMovieUrl(movieUrl);
			movie.setIs_playable(is_playable);

			//在控制台输出
//			System.out.println(movie.toString());
//			System.out.println("正在获取数据ing...");
			
			listMovie.add(movie);
		}
		return listMovie;
	}
	
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
