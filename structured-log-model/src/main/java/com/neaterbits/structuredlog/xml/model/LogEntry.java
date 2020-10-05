package com.neaterbits.structuredlog.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LogEntry {

	private String logMessage;

	private Integer pathIndex;
	
	private List<LogData> data;

	public LogEntry() {

	}

	public LogEntry(Integer pathIndex, String logMessage) {
		this.logMessage = logMessage;
		this.pathIndex = pathIndex;
	}

	@XmlElement
	public String getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	@XmlElement
	public Integer getPathIndex() {
		return pathIndex;
	}

	public void setPathIndex(Integer pathIndex) {
		this.pathIndex = pathIndex;
	}

	@XmlElement(name="data")
	public List<LogData> getData() {
		return data;
	}

	public void setData(List<LogData> data) {
		this.data = data;
	}
}
