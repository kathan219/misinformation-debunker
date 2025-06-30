package com.example.real_time.misinformation.debunker.service.impl;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.real_time.misinformation.debunker.model.FactCheckResult;
import com.example.real_time.misinformation.debunker.model.PromptTemplate;
import com.example.real_time.misinformation.debunker.service.FactCheckService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FactCheckServiceImpl implements FactCheckService {


    @Value("${openrouter.api.url}")
    private String openRouterApiUrl;

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @Value("${google.factcheck.api.url}")
    private String googleFactCheckApiUrl;

    @Value("${google.api.key}")
    private String googleApiKey;

	private static final String DEFAULT_VERDICT = "Unverified";
	private static final String DEFAULT_CONFIDENCE = "Medium";
	private static final String DEFAULT_EXPLANATION = "No explanation available";
	private static final String SYSTEM_NAME = "OpenRouter AI";
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final PromptTemplate promptTemplate;

	public FactCheckServiceImpl(ObjectMapper objectMapper, PromptTemplate promptTemplate) {
		this.restTemplate = new RestTemplate();
		this.objectMapper = objectMapper.copy().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.promptTemplate = promptTemplate;
	}

	@Override
	public FactCheckResult verifyClaim(String claim) {
		try {
			List<String> models = List.of("openai/gpt-4o-mini", "deepseek/deepseek-r1-distill-llama-70b:free",
					"mistralai/mistral-small-24b-instruct-2501", "google/gemma-3-1b-it:free",
					"qwen/qwen2.5-vl-72b-instruct:free");

			List<FactCheckResult> results = new ArrayList<>();

			for (String model : models) {
				System.out.println("Querying model: " + model);
				FactCheckResult result = queryOpenRouter(claim, model);
				if (result != null) {
					results.add(result);
				}
			}

			return aggregateResults(results, claim);
		} catch (Exception e) {
			return errorResult(e);
		}
	}

	private FactCheckResult queryOpenRouter(String claim, String model) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + openRouterApiKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		String formattedPrompt = promptTemplate.format(claim);

		// Ensure correct JSON formatting
		String payload = String.format(
				"""
						{
						    "model": "%s",
						    "messages": [
						        {"role": "system", "content": "You are a fact-checking AI. Analyze the claim and respond strictly in JSON format without extra text."},
						        {"role": "user", "content": "%s"}
						    ],
						    "temperature": 0.3
						}
						""",
				model, formattedPrompt.replace("\"", "\\\"")); // Escape quotes properly

		HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(openRouterApiUrl, HttpMethod.POST, requestEntity,
					String.class);

			System.out.println("Response from " + model + ": " + response.getBody());

			return parseAIResponse(response.getBody(), model);
		} catch (HttpClientErrorException e) {
			System.err.println("OpenRouter API Error: " + e.getResponseBodyAsString());
			return errorResult(e);
		} catch (Exception e) {
			return errorResult(e);
		}
	}

	private FactCheckResult parseAIResponse(String jsonResponse, String model) {
		try {
			JsonNode root = objectMapper.readTree(jsonResponse);

			// 🔥 Extract JSON from "content" inside "choices" -> "message"
			JsonNode choicesNode = root.path("choices");
			if (!choicesNode.isArray() || choicesNode.isEmpty()) {
				return null;
			}

			JsonNode messageNode = choicesNode.get(0).path("message").path("content");
			if (messageNode.isMissingNode() || messageNode.asText().isEmpty()) {
				return null;
			}

			// **Extract valid JSON if wrapped inside Markdown block (```json ... ```)**
			String content = messageNode.asText();
			content = extractJson(content);

			JsonNode jsonContent = objectMapper.readTree(content);

			return new FactCheckResult(safeGetText(jsonContent, "verdict", DEFAULT_VERDICT),
					safeGetText(jsonContent, "confidence", DEFAULT_CONFIDENCE),
					safeGetText(jsonContent, "explanation", DEFAULT_EXPLANATION), safeGetSources(jsonContent), model,
					safeGetThoughts(jsonContent));
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	private FactCheckResult aggregateResults(List<FactCheckResult> results, String claim) {
        if (results.isEmpty()) {
            return new FactCheckResult("Error", "Low", "No valid responses from AI models.", 
                    Collections.emptyList(), SYSTEM_NAME, Collections.emptyList());
        }

        Map<String, Integer> verdictCount = new HashMap<>();
        List<String> explanations = new ArrayList<>();
        Set<String> sources = fetchFactCheckSources(claim);
        List<String> aiThoughts = new ArrayList<>();

        for (FactCheckResult result : results) {
            verdictCount.put(result.getVerdict(), verdictCount.getOrDefault(result.getVerdict(), 0) + 1);
            explanations.add(result.getExplanation());
            aiThoughts.addAll(result.getAiThoughts());
        }

        String finalVerdict = verdictCount.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("Unverified");

        return new FactCheckResult(
            finalVerdict,
            "High",
            explanations.isEmpty() ? "Aggregated response from multiple AI models." : explanations.get(0),
            new ArrayList<>(sources),
            "Google Fact Check API + OpenRouter",
            refineThoughtProcess(aiThoughts)
        );
    }

	private Set<String> fetchFactCheckSources(String claim) {
	    String url = googleFactCheckApiUrl + "?query=" + claim + "&key=" + googleApiKey;
	    try {
	        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
	        
	        // 🔥 Log full API response to debug issues
	        System.out.println("Google Fact Check API Response: " + response.getBody());

	        JsonNode root = objectMapper.readTree(response.getBody());
	        JsonNode claimsNode = root.path("claims");

	        if (!claimsNode.isArray() || claimsNode.isEmpty()) {
	            System.out.println("❌ No fact-check claims found.");
	            return Collections.emptySet();
	        }

	        Set<String> sources = new HashSet<>();

	        // Iterate over each claim in the response
	        for (JsonNode claimNode : claimsNode) {
	            JsonNode claimReviewArray = claimNode.path("claimReview");

	            if (claimReviewArray.isArray()) {
	                for (JsonNode review : claimReviewArray) {
	                    String sourceUrl = review.path("url").asText();
	                    String publisherName = review.path("publisher").path("name").asText();
	                    String title = review.path("title").asText();

	                    // Ensure URL is valid
	                    if (sourceUrl.startsWith("http")) {
	                        // Format the source as: "Publisher: Title (URL)"
	                        sources.add(String.format("%s: %s (%s)", publisherName, title, sourceUrl));
	                    }
	                }
	            }
	        }

	        if (sources.isEmpty()) {
	            System.out.println("❌ No valid fact-check URLs found.");
	        }

	        return sources;
	    } catch (Exception e) {
	        System.err.println("⚠️ Error fetching Fact Check sources: " + e.getMessage());
	        return Collections.emptySet();
	    }
	}

    private List<String> refineThoughtProcess(List<String> thoughts) {
        return thoughts.stream().distinct().map(t -> t.replaceAll("Step \\d+: ", ""))
                .limit(4).toList();
    }

    private String extractJson(String content) {
        int jsonStart = content.indexOf("{");
        int jsonEnd = content.lastIndexOf("}");
        return (jsonStart != -1 && jsonEnd != -1 && jsonStart < jsonEnd) ?
                content.substring(jsonStart, jsonEnd + 1) : "{}";
    }

	private String safeGetText(JsonNode node, String field, String defaultValue) {
		JsonNode fieldNode = node.get(field);
		return (fieldNode == null || fieldNode.isNull()) ? defaultValue : fieldNode.asText().trim();
	}

	private List<String> safeGetSources(JsonNode root) {
		JsonNode sourcesNode = root.get("sources");
		return (sourcesNode == null || !sourcesNode.isArray()) ? Collections.emptyList()
				: StreamSupport.stream(sourcesNode.spliterator(), false).map(JsonNode::asText).toList();
	}

	private List<String> safeGetThoughts(JsonNode root) {
		JsonNode thoughtsNode = root.get("ai_thoughts");
		return (thoughtsNode == null || !thoughtsNode.isArray()) ? Collections.emptyList()
				: StreamSupport.stream(thoughtsNode.spliterator(), false).map(JsonNode::asText).toList();
	}

	private FactCheckResult errorResult(Exception e) {
		return new FactCheckResult("Error", "High",
				"Fact-checking failed: " + Optional.ofNullable(e.getMessage()).orElse("Unknown error"),
				List.of("Diagnostic: OpenRouter"), SYSTEM_NAME, null);
	}
}