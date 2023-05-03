package me.mikewarren.katalonstudiosdk.models.benchmark

import me.mikewarren.katalonstudiosdk.models.BaseModel

public class BenchmarkModel extends BaseModel {
	private Date submitTime;
	private String timeElapsed;

	public BenchmarkModel() {
	}

	public BenchmarkModel(Date submitTime, String timeElapsed) {
		super();
		this.submitTime = submitTime;
		this.timeElapsed = timeElapsed;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public String getTimeElapsed() {
		return timeElapsed;
	}

	public void setTimeElapsed(String timeElapsed) {
		this.timeElapsed = timeElapsed;
	}
}
