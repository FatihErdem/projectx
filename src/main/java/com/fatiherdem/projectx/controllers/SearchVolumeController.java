package com.fatiherdem.projectx.controllers;

import com.fatiherdem.projectx.domain.model.SearchVolumeResponse;
import com.fatiherdem.projectx.domain.estimation.EstimationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/estimate")
public class SearchVolumeController {

	private final EstimationService estimationService;

	@GetMapping
	public ResponseEntity<SearchVolumeResponse> getKeywordEstimate(@RequestParam("keyword") String keyword) throws IOException {
		SearchVolumeResponse searchVolume = estimationService.getSearchVolume(keyword);
		return ResponseEntity.ok(searchVolume);
	}
}
