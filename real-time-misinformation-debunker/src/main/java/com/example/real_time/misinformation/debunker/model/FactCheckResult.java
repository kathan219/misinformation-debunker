package com.example.real_time.misinformation.debunker.model;

import java.util.List;

public class FactCheckResult {
    private String verdict; // True/False/Misleading
    private String confidence; // High/Medium/Low
    private String explanation;
    private List<String> sources;
    private String sourceSystem;
    List<String> aiThoughts;


    // Constructor, getters, and setters
    public FactCheckResult(String verdict, String confidence, String explanation, List<String> sources, String sourceSystem,List<String> aiThoughts) {
        this.verdict = verdict;
        this.confidence = confidence;
        this.explanation = explanation;
        this.sources = sources;
        this.sourceSystem = sourceSystem;
        this.aiThoughts = aiThoughts;
        
    }

	public String getVerdict() {
		return verdict;
	}

	public void setVerdict(String verdict) {
		this.verdict = verdict;
	}

	public String getConfidence() {
		return confidence;
	}

	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public List<String> getSources() {
		return sources;
	}

	public void setSources(List<String> sources) {
		this.sources = sources;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public List<String> getAiThoughts() {
		return aiThoughts;
	}

	public void setAiThoughts(List<String> aiThoughts) {
		this.aiThoughts = aiThoughts;
	}
	
	
}