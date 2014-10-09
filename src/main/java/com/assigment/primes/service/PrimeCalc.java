package com.assigment.primes.service;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.assigment.primes.model.PrimesList;

/**
 * Utility class to calculate prime numbers.
 */
public class PrimeCalc {

	private static final Logger log = LoggerFactory.getLogger(PrimeCalc.class);

	/**
	 * Calculates prime numbers up to maxNum using optimised trial division algorithm. It checks for possible overflow
	 * of integer index by approximating number of primes as N/(ln N -1).
	 * 
	 * @param maxNum upper bound of primes
	 * @return list of prime numbers wrapped in the {@link PrimesList} object
	 */
	public static final PrimesList getPrimesByDivision(long maxNum) {
		log.trace("Staring getPrimesByDivision maxNum={}", maxNum);

		List<Long> primes = validateAndInit(maxNum);
		if (maxNum >= 2) {
			primes.add(2L); // add the only even prime
			// check all odd numbers up to maxNum
			for (long i = 3; i <= maxNum; i += 2) {
				if (isPrime(i)) {
					primes.add(i);
				}
			}
		}
		return new PrimesList(maxNum, primes);
	}

	/**
	 * Calculates prime numbers up to maxNum using optimised Sieve of Eratosthenes algorithm. The algorithm requires
	 * data structures with index up to upper bound. If the maxNum parameter is greater than Integer.MAX_VALUE it throws
	 * IllegalArgumentException..
	 * 
	 * @param maxNum upper bound of primes
	 * @return list of prime numbers wrapped in the {@link PrimesList} object
	 */
	public static final PrimesList getPrimesBySieve(long maxNum) {
		log.trace("Staring getPrimesBySieve maxNum={}", maxNum);
		if (maxNum + 1 > Integer.MAX_VALUE) {
			log.error("Overflow - Sieve algorithm structures too small for maxNum={}", maxNum);
			throw new IllegalArgumentException("Overflow - Sieve algorithm structures too small");
		}
		List<Long> primes = validateAndInit(maxNum);
		if (maxNum >= 2) {
			BitSet bits = new BitSet((int) (maxNum + 1));
			bits.set(0); // 0 is not a prime
			bits.set(1); // 1 is not a prime
			// The max divider of ant integer N is sqrt(N)
			long maxDivider = (long) Math.sqrt(maxNum) + 1;
			log.trace("for maxNum={} maxDivider={}", maxNum, maxDivider);
			int currentIndex = bits.nextClearBit(0);
			// find next unmarked bit up to max divider
			while (currentIndex >= 0 && currentIndex < maxDivider) {
				// we have a prime, mark all its multiples as non prime
				// start from i*i as all the smaller numbers were already marked by smaller primes
				for (long num = currentIndex * currentIndex; num < bits.size(); num += currentIndex) {
					bits.set((int) num);
				}
				currentIndex = bits.nextClearBit(currentIndex + 1);
			}
			for (int i = 2; i < bits.size() && i <= maxNum; i++) {
				if (!bits.get(i)) {
					primes.add((long) i);
				}
			}
		}
		return new PrimesList(maxNum, primes);
	}

	/**
	 * Functionally the same as {@link getPrimesByDivision} but uses multiple threads find primes.
	 * 
	 * @param maxNum upper bound of primes
	 * @return list of prime numbers wrapped in the {@link PrimesList} object
	 * @throws Exception if any of the spawned threads is interrupted
	 */
	public static final PrimesList getPrimesByDivisionConcurrent(long maxNum) {
		log.trace("Staring getPrimesByDivisionConcurrent maxNum={}", maxNum);

		List<Long> primes = validateAndInit(maxNum);
		if (maxNum >= 2) {
			primes.add(2L); // add the only even prime
			// we do not have any io so number of threads equal to avaliable cores will give the best concurrency
			// without too much context switching
			int threadCount = Runtime.getRuntime().availableProcessors();
			log.debug("threadCount={}", threadCount);
			ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
			AtomicLong counter = new AtomicLong(2);
			List<Future<List<Long>>> futures = new ArrayList<>(threadCount);
			// create a task for each thread
			for (long i = 0; i < threadCount; i++) {
				Future<List<Long>> f = threadPool.submit(new CalculatePrimesTask(maxNum, counter));
				futures.add(f);
			}
			// collect results, f.get will block until the task is finished
			for (Future<List<Long>> f : futures) {
				try {
					primes.addAll(f.get());
				} catch (Exception e) {
					log.error("Error while calculating primes concurrently: {}", e, e);
				}
			}
			// list merged from separate tasks not be in natural order
			// sort it to ensure ascending order of primes
			Collections.sort(primes);
		}
		return new PrimesList(maxNum, primes);
	}

	/**
	 * Checks if the given number is a prime. It uses trial division algorithm i.e. checks if the number can be divided
	 * only by 1 and itself. The division check skips all the multiples of 2 (even numbers) and 3 as the check is done
	 * at the start.
	 * 
	 * @param num number to check
	 * @return true if the number is a prime, false otherwise
	 */
	static final boolean isPrime(long num) {
		if (num % 2 == 0)
			// for odd numbers only 2 is a prime
			return num == 2;
		if (num % 3 == 0)
			// for multiples of 3 only 3 is a prime
			return num == 3;
		int step = 4;
		// for given number N max natural divider is sqrt(N)
		long maxDivider = (long) Math.sqrt(num) + 1;
		// check dividers skipping even numbers and multiples by 3
		// step is 2,4,2,4... and so on
		for (long i = 5; i < maxDivider; step = 6 - step, i += step) {
			if (num % i == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Calculates approximate number of primes up to a specified number. It uses the function N/(ln N-1) to approximate
	 * the number of primes up to N. This is a helper method to set initial sizes of the collections. It will always
	 * return number > 10. The approximate function underestimates the number of primes a bit, to avoid collections
	 * resizing the returned numbers is increased by 2%.
	 * 
	 * @param maxNum the maximum number to calculate primes up to
	 * @return approximate number of primes up to specified maximum
	 */
	private static final long approxPrimesToNumber(long maxNum) {
		double approx = maxNum / (Math.log(maxNum) - 1);
		log.trace("approx={}", approx);
		return Math.max(10, (long) Math.ceil(approx * 1.02));
	}

	/**
	 * Checks is approximate number of primes will not overflow Integer index of the List. It initialises the list to
	 * keep found primes with the initial capacity set to expected number of primes to avoid internal array
	 * resizing/copying.
	 * 
	 * @param maxNum upper bound of primes
	 * @return List to keep prime numbers initialised with expected capacity
	 * @throws IllegalArgumentException if the expected number of primes will overflow Integer index
	 */
	private static final List<Long> validateAndInit(long maxNum) throws IllegalArgumentException {
		long approxPrimes = approxPrimesToNumber(maxNum);
		log.trace("for maxNum={} approxPrimes={}", maxNum, approxPrimes);
		if (approxPrimes > Integer.MAX_VALUE) {
			log.error("Overflow - estimated number of primes exceed list capacity, approxPrimes={}", approxPrimes);
			throw new IllegalArgumentException("Overflow - estimated number of primes exceed list capacity");
		}
		List<Long> list = new ArrayList<>((int) approxPrimes);
		return list;
	}
}
