package it.camera.stampati.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "trial")
public class TrialController {

	@GetMapping(path = "hello")
	public ResponseEntity<String> call() {
		return new ResponseEntity<String>("Here I am", HttpStatus.OK);
	}
}
