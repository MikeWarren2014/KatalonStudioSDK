package me.mikewarren.katalonstudiosdk.utils

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.util.KeywordUtil

import me.mikewarren.katalonstudiosdk.models.benchmark.BenchmarkModel
import me.mikewarren.katalonstudiosdk.utils.recordHandler.BaseBenchmarkRecordHandler

public final class TimeLoggerUtil {
	public static boolean LogAction(Closure<Boolean> onAction, String elementDesc, String expectationDesc) throws StepFailedException {
		final long startTime = System.currentTimeMillis();

		try {
			if (onAction()) {
				KeywordUtil.logInfo("${elementDesc} took ${(System.currentTimeMillis() - startTime) / 1000} seconds to ${expectationDesc}");
				return true;
			} else {
				KeywordUtil.markWarning("${elementDesc} didn't ${expectationDesc} after ${(System.currentTimeMillis() - startTime) / 1000} seconds");
			}
		} catch (StepFailedException ex) {
			KeywordUtil.markFailedAndStop("${elementDesc} didn't ${expectationDesc} after ${(System.currentTimeMillis() - startTime) / 1000} seconds...\n${ex.getMessage()}");
			throw ex;
		}

		return false;
	}

	public static void BenchmarkAction(Closure<Boolean> onAction, BaseBenchmarkRecordHandler benchmarkHandler, String elementDesc, String expectationDesc) throws StepFailedException {
		final long startTime = System.currentTimeMillis();
		String benchmarkOutput;
		StepFailedException exception;

		boolean success = false;

		try {
			success = (this.LogAction(onAction, elementDesc, expectationDesc));
		} catch (StepFailedException ex) {
			exception = ex;
		} finally {
			String timestampStr = "${(System.currentTimeMillis() - startTime) / 1000}";
			if (success)
				benchmarkOutput = timestampStr;
			else
				benchmarkOutput = "failed after ${timestampStr} seconds";
		}

		benchmarkHandler.handle(new BenchmarkModel(new Date(), benchmarkOutput));

		if (exception != null)
			throw exception;
	}
}
