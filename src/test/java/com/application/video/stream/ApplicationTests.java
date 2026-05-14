package com.application.video.stream;

import com.application.video.stream.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

	@Autowired
	VideoService videoService;

	@Test
	void contextLoads() {



		System.out.println("Starting processing...");

		videoService.processVideo("224e3306-5fb6-4a50-8a63-171a0fe46e19");

		System.out.println("Processing finished");

	}

}
