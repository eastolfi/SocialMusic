package es.edu.android.socialmusic.constants;

public interface IConstantsCode {
	//TODO Pasarlo a strings.xml?
	
	// RETURN ERROR CODES --> 00X
	public static final String ERROR_CODE_EXISTING_USER = "001";
	public static final String ERROR_VALUE_EXISTING_USER = "El nombre de usuario debe ser unico";
	public static final String ERROR_CODE_LOGIN_FAILED = "002";
	public static final String ERROR_VALUE_LOGIN_FAILED = "Error al iniciar sesion";
	
	// RETURN SUCCESS CODES --> 10X
	public static final String SUCCESS_CODE_REGISTER = "101";
	public static final String SUCCESS_VALUE_REGISTER = "Usuario creado correctamente";
	public static final String SUCCESS_CODE_LOGIN = "102";
	public static final String SUCCESS_VALUE_LOGIN = "Usuario logeado correctamente";
	
	// GENERIC ERROR CODES --> 20X
	public static final String ERROR_CODE_RESPONSE_NULL = "201";
	public static final String ERROR_VALUE_RESPONSE_NULL = "No hay respuesta";
	public static final String ERROR_CODE_SIMULATE_NETWORK = "202";
	public static final String ERROR_VALUE_SIMULATE_NETWORK = "Simulate network access";
	
	// OTHER CODES --> 30X
	
}
