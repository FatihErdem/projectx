package com.fatiherdem.projectx.domain.estimation;

import com.fatiherdem.projectx.domain.FibonacciUtils;
import com.fatiherdem.projectx.domain.httpclient.AmazonHttpClient;
import com.fatiherdem.projectx.domain.model.SearchVolumeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstimationService {

	private static final double MAX_POINT_FOR_SEARCH = 100.0;

	private final AmazonHttpClient amazonHttpClient;

	public SearchVolumeResponse getSearchVolume(String searchedKeyword) {

		long startTime = System.currentTimeMillis();

		List<String> separatedKeywords = getSeparatedKeywords(searchedKeyword);

		Double totalScoreOfKeyword = 0.0;

		int letterIndex = 1;

		// We calculate for each keyword in the keyword space
		for (String separatedKeyword : separatedKeywords) {

			List<String> resultsForKeyword = amazonHttpClient.getAutocompleteResult(separatedKeyword);

			Integer resultOrder = getResultOrderOfKeyword(searchedKeyword, resultsForKeyword);

			Double resultOrderMultiplier = calculateResultOrderMultiplier(resultOrder, resultsForKeyword.size());

			Double rawLetterPoint = calculateRawLetterPoint(letterIndex, searchedKeyword.length());

			Double finalLetterScore = calculateLetterPointWithResultOrder(rawLetterPoint, resultOrderMultiplier);

			totalScoreOfKeyword += finalLetterScore;
			letterIndex++;
		}

		long endTime = System.currentTimeMillis();
		long totalElapsedTime = endTime - startTime;

		return new SearchVolumeResponse(searchedKeyword, totalScoreOfKeyword.intValue(), totalElapsedTime);
	}

	/**
	 * This method separate keyword letter by letter
	 * <p>
	 * For example: input = sellics
	 * <p>
	 * s, se, sel, sell, selli, sellic, sellics will be generated
	 */
	private List<String> getSeparatedKeywords(String keyword) {

		List<String> separatedKeywords = new ArrayList<>();

		for (int i = 0; i < keyword.length(); i++) {
			separatedKeywords.add(keyword.substring(0, i + 1));
		}

		return separatedKeywords;
	}

	/**
	 * Calculates and returns result order multiplier.
	 * This result will multiply with raw letter point.
	 */
	private Double calculateResultOrderMultiplier(Integer resultOrderOfExactMatch, Integer resultCount) {

		if (resultOrderOfExactMatch == 0 || resultCount == 0) {
			return 0.0;
		}

		return 1.0 - ((resultOrderOfExactMatch - 1.0) / resultCount);
	}

	private Double calculateLetterPointWithResultOrder(Double maxPoint, Double resultOrderMultiplier) {
		return maxPoint * resultOrderMultiplier;
	}

	/**
	 * This method try to boost early match and penalizes late match for exact match.
	 */
	private Double calculateRawLetterPoint(Integer letterIndex, Integer keywordLength) {

		Integer sumOfFibonacciSeries = FibonacciUtils.sumOfFibonacciSeries(keywordLength);

		int i = keywordLength - letterIndex + 1;

		Integer fibonacci = FibonacciUtils.fibonacci(i);

		return fibonacci * MAX_POINT_FOR_SEARCH / sumOfFibonacciSeries;
	}

	/**
	 * Result result order of keyword.
	 * For example if search keyword found at 4. order return 4
	 */
	private Integer getResultOrderOfKeyword(String searchedKeyword, List<String> searchResult) {

		int indexOfExactMatch = searchResult.indexOf(searchedKeyword);

		// search results not contain exact match
		if (indexOfExactMatch == -1) {
			return 0;
		}

		return indexOfExactMatch + 1;
	}
}
