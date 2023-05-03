package me.mikewarren.katalonstudiosdk.enums

public enum SubmitType {
	CLICK_BUTTON("Click Button"), PRESS_ENTER("Press ENTER");

	public final String textValue;

	public SubmitType(String textValue) {
		this.textValue = textValue;
	}
}
