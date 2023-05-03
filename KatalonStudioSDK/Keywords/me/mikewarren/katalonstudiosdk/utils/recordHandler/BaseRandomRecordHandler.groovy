package me.mikewarren.katalonstudiosdk.utils.recordHandler

import com.kms.katalon.core.exception.StepFailedException

import groovy.transform.InheritConstructors

@InheritConstructors
public abstract class BaseRandomRecordHandler<T> extends BaseRecordHandler<T> {
	/**
	 *
	 * @param onGenerate
	 * @param onCheckDuplicate
	 * @return a randomly generated record not already recorded in the system
	 */
	protected T nextRecord(Closure<T> onGenerate, Closure<Boolean> onCheckDuplicate) {
		T record;
		// NOTE: this is the closest we can get to a do-while loop in this bizarre language dialect
		while ({
			// action
			record = onGenerate();
			// condition
			return ((this.usedRecords.size() > 0) && (onCheckDuplicate(record)))
		}());

			return record;
	}

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
