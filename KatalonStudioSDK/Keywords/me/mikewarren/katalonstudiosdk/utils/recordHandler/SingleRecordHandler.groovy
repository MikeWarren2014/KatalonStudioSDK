package me.mikewarren.katalonstudiosdk.utils.recordHandler

import com.kms.katalon.core.exception.StepFailedException

import groovy.transform.InheritConstructors

@InheritConstructors
public abstract class SingleRecordHandler<T> extends BaseRandomRecordHandler<T> {

	/**
	 * @param record
	 * @param onSubmit @throws StepFailedException
	 * @param onCheckDuplicate
	 * @param onRetry
	 */
	public void handle(T record, Closure onSubmit, Closure<Boolean> onCheckDuplicate, Closure<T> onRetry) {
		this.handle([record], onSubmit, onCheckDuplicate, onRetry);
	}

	@Override
	protected void handleFailure(StepFailedException ex, Closure onSubmit, Closure<Boolean> onCheckDuplicate, Closure<T> onRetry,
			List<T> newRecords) throws StepFailedException {
		if (onCheckDuplicate(ex)) {
			newRecords.add(onRetry());
			return;
		}

		newRecords.pop();
		throw ex;
	}
}
