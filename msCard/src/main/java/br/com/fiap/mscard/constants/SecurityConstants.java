package br.com.fiap.mscard.constants;

public final class SecurityConstants {
	private SecurityConstants(){}

	public static final Integer TOKEN_EXPIRATION_TIME = 2; //Expiration time in minutes
	public static final String USER_NOT_FOUND = "Usuário %s não encontrado"; // User not found

	public static final String COMMON_USER = "COMMON";

	public static final String[] ENDPOINTS_WITHOUT_AUTH = {
			"/v3/api-docs/**",
			"/swagger-resources/**",
			"/swagger-ui/index.html/**",
			"/swagger-ui/index.html",
			"/configuration/ui",
			"/configuration/security",
			"/swagger-ui/**",
			"/h2-console/**"
	};

	public static final String[] ENDPOINTS_WITH_COMMON_AUTH = {
			"/api/cartao/**"
	};
}
