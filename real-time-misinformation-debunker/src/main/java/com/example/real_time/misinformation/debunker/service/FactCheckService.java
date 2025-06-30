package com.example.real_time.misinformation.debunker.service;

import com.example.real_time.misinformation.debunker.model.FactCheckResult;

public interface FactCheckService {
    FactCheckResult verifyClaim(String claim);
}