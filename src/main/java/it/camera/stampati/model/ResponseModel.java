package it.camera.stampati.model;

import java.io.Serializable;

public abstract class ResponseModel implements Serializable {

	private static final long serialVersionUID = -7222760384017723791L;

	public Integer status;

	protected ResponseModel(int status) {
		this.status = status;
	}
}
