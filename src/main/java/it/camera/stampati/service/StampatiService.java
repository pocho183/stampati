package it.camera.stampati.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import it.camera.stampati.domani.Stampato;
import it.camera.stampati.model.TypographyToProcessModel;
import it.camera.stampati.repository.StampatiRepository;

public class StampatiService {
	
	@Autowired
	private StampatiRepository stampatiRepository;
	

	public List<TypographyToProcessModel> getStampatiToProcess(String leg, String format) {
		List<TypographyToProcessModel> stampatiToProcess = new ArrayList<>();
		List<Stampato> barcodes = stampatiRepository.findByLegislaturaIdAndNotDeleted(leg);
		// ToDO
		return stampatiToProcess;
	}
}