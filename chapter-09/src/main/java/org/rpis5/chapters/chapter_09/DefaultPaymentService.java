package org.rpis5.chapters.chapter_09;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DefaultPaymentService implements PaymentService {
	Logger logger = LoggerFactory.getLogger(DefaultPaymentService.class);

	private final PaymentRepository paymentRepository;
	private final WebClient         client;

	public DefaultPaymentService(PaymentRepository repository,
			                     WebClient.Builder builder) {
		paymentRepository = repository;
		client = builder.baseUrl("http://api.bank.com/submit")
		                .build();
	}

	@Override
	public Mono<String> send(Mono<Payment> payment) {
		return payment.zipWith(
						ReactiveSecurityContextHolder.getContext(),
						(p, c) -> p.withUser(c.getAuthentication().getName())
					  )
		              .flatMap(p -> {
						  logger.info("{}", p );
						  return client.post()
							  .syncBody(p)
							  .retrieve()
							  .bodyToMono(String.class)
							  .then(paymentRepository.save(p));
					  })
		              .map(Payment::getId);
	}

	public Mono<String> sendWithoutContext(Mono<Payment> payment) {
		return client.post()
			.syncBody(payment)
			.retrieve()
			.bodyToMono(String.class)
			.then(paymentRepository.save(payment.block()))
			.map(Payment::getId);
	}

	@Override
	public Flux<Payment> list() {
		return ReactiveSecurityContextHolder
				.getContext()
				.map(SecurityContext::getAuthentication)
				.map(Principal::getName)
				.flatMapMany(paymentRepository::findAllByUser);
	}

	public Flux<Payment> list(String id) {
		return paymentRepository.findAllByUser(id);
	}
}