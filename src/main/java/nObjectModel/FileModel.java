package nObjectModel;

import java.sql.Date;
/**
 * File object entity
 * Contains fields which can hold all file's information, and accessed through getters and setters.
 * 
 * @author ottoma
 *
 */
public class FileModel {
	private int fileID;
	private String fileName = null;
	private String username = null;
	private String fileLength = null;
	private String ownderID = null;
	private String createdOn = null;
	private Date expiredDate = null;
	private String filePath = null;
	private byte[] pathByte;
	private byte[] ivByte;
	private byte[] saltByte;
	
	public String getFileName() {
		return fileName;
	}
	public String getUserName() {
		return username;
	}
	public String getFileLength() {
		return fileLength;
	}
	public String getOwnderID() {
		return ownderID;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public byte[] getPathByte() {
		return pathByte;
	}
	public byte[] getIvByte() {
		return ivByte;
	}
	public byte[] getSaltByte() {
		return saltByte;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setUserName(String userName) {
		this.username = userName;
	}
	public void setFileLength(String fileLength) {
		this.fileLength = fileLength;
	}
	public void setOwnderID(String ownderID) {
		this.ownderID = ownderID;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public void setPathByte(byte[] pathByte) {
		this.pathByte = pathByte;
	}
	public void setIvByte(byte[] ivByte) {
		this.ivByte = ivByte;
	}
	public void setSaltByte(byte[] saltByte) {
		this.saltByte = saltByte;
	}
	public Date getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public int getFileID() {
		return fileID;
	}
	public void setFileID(int fileID) {
		this.fileID = fileID;
	}
	
}
