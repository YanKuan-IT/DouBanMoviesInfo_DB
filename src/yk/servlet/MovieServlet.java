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
			 * ��ȡ���ݲ����浽���ݿ�
			 */
			try {
				getData();
			} catch (Exception e) {
				System.out.println("error");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			long endTime = System.currentTimeMillis();
			System.out.println("����ʱ��"+((endTime-startTime)/1000)+"s");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}
		
		// �رյ�ǰCacheManager����
        manager.shutdown();
        // �ر�CacheManager����ʵ��
        CacheManager.getInstance().shutdown();
	}
	
	private void getData(){
		
		HashMap<String, String> urlandnames = new HashMap<String, String>();
		MovieService movieService = new MovieService();
		// ���а�ҳ��
		String url = "http://movie.douban.com/chart";
		// ��ȡ���������������Ӻͷ�������
		try {
			Document kinds = Jsoup.connect(url)
							  .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36")
							  .timeout(10000)
							  .get();
			Elements elements = kinds.select("#content .types a");
			for(Element element : elements){
				String kindurl = element.attr("href");	// ���ӵ�ַ
				String name = element.text();			// ���
				urlandnames.put(kindurl,name);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("��ȡurlandname���ִ��󣡣�");
		}
		//��ȡ���е�key
		Set<String> keySet = urlandnames.keySet();	
		//����keyֵ
		Iterator<String> iterator = keySet.iterator();
		List<Movie> allMovies = new ArrayList<Movie>();
		while(iterator.hasNext()){
			// ��ȡ��keyֵ,��url
			String next = iterator.next();
			// ����ĳһ���������ӣ���ȡ�ж�Ӧ�ĵ�Ӱ����
			List<Movie> listMovie = getMovieInfo(next);
			allMovies.addAll(listMovie);
		}
		int size = allMovies.size();
		System.out.println("����"+size+"�����ݡ�����������������������");
		try {
			if(allMovies.size()!=0)
				movieService.save(allMovies);
		} catch (Exception e) {
			System.out.println("�������ݳ��ִ��� MovieServlet");
			e.printStackTrace();
		}
	}
	/**
	 * ��ȡ�����Ӱ��Ϣ�����浽���ݿ�
	 * @param url ĳһ����������ӵ�ַ
	 */
	private List<Movie> getMovieInfo(String url){
		String[] tempurl = url.split("&");
		String finalurl = "http://movie.douban.com/j/chart/top_list_count?"+tempurl[1]+"&"+tempurl[2];
		// finalurl ---------http://movie.douban.com/j/chart/top_list_count?type=18&interval_id=100:90
		String document = null;
		try {
			//��ȡ�����ӰƬ������total�������߹ۿ�����playable_count
			document = Jsoup.connect(finalurl).timeout(10000).ignoreContentType(true).execute().body();	
			// document------{"playable_count":18,"total":32,"unwatched_count":32}�����߹ۿ�18������32����δ�ۿ�32��
		} catch (IOException e) {
			e.printStackTrace();
		}

		//json������
		JsonParser parser = new JsonParser();
		//��ȡjson����
		JsonObject jsonObject = (JsonObject) parser.parse(document);
		//��json����תΪint������
		int movienum = jsonObject.get("total").getAsInt();
		System.out.println(movienum);//�����͵�����
		String nameurl = "http://movie.douban.com/j/chart/top_list?"+tempurl[1]+"&"+tempurl[2]+"&action=&start=0&limit="+movienum;
		// nameurl-------------http://movie.douban.com/j/chart/top_list?type=18&interval_id=100:90&action=&start=0&limit=32
		FileWriter fw = null;
		String doc = null;
		try {
			//��ȡ����������ӰƬ����Ϣ
			doc = Jsoup.connect(nameurl).timeout(10000).ignoreContentType(true).execute().body();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//��json��һ���������������JsonElement����
		JsonElement element = null;
		try {
			//ͨ��JsonParser������԰�json��ʽ���ַ���������һ��JsonElement����
			element = parser.parse(doc);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		JsonArray jsonArray = null;
		if(element.isJsonArray()){
			//JsonElement���������һ������Ļ�ת����jsonArray
			jsonArray = element.getAsJsonArray();
		}
		
		//����json�Ķ�������
		Iterator it = jsonArray.iterator();
		List<Movie> listMovie = new ArrayList<Movie>();
		while (it.hasNext()) {
			JsonObject e = (JsonObject)it.next();
			//��Ӱ����
			String name = e.get("title").getAsString();
			//��������
			float score = e.get("score").getAsFloat();
			//����ʱ��
			String release_date = e.get("release_date").getAsString();
			//����
			JsonArray jsonArray2 = e.get("types").getAsJsonArray();
			String types = jsonArray2.toString();
			//���ӵ�ַ
			String movieUrl = e.get("url").getAsString();
			//�Ƿ�������߲���
			String is_playable = e.get("is_playable").getAsString();
			
			String substring = movieUrl.substring(0, movieUrl.lastIndexOf("/"));
			String keyID = substring.substring(substring.lastIndexOf("/"), substring.length());
			
			if(cache.get(keyID) != null){
				String value = (String) cache.get(keyID).getObjectValue();
				if(!name.equals(value)){
					net.sf.ehcache.Element element2 = new net.sf.ehcache.Element(keyID,name);
					cache.put(element2);
				}else {
//					System.out.println("�ظ��� movie Info");
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

			//�ڿ���̨���
//			System.out.println(movie.toString());
//			System.out.println("���ڻ�ȡ����ing...");
			
			listMovie.add(movie);
		}
		return listMovie;
	}
	
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
