package com.dominic.network_apk;

import java.util.Arrays;
import java.util.stream.IntStream;

import processing.core.PApplet;

public class SortHelper {
	PApplet p;

	public SortHelper(PApplet p) {
		this.p = p;
	}

	public int[] sortDupplet(int[] arr, int weighted[], Boolean getWeighted) {
		if (p.min(arr[0], arr[1]) != arr[0]) {
			int k = arr[0];
			arr[0] = arr[1];
			arr[1] = k;

			weighted[0] = 1;
			weighted[1] = 0;
		}
		if (getWeighted) {
			return weighted;
		} else {
			return arr;
		}
	}

	public int[] sortIntAndGetWeight(int[] arr) {
		int[] sorted = new int[arr.length];
		// int[] weighted = new int[arr.length];
		int[] weighted = IntStream.rangeClosed(0, arr.length - 1).toArray();
		if(arr.length>2) {
			sort(arr, weighted, 0, arr.length - 1);
		}else {
			weighted=sortDupplet(arr.clone(), weighted.clone(), true);
		}
		return weighted;
	}

	// Quicksort ---------------------------------

	public int[] sort(int[] intArr, int[] weighted, int l, int r) {
		int q;
		if (l < r) {
			q = partition(intArr, weighted, l, r);
			sort(intArr, weighted, l, q);
			sort(intArr, weighted, q + 1, r);
		}
		return intArr;
	}

	public int partition(int[] intArr, int[] weighted, int l, int r) {

		int i, j, x = intArr[(l + r) / 2];
		i = l - 1;
		j = r + 1;
		while (true) {
			do {
				i++;
			} while (intArr[i] < x);

			do {
				j--;
			} while (intArr[j] > x);

			if (i < j) {
				int k = intArr[i];
				intArr[i] = intArr[j];
				intArr[j] = k;

				if (weighted != null) {
					int k2 = weighted[i];
					weighted[i] = weighted[j];
					weighted[j] = k2;
				}

			} else {
				return j;
			}
		}
	}
	// Quicksort ---------------------------------

}
