package me.mikewarren.katalonstudiosdk.utils.autocompleteSelectionStrategies

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import me.mikewarren.katalonstudiosdk.utils.GeneralWebUIUtils

public class BaseSelectionStrategy {
	protected final String input;

	public BaseSelectionStrategy(String input) {
		this.input = input;
	}

	public void doSelect(TestObject dropdownOption) {
		this.waitForDropdownOption(dropdownOption);
		WebUI.click(dropdownOption);
	}

	public boolean waitForDropdownOption(TestObject dropdownOption, FailureHandling failureHandling = FailureHandling.STOP_ON_FAILURE) {
		return GeneralWebUIUtils.WaitForElementHasText(dropdownOption, input, this.getWaitTime(), failureHandling);
	}

	public int getWaitTime() {
		return 5;
	}
}
