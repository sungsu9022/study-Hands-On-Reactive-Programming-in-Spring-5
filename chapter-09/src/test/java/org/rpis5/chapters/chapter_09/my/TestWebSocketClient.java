package org.rpis5.chapters.chapter_09.my;

import java.net.URI;

import org.reactivestreams.Publisher;
import org.springframework.web.reactive.socket.WebSocketMessage;

import reactor.core.publisher.Flux;

public interface TestWebSocketClient {
	Flux<WebSocketMessage> sendAndReceive(Publisher<?> outgoingSource);

	static TestWebSocketClient create(URI uri) {
		return outgoingSource -> null;
	}
}
