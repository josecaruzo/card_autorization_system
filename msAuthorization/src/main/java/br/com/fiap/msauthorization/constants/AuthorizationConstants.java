package br.com.fiap.msauthorization.constants;

public final class AuthorizationConstants {
	private AuthorizationConstants(){}
	// Exception messages
	public static final String CARD_NOT_FOUND = "Cartão de crédito inválido";
	public static final String VALIDATION_FAILED = "A validação do cartão falhou";
	public static final String INSUFFICIENT_LIMIT = "Limite insuficiente para esta compra";
	public static final String AUTHORIZATIONS_NOT_FOUND = "Não foi encontrada nenhuma compra para o cliente";

	// Authorization constant descriptions
	public static final String VALIDATION_FAILED_DESCRIPTION = "Compra rejeitada por validação do cartão";
	public static final String INSUFFICIENT_LIMIT_DESCRIPTION = "Compra rejeitada por falta de limite";
	public static final String SUCCESSFUL_TRANSACTION_DESCRIPTION = "Compra realizada com sucesso";

	// Card Constants
	public static final Integer BIN_LENGTH = 6; // Card Bin reference, default 6 characters
	public static final Integer CARD_SUFFIX = 4; // Card clear suffix digits, default = 4
}
