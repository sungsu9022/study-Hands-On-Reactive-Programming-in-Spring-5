package org.rpis5.chapters.chapter_03.conversion_problem;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FakeAsyncDatabaseClient implements AsyncDatabaseClient {

	@Override
	public <T> CompletionStage<T> store(CompletionStage<T> stage) {
		log.info("3. store main");
		return stage.thenCompose(e -> {
		log.info("5. store");
			return CompletableFuture.supplyAsync(() -> e);
		});
	}
}
