package org.rpis5.chapters.chapter_03.conversion_problem;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TestController {

	@RequestMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
	public String hello() throws InterruptedException {
		Thread.sleep(10);
		log.info("4?. hello");
		return "Hello World";
	}

}
