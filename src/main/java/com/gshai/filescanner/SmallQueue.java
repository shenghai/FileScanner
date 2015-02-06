package com.gshai.filescanner;

import java.util.Iterator;

public class SmallQueue implements Iterable<String> {
	private int size;
	private int start = 0;
	private int end = 0;
	private int cur = 0;
	private String[] buffer;
	private int iteratorOffset;

	public SmallQueue(int size) {
		this.size = size;
		buffer = new String[size];
		iteratorOffset = size;
	}

	public void push(String s) {
		iteratorOffset++;
		iteratorOffset = iteratorOffset > size ? size : iteratorOffset;
		buffer[cur % size] = s;
		cur++;
		start = (cur < size + 1) ? start : start + 1;
	}

	public String pull() {
		iteratorOffset--;
		return buffer[start++ % size];
	}

	public String[] getBuffer() {
		return buffer;
	}

	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {
			@Override
			public boolean hasNext() {
				return iteratorOffset > 0;
			}

			@Override
			public String next() {
				return pull();
			}

			@Override
			public void remove() {

			}
		};
	}

	@Override
	public String toString() {
		String result = "[";
		for (String s : this) {
			result = result + s + ", ";
		}
		result = result.substring(0, result.length() - 2) + "]";
		return result;
	}

}
