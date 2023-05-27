package me.mikewarren.katalonstudiosdk.utils.recordHandler

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

import me.mikewarren.katalonstudiosdk.utils.SpreadsheetUtils


public abstract class BaseSheetHandler<T> implements RandomRecordGenerator<T> {
	protected Sheet sheet;
	protected List<T> usedRecords;

	protected static final int primaryKeyCellIdx = 0;

	protected BaseSheetHandler(Closure<Sheet> onGetSheet) {
		onGetSheet.setResolveStrategy(Closure.DELEGATE_FIRST);
		onGetSheet.setDelegate(this);
		this.sheet = onGetSheet(this.getSheetName());
		this.init();
	}

	protected void init() {
		this.usedRecords = [];
		SpreadsheetUtils.ExtractDataFromSheet(this.sheet,
				{ int rowNum -> this.extractRecordFromSheet(rowNum) });
	}

	public void handle(T record) {
		this.usedRecords.push(record);
	}

	public List<T> getUsedRecords() {
		return usedRecords;
	}

	protected void writeRecordsToSheet(Closure onDone) {
		this.usedRecords
				.forEach({ T record ->
					// for some odd reason, in here, this refers to this Closure, nothing else
					fillInRow(getRow(record),
							record);
				});
		onDone(this.sheet);
	}

	protected abstract Row getRow(T record);

	protected abstract String getSheetName();

	protected abstract void setupFirstRow(Row row);

	protected abstract void extractRecordFromSheet(int rowNum);

	protected abstract void fillInRow(Row row, T record);
}
