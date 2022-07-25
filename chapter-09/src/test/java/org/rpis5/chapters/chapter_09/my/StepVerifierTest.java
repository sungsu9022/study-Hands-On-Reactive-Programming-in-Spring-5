package org.rpis5.chapters.chapter_09.my;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.Duration;

import org.junit.Test;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

@SuppressWarnings("unchecked")
public class StepVerifierTest {

	@Test
	public void test() {
		StepVerifier
			.create(Flux.just("foo","bar"))
			.expectSubscription()
			.expectNext("foo")
			.expectNext("bar")
			.expectComplete()
			.verify();
	}


	@Test(expected = AssertionError.class)
	public void testFailure() {
		StepVerifier
			.create(Flux.just("foo","bar","test"))
			.expectSubscription()
			.expectNext("foo")
			.expectNext("test") // stream 순서가 다르면 Error 발생
			.expectComplete()
			.verify();
	}

	@Test
	public void test2() {
		StepVerifier
			.create(Flux.range(0,100))
			.expectSubscription()
			.expectNext(0)
			.expectNextCount(98)
			.expectNext(99)
			.expectComplete()
			.verify();
	}

	@Test
	public void test3() {
		StepVerifier
			.create(Flux.just("alpha-foo", "betta-bar"))
			.expectSubscription()
			.expectNextMatches(e -> e.startsWith("alpha"))
			.expectNextMatches(e -> e.startsWith("betta"))
			.expectComplete()
			.verify();
	}

	@Test
	public void assertThatTest() {
		StepVerifier.create(Flux.range(1, 12)
				.windowTimeout(5, Duration.ofHours(1))
				.concatMap(Flux::collectList)
			)
			.assertNext(l -> assertThat(l).containsExactly(1, 2, 3, 4, 5))
			.assertNext(l -> assertThat(l).containsExactly(6, 7, 8, 9, 10))
			.assertNext(l -> assertThat(l).containsExactly(11, 12))
			.verifyComplete();
	}

	@Test
	public void throwError() {
		StepVerifier
			.create(Flux.error(new RuntimeException("Error")))
			.expectError()
			.verify();
	}

	@Test
	public void infiniteStream() {
		Flux<String> infiniteStream = Flux.just("Connected","Price: $12.00","3","4");
		StepVerifier
			.create(infiniteStream)
			.expectSubscription()
			.expectNext("Connected")
			.expectNext("Price: $12.00")
			.thenCancel()
			.verify();
	}

	@Test
	public void infiniteStreamBackPressure() {
		Flux<String> infiniteStream = Flux.just("Connected","Price: $12.00","3","4","5","6","7");
		StepVerifier
			.create(infiniteStream.onBackpressureBuffer(5), 0)
			.expectSubscription()
			.thenRequest(1)
			.expectNext("Connected")
			.thenRequest(1)
			.expectNext("Price: $12.00")
			.expectError(Exceptions.failWithOverflow().getClass())
			.verify();
	}

	@Test
	public void virtualTime() {
		StepVerifier
			.create(sendWithInterval())
			.expectSubscription()
			.expectNext("a", "b","c")
			.expectComplete()
			.verify();
	}

	@Test
	public void virtualTime2() {
		StepVerifier
			.withVirtualTime(() -> Flux.interval(Duration.ofMinutes(1))
				.zipWith(Flux.just("a", "b", "c"))
				.map(Tuple2::getT2))
			.expectSubscription()
//			.then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofMinutes(3)))
			.thenAwait(Duration.ofMillis(3))
			.expectNext("a", "b","c")
			.expectComplete()
			.verify();
	}

	Flux<String> sendWithInterval() {
		return Flux.interval(Duration.ofMinutes(1))
				.zipWith(Flux.just("a", "b", "c"))
				.map(Tuple2::getT2);
	}

	// @Test
	public void reactorContext() {
		StepVerifier
			.create(Flux.just("1","2","3"))
			.expectSubscription()
			.expectAccessibleContext()
			.hasKey("security")
			.then()
			.expectComplete()
			.verify();
	}




}
