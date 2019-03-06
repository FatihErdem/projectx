package com.fatiherdem.projectx.domain.model;

import lombok.Data;

@Data
public class SearchVolumeResponse {

	private String keyword;

	private Integer score;

	private Long totalElapsedTimeInMillis;

	public SearchVolumeResponse(String keyword, Integer score, Long totalElapsedTimeInMillis) {
		this.keyword = keyword;
		this.score = score;
		this.totalElapsedTimeInMillis = totalElapsedTimeInMillis;
	}
}
