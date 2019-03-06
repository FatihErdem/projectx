package com.fatiherdem.projectx.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchVolumeResponse {

	private String keyword;

	private Double score;
}
