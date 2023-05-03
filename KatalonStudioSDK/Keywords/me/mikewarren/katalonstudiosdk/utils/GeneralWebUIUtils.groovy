package me.mikewarren.katalonstudiosdk.utils


import org.openqa.selenium.Keys
import org.testng.Assert

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable
import me.mikewarren.katalonstudiosdk.utils.autocompleteSelectionStrategies.BaseSelectionStrategy

public final class GeneralWebUIUtils {
	public static final String DISABLED = "disabled";
	public static final String Value = "value"
	public static final String ClassNameList = "class";

	public static boolean GlobalVariableExists(String variableName) {
		return GlobalVariable.metaClass.hasProperty(GlobalVariable, variableName);
	}

	public static String GetTextValue(TestObject to) {
		return WebUI.getAttribute(to, this.Value);
	}

	public static Date GetDateValue(TestObject to) {
		return DateUtils.ToDate(this.GetTextValue(to))
	}

	public static Keys GetCommandKey() {
		final String os = System.getProperty("os.name")

		if (os.toUpperCase().contains("WINDOWS"))
			return Keys.CONTROL;

		return Keys.COMMAND;
	}

	public static void ClearAndEnterText(TestObject to, String text) {
		WebUI.sendKeys(to,
				Keys.chord("${this.GetCommandKey().toString()}A"),
				FailureHandling.STOP_ON_FAILURE);

		WebUI.sendKeys(to, text, FailureHandling.STOP_ON_FAILURE);
	}

	public static void UpdateDateField(TestObject to, Date newDate) {
		this.WaitForTextFieldNonEmpty(to, 1, FailureHandling.CONTINUE_ON_FAILURE)

		if (newDate == null) {
			WebUI.clearText(to, FailureHandling.STOP_ON_FAILURE);
			return;
		}

		final String fieldTextValue = this.GetTextValue(to),
		newDateTextValue = DateUtils.ToDateString(newDate);

		if ((!newDateTextValue.isEmpty()) && (!fieldTextValue.equals(newDateTextValue))) {
			this.ClearAndEnterText(to, newDateTextValue);

			KeywordUtil.logInfo("'${newDateTextValue}' written to the date field")
		}

		WebUI.sendKeys(to, Keys.TAB.toString());
		KeywordUtil.logInfo("After trying to write to the field, it has value '${this.GetTextValue(to)}'")
	}

	public static void EnterTextThenEscape(TestObject textField, String text) {
		WebUI.sendKeys(textField,
				text);

		KeywordUtil.logInfo("Text '${text}' entered into the text field");

		WebUI.click(textField);

		WebUI.sendKeys(textField,
				Keys.ESCAPE.toString());
	}

	public static void EnterTextThenTab(TestObject textField, String text) {
		WebUI.sendKeys(textField,
				text);

		KeywordUtil.logInfo("Text '${text}' entered into the text field");

		WebUI.click(textField);

		WebUI.sendKeys(textField,
				Keys.TAB.toString());
	}

	private static boolean WaitForURLCondition(Closure<Boolean> onCheckCondition, int timeOut, Closure<String> onErrorMessage, FailureHandling failureHandling = FailureHandling.STOP_ON_FAILURE) {
		final long startTime = System.currentTimeMillis()
		boolean isConditionSatisfied = false;
		while ((System.currentTimeMillis() < startTime + timeOut * 1000) && (!isConditionSatisfied)) {
			isConditionSatisfied = onCheckCondition(WebUI.getUrl())

			if (!isConditionSatisfied)
				sleep(500);
		}
		if ((!isConditionSatisfied) && (failureHandling.equals(FailureHandling.STOP_ON_FAILURE))) {
			KeywordUtil.markFailedAndStop("${onErrorMessage(WebUI.getUrl())} after ${(System.currentTimeMillis() - startTime) / 1000} seconds");
		}
		return isConditionSatisfied;
	}

