package me.mikewarren.katalonstudiosdk.utils

import java.text.NumberFormat

import com.github.javafaker.Faker





public final class NumberUtils {
	private static NumberFormat CurrencyFormat = NumberFormat.getCurrencyInstance();
	private static com.github.javafaker.Number NumberFactory = new Faker().number();

	private static final double MemberNetAmount = 1800.0;
	public static final int MemberVarCount = 4;
	public static final int FirstMemberVarTierCutoff = 2;
	public static final int FirstPercentageTierCutoff = 50;

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

	public static long NextNPINumberStartingWith(int digit) {
		return (Math.random() + digit) * (1E9);
	}

	public static int parseInt(String numberString) {
		if (numberString.equals(""))
			return 0;

		return Integer.parseInt(numberString);
	}

	public static double parseDouble(String numberString) {
		if ((numberString == null) || (numberString.equals("")))
			return 0.0;

		if (numberString.contains(','))
			return Double.parseDouble(numberString.replace(',', ''))

		return Double.parseDouble(numberString);
	}
}
