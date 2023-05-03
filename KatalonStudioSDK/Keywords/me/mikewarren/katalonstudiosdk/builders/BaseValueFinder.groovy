package me.mikewarren.katalonstudiosdk.builders

import com.kms.katalon.core.testdata.TestData

public abstract class BaseValueFinder<T> implements Finder {

	protected TestData testData;

	public BaseValueFinder() {
		this.init();
	}

	public BaseValueFinder(TestData testData) {
		this.testData = testData;
		this.init();
	}

	protected void init() {
		// implementation go here
	}

	@Override
	public TestData getTestData() {
		return this.testData;
	}

	public abstract int getValueIdx();

	public T findValueFromID(int foreignKey) {
		return this.testData.getObjectValue(this.getValueIdx(), this.findIndex(foreignKey) + 1);
	}
}
