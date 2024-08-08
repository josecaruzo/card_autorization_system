package br.com.fiap.mscard.repository;

import br.com.fiap.mscard.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
	Optional<Card> findByCardNumber(String cardNumber);
	Integer countByCustomerCpf(String customerCpf);
}
