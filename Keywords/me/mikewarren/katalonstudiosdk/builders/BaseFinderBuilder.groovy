package me.mikewarren.katalonstudiosdk.builders

import groovy.transform.InheritConstructors

@InheritConstructors
public abstract class BaseFinderBuilder<T> extends BaseModelBuilder<T> implements Finder {

	public T createModelFromID(long id) {
		return this.createModelFromRowNum(this.findIndex(id));
	}
}
