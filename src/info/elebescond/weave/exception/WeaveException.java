/**
 * 
 * Source borrowed from the jweave project
 * (http://code.google.com/p/jweave/source/browse/trunk/jweave/src/main/java/info/elebescond/weave/exception/WeaveException.java) 
 * 
 * Licensed under Mozilla Public License 1.1 (http://www.mozilla.org/MPL/)
 */
package info.elebescond.weave.exception;

public class WeaveException extends Exception {

	public static enum Type {
		WEAVE_ERROR_INVALID_PROTOCOL, WEAVE_ERROR_INCORRECT_CAPTCHA, WEAVE_ERROR_INVALID_USERNAME, WEAVE_ERROR_NO_OVERWRITE, WEAVE_ERROR_USERID_PATH_MISMATCH, WEAVE_ERROR_JSON_PARSE, WEAVE_ERROR_MISSING_PASSWORD, WEAVE_ERROR_INVALID_WBO, WEAVE_ERROR_BAD_PASSWORD_STRENGTH, WEAVE_ERROR_INVALID_RESET_CODE, WEAVE_ERROR_FUNCTION_NOT_SUPPORTED, WEAVE_ERROR_NO_EMAIL, WEAVE_UNKNOWN_ERROR
	}

	private static final long serialVersionUID = 1L;

	private Type type;

	public WeaveException() {
		super();
		this.type = Type.WEAVE_UNKNOWN_ERROR;
	}

	public WeaveException(String message) {
		super(message);
		this.type = Type.WEAVE_UNKNOWN_ERROR;
	}

	public WeaveException(String message, Throwable cause) {
		super(message, cause);
		this.type = Type.WEAVE_UNKNOWN_ERROR;
	}

	public WeaveException(String message, Type type) {
		super(message + " Type: " + type);
		this.type = type;
	}

	public WeaveException(String message, Type type, Throwable cause) {
		super(message + " Type: " + type, cause);
		this.type = type;
	}

	public WeaveException(Type type) {
		super();
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}