	public static boolean WaitForURLNotEquals(String url, int timeOut, FailureHandling failureHandling = FailureHandling.STOP_ON_FAILURE) {
		return this.WaitForURLCondition({ String browserURL ->
			return !(browserURL =~ StringUtils.GetURLPattern(url)).matches();
		},
		timeOut, { String browserURL ->
			"URL '${browserURL}' matches unexpected '${url}'"
		},
		failureHandling)
	}

	public static boolean WaitForURLEquals(String url, int timeOut, FailureHandling failureHandling = FailureHandling.STOP_ON_FAILURE) {
		return this.WaitForURLCondition({ String browserURL ->
			return (browserURL =~ StringUtils.GetURLPattern(url)).matches();
		},
		timeOut, { String browserURL ->
			"URL '${browserURL}' does not match expected '${url}'"
		},
		failureHandling)
	}

	public static boolean WaitForElementCondition(Closure<Boolean> onCheckCondition, Closure onContinue, TestObject to, int timeOut, FailureHandling failureHandling = FailureHandling.STOP_ON_FAILURE) {
		final long startTime = System.currentTimeMillis()
		boolean isConditionSatisfied = false;
		while ((System.currentTimeMillis() < startTime + timeOut * 1000) && (!isConditionSatisfied)) {
			isConditionSatisfied = WebUI.waitForElementPresent(to, 1, failureHandling) && onCheckCondition(to);
			if (onContinue != null)
				onContinue(isConditionSatisfied, to);
		}
		if ((!isConditionSatisfied) && (failureHandling.equals(FailureHandling.STOP_ON_FAILURE))) {
			KeywordUtil.markFailedAndStop("Condition for TestObject '${to.getObjectId()}' not met after ${(System.currentTimeMillis() - startTime) / 1000} seconds");
		}
		return isConditionSatisfied;
	}

	public static boolean WaitForTextFieldNonEmpty(TestObject to, int timeOut, FailureHandling failureHandling = FailureHandling.STOP_ON_FAILURE) {
		return this.WaitForElementCondition({ TestObject testObj ->
			return (!this.GetTextValue(testObj).isEmpty());
		},
		null,
		to,
		timeOut,
		failureHandling);
	}

	public static boolean WaitForElementHasText(TestObject to, String expectedText, int timeOut, FailureHandling failureHandling = FailureHandling.STOP_ON_FAILURE) {
		return this.WaitForElementCondition({ TestObject testObj ->
			return WebUI.getText(testObj).contains(expectedText);
		}, { boolean success, TestObject testObj ->
			if (success)
				return;

			if (!WebUI.waitForElementNotPresent(testObj, 1, FailureHandling.OPTIONAL))
				return;

			WebUI.waitForElementPresent(testObj, timeOut);
		},
		to,
		timeOut,
		failureHandling);
	}

	public static boolean WaitForElementTextMatches(TestObject to, String expectedText, int timeOut, FailureHandling failureHandling = FailureHandling.STOP_ON_FAILURE) {
		return this.WaitForElementCondition({ TestObject testObj ->
			String testObjectText = WebUI.getText(testObj);
			if (StringUtils.IsNullOrEmpty(testObjectText))
				testObjectText = (String)WebUI.executeJavaScript('return arguments[0].innerText', [WebUiCommonHelper.findWebElement(testObj, 1)]);

			return testObjectText.equals(expectedText);
		}, { boolean success, TestObject testObj ->
			if (success)
				return;

			if (WebUI.waitForElementNotPresent(testObj, 1, FailureHandling.OPTIONAL)) {
				WebUI.waitForElementPresent(testObj, timeOut);
			}

			if (WebUI.waitForElementVisible(testObj, timeOut, FailureHandling.OPTIONAL))
				KeywordUtil.logInfo("TestObject '${testObj.getObjectId()}' has text '${WebUI.getText(testObj)}'")
		},
		to,
		timeOut,
		failureHandling);
	}

	public static void OpenLinkInNewTab(TestObject link) {
		// SOURCE: https://forum.katalon.com/t/how-to-open-link-in-new-tab/13555/2
		WebUI.sendKeys(link, Keys.chord(Keys.CONTROL, Keys.SHIFT, Keys.ENTER))

		this.SwitchToNextTab()
	}

