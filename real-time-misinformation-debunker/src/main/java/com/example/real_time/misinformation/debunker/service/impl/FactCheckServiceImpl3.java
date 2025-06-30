//package com.example.real_time.misinformation.debunker.service.impl;
//
//import org.springframework.ai.chat.messages.UserMessage;
//import org.springframework.ai.chat.model.ChatResponse;
//import org.springframework.ai.chat.model.Generation;
//import org.springframework.ai.chat.messages.Message;
//
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.ollama.OllamaChatModel;
//import org.springframework.stereotype.Service;
//
//import com.example.real_time.misinformation.debunker.model.FactCheckResult;
//import com.example.real_time.misinformation.debunker.model.PromptTemplate;
//import com.example.real_time.misinformation.debunker.service.FactCheckService;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.stream.StreamSupport;
//
//@Service
//public class FactCheckServiceImpl implements FactCheckService {
//
//	private static final String DEFAULT_VERDICT = "Unverified";
//	private static final String DEFAULT_CONFIDENCE = "Medium";
//	private static final String DEFAULT_EXPLANATION = "No explanation available";
//	private static final String SYSTEM_NAME = "DeepSeek R1 (Local)";
//
//	private final OllamaChatModel aiModel;
//	private final ObjectMapper objectMapper;
//	private final PromptTemplate promptTemplate;
//
//	public FactCheckServiceImpl(OllamaChatModel aiModel, ObjectMapper objectMapper, PromptTemplate promptTemplate) {
//		this.aiModel = aiModel;
//		this.objectMapper = objectMapper.copy().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		this.promptTemplate = promptTemplate;
//
//		// Debugging
//		System.out.println("Injected PromptTemplate: " + promptTemplate);
//	}
//
//	@Override
//	public FactCheckResult verifyClaim(String claim) {
//		try {
//			String formattedPrompt = promptTemplate.format(claim);
//			String jsonResponse = getAIResponse(formattedPrompt);
//			return parseAIResponse(jsonResponse);
//		} catch (Exception e) {
//			return errorResult(e);
//		}
//	}
//
//	private String getAIResponse(String formattedPrompt) {
//		ChatResponse response = aiModel.call(new Prompt(new UserMessage(formattedPrompt)));
//
//		// Validate response exists
//		if (response == null) {
//			throw new IllegalStateException("Null AI response");
//		}
//
//		// Get generations safely
//		List<Generation> generations = response.getResults();
//		if (generations == null || generations.isEmpty()) {
//			throw new IllegalStateException("Empty results in AI response");
//		}
//
//		// Get first generation
//		Generation generation = generations.get(0);
//		if (generation == null) {
//			throw new IllegalStateException("Null generation in AI response");
//		}
//
//		// Get output as Message (correct base type)
//		Message message = generation.getOutput();
//		if (message == null) {
//			throw new IllegalStateException("Null output in AI generation");
//		}
//
//		// Get content safely
//		String content = message.getText();
//		System.out.println("RAW AI Response: " + content);
//		if (content == null || content.isBlank()) {
//			throw new IllegalStateException("Empty or null content in AI output.");
//		}
//
//		// Extract JSON from response
//		return extractJson(content);
//	}
//
//	private String extractJson(String content) {
//	    int jsonStart = content.indexOf("{");
//	    int jsonEnd = content.lastIndexOf("}");
//
//	    if (jsonStart == -1 || jsonEnd == -1 || jsonStart >= jsonEnd) {
//	        throw new IllegalStateException("No valid JSON found in AI response.");
//	    }
//
//	    String rawJson = content.substring(jsonStart, jsonEnd + 1);
//	    System.out.println("Unmodified AI Response"  +rawJson);
////
////
////	    // Ensure valid JSON by escaping unescaped newlines and removing non-printable characters
////	    rawJson = rawJson.replaceAll("\n", "\\n").replaceAll("\r", "\\r").replaceAll("\t", "\\t");
////	    System.out.println("Modified AI Response"  +rawJson);
//
//	    return rawJson;
//	}
//
//
//
//	private FactCheckResult parseAIResponse(String jsonResponse) throws JsonProcessingException {
//		JsonNode root = objectMapper.readTree(jsonResponse);
//
//		return new FactCheckResult(safeGetText(root, "verdict", DEFAULT_VERDICT),
//				safeGetText(root, "confidence", DEFAULT_CONFIDENCE),
//				safeGetText(root, "explanation", DEFAULT_EXPLANATION), safeGetSources(root), SYSTEM_NAME,
//				safeGetThoughts(root) // Extract AI's reasoning steps
//		);
//	}
//
//	private String safeGetText(JsonNode node, String field, String defaultValue) {
//		JsonNode fieldNode = node.get(field);
//		if (fieldNode == null || fieldNode.isNull())
//			return defaultValue;
//		String text = fieldNode.asText();
//		return text != null ? text.trim() : defaultValue;
//	}
//
//	private List<String> safeGetSources(JsonNode root) {
//		JsonNode sourcesNode = root.get("sources");
//		if (sourcesNode == null || !sourcesNode.isArray()) {
//			return Collections.emptyList();
//		}
//
//		return StreamSupport.stream(sourcesNode.spliterator(), false).filter(JsonNode::isTextual).map(JsonNode::asText)
//				.filter(Objects::nonNull).filter(s -> !s.isBlank()).toList();
//	}
//
//	private List<String> safeGetThoughts(JsonNode root) {
//		JsonNode thoughtsNode = root.get("ai_thoughts");
//		if (thoughtsNode == null || !thoughtsNode.isArray()) {
//			return Collections.emptyList();
//		}
//		return StreamSupport.stream(thoughtsNode.spliterator(), false).filter(JsonNode::isTextual).map(JsonNode::asText)
//				.filter(Objects::nonNull).filter(s -> !s.isBlank()).toList();
//	}
//
//	private FactCheckResult errorResult(Exception e) {
//		String errorMsg = Optional.ofNullable(e.getMessage()).orElse("Unknown error during fact-checking");
//
//		return new FactCheckResult("Error", "High", "Fact-checking failed: " + errorMsg,
//				List.of("Diagnostic: " + SYSTEM_NAME), SYSTEM_NAME, null);
//	}
//}


