package org.rpis5.chapters.chapter_09;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/payments")
public class PaymentController {
	Logger logger = LoggerFactory.getLogger(PaymentController.class);

	private final DefaultPaymentService paymentService;

	public PaymentController(DefaultPaymentService service) {
		paymentService = service;
	}

	@GetMapping("")
	public Flux<Payment> list() {
		return paymentService.list();
	}

	@GetMapping("/{id}")
	public Flux<Payment> list(@PathVariable String id) {
		return paymentService.list(id);
	}

	@PostMapping("")
	public Mono<String> send(@RequestBody Payment payment) {
		logger.info("send : {}", payment);
		return paymentService.send(Mono.just(payment));
	}
}