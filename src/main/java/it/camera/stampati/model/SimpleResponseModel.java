package it.camera.stampati.model;

import java.util.List;

public class SimpleResponseModel extends ResponseModel {

	private static final long serialVersionUID = -3263557963662063835L;
	public List<StampatiModel> content;

	public SimpleResponseModel() {
		super(1);
	}
}
