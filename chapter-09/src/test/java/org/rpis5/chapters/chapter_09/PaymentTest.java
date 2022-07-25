package org.rpis5.chapters.chapter_09;

import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PaymentTest {
	static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Test
	public void create() throws JsonProcessingException {

		final Payment test1 = Payment.builder()
			.id(UUID.randomUUID().toString())
			.user("test1")
			.build();

		final String json = OBJECT_MAPPER.writeValueAsString(test1);
		System.out.println(json);

	}

}