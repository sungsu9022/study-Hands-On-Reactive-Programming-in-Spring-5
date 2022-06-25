package org.rpis5.chapters.chapter_03.conversion_problem;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AsyncAdapters {

	public static <T> CompletionStage<T> toCompletion(ListenableFuture<T> future) {

		log.info("2. toCompletion");
		CompletableFuture<T> completableFuture = new CompletableFuture<>();

		future.addCallback(completableFuture::complete,
				completableFuture::completeExceptionally);

		return completableFuture;
	}

	public static <T> ListenableFuture<T> toListenable(CompletionStage<T> stage) {
		SettableListenableFuture<T> future = new SettableListenableFuture<>();
		log.info("4?. toListenable main");

		stage.whenComplete((v, t) -> {
			log.info("6. toListenable, t : {}, v : {}", t, v);
			if (t == null) {
				future.set(v);
			}
			else {
				future.setException(t);
			}
		});

		return future;
	}
}
