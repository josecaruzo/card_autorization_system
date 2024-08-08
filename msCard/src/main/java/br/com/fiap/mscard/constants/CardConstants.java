package br.com.fiap.mscard.constants;

public final class CardConstants {
	private CardConstants(){}

	// Exception messages
	public static final String CUSTOMER_HAS_TOO_MANY_CARDS = "Cliente já possui %d cartões";
	public static final String CUSTOMER_CPF_NOT_FOUND = "Não foi encontrado nenhum cliente com esse CPF";
	public static final String CARD_ALREADY_EXISTS = "Já foi cadastrado um cartão com esse número";

	// Cards constants
	public static final Integer NUM_MAX_CARDS = 2;
	public static final Integer BIN_LENGTH = 6; // Card Bin reference, default 6 characters
	public static final Integer CARD_SUFFIX = 4; // Card clear suffix digits, default = 4
}
