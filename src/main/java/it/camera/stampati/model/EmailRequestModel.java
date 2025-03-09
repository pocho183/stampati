package it.camera.stampati.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequestModel {

	private String titleFel;
    private String title;
    private List<String> emails;
    private String numeriPDL;
}
