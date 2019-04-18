package cc.cnplay.platform.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import cc.cnplay.core.CnplayRuntimeException;

public class ExcelImportHelp {
	private final static String SEPARATOR = ",";

	public static List<String> getTargetList(MultipartFile multipartFile) throws Exception {
		String fileType = "";
		InputStream is = multipartFile.getInputStream();
		Workbook wb = null;
		try {
			String fileName = multipartFile.getOriginalFilename();
			fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.lastIndexOf(".") + 4);
			if (fileType.toLowerCase().equals("xls") || fileType.toLowerCase().equals("xlsx")) {
				wb = WorkbookFactory.create(is);
			}
		} catch (Exception e) {
			// logger.error(e);
		}

		// sheet默认取0
		return exportListFromExcel(wb, 0);
	}

	/**
	 * 由指定的Sheet导出至List 其中sheet默认取0
	 * 
	 * @param workbook
	 * @param sheetNum
	 * @return
	 * @throws IOException
	 */
	private static List<String> exportListFromExcel(Workbook workbook, int sheetNum) {
		Sheet sheet = workbook.getSheetAt(sheetNum);
		// 解析公式结果
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		List<String> list = new ArrayList<String>();
		// 获取记录号
		int minRowIx = sheet.getFirstRowNum();
		int maxRowIx = sheet.getLastRowNum();
		for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
			// 标题行默认不解析
			if (rowIx == 0) {
				continue;
			}
			if (rowIx == 1) {
				continue;
			}
			Row row = sheet.getRow(rowIx);
			StringBuilder sb = new StringBuilder();
			// 处理每行内各单元格内容
			short minColIx = row.getFirstCellNum();
			short maxColIx = row.getLastCellNum();
			for (short colIx = minColIx; colIx <= maxColIx; colIx++) {
				Cell cell = row.getCell(new Integer(colIx));
				CellValue cellValue = evaluator.evaluate(cell);
				if (cellValue == null) {
					continue;
				}
				// 经过公式解析，最后只存在Boolean、Numeric和String三种数据类型，此外就是Error了
				// 其余数据类型，根据官方文档，完全可以忽略http://poi.apache.org/spreadsheet/eval.html
				switch (cellValue.getCellType()) {
				case Cell.CELL_TYPE_BOOLEAN:
					sb.append(SEPARATOR + cellValue.getBooleanValue());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					// 这里的日期类型会被转换为数字类型，需要判别后区分处理
					if (DateUtil.isCellDateFormatted(cell)) {
						sb.append(SEPARATOR + cell.getDateCellValue());
					} else {
						// 数值类型的默认double 将其四舍五入转string 避免".0"的问题
						sb.append(SEPARATOR + String.valueOf(Math.round(cellValue.getNumberValue())));
					}
					break;
				case Cell.CELL_TYPE_STRING:
					sb.append(SEPARATOR + cellValue.getStringValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					break;
				case Cell.CELL_TYPE_BLANK:
					break;
				case Cell.CELL_TYPE_ERROR:
					break;
				default:
					break;
				}
			}
			list.add(sb.toString());
		}
		return list;
	}

	public static List<String> getTargetList(InputStream in, String xlsType) throws Exception {
		Workbook wb = null;
		try {
			// 2003
			/*
			 * if (xlsType.equalsIgnoreCase("xls")) { wb = new HSSFWorkbook(in); } //2007 if
			 * (xlsType.equalsIgnoreCase("xlsx")) { //wb = new XSSFWorkbook(is); }
			 */
			if (xlsType.equalsIgnoreCase("xls") || xlsType.equalsIgnoreCase("xlsx")) {
				wb = WorkbookFactory.create(in);
			}
			if (wb == null) {
				throw new CnplayRuntimeException("workbook生成失败，请确认！");
			}
		} catch (Exception e) {
			// logger.error(e);
			throw new CnplayRuntimeException("workbook生成失败，请确认！");
		}

		// sheet默认取0
		return exportListFromExcel(wb, 0);
	}

	private final static String xls = "xls";
	private final static String xlsx = "xlsx";

	public static List<String[]> readExcel(String filename) throws Exception {
		Workbook workbook = null;
		FileInputStream fis = new FileInputStream(filename);
		if (filename.endsWith(xls)) {
			// 2003
			workbook = new HSSFWorkbook(fis);
		} else if (filename.endsWith(xlsx)) {
			// 2007
			workbook = new XSSFWorkbook(fis);
		}
		List<String[]> list = new ArrayList<String[]>();
		if (workbook != null) {
			for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
				// 获得当前sheet工作表
				Sheet sheet = workbook.getSheetAt(sheetNum);
				if (sheet == null) {
					continue;
				}
				// 获得当前sheet的开始行
				int firstRowNum = sheet.getFirstRowNum();
				// 获得当前sheet的结束行
				int lastRowNum = sheet.getLastRowNum();
				// 循环除了第一行的所有行
				for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
					// 获得当前行
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
					// 获得当前行的开始列
					int firstCellNum = row.getFirstCellNum();
					// 获得当前行的列数
					int lastCellNum = row.getPhysicalNumberOfCells();
					String[] cells = new String[row.getPhysicalNumberOfCells()];
					// 循环当前行
					for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
						Cell cell = row.getCell(cellNum);
						cells[cellNum] = getCellValue(cell);
					}
					list.add(cells);
				}
			}
		}
		return list;
	}

	public static String getCellValue(Cell cell) {
		String cellValue = "";
		if (cell == null) {
			return cellValue;
		}
		// 把数字当成String来读，避免出现1读成1.0的情况
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
		}
		// 判断数据的类型
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC: // 数字
			cell.getCellStyle().getDataFormat();
			cellValue = String.valueOf(cell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_STRING: // 字符串
			cellValue = String.valueOf(cell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_BOOLEAN: // Boolean
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA: // 公式
			cellValue = String.valueOf(cell.getCellFormula());
			break;
		case Cell.CELL_TYPE_BLANK: // 空值
			cellValue = "";
			break;
		case Cell.CELL_TYPE_ERROR: // 故障
			cellValue = "非法字符";
			break;
		default:
			cellValue = "未知类型";
			break;
		}
		return cellValue;
	}
}
