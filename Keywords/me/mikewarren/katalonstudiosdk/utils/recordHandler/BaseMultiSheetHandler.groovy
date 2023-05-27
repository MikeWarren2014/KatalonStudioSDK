package me.mikewarren.katalonstudiosdk.utils.recordHandler

import java.lang.reflect.Field

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

import me.mikewarren.katalonstudiosdk.utils.SpreadsheetUtils
import me.mikewarren.katalonstudiosdk.utils.StringUtils

import groovy.transform.InheritConstructors


@InheritConstructors
public class BaseMultiSheetHandler<T> extends BaseRecordHandler<T> {

	@Override
	public void open() {
		try {
			this.initDataSourceFile();

			if (this.excelFile == null) {
				this.initReadableExcelFile();
			}
		} catch (IOException ex) {
			this.close();
		}
	}

	@Override
	protected void initDataSourceFile() throws IOException {
		if (!new File(this.dataSourceFilename).exists()) {
			super.initDataSourceFile();
			return;
		}

		this.recoverSourceFileFromCache();

		this.initReadableExcelFile();
		this.setupSpreadsheet(null);

		this.cacheSourceFile();
	}
	
	@Override
	public void handle(T record) {
		List<Field> childSheetHandlerFields = this.getClass().declaredFields
				.findAll { Field field -> return BaseSheetHandler.isAssignableFrom(field.type) }
		
		if (childSheetHandlerFields.isEmpty())
			throw new IllegalStateException("Your multi-sheet handler contains no child BaseSheetHandlers :'(");
			
		childSheetHandlerFields.forEach({ Field field -> 
			(this."${field.name}" as BaseSheetHandler).handle(record);
		})
	}
	
	@Override
	protected void setupSpreadsheet(Closure<Sheet> onSetupSheet) {
		List<Field> childSheetHandlerFields = this.getClass().declaredFields
				.findAll { Field field -> return BaseSheetHandler.isAssignableFrom(field.type) }
		
		if (childSheetHandlerFields.isEmpty())
			throw new IllegalStateException("Your multi-sheet handler contains no child BaseSheetHandlers :'(");
			
		if (this."${childSheetHandlerFields.first().name}" != null) { 
			return;
		}	
		
		Closure onGetAndSetupSheet = { String sheetName -> this.excelFile.getSheet(sheetName) };
		if (onSetupSheet != null)
			onGetAndSetupSheet = SpreadsheetUtils.OnCreateIfNotExistSheet(this.excelFile) >> onSetupSheet;

		childSheetHandlerFields.forEach({ Field field ->
			this."${field.name}" = field.type.newInstance(onGetAndSetupSheet);	
		})
	}
	
	@Override
	protected void writeRecordsToFile() {
		final Closure onCloneIntoWorkbook = SpreadsheetUtils.OnCopySheetIntoSpreadsheet(this.excelFile);

		List<Field> childSheetHandlerFields = this.getClass().declaredFields
			.findAll { Field field -> return BaseSheetHandler.isAssignableFrom(field.type) }
		
		if (childSheetHandlerFields.isEmpty())
			throw new IllegalStateException("Your multi-sheet handler contains no child BaseSheetHandlers :'(");
		
		childSheetHandlerFields.forEach({ Field field -> 
			(this."${field.name}" as BaseSheetHandler).writeRecordsToSheet(onCloneIntoWorkbook);
		})
	}

	@Override
	protected void setupFirstRow(Row firstRow) {
		throw new Exception("${StringUtils.GetClassName(this)} should not be trying to set up the first row of a Sheet");
	}

	@Override
	protected void extractRecordFromSheet(int rowNum) {
		throw new Exception("${StringUtils.GetClassName(this)} should not be trying to extract records from a Sheet");
	}

	@Override
	protected void fillInRow(Row row, T record) {
		throw new Exception("${StringUtils.GetClassName(this)} should NOT be trying to fill in Rows.");
	}
}
