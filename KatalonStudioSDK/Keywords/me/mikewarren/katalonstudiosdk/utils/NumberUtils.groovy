package me.mikewarren.katalonstudiosdk.utils

import java.text.NumberFormat

import com.github.javafaker.Faker





public final class NumberUtils {
	private static NumberFormat CurrencyFormat = NumberFormat.getCurrencyInstance();
	private static com.github.javafaker.Number NumberFactory = new Faker().number();

	public static String ToCurrencyString(double amount, boolean wantSpaceDelimiter = true) {
		StringBuffer buffer = new StringBuffer(this.CurrencyFormat.format(amount))
		if (wantSpaceDelimiter)
			buffer.insert(1, ' ')
		return buffer.toString()
	}

	public static String ToWholeDollarString(double amount, boolean wantSpaceDelimiter) {
		String currencyString = this.ToCurrencyString(amount, wantSpaceDelimiter)
		return currencyString.substring(0, currencyString.indexOf('.'))
	}

	public static String TenDigitPhoneNumber() {
		return new Faker().phoneNumber().subscriberNumber(10);
	}

	public static long NextLong(int digitCount) {
		return this.NumberFactory.randomNumber(digitCount, true);
	}

	public static int ParseInt(String numberString) {
		if (numberString.equals(""))
			return 0;

		return Integer.parseInt(numberString);
	}

	public static double ParseDouble(String numberString) {
		if ((numberString == null) || (numberString.equals("")))
			return 0.0;

		if (numberString.contains(','))
			return Double.parseDouble(numberString.replace(',', ''))

		return Double.parseDouble(numberString);
	}
}
