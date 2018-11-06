package cc.cnplay.platform.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import cc.cnplay.core.CnplayRuntimeException;

public class ExcelImportHelp
{
	private final static String SEPARATOR = ",";

	public static List<String> getTargetList(MultipartFile multipartFile) throws Exception
	{
		String fileType = "";
		InputStream is = multipartFile.getInputStream();
		Workbook wb = null;
		try
		{
			String fileName = multipartFile.getOriginalFilename();
			fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.lastIndexOf(".") + 4);
			if (fileType.toLowerCase().equals("xls") || fileType.toLowerCase().equals("xlsx"))
			{
				wb = WorkbookFactory.create(is);
			}
		}
		catch (Exception e)
		{
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
	private static List<String> exportListFromExcel(Workbook workbook, int sheetNum)
	{
		Sheet sheet = workbook.getSheetAt(sheetNum);
		// 解析公式结果
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		List<String> list = new ArrayList<String>();
		// 获取记录号
		int minRowIx = sheet.getFirstRowNum();
		int maxRowIx = sheet.getLastRowNum();
		for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++)
		{
			// 标题行默认不解析
			if (rowIx == 0)
			{
				continue;
			}
			if (rowIx == 1)
			{
				continue;
			}
			Row row = sheet.getRow(rowIx);
			StringBuilder sb = new StringBuilder();
			// 处理每行内各单元格内容
			short minColIx = row.getFirstCellNum();
			short maxColIx = row.getLastCellNum();
			for (short colIx = minColIx; colIx <= maxColIx; colIx++)
			{
				Cell cell = row.getCell(new Integer(colIx));
				CellValue cellValue = evaluator.evaluate(cell);
				if (cellValue == null)
				{
					continue;
				}
				// 经过公式解析，最后只存在Boolean、Numeric和String三种数据类型，此外就是Error了
				// 其余数据类型，根据官方文档，完全可以忽略http://poi.apache.org/spreadsheet/eval.html
				switch (cellValue.getCellType())
				{
				case Cell.CELL_TYPE_BOOLEAN:
					sb.append(SEPARATOR + cellValue.getBooleanValue());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					// 这里的日期类型会被转换为数字类型，需要判别后区分处理
					if (DateUtil.isCellDateFormatted(cell))
					{
						sb.append(SEPARATOR + cell.getDateCellValue());
					}
					else
					{
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

	public static List<String> getTargetList(InputStream in,String xlsType)throws Exception 
    {        
        Workbook wb = null;
        try {
            //2003
           /* if (xlsType.equalsIgnoreCase("xls")) {
            	wb = new HSSFWorkbook(in);
            }
            //2007
            if (xlsType.equalsIgnoreCase("xlsx")) {
            	//wb = new XSSFWorkbook(is);
            }*/
            if (xlsType.equalsIgnoreCase("xls") || xlsType.equalsIgnoreCase("xlsx"))
			{
				wb = WorkbookFactory.create(in);
			}
            if(wb == null){
            	throw new CnplayRuntimeException("workbook生成失败，请确认！");
            }
        } catch (Exception e) {
            //logger.error(e);      
        	throw new CnplayRuntimeException("workbook生成失败，请确认！");
        }
       
        //sheet默认取0
        return exportListFromExcel(wb, 0);  
     }
}
