package me.mikewarren.katalonstudiosdk.builders

import com.kms.katalon.core.testdata.TestData

public trait Finder {

	public abstract TestData getTestData();

	public int getMatchKeyIdx() {
		return 1;
	}

	// NOTE: this should really be protected visibility, but Groovylang doesn't support that
	public int findIndex(long foreignKey) {
		for (int rowIdx = 0; rowIdx < this.getTestData().getRowNumbers(); rowIdx++) {
			if (Long.parseLong(this.getTestData().getValue(this.getMatchKeyIdx(), rowIdx + 1)) == foreignKey) {
				return rowIdx;
			}
		}

		return -1;
	}
}
