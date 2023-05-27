package me.mikewarren.katalonstudiosdk.pages

import me.mikewarren.katalonstudiosdk.utils.GeneralWebUIUtils

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

public trait Scrollable {
	public abstract TestObject getTopRow();
	public TestObject getBottomRow() { return null; }

	public void scrollDown() {
		GeneralWebUIUtils.ScrollDown(getTopRow(), getBottomRow());
	}

	public void scrollToElement(TestObject element) {  
		while ((!WebUI.verifyElementPresent(element, 1, FailureHandling.OPTIONAL)) &&
			(!GeneralWebUIUtils.IsAtBottomOfPage()))
			this.scrollDown();
			
		if (GeneralWebUIUtils.IsAtBottomOfPage())
			throw new IllegalStateException("We hit the bottom of the page, but we couldn't find the TestObject '${element.getObjectId()}'");
		
		WebUI.scrollToPosition(0,
			WebUiCommonHelper.findWebElement(element, 1).getLocation().getY() - WebUI.getElementHeight(this.getTopRow()))
	}
}
