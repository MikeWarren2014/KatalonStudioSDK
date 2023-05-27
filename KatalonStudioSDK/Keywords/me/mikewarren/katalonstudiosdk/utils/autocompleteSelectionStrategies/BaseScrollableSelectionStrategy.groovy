package me.mikewarren.katalonstudiosdk.utils.autocompleteSelectionStrategies

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import groovy.transform.InheritConstructors
import me.mikewarren.katalonstudiosdk.utils.ActionHandler
import me.mikewarren.katalonstudiosdk.utils.GeneralWebUIUtils
import me.mikewarren.katalonstudiosdk.utils.TimeLoggerUtil


@InheritConstructors
public class BaseScrollableSelectionStrategy extends BaseSelectionStrategy {

	@Override
	public void doSelect(TestObject dropdownOption) {
		final long startTime = System.currentTimeMillis();

		ActionHandler.HandleFailableAction({
			this.waitForDropdownOption(dropdownOption);
		}, { boolean success, _ ->
			KeywordUtil.logInfo("${dropdownOption.getObjectId()} ${this.getActionStatus(success, startTime)}")
			if (!success) {
				final TestObject lastAvailableDropdownItem = new TestObject("Last available dropdown item")
						.addProperty("xpath",
						ConditionType.EQUALS,
						"//lyte-drop-box[not(contains(concat(' ', @class, ' '), ' lyteDropdownHidden '))]//lyte-drop-item[last()]");

				TimeLoggerUtil.LogAction({
					if (!WebUI.waitForElementPresent(lastAvailableDropdownItem, 2))
						return false;

					GeneralWebUIUtils.ScrollDropdownOptionIntoView(lastAvailableDropdownItem);

					return true;
				}, lastAvailableDropdownItem.getObjectId(),
				"scroll into view");
			}
		}, 15)
		GeneralWebUIUtils.ScrollDropdownOptionIntoView(dropdownOption);
		WebUI.click(dropdownOption);
	}

	protected String getActionStatus(boolean success, long startTime) {
		if (success)
			return "took ${(System.currentTimeMillis() - startTime) / 1000} seconds to show up";
		return "didn't show up after ${(System.currentTimeMillis() - startTime) / 1000} seconds";
	}

	@Override
	public int getWaitTime() {
		return 2;
	}
}
