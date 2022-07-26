package org.rpis5.chapters.chapter_09.my;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class WebSocketTest {

	@Test
	@WithMockUser
	public void checkThatUserIsAbleToMakeATrade() {
		URI uri = URI.create("ws://localhost:8080/stream");
		TestWebSocketClient client = TestWebSocketClient.create(uri);
		TestPublisher<String> testPublisher = TestPublisher.create();
		Flux<String> inbound = testPublisher.flux()
			.subscribeWith(ReplayProcessor.create(1))
			.transform(client::sendAndReceive)
			.map(WebSocketMessage::getPayloadAsText);

		StepVerifier
			.create(inbound)
			.expectSubscription()
			.then(() -> testPublisher.next("TRADES|BTC"))
			.expectNext("PRICE|AMOUNG|CURRENCY")
			.then(() -> testPublisher.next("TRADE: 10123|1.54|BTC"))
			.expectNext("10123|1.54|BTC")
			.then(() -> testPublisher.next("TRADE: 10090|-0.01|BTC"))
			.expectNext("10090|-0.01|BTC")
			.thenCancel()
			.verify();
	}

	@Test
	public void webSocketTest() {
		final WebSocketClient webSocketClient = new ReactorNettyWebSocketClient();
		final Mono<Void> execute = webSocketClient.execute(URI.create("/websocket"), new WebSocketHandler() {
			@Override
			public Mono<Void> handle(WebSocketSession session) {
				return null;
			}
		});


		Mono.create(sink ->
			sink.onCancel(
				webSocketClient.execute(URI.create("/test"), session -> {
						sink.success(session);
						return Mono.never();
					})
					.doOnError(sink::error)
					.subscribe()
			)
		);
	}

}
