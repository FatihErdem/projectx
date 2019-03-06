package com.fatiherdem.projectx.domain.estimation;

import com.fatiherdem.projectx.domain.model.SearchVolumeResponse;
import com.fatiherdem.projectx.domain.httpclient.AmazonHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstimationService {

	private final AmazonHttpClient amazonHttpClient;

	public SearchVolumeResponse getSearchVolume(String searchedKeyword) {

		long startTime = System.currentTimeMillis();

		List<String> separatedKeywords = getSeparatedKeywords(searchedKeyword);

		Map<String, Integer> keywordResultOrderMap = new LinkedHashMap<>();

		Map<String, Double> keywordOrderMultiplierMap = new LinkedHashMap<>();

		Map<String, Double> keywordPointLetterByLetterMap = new LinkedHashMap<>();

		Double maxPointForLetter = calculateMaxLetterPoint(searchedKeyword.length());

		for (String separatedKeyword : separatedKeywords) {

			List<String> resultsForKeyword = amazonHttpClient.getAutocompleteResult(separatedKeyword);

			Integer resultOrder = getResultOrderOfKeyword(searchedKeyword, resultsForKeyword);

			keywordResultOrderMap.put(separatedKeyword, resultOrder);

			Double resultOrderMultiplier = calculateResultOrderMultiplier(resultOrder);
			keywordOrderMultiplierMap.put(separatedKeyword, resultOrderMultiplier);

			Double letterPoint = calculateLetterPointWithResultOrderMultiplier(maxPointForLetter, resultOrderMultiplier);
			keywordPointLetterByLetterMap.put(separatedKeyword, letterPoint);
		}

		Double searchVolume = calculateTotalPointOfKeyword(keywordPointLetterByLetterMap.values());

		long endTime = System.currentTimeMillis();

		log.error("Total execution time: " + (endTime - startTime));

		return new SearchVolumeResponse(searchedKeyword, searchVolume);
	}

	private Double calculateTotalPointOfKeyword(Collection<Double> keywordPointLetterByLetters) {

		double totalPoint = 0.0;

		for (Double point : keywordPointLetterByLetters) {
			totalPoint += point;
		}

		return totalPoint;
	}

	/**
	 * This method separate keyword letter by letter
	 * <p>
	 * For example: input = sellics
	 * <p>
	 * s, se, sel, sell, selli, sellic, sellics will be generated
	 *
	 * @param keyword
	 * @return list of separated keyword
	 */
	private List<String> getSeparatedKeywords(String keyword) {

		List<String> separatedKeywords = new ArrayList<>();

		for (int i = 0; i < keyword.length(); i++) {
			separatedKeywords.add(keyword.substring(0, i + 1));
		}

		return separatedKeywords;
	}

	private Double calculateResultOrderMultiplier(Integer resultOrderOfExactMatch) {

		if (resultOrderOfExactMatch == 0) {
			return 0.0;
		}

		return 1.0 - ((resultOrderOfExactMatch - 1.0) / 10.0);

	}

	private Double calculateMaxLetterPoint(Integer searchedKeywordLength) {

		double maxPointForSearch = 100.0;

		return maxPointForSearch / searchedKeywordLength;
	}

	private Double calculateLetterPointWithResultOrderMultiplier(Double maxPoint, Double resultOrderMultiplier) {

		return maxPoint * resultOrderMultiplier;
	}

	private Integer getResultOrderOfKeyword(String searchedKeyword, List<String> searchResult) {

		int indexOfExactMatch = searchResult.indexOf(searchedKeyword);

		// search results not contain exact match
		if (indexOfExactMatch == -1) {
			return 0;
		}

		return indexOfExactMatch + 1;
	}
}
