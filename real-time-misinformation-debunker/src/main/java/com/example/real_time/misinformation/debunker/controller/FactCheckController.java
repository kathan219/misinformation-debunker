package com.example.real_time.misinformation.debunker.controller;

import org.springframework.web.bind.annotation.*;

import com.example.real_time.misinformation.debunker.model.FactCheckResult;
import com.example.real_time.misinformation.debunker.service.FactCheckService;

@RestController
@RequestMapping("/api")
public class FactCheckController {

	private final FactCheckService factCheckingService;

	public FactCheckController(FactCheckService factCheckingService) {
		this.factCheckingService = factCheckingService;
	}

	@GetMapping
	public FactCheckResult checkClaim(@RequestParam String claim) {
		return factCheckingService.verifyClaim(claim);
	}
}
