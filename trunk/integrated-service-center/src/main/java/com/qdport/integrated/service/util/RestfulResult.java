package com.qdport.integrated.service.util;

/**
 * Restful返回信息封装类
 * 
 * @author wangxiaolong
 *
 * @param <T>
 * 
 * @since 2019-07-10
 */
public class RestfulResult<T> {
	private int errorCode;
	private String errMsg;
	private T value;

	public RestfulResult(T value) {
		super();
		this.value = value;
	}

	public RestfulResult(int errorCode, String errMsg) {
		super();
		this.errorCode = errorCode;
		this.errMsg = errMsg;

	}

	public RestfulResult(int errorCode, String errMsg, T value) {
		super();
		this.errorCode = errorCode;
		this.errMsg = errMsg;
		this.value = value;
	}

	public static RestfulResult<String> error(int errorCode, String errMsg) {
		RestfulResult<String> restfulResult = new RestfulResult<>(errorCode, errMsg);
		return restfulResult;
	}

	public static RestfulResult<String> success() {
		RestfulResult<String> restfulResult = new RestfulResult<>("success");
		return restfulResult;
	}

	public static <T> RestfulResult<T> success(T data) {
		RestfulResult<T> restfulResult = new RestfulResult<>(data);
		return restfulResult;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
