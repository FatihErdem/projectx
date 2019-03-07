# Project-X

This project is developed for understanding of Amazon Autocomplete API.

## How to install

1. Download the source code
2. Navigate the root of project and run `./mvnw clean package`
3. Run `docker build -t fatiherdem/projectx .`
4. Run `docker run -p 8080:8080  fatiherdem/projectx`

After these steps you are ready to use rest API. You can call API with `GET http://localhost:8080/estimate?keyword=blanket`

## Assumptions

First I made lots of Amazon autocomplete API request for understand and reverse engineering the API. After many tries I made these assumptions.

1. I think given hint is not completely correct. Amazon boost result order of keyword if hottest. 
For example if I want to search for `iphone charger` API gives the keyword in first letter of autocomplete and top of the result list.
So I thought `iphone charger` is hottest. But if my keyword is not hot as order keyword result order is decrease.

2. When keyword become hotter autocomplete API complete exact keyword more easily and quick. I can use same example for this situation.

API completes `iphone charger` at first letter. So it must be hot. Bu also API completes `blanket` at 3th letter. So `blanket` not hotter then `iphone charger`.
So I thought I should consider both result order of exact match and letter index count of keyword.

## How is my algorithm works

First algorithm split search keyword letter by letter. For example:
```
Seached keyword: blanket
My search keyword space: b, bl, bla, blan, blank, blanke, blanket
```

After this cumulative separation algorithm makes autocomplete API call for each of them. And get all of their result list.

After this I calculated point of all letters in keyword. But there is one more variable. 
If keywords exact match finds in result at early letters I should boost, else I should decrease point.
For these reason I put fibonacci numbers in reverse on searched keyword letters.

##### Formula for letter point

```
n = keyword length
i = keyword length - letter index
letter_point = fib(i) * max_point_of_scale / sum(fib(1..n))
```


|  b | l | a | n | k | e | t |
|---|---|---|---|---|---|---|
| 13 | 8 | 5 | 3 | 2 | 1 | 1 |

Sum of all letters fibonacci numbers: 33

| Letter | Point |
|--------|-------|
| b      | 39.39 |
| l      | 24.24 |
| a      | 15.15 |
| n      | 9.09  |
| k      | 6.06  |
| e      | 3.03  |
| t      | 3.03  |

These points are raw point. Also algorithm consider the result order order exact match in search result list.

After this step algorithm calculates result order multiplier. Hint said result order `comparatively insignificant`. But it think result order is important then it said.
 

| Result Order | Multiplier |
|--------------|------------|
| 0 (no match) | 0          |
| 1            | 1          |
| 2            | 0.9        |
| 3            | 0.8        |
| 4            | 0.7        |
| 5            | 0.6        |
| 6            | 0.5        |
| 7            | 0.4        |
| 8            | 0.3        |
| 9            | 0.2        |
| 10           | 0.1        |

For calculate final point of letter multiply with raw point.

| Letter | Raw Point | Result order | Result Order Multiplier | Final Letter point |
|--------|-----------|--------------|-------------------------|--------------------|
| b      | 39.39     | 0 - no match | 0                       | 0                  |
| l      | 24.24     | 0 - no match | 0                       | 0                  |
| a      | 15.15     | 4            | 0.7                     | 10.60              |
| n      | 9.09      | 1            | 1                       | 9.09               |
| k      | 6.06      | 1            | 1                       | 6.06               |
| e      | 3.03      | 1            | 1                       | 3.03               |
| t      | 3.03      | 1            | 1                       | 3.03               |

And finally sum of final letter points makes result search volume point. For `blanket` it makes `31.81`.

The algorithm scale 0-100 to any given keyword.


## Final words

I like this assignment because it challenges me about reverse engineering of the API. I am not sure about this is correct way to calculate volume search.

I think Sellics consider lots of think when calculate these points. For example user search data, machine learning and training set.

But I don't have any of these. I know my algorithm needs many improvements. But I don't think there is right way or wrong way about calculating search volume.

Because Amazon do this in blackbox and we are try to understand how its works.

I hope you enjoy with my algorithm.