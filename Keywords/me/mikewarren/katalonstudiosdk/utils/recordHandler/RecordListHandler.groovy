package me.mikewarren.katalonstudiosdk.utils.recordHandler

import com.kms.katalon.core.exception.StepFailedException

import groovy.transform.InheritConstructors

@InheritConstructors
public abstract class RecordListHandler<T> extends BaseRandomRecordHandler<T> {

	@Override
	protected void handleFailure(StepFailedException ex, Closure onSubmit, Closure onCheckDuplicate, Closure<T> onRetry,
			List<T> newRecords) throws StepFailedException {
		int duplicateRecordIdx = onCheckDuplicate(ex);

		if (duplicateRecordIdx > -1) {
			newRecords.push(onRetry(duplicateRecordIdx));
		} else {
			newRecords.pop();
			throw ex;
		}
	}
}
