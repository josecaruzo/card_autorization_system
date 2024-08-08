package br.com.fiap.msauthorization.repository;

import br.com.fiap.msauthorization.entity.Authorization;
import br.com.fiap.msauthorization.entity.enums.StatusDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface AuthorizationRepository extends JpaRepository<Authorization, Long> {
	ArrayList<Authorization> findByCustomerCpfAndStatusDescription(String cpf, StatusDesc status);
}
