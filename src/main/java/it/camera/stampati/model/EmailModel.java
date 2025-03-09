package it.camera.stampati.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailModel {

	@NotEmpty
	private List<String> recipient;
	private List<String> recipientCC;
	private List<String> recipientCCn;
	@Valid
	@NotNull
	private SenderModel sender;
	private String subject;
	private String body;

}