package cc.cnplay.core.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleGraphics2DExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.view.JasperViewer;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

public class IReportExporter
{

	private JasperPrint jasperPrint;

	public IReportExporter(InputStream jrxml, Map<String, Object> parameters) throws Exception
	{
		this(jrxml, parameters, null);
	}

	public IReportExporter(InputStream jrxml, Map<String, Object> parameters, JRDataSource dataSource) throws Exception
	{
		JasperReport jr = JasperCompileManager.compileReport(jrxml);
		jasperPrint = JasperFillManager.fillReport(jr, parameters, dataSource);
	}

	public IReportExporter(String filename, Map<String, Object> parameters) throws Exception
	{
		JRDataSource ds = new JREmptyDataSource();
		JasperReport jr = null;
		if (StringUtils.endsWithIgnoreCase(filename, ".jrxml"))
		{
			jr = JasperCompileManager.compileReport(filename);
		}
		else
		{
			jr = (JasperReport) JRLoader.loadObjectFromFile(filename);
		}
		jasperPrint = JasperFillManager.fillReport(jr, parameters, ds);
	}

	public IReportExporter(String filename, Map<String, Object> parameters, JRDataSource dataSource) throws Exception
	{
		JasperReport jr = null;
		if (StringUtils.endsWithIgnoreCase(filename, ".jrxml"))
		{
			jr = JasperCompileManager.compileReport(filename);
		}
		else
		{
			jr = (JasperReport) JRLoader.loadObjectFromFile(filename);
		}
		jasperPrint = JasperFillManager.fillReport(jr, parameters, dataSource);
	}

	public IReportExporter(File file, Map<String, Object> parameters, JRDataSource dataSource) throws Exception
	{
		JasperReport jr = null;
		String fileName = file.getName();
		String path = file.getAbsolutePath();
		if (StringUtils.endsWithIgnoreCase(fileName, ".jrxml"))
		{
			jr = JasperCompileManager.compileReport(path);
		}
		else
		{
			jr = (JasperReport) JRLoader.loadObjectFromFile(path);
		}
		jasperPrint = JasperFillManager.fillReport(jr, parameters, dataSource);
	}

	public void exp(OutputStream os, String format) throws Exception
	{
		if (!format.startsWith("."))
		{
			format = "." + format;
		}
		format = format.toLowerCase();
		if (format.equalsIgnoreCase(".pdf"))
		{
			expPDF(os);
		}
		else if (format.startsWith(".xls"))
		{
			expXls(os);
		}
		else if (format.startsWith(".htm"))
		{
			expHtml(os);
		}
		else if (format.startsWith(".doc"))
		{
			expDocx(os);
		}
	}

	public void expHtml(OutputStream os) throws Exception
	{
		HtmlExporter jrHtmlExp = new HtmlExporter();
		ExporterInput input = new SimpleExporterInput(jasperPrint);
		SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(os);
		jrHtmlExp.setExporterInput(input);
		jrHtmlExp.setExporterOutput(output);
		jrHtmlExp.exportReport();
		// os.flush();
	}

	public String toHtmlString() throws Exception
	{
		HtmlExporter jrExp = new HtmlExporter();
		ExporterInput input = new SimpleExporterInput(jasperPrint);
		StringBuffer html = new StringBuffer();
		SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(html);
		jrExp.setExporterInput(input);
		jrExp.setExporterOutput(output);
		jrExp.exportReport();
		return html.toString();
	}

	public void expHtml(String filename) throws Exception
	{
		File file = new File(filename);
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(fos);
		String html = toHtmlString();
		html = html.replaceAll("<td style=\"width: 45px; height: 1px;\"></td>", "<td style=\"width: 45px; height: 0px;\"></td>");
		html = html.replaceAll("width: 540px; border-collapse: collapse; empty-cells: show", "border-collapse: collapse; empty-cells: show");
		pw.write(html);
		pw.flush();
		pw.close();
	}

	public void expPDF(String filename) throws Exception
	{
		File file = new File(filename);
		OutputStream os = new FileOutputStream(file);
		expPDF(os);
	}

	/**
	 * @param os
	 * @throws Exception
	 */
	public void expPDF(OutputStream os) throws Exception
	{
		JRPdfExporter exp = new JRPdfExporter();
		ExporterInput input = new SimpleExporterInput(jasperPrint);
		OutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(os);
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		exp.setExporterInput(input);
		exp.setExporterOutput(output);
		exp.setConfiguration(configuration);
		exp.exportReport();
		// os.flush();
	}

	public void expDocx(String filename) throws Exception
	{
		File file = new File(filename);
		OutputStream os = new FileOutputStream(file);
		expDocx(os);
	}

