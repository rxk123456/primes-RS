package com.assigment.primes.service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task for a single thread to search for primes. Multiple threads synchronise on counter of type AtomicLong
 */
public class CalculatePrimesTask implements Callable<List<Long>> {

	private static final Logger log = LoggerFactory.getLogger(CalculatePrimesTask.class);

	private final long maxNum;
	private final AtomicLong counter;

	public CalculatePrimesTask(long maxNum, AtomicLong counter) {
		this.maxNum = maxNum;
		this.counter = counter;
	}

	@Override
	public List<Long> call() {
		List<Long> res = new LinkedList<>();
		long toCheck = counter.incrementAndGet();
		log.trace("Start loop checking {}", toCheck);
		while (toCheck <= maxNum) {
			if (PrimeCalc.isPrime(toCheck)) {
				res.add(toCheck);
			}
			toCheck = counter.incrementAndGet();
		}
		return res;
	}
}
