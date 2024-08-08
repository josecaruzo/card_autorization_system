package br.com.fiap.msauthorization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cards",
		indexes = {
				@Index(name = "ccustomer_cpf_index", columnList = "customer_cpf"), // Each customer can have more than one card
				@Index(name = "ccard_number_index", columnList = "card_number", unique = true)
		})
public class Card {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 11, nullable = false)
	private String customerCpf;

	@Column(length = 16, nullable = false)
	private String cardNumber;

	@Column(nullable = false)
	private float creditLimit;

	@Column(nullable = false)
	private float creditOtb; //Open to buy

	@Column(length = 5, nullable = false)
	private String expiryDate;

	@Column(length = 3, nullable = false)
	private String cvv;
}