	public static void OpenTab() {
		WebUI.executeJavaScript("window.open()", null);

		this.SwitchToNextTab()
	}

	public static void SwitchToNextTab() {
		WebUI.switchToWindowIndex(WebUI.getWindowIndex() + 1, FailureHandling.STOP_ON_FAILURE)
	}

	public static void CloseLastTab() {
		final int initWindowIdx = WebUI.getWindowIndex()

		if (initWindowIdx == 0)
			return;

		WebUI.closeWindowIndex(initWindowIdx)

		WebUI.switchToWindowIndex(initWindowIdx - 1)
	}

	public static void HandleAutoComplete(TestObject textField, String input, TestObject loader, TestObject dropdownOption, BaseSelectionStrategy strategy = null) throws StepFailedException {
		WebUI.sendKeys(textField, input)

		WebUI.click(textField)

		TimeLoggerUtil.LogAction({
			return WebUI.waitForElementNotVisible(loader, 3)
		},
		"Loader",
		"disappear");

		TimeLoggerUtil.LogAction({
			return WebUI.waitForElementPresent(dropdownOption, 3, FailureHandling.STOP_ON_FAILURE);
		},
		"Dropdown option",
		"become present")

		BaseSelectionStrategy selectionStrategy = strategy;
		if (strategy == null)
			selectionStrategy = new BaseSelectionStrategy(input);

		selectionStrategy.doSelect(dropdownOption);
	}

	public static void HandleRefreshButton(TestObject refreshButton) {
		WebUI.waitForElementClickable(refreshButton, 2)

		WebUI.click(refreshButton)

		WebUI.waitForElementVisible(refreshButton, 1)

		WebUI.waitForElementNotVisible(refreshButton, 5)
	}

	public static void HandleSaveButton(TestObject saveButton) throws StepFailedException {
		this.HandleSaveButton(saveButton, true);
	}

	public static void HandleSaveButton(TestObject saveButton, boolean shouldSuccessfullySave) throws StepFailedException {
		WebUI.scrollToElement(saveButton, 3)

		TimeLoggerUtil.LogAction({
			return WebUI.waitForElementClickable(saveButton, 3, FailureHandling.STOP_ON_FAILURE);
		},
		"Save button",
		"become clickable");

		WebUI.click(saveButton)

		TimeLoggerUtil.LogAction({
			return WebUI.waitForElementHasAttribute(saveButton, GeneralWebUIUtils.DISABLED, 5, FailureHandling.STOP_ON_FAILURE);
		},
		"Save button",
		"disable");

		if (shouldSuccessfullySave) {
			TimeLoggerUtil.LogAction({
				return WebUI.waitForElementNotPresent(saveButton, 5, FailureHandling.STOP_ON_FAILURE);
			},
			"Save button",
			"disappear from the DOM");

			TimeLoggerUtil.LogAction({
				WebUI.waitForPageLoad(5);
				return true;
			},
			"Page",
			"load")
		}
	}

	public static void ScrollDropdownOptionIntoView(TestObject to) {
		WebUI.executeJavaScript("arguments[0].scrollIntoView({block: 'center'})", [WebUiCommonHelper.findWebElement(to, 3)]);

		WebUI.waitForElementNotPresent(to, 1, FailureHandling.OPTIONAL);
		WebUI.waitForElementVisible(to, 2);
	}

