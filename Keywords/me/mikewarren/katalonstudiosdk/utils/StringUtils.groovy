package me.mikewarren.katalonstudiosdk.utils

import com.github.javafaker.Address
import com.github.javafaker.Faker
import com.github.javafaker.Name


public final class StringUtils {
	private static Name NameFactory = new Faker().name();
	private static Address AddressFactory = new Faker().address();

	public static String UNDERSCORE_WORD_BREAK = '_';

	public static String CURRENT_TEST_CASE_KEY = "current_testcase";

	public static String DefaultToEmptyString(String str) {
		if (str == null)
			return '';
		return str;
	}

	public static boolean IsNullOrEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}

	public static String CreateFirstName() {
		return this.NameFactory.firstName();
	}

	public static String CreateLastName() {
		return this.NameFactory.lastName();
	}

	public static String CreateZip() {
		String zip = "";

		while ((zip.equals("")) || (zip.indexOf('-') > -1))
			zip = this.AddressFactory.zipCode();

		return zip;
	}

	public static String GetFullName(String firstName, String lastName) {
		if (lastName.equals(""))
			return firstName;
		return "${firstName} ${lastName}";
	}

	public static String GetClassName(Object obj) {
		final String fullyQualifiedClassName = obj.getClass().getName();
		return fullyQualifiedClassName
				.substring(fullyQualifiedClassName.lastIndexOf('.') + 1);
	}

	public static String GetBenchmarkFilename(String testCaseId) {
		return "./${testCaseId.replace("Test Cases/", "Benchmark Data/")}.xlsx";
	}

	public static String GetURLPattern(String url) {
		return (/^(http(s)?\:\/\/)?${url}(\/)?$/);
	}
}
