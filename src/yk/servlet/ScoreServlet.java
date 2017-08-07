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
			String []columnKeys = {"����"};
			
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys, data);
			
	        JFreeChart chart = ChartFactory.createBarChart3D(
	        		"��Ӱ������״ͼ", // ͼ�����
	                "��Ӱ", // Ŀ¼�����ʾ��ǩ
	                "����", // ��ֵ�����ʾ��ǩ
	                 dataset, // ���ݼ�
	                 PlotOrientation.VERTICAL, // ͼ����ˮƽ����ֱ
	                 true,  // �Ƿ���ʾͼ��(���ڼ򵥵���״ͼ������ false)
	                 false, // �Ƿ񴴽�������ʾ (tooltip) 
	                 false  // �Ƿ����� URL ����
	                 ); 
	        
	        CategoryPlot plot = chart.getCategoryPlot();
	        // �������񱳾���ɫ
	 		plot.setBackgroundPaint(Color.white);
	 		// ��������������ɫ
	 		plot.setDomainGridlinePaint(Color.pink);
	 		// �������������ɫ
	 		plot.setRangeGridlinePaint(Color.pink);
	 		
	 		// ��ʾÿ��������ֵ�����޸ĸ���ֵ����������
	 		BarRenderer3D renderer=new BarRenderer3D();
	 		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
	 		renderer.setBaseItemLabelsVisible(true);
	 		
	 		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
	 		renderer.setItemLabelAnchorOffset(10D);  
	 		
	 		// ����ƽ������֮�����
	 		renderer.setItemMargin(0.4);
	 		plot.setRenderer(renderer);
	        
	        FileOutputStream fos_jpg = null; 
	        try { 
	        	//��ͼƬ������Tomcat������WebRoot�µ�imgĿ¼��
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
	        		"���ֱ�״ͼ",  		// ͼ�����
			        data, 
			        true, 			// �Ƿ���ʾͼ��
			        false, 			// �Ƿ񴴽�������ʾ (tooltip) 
	                false  			// �Ƿ����� URL ����
			        ); 
	        
			//��ʾ�ٷֱ�
			PiePlot pieplot = (PiePlot)chart.getPlot();
	        pieplot.setLabelFont(new Font("����", 0, 12));
	        pieplot.setNoDataMessage("������");
	        pieplot.setCircular(true);
	        pieplot.setLabelGap(0.02D);
	        pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} {2}",NumberFormat.getNumberInstance(),new DecimalFormat("0.00%")));
	        
	        PiePlot3D pieplot3d = (PiePlot3D)chart.getPlot(); 
			//���ÿ�ʼ�Ƕ�  
			pieplot3d.setStartAngle(120D);  
			//���÷���Ϊ��˳ʱ�뷽��  
			pieplot3d.setDirection(Rotation.CLOCKWISE);  
			//����͸���ȣ�0.5FΪ��͸����1Ϊ��͸����0Ϊȫ͸��  
			pieplot3d.setForegroundAlpha(0.7F); 
	        
	        FileOutputStream fos_jpg = null; 
	        try { 
	        	//��ͼƬ������Tomcat������WebRootĿ¼��
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
			XYSeries series = new XYSeries("����");
			
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
				        "��������ͼ",
				        "����",
				        "����",				
				        collection,
				        PlotOrientation.VERTICAL,
				        true, 
				        true, 
				        false);
			
			XYPlot plot = (XYPlot) chart.getPlot(); 
			//���������Ƿ���ʾ���ݵ�
			XYLineAndShapeRenderer xylinerenderer = (XYLineAndShapeRenderer)plot.getRenderer();
			xylinerenderer.setBaseShapesVisible(true); 
			
			//����������ʾ�����ݵ��ֵ
			XYItemRenderer xyitem = plot.getRenderer(); 
			xyitem.setBaseItemLabelsVisible(true);
			xyitem.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER)); 
			xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
			xyitem.setBaseItemLabelFont(new Font("Dialog", 1, 12)); 
			plot.setRenderer(xyitem);
			
			FileOutputStream fos_jpg = null; 
	        try { 
	        	//��ͼƬ������Tomcat������WebRootĿ¼��
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
