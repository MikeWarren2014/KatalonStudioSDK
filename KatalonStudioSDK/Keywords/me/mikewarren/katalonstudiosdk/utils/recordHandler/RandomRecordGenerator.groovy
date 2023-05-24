package com.signaturemd.utils.recordHandler

public trait RandomRecordGenerator<T> {
	public abstract List<T> getUsedRecords();
	
	public T nextRecord(Closure<T> onGenerate) { 
		return onGenerate();
	}
	
	/**
	 *
	 * @param onGenerate
	 * @param onCheckDuplicate
	 * @return a randomly generated record not already recorded in the system
	 */
	public T nextRecord(Closure<T> onGenerate, Closure<Boolean> onCheckDuplicate) {
		if (onCheckDuplicate == null)
			return this.nextRecord(onGenerate);
		
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
}
