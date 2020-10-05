package com.neaterbits.structuredlog.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Log {

	private List<LogEntry> entries;
	private List<LogPath> paths;

	@XmlElement(name="logEntry")
	public List<LogEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<LogEntry> entries) {
		this.entries = entries;
	}

	@XmlElementWrapper(name="paths")
	@XmlElement(name="path")
	public List<LogPath> getPaths() {
		return paths;
	}

	public void setPaths(List<LogPath> paths) {
		this.paths = paths;
	}
}
