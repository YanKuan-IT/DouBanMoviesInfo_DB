package yk.servlet;

import java.awt.Color;
import java.awt.Font;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;
import org.jfree.util.Rotation;

import yk.service.MovieService;

@WebServlet("/ScoreServlet")
public class ScoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		System.out.println(method+"===================method");
		MovieService movieService = new MovieService();
		
		Map<String, Integer> map = movieService.Count();
		Integer one = map.get("one");
		Integer two = map.get("two");
		Integer three = map.get("three");
		Integer four = map.get("four");
		Integer five = map.get("five");
		
		if(method.equals("barChart")){
			double [][]data = new double[][]{{one},{two},{three},{four},{five}};
			String []rowKeys = {">=9",">=8.5",">=8",">=7.5","<7.5"}; 
			String []columnKeys = {"评分"};
			
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys, data);
			
	        JFreeChart chart = ChartFactory.createBarChart3D(
	        		"电影评分柱状图", // 图表标题
	                "电影", // 目录轴的显示标签
	                "数量", // 数值轴的显示标签
	                 dataset, // 数据集
	                 PlotOrientation.VERTICAL, // 图表方向：水平、垂直
	                 true,  // 是否显示图例(对于简单的柱状图必须是 false)
	                 false, // 是否创建工具提示 (tooltip) 
	                 false  // 是否生成 URL 链接
	                 ); 
	        
	        CategoryPlot plot = chart.getCategoryPlot();
	        // 设置网格背景颜色
	 		plot.setBackgroundPaint(Color.white);
	 		// 设置网格竖线颜色
	 		plot.setDomainGridlinePaint(Color.pink);
	 		// 设置网格横线颜色
	 		plot.setRangeGridlinePaint(Color.pink);
	 		
	 		// 显示每个柱的数值，并修改该数值的字体属性
	 		BarRenderer3D renderer=new BarRenderer3D();
	 		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
	 		renderer.setBaseItemLabelsVisible(true);
	 		
	 		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
	 		renderer.setItemLabelAnchorOffset(10D);  
	 		
	 		// 设置平行柱的之间距离
	 		renderer.setItemMargin(0.4);
	 		plot.setRenderer(renderer);
	        
	        FileOutputStream fos_jpg = null; 
	        try { 
	        	//将图片保存至Tomcat服务器WebRoot下的img目录中
	            fos_jpg = new FileOutputStream(request.getSession().getServletContext().getRealPath("/")+"barChart.jpg");
	            ChartUtilities.writeChartAsJPEG(fos_jpg,1,chart,700,500,null); 
	        } catch (Exception e) {
	        	System.out.println("error");
			} finally { 
	            try { 
	                fos_jpg.close(); 
	            } catch (Exception e) {
	            	System.out.println("error2");
	            } 
	        }
	        request.setAttribute("barChart", "barChart.jpg");
			
		}else if (method.equals("pieChart")) {
			
			DefaultPieDataset data = new DefaultPieDataset();
			data.setValue(">=9",one); 
			data.setValue(">=8.5",two); 
			data.setValue(">=8",three); 
			data.setValue(">=7.5",four); 
			data.setValue("<7.5",five); 
	        
	        JFreeChart chart = ChartFactory.createPieChart3D(
	        		"评分饼状图",  		// 图表标题
			        data, 
			        true, 			// 是否显示图例
			        false, 			// 是否创建工具提示 (tooltip) 
	                false  			// 是否生成 URL 链接
			        ); 
	        
			//显示百分比
			PiePlot pieplot = (PiePlot)chart.getPlot();
	        pieplot.setLabelFont(new Font("宋体", 0, 12));
	        pieplot.setNoDataMessage("无数据");
	        pieplot.setCircular(true);
	        pieplot.setLabelGap(0.02D);
	        pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} {2}",NumberFormat.getNumberInstance(),new DecimalFormat("0.00%")));
	        
	        PiePlot3D pieplot3d = (PiePlot3D)chart.getPlot(); 
			//设置开始角度  
			pieplot3d.setStartAngle(120D);  
			//设置方向为”顺时针方向“  
			pieplot3d.setDirection(Rotation.CLOCKWISE);  
			//设置透明度，0.5F为半透明，1为不透明，0为全透明  
			pieplot3d.setForegroundAlpha(0.7F); 
	        
	        FileOutputStream fos_jpg = null; 
	        try { 
	        	//将图片保存至Tomcat服务器WebRoot目录下
	            fos_jpg = new FileOutputStream(request.getSession().getServletContext().getRealPath("/")+"pieChart.jpg");
	            ChartUtilities.writeChartAsJPEG(fos_jpg,1,chart,700,500,null); 
	        } catch (Exception e) {
	        	System.out.println("error");
			} finally { 
	            try { 
	                fos_jpg.close(); 
	            } catch (Exception e) {
	            	System.out.println("error2");
	            } 
	        }
	        request.setAttribute("pieChart", "pieChart.jpg");
			
		}else if (method.equals("lineChart")) {
			XYSeriesCollection collection = new XYSeriesCollection();
			XYSeries series = new XYSeries("折线");
			
			Map<String, Integer> map2 = movieService.lineChart();
			int number = 99;
			for(int i=0; i<map2.size(); i++){
				String s= number+"";
				String score = s.charAt(0)+"."+s.charAt(1);
				series.add(Double.parseDouble(score),map2.get(score));
//				System.out.println(Double.parseDouble(score)+"--"+map2.get(score));
				number--;
			}
			collection.addSeries(series);
			
			JFreeChart chart = ChartFactory.createXYLineChart(
				        "评分折线图",
				        "评分",
				        "数量",				
				        collection,
				        PlotOrientation.VERTICAL,
				        true, 
				        true, 
				        false);
			
			XYPlot plot = (XYPlot) chart.getPlot(); 
			//设置曲线是否显示数据点
			XYLineAndShapeRenderer xylinerenderer = (XYLineAndShapeRenderer)plot.getRenderer();
			xylinerenderer.setBaseShapesVisible(true); 
			
			//设置曲线显示各数据点的值
			XYItemRenderer xyitem = plot.getRenderer(); 
			xyitem.setBaseItemLabelsVisible(true);
			xyitem.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER)); 
			xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
			xyitem.setBaseItemLabelFont(new Font("Dialog", 1, 12)); 
			plot.setRenderer(xyitem);
			
			FileOutputStream fos_jpg = null; 
	        try { 
	        	//将图片保存至Tomcat服务器WebRoot目录下
	            fos_jpg = new FileOutputStream(request.getSession().getServletContext().getRealPath("/")+"lineChart.jpg");
	            ChartUtilities.writeChartAsJPEG(fos_jpg,1,chart,700,500,null); 
	        } catch (Exception e) {
	        	System.out.println("error");
			} finally { 
	            try { 
	                fos_jpg.close(); 
	            } catch (Exception e) {
	            	System.out.println("error2");
	            } 
	        }
	        request.setAttribute("lineChart", "lineChart.jpg");
		}
		response.sendRedirect(request.getContextPath()+"/Score.jsp");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
