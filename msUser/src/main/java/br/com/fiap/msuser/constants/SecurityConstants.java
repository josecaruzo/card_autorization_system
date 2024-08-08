package br.com.fiap.msuser.constants;

public final class SecurityConstants {
	private SecurityConstants(){}

	public static final Integer TOKEN_EXPIRATION_TIME = 2; //Expiration time in minutes
	public static final String USER_NOT_FOUND = "Usuário %s não encontrado"; // User not found

	public static final String[] ENDPOINTS_WITHOUT_AUTH = {
			"/api/autenticacao",
			"/v3/api-docs/**",
			"/swagger-resources/**",
			"/swagger-ui/index.html/**",
			"/swagger-ui/index.html",
			"/configuration/ui",
			"/configuration/security",
			"/swagger-ui/**",
			"/h2-console/**"
	};
}
