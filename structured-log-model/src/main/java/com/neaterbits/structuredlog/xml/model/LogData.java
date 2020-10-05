package com.neaterbits.structuredlog.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LogData {

	private String type;
	
	private List<LogDataEntry> entries;

	public LogData() {

	}
	
	public LogData(String type, List<LogDataEntry> entries) {
		this.type = type;
		this.entries = entries;
	}

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name="entry")
	public List<LogDataEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<LogDataEntry> entries) {
		this.entries = entries;
	}
}
