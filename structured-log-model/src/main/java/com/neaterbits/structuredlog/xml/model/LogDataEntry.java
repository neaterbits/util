package com.neaterbits.structuredlog.xml.model;


import javax.xml.bind.annotation.XmlElement;

public class LogDataEntry {

	private Integer pathIndex;
	private String data;

	public LogDataEntry() {

	}
	
	public LogDataEntry(Integer pathIndex, String data) {
		this.pathIndex = pathIndex;
		this.data = data;
	}

	@XmlElement
	public Integer getPathIndex() {
		return pathIndex;
	}

	public void setPathIndex(Integer pathIndex) {
		this.pathIndex = pathIndex;
	}

	@XmlElement
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
