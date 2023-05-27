package me.mikewarren.katalonstudiosdk.utils.autocompleteSelectionStrategies

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject

import groovy.transform.InheritConstructors
import me.mikewarren.katalonstudiosdk.utils.GeneralWebUIUtils

@InheritConstructors
public class ExactMatchSelectionStrategy extends BaseScrollableSelectionStrategy {

	@Override
	public boolean waitForDropdownOption(TestObject dropdownOption, FailureHandling failureHandling = FailureHandling.STOP_ON_FAILURE) {
		return GeneralWebUIUtils.WaitForElementTextMatches(dropdownOption, input, this.getWaitTime(), failureHandling);
	}
}
