package me.mikewarren.katalonstudiosdk.utils

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook

public final class SpreadsheetUtils {
	public static final int FirstDataRowNumber = 1;

	public static boolean IsRowEmpty(Row row) {
		return row.getLastCellNum() == -1;
	}

	public static void ExtractDataFromSheet(Sheet sheet, Closure onExtractData) {
		for (int rowNum = this.FirstDataRowNumber; rowNum <= this.GetLastNonEmptyRowNum(sheet); rowNum++) {
			onExtractData(rowNum);
		}
	}

	public static int GetLastNonEmptyRowNum(Sheet sheet) {
		return sheet.findLastIndexOf { Row row -> !this.IsRowEmpty(row) };
	}

	public static Closure<Sheet> OnCreateIfNotExistSheet(Workbook spreadsheet) {
		return { String sheetName ->
			Sheet sheet = spreadsheet.getSheet(sheetName);
			if (sheet != null)
				return sheet;
			return spreadsheet.createSheet(sheetName);
		}
	}

	public static Closure OnCopySheetIntoSpreadsheet(Workbook spreadsheet) {
		return { Sheet sheet ->
			Sheet targetSheet = this.OnCreateIfNotExistSheet(spreadsheet)(sheet.getSheetName());

			sheet.eachWithIndex({ Row row, int rowIdx ->
				row.eachWithIndex({ Cell cell, int colIdx ->
					Row targetRow = targetSheet.getRow(rowIdx);
					if (targetRow == null)
						targetRow = targetSheet.createRow(rowIdx);

					Cell targetCell = targetRow.getCell(colIdx);
					if (targetCell == null)
						targetCell = targetRow.createCell(colIdx);

					targetCell.setCellValue(cell.getStringCellValue());
				})
			})
		}
	}
}
