package it.camera.stampati.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName="ultimiStampati")
public class ExtractResponseModel implements Serializable {

	private static final long serialVersionUID = -3263557963662089835L;

	@JacksonXmlProperty(localName="item")
	@JacksonXmlElementWrapper(useWrapping = false)
	public List<ExtractModel> ultimiStampati;

	public ExtractResponseModel() { }
}
