package com.jwtapp.demo.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Data {

	@Id
	private long id;
	private String filename;
	private boolean status;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Data [id=" + id + ", filename=" + filename + ", status=" + status + "]";
	}
	public Data() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Data(long id, String filename, boolean status) {
		super();
		this.id = id;
		this.filename = filename;
		this.status = status;
	}
	
}
