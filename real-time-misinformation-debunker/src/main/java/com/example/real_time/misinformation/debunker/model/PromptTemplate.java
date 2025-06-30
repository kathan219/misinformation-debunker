package com.example.real_time.misinformation.debunker.model;

public record PromptTemplate(String template) {
	public String format(Object... args) {
		return String.format(template, args);
	}
}