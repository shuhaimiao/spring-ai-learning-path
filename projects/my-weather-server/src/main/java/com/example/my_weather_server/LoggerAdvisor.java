package com.example.my_weather_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.lang.NonNull;

import reactor.core.publisher.Flux;

public class LoggerAdvisor implements CallAdvisor, StreamAdvisor {

	private static final Logger logger = LoggerFactory.getLogger(SimpleLoggerAdvisor.class);

	@Override
	public String getName() { 
		return this.getClass().getSimpleName();
	}

	@Override
	public int getOrder() { 
		return 0;
	}


	@Override
	public @NonNull Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest chatClientRequest,
			@NonNull StreamAdvisorChain streamAdvisorChain) {

		logger.info("BEFORE: {}", chatClientRequest);
		
		Flux<ChatClientResponse> advisedResponses = streamAdvisorChain.nextStream(chatClientRequest);
		
		return advisedResponses.doOnNext(response -> logger.info("AFTER: {}", response));

	}

	@Override
	public @NonNull ChatClientResponse adviseCall(@NonNull ChatClientRequest chatClientRequest, @NonNull CallAdvisorChain callAdvisorChain) {

		logger.info("BEFORE: {}", chatClientRequest);
		
		ChatClientResponse advisedResponse = callAdvisorChain.nextCall(chatClientRequest);
		
		logger.info("AFTER: {}", advisedResponse);
		
		return advisedResponse;
	}
}