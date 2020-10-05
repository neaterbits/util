package com.neaterbits.structuredlog.xml.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class LogIO {

	private final JAXBContext jaxbContext;
	
	public LogIO() {

		try {
			this.jaxbContext = JAXBContext.newInstance(Log.class, LogEntry.class, LogData.class, LogDataEntry.class, LogPath.class);
		} catch (JAXBException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	public void writeLog(Log log, OutputStream outputStream) throws IOException, JAXBException {
		
		final Marshaller marshaller = jaxbContext.createMarshaller();
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		marshaller.marshal(log, outputStream);
	}
	
	public Log readLog(InputStream inputStream) throws JAXBException {
		
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		
		return (Log)unmarshaller.unmarshal(inputStream);
	}
}