	public static void GoToTab(TestObject tab, TestObject tabHeader, TestObject loader) throws StepFailedException {
		WebUI.scrollToElement(tab,
				1,
				FailureHandling.STOP_ON_FAILURE)

		WebUI.click(tab,
				FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementPresent(tabHeader,
				3,
				FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(loader,
				3,
				FailureHandling.STOP_ON_FAILURE)
	}

	public static int GetTabModelCount(Closure onVisitTab, TestObject numberWidget) {
		onVisitTab();

		WebUI.scrollToElement(numberWidget, 1, FailureHandling.STOP_ON_FAILURE)

		TimeLoggerUtil.LogAction({
			return WebUI.waitForElementVisible(numberWidget, 2);
		},
		"Number widget",
		"become visible");


		return NumberUtils.ParseInt(WebUI.getText(numberWidget));
	}


	public static boolean VerifyErrorModal(TestObject errorModal, String expectedMessage) {
		WebUI.waitForElementVisible(errorModal,
				3,
				FailureHandling.STOP_ON_FAILURE);

		final String actualErrorMessage = WebUI.getText(errorModal)

		return Assert.assertTrue(actualErrorMessage.contains(expectedMessage),
				"Expected error modal to contain the following text: '${expectedMessage}'\nActual error modal message: '${actualErrorMessage}'");
	}


	public static boolean VerifyElementText(TestObject element, String text) {
		final String elementText = WebUI.getText(element);

		return Assert.assertTrue(elementText.toLowerCase().contains(text.toLowerCase()),
				"Element ${element.getObjectId()} had text '${elementText}', which does NOT contain the text '${text}'");
	}

	public static void HandleConfirmableModal(TestObject modal, TestObject confirmButton) {
		this.HandleConfirmableModal(modal,
				null,
				null,
				confirmButton,
				this.OnWaitForDisappear(WebUI.&waitForElementNotVisible, 4),
				)
	}

	public static void HandleConfirmableModal(TestObject modal, TestObject confirmButton, Closure<Boolean> onDone) {
		this.HandleConfirmableModal(modal,
				null,
				null,
				confirmButton,
				onDone,
				)
	}

	public static void HandleConfirmableModal(TestObject modal, Closure onModalAction, TestObject confirmButton) {
		this.HandleConfirmableModal(modal,
				null,
				onModalAction,
				confirmButton,
				this.OnWaitForDisappear(WebUI.&waitForElementNotVisible, 4),
				)
	}

	public static void HandleConfirmableModal(TestObject modal, TestObject modalBody, Closure onModalAction, TestObject confirmButton) {
		this.HandleConfirmableModal(modal,
				modalBody,
				onModalAction,
				confirmButton,
				this.OnWaitForDisappear(WebUI.&waitForElementNotVisible, 4),
				)
	}

	public static void HandleConfirmableModal(TestObject modal, TestObject modalBody, Closure onModalAction, TestObject confirmButton, Closure<Boolean> onDone) {
		WebUI.waitForElementPresent(modal, 3);

		WebUI.waitForElementVisible(modal, 3);

		if (modalBody != null)
			WebUI.waitForElementVisible(modalBody, 5);

		if (onModalAction != null)
			onModalAction(modalBody);

		WebUI.click(confirmButton);

		Closure<Boolean> onWaitForModalDisappear = onDone;
		if (onDone == null)
			onWaitForModalDisappear = this.OnWaitForDisappear(WebUI.&waitForElementNotVisible, 4)

		onWaitForModalDisappear(modal);
	}

	public static Closure<Boolean> OnWaitForDisappear(Closure<Boolean> onDisappear, int timeOut) {
		return onDisappear.rcurry(timeOut);
	}

	public static boolean VerifyTextFieldEmpty(TestObject textField) {
		return WebUI.verifyMatch(GeneralWebUIUtils.GetTextValue(textField), "", false)
	}

	public static boolean HasClassName(TestObject to, String className, FailureHandling failureHandling = FailureHandling.STOP_ON_FAILURE) {
		boolean elementHasClassName = WebUI.getAttribute(to, this.ClassNameList).contains(className);
		if (failureHandling.equals(FailureHandling.STOP_ON_FAILURE))
			return Assert.assertTrue(elementHasClassName,
					"Element '${to.getObjectId()}' does NOT have class named '${className}");
		return elementHasClassName;
	}

	public static void ScrollDown(TestObject topRow, TestObject bottomRow) {
		WebUI.scrollToPosition(0,
				WebUI.getViewportTopPosition() + WebUI.getViewportHeight() - (WebUI.getElementHeight(topRow) + WebUI.getElementHeight(bottomRow)));
	}
}
