package me.mikewarren.katalonstudiosdk.utils.recordHandler

import com.kms.katalon.core.exception.StepFailedException

import groovy.transform.InheritConstructors

@InheritConstructors
public abstract class BaseRandomRecordHandler<T> extends BaseRecordHandler<T> implements RandomRecordGenerator<T> {


	/**
	 *
	 * @param records
	 * @param onSubmit
	 * @param onCheckDuplicate
	 * @param onRetry
	 */
	public void handle(List<T> records, Closure onSubmit, Closure<Boolean> onCheckDuplicate, Closure<T> onRetry) {
		boolean keepTrying = true;

		while(keepTrying) {
			try {
				onSubmit();
				keepTrying = false;
			} catch (StepFailedException ex) {
				this.handleFailure(ex, onSubmit, onCheckDuplicate, onRetry, records);
			}
		}

		for (T record : records)
			this.handle(record);
	}

	protected abstract void handleFailure(StepFailedException ex, Closure onSubmit, Closure<Boolean> onCheckDuplicate, Closure<T> onRetry, List<T> newRecords) throws StepFailedException;
}
