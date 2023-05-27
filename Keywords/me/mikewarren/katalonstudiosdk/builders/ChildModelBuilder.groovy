package me.mikewarren.katalonstudiosdk.builders

import java.util.stream.Collectors

import groovy.transform.InheritConstructors

@InheritConstructors
public abstract class ChildModelBuilder<T> extends BaseFinderBuilder<T> {

	@Override
	public int getMatchKeyIdx() {
		return 2;
	}

	private List<Integer> findModelIndices(long parentID) {
		List<Integer> indices = [];
		for (int j = 0; j < this.testData.getRowNumbers(); j++) {
			if (Long.parseLong(this.testData.getValue(this.getMatchKeyIdx(), j + 1)) == parentID)
				indices.add(j);
		}
		return indices;
	}

	public List<T> createModelsForParentID(long parentID) {
		return this.findModelIndices(parentID).stream()
				.map { idx -> this.createModelFromRowNum(idx) }
				.collect(Collectors.toList())
	}
}
