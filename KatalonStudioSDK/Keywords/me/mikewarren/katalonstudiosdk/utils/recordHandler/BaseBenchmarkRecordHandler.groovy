package me.mikewarren.katalonstudiosdk.utils.recordHandler

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row

import com.kms.katalon.core.configuration.RunConfiguration
import com.signaturemd.models.benchmark.BenchmarkModel

import groovy.transform.InheritConstructors
import me.mikewarren.katalonstudiosdk.utils.DateUtils
import me.mikewarren.katalonstudiosdk.utils.StringUtils

@InheritConstructors
public abstract class BaseBenchmarkRecordHandler extends BaseRecordHandler<BenchmarkModel> {

	public BaseBenchmarkRecordHandler() {
		super(StringUtils.GetBenchmarkFilename(RunConfiguration.getExecutionProperties().get(StringUtils.CURRENT_TEST_CASE_KEY)));
	}

	@Override
	protected void setupFirstRow(Row firstRow) {
		Cell firstColumnDescCell = firstRow.createCell(0),
		secondColumnDescCell = firstRow.createCell(1),
		thirdColumnDescCell = firstRow.createCell(2);

		firstColumnDescCell.setCellValue("Submit Time");
		secondColumnDescCell.setCellValue("Time Elapsed");
		thirdColumnDescCell.setCellValue("Called from parent test case");
	}

	@Override
	protected void extractRecordFromSheet(int rowNum) {
		Row row = this.sheet.getRow(rowNum);

		BenchmarkModel model = new BenchmarkModel();
		model.setSubmitTime(DateUtils.ToDateTime(row.getCell(0).getStringCellValue()));
		model.setTimeElapsed(row.getCell(1).getStringCellValue());

		this.usedRecords.push(model);
	}

	@Override
	protected void fillInRow(Row row, BenchmarkModel record) {
		Cell submitTimeCell = row.createCell(0),
		timeElapsedCell = row.createCell(1),
		sourceTestCaseIDCell = row.createCell(2);

		submitTimeCell.setCellValue(DateUtils.ToDateTimeString(record.getSubmitTime()));
		timeElapsedCell.setCellValue(record.getTimeElapsed());

		if (!RunConfiguration.getExecutionProperties().get(StringUtils.CURRENT_TEST_CASE_KEY).equals(RunConfiguration.getExecutionSourceId()))
			sourceTestCaseIDCell.setCellValue(RunConfiguration.getExecutionSourceId());
	}
}
