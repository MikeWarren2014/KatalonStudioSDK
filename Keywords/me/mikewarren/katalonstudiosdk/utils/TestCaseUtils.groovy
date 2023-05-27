package me.mikewarren.katalonstudiosdk.utils

import org.testng.Assert



















public class TestCaseUtils {
	public static void ValidatePrerequisite(Closure<Boolean> onCheckPrerequisite, String errorMessage) {
		Assert.assertTrue(onCheckPrerequisite(), errorMessage);
	}
}
