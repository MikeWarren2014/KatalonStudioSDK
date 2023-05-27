package me.mikewarren.katalonstudiosdk.builders

import java.util.stream.Collectors

import com.kms.katalon.core.testdata.TestData

public abstract class BaseModelBuilder<T> {

	protected TestData testData;

	public BaseModelBuilder() {
		this.init();
	}

	public BaseModelBuilder(TestData testData) {
		this.testData = testData;
		this.init();
	}

	protected void init() {
		// implementation go here
	}

	public TestData getTestData() {
		return this.testData;
	}

	public List<T> createModels() {
		return this.testData.getAllData()
				.stream()
				.filter { row ->
					row.stream().anyMatch { String cell ->
						cell != null && !cell.isEmpty()
					}
				}
				.map { row -> this.createModelFromRow(row) }
				.collect(Collectors.toList())
	}

	public T createModelFromRowNum(int rowNum) {
		return this.createModelFromRow(this.testData.getAllData().get(rowNum))
	}

	public abstract T createModelFromRow(List<Object> row)
}
