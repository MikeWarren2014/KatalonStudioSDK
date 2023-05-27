package me.mikewarren.katalonstudiosdk.utils.recordHandler

import org.apache.poi.ss.usermodel.Sheet

import groovy.transform.InheritConstructors
import me.mikewarren.katalonstudiosdk.enums.SubmitType

@InheritConstructors
public class SubmitBenchmarkRecordHandler extends BaseBenchmarkRecordHandler {

	private static SubmitBenchmarkRecordHandler _instance;

	Map<SubmitType, Integer> sheetIndicesDict;

	private SubmitBenchmarkRecordHandler() {
		super();
		this.sheetIndicesDict = new HashMap();
		this.sheetIndicesDict.put(SubmitType.CLICK_BUTTON, 0);
		this.sheetIndicesDict.put(SubmitType.PRESS_ENTER, 1);
	}

	public static SubmitBenchmarkRecordHandler GetInstance() {
		if (this._instance == null)
			this._instance = new SubmitBenchmarkRecordHandler();

		return this._instance;
	}


	@Override
	protected List<Sheet> createSheets() {
		super.createSheets();
		this.excelFile.setSheetName(0,
				SubmitType.CLICK_BUTTON.textValue);
		this.excelFile.createSheet(SubmitType.PRESS_ENTER.textValue);

		return SubmitType.values()
				.collect({ SubmitType submitType -> this.excelFile.getSheet(submitType.textValue) });
	}

	public void changeActiveSheet(SubmitType submitType) {
		int activeSheetIdx = this.sheetIndicesDict.get(submitType);

		if (this.excelFile == null)
			this.open();

		this.excelFile.setActiveSheet(activeSheetIdx);
		this.sheet = this.excelFile.getSheetAt(activeSheetIdx);
	}
}
