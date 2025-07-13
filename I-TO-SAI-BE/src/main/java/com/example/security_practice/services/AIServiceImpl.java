package com.example.security_practice.services;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private final OpenAIClient client;
    private final ChatModel model;
    private final String atfSuggestionSysPrompt;
    private final AiUsageService aiUsageService;

    private Optional<String> openAIChat(String systemPrompt, String userMessage) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        aiUsageService.validateDailyLimit(userId);

        var builder = ChatCompletionCreateParams.builder()
                .model(model)
                .addSystemMessage(systemPrompt)
                .addUserMessage(userMessage);

        ChatCompletion completion = client
                .chat()
                .completions()
                .create(builder.build());

        long tokensUsed = completion.usage().orElseThrow(()->new IllegalStateException("oai usage object is null")).totalTokens();
        aiUsageService.addTokensUsed(userId, tokensUsed);
        aiUsageService.incrementRequestCount(userId);

        return completion
                .choices()
                .get(0)
                .message()
                .content();
    }

    @Override
    public Optional<String> getATFSuggestion(String goal, String reason) {
        String userMessage = "goal: "+goal+"\nreason: "+reason;
        return openAIChat(atfSuggestionSysPrompt, userMessage);
    }
}
