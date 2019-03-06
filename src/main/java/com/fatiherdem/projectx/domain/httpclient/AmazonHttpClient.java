package com.fatiherdem.projectx.domain.httpclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmazonHttpClient {

	private static final String BASE_AMAZON_AUTOCOMPLETE_URL = "http://completion.amazon.com/search/complete";

	private final RestTemplate restTemplate;

	private final ObjectMapper objectMapper;

	public List<String> getAutocompleteResult(String keyword) {

		String url = prepareUrl(keyword);

		String response = restTemplate.getForEntity(url, String.class).getBody();

		TypeReference<List<String>> stringListTypeRef = new TypeReference<List<String>>() {
		};

		List<String> resultList;
		try {
			JsonNode array = objectMapper.readValue(response, JsonNode.class);
			String result = array.get(1).toString();
			resultList = objectMapper.readValue(result, stringListTypeRef);
		}
		catch (IOException e) {
			throw new UnsupportedOperationException();
		}

		return resultList;

	}

	private String prepareUrl(String keyword) {
		return UriComponentsBuilder
				.fromHttpUrl(BASE_AMAZON_AUTOCOMPLETE_URL)
				.queryParam("search-alias", "aps")
				.queryParam("client", "amazon-search-ui")
				.queryParam("mkt", "1")
				.queryParam("q", keyword)
				.build()
				.toString();
	}
}
