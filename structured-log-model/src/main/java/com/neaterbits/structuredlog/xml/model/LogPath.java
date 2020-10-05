package com.neaterbits.structuredlog.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class LogPath {

	private List<String> entries;

	public LogPath() {

	}

	public LogPath(List<String> entries) {
		this.entries = entries;
	}

	@XmlElement(name="entry")
	public List<String> getEntries() {
		return entries;
	}

	public void setEntries(List<String> entries) {
		this.entries = entries;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogPath other = (LogPath) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		return true;
	}
}