	public void expDocx(OutputStream os) throws Exception
	{
		JRDocxExporter exporter = new JRDocxExporter();
		ExporterInput input = new SimpleExporterInput(jasperPrint);
		OutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(os);
		exporter.setExporterInput(input);
		exporter.setExporterOutput(output);
		exporter.exportReport();
		// os.flush();
	}

	public void expXls(String fileName) throws Exception
	{
		File file = new File(fileName);
		OutputStream os = new FileOutputStream(file);
		expXls(os);
	}

	public void expXls(OutputStream os) throws Exception
	{
		JRXlsExporter exporter = new JRXlsExporter();
		ExporterInput input = new SimpleExporterInput(jasperPrint);
		OutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(os);
		exporter.setExporterInput(input);
		exporter.setExporterOutput(output);
		exporter.exportReport();
		// os.flush();
	}

	public BufferedImage expImage() throws Exception
	{
		BufferedImage image = new BufferedImage(jasperPrint.getPageWidth() + 1, jasperPrint.getPageHeight() + 1, BufferedImage.TYPE_INT_RGB);
		JRGraphics2DExporter exporter = new JRGraphics2DExporter();
		ExporterInput input = new SimpleExporterInput(jasperPrint);
		SimpleGraphics2DExporterOutput output = new SimpleGraphics2DExporterOutput();
		output.setGraphics2D((Graphics2D) image.getGraphics());
		exporter.setExporterInput(input);
		exporter.setExporterOutput(output);
		exporter.exportReport();
		return image;
	}

	public void viewReport()
	{
		JasperViewer.viewReport(jasperPrint);
	}

	public static Map<String, Object> toMap(Object value, String dateformat)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		Class<?> clazz = value.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields)
		{
			if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
			{
				try
				{
					map.put(f.getName(), f.get(value));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		while ((clazz = clazz.getSuperclass()) != null)
		{
			fields = clazz.getDeclaredFields();
			for (Field f : fields)
			{
				if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
				{
					try
					{
						if (!map.containsKey(f.getName()))
							map.put(f.getName(), f.get(value));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		return map;
	}

	public static Map<String, Object> toParameters(Object bean, boolean printZero, String dateformat)
	{
		SimpleDateFormat df = null;
		String nullDate = "";
		if (dateformat != null && dateformat.trim().length() > 0)
		{
			df = new SimpleDateFormat(dateformat);
			nullDate = dateformat.replaceAll("Y", "　");
			nullDate = nullDate.replaceAll("y", "　");
			nullDate = nullDate.replaceAll("M", "　");
			nullDate = nullDate.replaceAll("m", "　");
			nullDate = nullDate.replaceAll("D", "　");
			nullDate = nullDate.replaceAll("d", "　");
			nullDate = nullDate.replaceAll("H", "　");
			nullDate = nullDate.replaceAll("h", "　");
			nullDate = nullDate.replaceAll("S", "　");
			nullDate = nullDate.replaceAll("s", "　");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean.getClass());
		for (PropertyDescriptor pd : pds)
		{
			try
			{
				Type returnType = pd.getReadMethod().getReturnType();
				Object value = pd.getReadMethod().invoke(bean, new Object[] {});
				if (value != null)
				{
					if (value instanceof Number)
					{
						if (Double.parseDouble(value.toString()) > 0)
						{
							map.put(pd.getName(), value.toString());
						}
						else
						{
							if (printZero)
							{
								map.put(pd.getName(), value.toString());
							}
							else
							{
								map.put(pd.getName(), " ");
							}
						}
					}
					if (value instanceof Date && df != null)
					{
						map.put(pd.getName(), df.format((Date) value));
					}
					else
					{
						map.put(pd.getName(), value);
					}
				}
				else
				{
					if (returnType.equals(Date.class))
					{

						map.put(pd.getName(), nullDate);
					}
					else
					{
						map.put(pd.getName(), " ");
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}

	public static void main(String[] args)
	{
		try
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("year", "2015");
			String filename = "C:/Users/icc/test.jrxml";
			List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("id", "0001");
			m.put("name", "测试1");
			Map<String, Object> m2 = new HashMap<String, Object>();
			m2.put("id", "0002");
			m2.put("name", "测试2");
			list.add(m);
			list.add(m2);
			JRDataSource dataSource = new JRMapCollectionDataSource(list);
			IReportExporter exporter = new IReportExporter(filename, parameters, dataSource);
			BufferedImage image = exporter.expImage();
			exporter.expHtml("C:/Users/icc/Desktop/temp/AYearReport.html");
			exporter.expPDF("C:/Users/icc/Desktop/temp/AYearReport.pdf");
			exporter.expDocx("C:/Users/icc/Desktop/temp/AYearReport.doc");
			exporter.expXls("C:/Users/icc/Desktop/temp/AYearReport.xls");
			ImageIO.write(image, "jpg", new File("C:/Users/icc/Desktop/temp/AYearReport.jpg"));
			exporter.viewReport();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
