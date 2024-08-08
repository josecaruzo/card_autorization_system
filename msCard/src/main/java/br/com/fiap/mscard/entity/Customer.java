package br.com.fiap.mscard.entity;

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
@Table(name = "customers",
	indexes = {
		@Index(name = "cpf_index", columnList = "cpf", unique = true)
})
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 11, nullable = false)
	private String cpf;

	@Column(length = 40, nullable = false)
	private String name;

	@Column(length = 80, nullable = false)
	private String email;

	@Column(length = 17, nullable = false)
	private String phoneNumber;

	@Column(length = 200, nullable = false)
	private String street;

	@Column(length = 50, nullable = false)
	private String city;

	@Column(length = 30, nullable = false)
	private String state;

	@Column(length = 9, nullable = false)
	private String zipCode;

	@Column(length = 30, nullable = false)
	private String country;
}
