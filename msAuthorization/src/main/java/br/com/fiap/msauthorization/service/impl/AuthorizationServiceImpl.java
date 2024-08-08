package br.com.fiap.msauthorization.service.impl;

import br.com.fiap.msauthorization.entity.Authorization;
import br.com.fiap.msauthorization.entity.Card;
import br.com.fiap.msauthorization.entity.enums.StatusCode;
import br.com.fiap.msauthorization.entity.enums.StatusDesc;
import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import br.com.fiap.msauthorization.entity.response.AuthorizationHistoryResponse;
import br.com.fiap.msauthorization.entity.response.AuthorizationResponse;
import br.com.fiap.msauthorization.repository.AuthorizationRepository;
import br.com.fiap.msauthorization.repository.CardRepository;
import br.com.fiap.msauthorization.service.AuthorizationService;
import br.com.fiap.msauthorization.service.mapper.AuthorizationMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.fiap.msauthorization.constants.AuthorizationConstants.*;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {
	private final AuthorizationRepository authorizationRepository;
	private final CardRepository cardRepository;

	@Override
	public List<AuthorizationHistoryResponse> getCustomerAuthorizations(String cpf) {
		// Only approved authorizations
		List<Authorization> autorizationList = authorizationRepository.findByCustomerCpfAndStatusDescription(cpf, StatusDesc.APPROVED);

		if(autorizationList.isEmpty())
			throw new EntityNotFoundException(AUTHORIZATIONS_NOT_FOUND);

		return autorizationList.stream().map(AuthorizationMapper::entityToHistoryResponse).toList();
	}

	public AuthorizationResponse add(AuthorizationRequest authorizationRequest){
		Authorization authorization = AuthorizationMapper.requestToEntity(authorizationRequest);

		Card card = cardRepository.findByCardNumber(authorization.getCardNumber())
				.orElseThrow(() -> new EntityNotFoundException(CARD_NOT_FOUND));

		checkAuthorization(authorization, card);
		updateCardOtb(card,authorization);

		return AuthorizationMapper.entityToResponse(authorizationRepository.save(authorization));
	}

	private void checkAuthorization(Authorization authorization, Card card) {
		boolean securityValidationFailed = false;
		StatusCode currentStatus = StatusCode.APPR;

		if(!card.getCustomerCpf().equals(authorization.getCustomerCpf())){
			currentStatus = StatusCode.CPF_REJCT;
			securityValidationFailed = true;
		}

		if(!card.getExpiryDate().equals(authorization.getExpiryDate())){
			currentStatus = StatusCode.EXP_REJC;
			securityValidationFailed = true;
		}

		if(!card.getCvv().equals(authorization.getCvv())){
			currentStatus = StatusCode.CVV_REJC;
			securityValidationFailed = true;
		}

		if (securityValidationFailed){
			setAuthorizationDetails(authorization, currentStatus, StatusDesc.REJECTED, VALIDATION_FAILED_DESCRIPTION);
			authorizationRepository.save(authorization);
			throw new DataIntegrityViolationException(VALIDATION_FAILED);
		}
		else{
			if (card.getCreditOtb() < authorization.getAmount()){
				currentStatus = StatusCode.OTB_REJC;

				setAuthorizationDetails(authorization, currentStatus, StatusDesc.REJECTED, INSUFFICIENT_LIMIT_DESCRIPTION);
				authorizationRepository.save(authorization);
				throw new DataIntegrityViolationException(INSUFFICIENT_LIMIT);
			}
			else{
				setAuthorizationDetails(authorization, currentStatus, StatusDesc.APPROVED, SUCCESSFUL_TRANSACTION_DESCRIPTION);
			}
		}
	}

	private void updateCardOtb(Card card, Authorization authorization) {
		card.setCreditOtb(card.getCreditOtb() - authorization.getAmount());
		cardRepository.save(card);
	}

	private void setAuthorizationDetails(Authorization authorization, StatusCode statusCode, StatusDesc statusDesc, String description){
		authorization.setStatusCode(statusCode);
		authorization.setStatusDescription(statusDesc);
		authorization.setDescription(description);
		authorization.setTrxnDatetime(LocalDateTime.now());
	}
}
