package it.camera.stampati.model;

public class ErrorResponseModel extends ResponseModel {

	private static final long serialVersionUID = 8639275565838973538L;
	public String message;

	public ErrorResponseModel(int status) {
		super(status);
	}

	public ErrorResponseModel(String message) {
		super(0);
		this.message = message;
	}
}
