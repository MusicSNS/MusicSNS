package com.co.kr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.co.kr.vo.RequestQuestionVo;
import com.co.kr.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:application.properties")
public class GptService {
    @Value("${apikey}")
    private String API_KEY = "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";
    private static final String CHAT_COMPLETION_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String EDIT_ENDPOINT = "https://api.openai.com/v1/edits";
    private static final String IMAGES_ENDPOINT = "https://api.openai.com/v1/images/generations";

    public String generateText(String prompt, float temperature, int maxTokens) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(message);
        requestBody.put("messages", messages);
        requestBody.put("temperature", temperature);
        requestBody.put("max_tokens", maxTokens);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(CHAT_COMPLETION_ENDPOINT, requestEntity, String.class);
        String responseBody = response.getBody();
        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode choicesNode = jsonNode.get("choices");
            if (choicesNode != null && choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode messageNode = choicesNode.get(0).get("message");
                if (messageNode != null && messageNode.has("content")) {
                    return messageNode.get("content").asText();
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ResponseVo getConversation(RequestQuestionVo requestquestion) {
        ResponseVo responseVo = new ResponseVo();
        try {
            String answer = generateText(requestquestion.getQuestion(), 0.5f, 1000);
            responseVo.setCode(200);
            responseVo.setResponse(answer);
        } catch (Exception e) {
            responseVo.setCode(500);
            responseVo.setResponse("generateText error(서버 에러)");
        }
        return responseVo;
    }
}
