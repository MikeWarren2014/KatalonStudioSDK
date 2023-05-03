package me.mikewarren.katalonstudiosdk.utils.recordHandler

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import me.mikewarren.katalonstudiosdk.utils.FileUtils
import me.mikewarren.katalonstudiosdk.utils.SpreadsheetUtils


public abstract class BaseRecordHandler<T> implements AutoCloseable {
	protected final String dataSourceFilename;

	protected static final String CacheFolder = './.cache';

	protected List<T> usedRecords = [];

	protected FileInputStream inputStream;
	protected Workbook excelFile;
	protected XSSFSheet sheet;

	protected BaseRecordHandler() {
		this.open();
	}

	protected BaseRecordHandler(String dataSourceFilename) {
		this.dataSourceFilename = dataSourceFilename;
		this.open();
	}

	public void open() {
		try {
			this.initDataSourceFile();

			this.initReadableExcelFile();

			this.sheet = this.excelFile.getSheetAt(this.excelFile.getActiveSheetIndex());

			if (this.usedRecords.isEmpty()) {
				SpreadsheetUtils.ExtractDataFromSheet(this.sheet,
						{ int rowNum -> this.extractRecordFromSheet(rowNum)});
			}
		} catch (IOException ex) {
			this.close();
		}
	}

	protected void initDataSourceFile() throws IOException {
		File file = new File(this.dataSourceFilename);

		this.recoverSourceFileFromCache();

		if (file.exists())
			return;

		file.getParentFile().mkdirs();

		//	NOTE: this is here only because the version of POI that Katalon Studio uses by default, doesn't support factory creation of Excel file from scratch...
		this.excelFile = new XSSFWorkbook();
		FileOutputStream outputStream = new FileOutputStream(file);

		this.setupSpreadsheet({ Sheet sheet ->
			setupFirstRow(sheet.createRow(0));

			sheet.createFreezePane(0, 1);

			// returning the sheet allows for the functional programming technique known as composition
			return sheet;
		});

		this.excelFile.write(outputStream);
		outputStream.close();
		this.excelFile.close();
		this.excelFile = null;

		this.cacheSourceFile();
	}

	protected void recoverSourceFileFromCache() {
		File cachedFile = new File(this.getCachedFilename());

		if (!cachedFile.exists())
			return;

		if (new File(this.dataSourceFilename).size() > 0)
			return;

		this.copyExcelFile(cachedFile.getPath(), FileUtils.CurrentDirectorySymbol);
	}

	protected String getCachedFilename() {
		return "${this.CacheFolder}/${this.dataSourceFilename}";
	}

	protected void cacheSourceFile() {
		this.copyExcelFile(this.dataSourceFilename, this.CacheFolder);
	}

	protected void copyExcelFile(String sourceFileName, String destinationFolder) throws IOException {
		final Path destinationFilePath = Paths.get(destinationFolder, this.dataSourceFilename);

		final String destinationFileParentFolder = FileUtils.GetParentPathname(destinationFilePath.toString());

		Files.createDirectories(Paths.get(destinationFileParentFolder));

		Files.copy(Paths.get(sourceFileName),
				destinationFilePath,
				StandardCopyOption.REPLACE_EXISTING,
				);
	}


	/**
	 * @param Closure onSetupSheet
	 */
	protected void setupSpreadsheet(Closure<Sheet> onSetupSheet) {
		this.createSheets()
				.forEach(onSetupSheet);
	}

	protected List<Sheet> createSheets() {
		if (this.excelFile.getNumberOfSheets() == 0)
			this.excelFile.createSheet();
		return [this.excelFile.getSheetAt(0)];
	}

	protected void initReadableExcelFile() {
		this.inputStream = new FileInputStream(this.dataSourceFilename);
		this.excelFile = WorkbookFactory.create(this.inputStream);
	}

	protected abstract void setupFirstRow(Row firstRow);

	protected abstract void extractRecordFromSheet(int rowNum);

	public void handle(T record) {
		this.usedRecords.push(record);
	}

	protected void writeToFile() {
		if (!new File(this.dataSourceFilename).exists())
			return;

		if (this.excelFile == null)
			this.open();

		this.writeRecordsToFile();

		FileOutputStream outputStream = new FileOutputStream(this.dataSourceFilename);
		try {
			this.excelFile.write(outputStream);
		} finally {
			outputStream.close();
		}
	}

	protected void writeRecordsToFile() {
		for (int j = this.sheet.getLastRowNum(); j < this.usedRecords.size(); j++) {
			Row row = this.sheet.createRow(j + SpreadsheetUtils.FirstDataRowNumber);
			this.fillInRow(row, this.usedRecords[j]);
		}
	}

	@Override
	public void close() throws Exception {
		this.writeToFile();
		this.closeStreams();
	}

	protected void closeStreams() {
		this.inputStream.close();
		this.excelFile.close();

		// resetting state of streams for next use
		this.excelFile = null;
		this.inputStream = null;
	}

	protected abstract void fillInRow(Row row, T record);
}
