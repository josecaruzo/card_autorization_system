package br.com.fiap.mscard.entity.request;

import br.com.fiap.mscard.entity.validation.cardnumber.ValidNumber;
import br.com.fiap.mscard.entity.validation.cvv.ValidCvv;
import br.com.fiap.mscard.entity.validation.expirydate.ValidDate;
import br.com.fiap.mscard.entity.validation.expirydate.ValidDateFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidNumber
@ValidCvv
@ValidDateFormat
@ValidDate
public class CardRequest {
	@NotBlank
	private String cpf;

	@NotBlank
	private String numero; // format 0000000000000000

	@Positive
	@NotNull
	private float limite;

	@NotBlank
	private String data_validade; // format MM/YY

	@NotBlank
	private String cvv; // format 000
}
