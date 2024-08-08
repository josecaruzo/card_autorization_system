package br.com.fiap.msauthorization.entity;

import br.com.fiap.msauthorization.entity.enums.StatusCode;
import br.com.fiap.msauthorization.entity.enums.StatusDesc;
import br.com.fiap.msauthorization.entity.enums.TrxnMethodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authorizations",
		indexes = {
				@Index(name = "acustomer_cpf_index", columnList = "customer_cpf"), // Each customer can have more than one authorization
				@Index(name = "acard_number_index", columnList = "card_number"), // Each card number can have more than one authorization
				@Index(name = "aauth_status", columnList = "status_description") // To optimizing search
		})
public class Authorization {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 11, nullable = false)
	private String customerCpf;

	@Column(length = 16, nullable = false)
	private String cardNumber;

	@Column(length = 5, nullable = false)
	private String expiryDate;

	@Column(length = 3, nullable = false)
	private String cvv;

	@Column(nullable = false)
	private float amount;

	@Column(length = 50)
	private String description;

	@Column(columnDefinition = "VARCHAR(20)", nullable = false)
	@Enumerated(EnumType.STRING)
	private TrxnMethodType trxnMethod;

	@Column(nullable = false)
	private LocalDateTime trxnDatetime;

	@Column(columnDefinition = "VARCHAR(10)", nullable = false)
	@Enumerated(EnumType.STRING)
	private StatusDesc statusDescription;

	@Column(columnDefinition = "VARCHAR(10)", nullable = false)
	@Enumerated(EnumType.STRING)
	private StatusCode statusCode;
}
