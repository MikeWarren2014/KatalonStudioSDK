package me.mikewarren.katalonstudiosdk.factories

public abstract class BaseModelFactory<T> {

	public abstract T createSanityCase();

	public T changeChildModel(T model, Object childModel, Closure onChangeChildModel) {
		onChangeChildModel(childModel);

		return model;
	}
}
