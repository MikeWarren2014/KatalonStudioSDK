package me.mikewarren.katalonstudiosdk.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

import com.github.javafaker.DateAndTime
import com.github.javafaker.Faker



public final class DateUtils {
	private static final DateAndTime DateAndTimeFactory = new Faker().date();

	public static DateFormat DateFormat =  new SimpleDateFormat("MM-dd-yyyy");
	public static DateFormat DateTimeFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

	private static final int DAYS_IN_A_WEEK = 7;
	private static final int DAYS_IN_A_MONTH = 30;

	private static final int MIN_MAIN_MEMBER_AGE = 26;
	private static final int MAX_AGE = 99;

	public static final int SecondsInOneHour = TimeUnit.HOURS.toSeconds(1);
	public static final int MillisecondsInOneDay = TimeUnit.DAYS.toMillis(1);

	public static String ToDateString(Date date) {
		return this.DateFormat.format(date);
	}

	public static String ToDateTimeString(Date date) {
		return this.DateTimeFormat.format(date);
	}

	public static Date ToDate(String dateStr) {
		if (dateStr == null) return null;
		return this.DateFormat.parse(dateStr);
	}

	public static Date ToDateTime(String dateTimeStr) {
		if (dateTimeStr == null) return null;
		return this.DateTimeFormat.parse(dateTimeStr);
	}

	public static Date GetEndDate(Date startDate, int years) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.YEAR, years);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	private static Date GenerateDateDifferentFrom(Date referenceDate, Closure<Date> onNextDate) {
		Date date = referenceDate;

		while ((date.equals(referenceDate)) || (this.CompareDates(date, referenceDate) == 0)) {
			date = onNextDate(referenceDate);
		}

		return date;
	}

	public static int CompareDates(Date firstDate, Date secondDate) {
		if (this.ToDateString(firstDate).equals(this.ToDateString(secondDate)))
			return 0;

		long firstDaysSinceEpoch = firstDate.getTime() / this.MillisecondsInOneDay;
		long secondDaysSinceEpoch = secondDate.getTime() / this.MillisecondsInOneDay;
		return Long.compare(firstDaysSinceEpoch, secondDaysSinceEpoch);
	}

	public static Date GetFutureDOB() {
		return this.GetEndDate(new Date(), 10);
	}

	public static Date WithinLastMonth() {
		return this.WithinLastMonthOf(new Date());
	}

	public static Date WithinLastMonthOf(Date date) {
		return this.GenerateDateDifferentFrom(date, { Date d -> this.DateAndTimeFactory.past(this.DAYS_IN_A_MONTH, TimeUnit.DAYS, d); });
	}

	public static Date WithinNextWeekOf(Date date) {
		return this.GenerateDateDifferentFrom(date, { Date d -> this.DateAndTimeFactory.future(this.DAYS_IN_A_WEEK, TimeUnit.DAYS, d); });
	}

	public static Date WithinNextMonthOf(Date date) {
		return this.GenerateDateDifferentFrom(date, { Date d -> this.DateAndTimeFactory.future(this.DAYS_IN_A_MONTH, TimeUnit.DAYS, d); });
	}

	public static Date DOB() {
		return this.DateAndTimeFactory.birthday();
	}

	public static Date DOBForUpcomingBirthday() {
		Calendar calendar = Calendar.getInstance();

		Random random = new Random();

		final int numberOfDaysLeft = calendar.getActualMaximum(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
		final int age = random.nextInt(this.MAX_AGE - this.MIN_MAIN_MEMBER_AGE) + this.MIN_MAIN_MEMBER_AGE;

		calendar.add(Calendar.DAY_OF_YEAR, random.nextInt(numberOfDaysLeft));
		calendar.add(Calendar.YEAR, -(age + 1));

		return calendar.getTime();
	}

	public static Date DOBForStandardMember() {
		return this.DateAndTimeFactory.birthday(this.MIN_MAIN_MEMBER_AGE, this.MAX_AGE)
	}

	public static Date DOBForPediatric() {
		return this.DateAndTimeFactory.birthday(16, this.MIN_MAIN_MEMBER_AGE);
	}

	public static Date AYearAgoTomorrow() {
		return this.AYearAgoPlusDays(1);
	}

	public static Date AYearAgoPlusDays(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTime();
	}

	public static int GetHoursDifference(LocalDateTime first, LocalDateTime second) {
		return (int)Math.ceil((double)(this.ToEpochSeconds(first) - this.ToEpochSeconds(second)) / this.SecondsInOneHour);
	}

	public static long ToEpochSeconds(LocalDateTime time) {
		return time.toInstant(ZoneOffset.UTC)
				.getEpochSecond();
	}

	public static Date LastYear() {
		return this.YearsAfterDate(-1, new Date());
	}

	public static Date YearsAfterDate(int years, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, years);
		return cal.getTime();
	}
}
