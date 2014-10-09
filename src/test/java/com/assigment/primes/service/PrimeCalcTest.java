package com.assigment.primes.service;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.assigment.primes.model.PrimesList;

public class PrimeCalcTest {

	private static final Logger log = LoggerFactory.getLogger(PrimeCalcTest.class);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testDivision() {
		log.info("testDivision");
		assertEquals("ten gives four primes", PrimeCalc.getPrimesByDivision(10).getPrimes().size(), 4);
		assertEquals("max 100", PrimeCalc.getPrimesByDivision(100).getPrimes().size(), 25);
		assertEquals("max 1000", PrimeCalc.getPrimesByDivision(1000).getPrimes().size(), 168);
		assertEquals("max 1000000", PrimeCalc.getPrimesByDivision(1000000).getPrimes().size(), 78498);
		PrimesList primes997 = PrimeCalc.getPrimesByDivision(997);
		assertEquals("max 997 is the same as 1000", primes997.getPrimes().size(), 168);
		long lastPrime = primes997.getPrimes().get(167);
		assertEquals("max 997 the last prime is 997", lastPrime, 997L);
	}

	@Test
	public void testDivisionNegative() {
		log.info("testDivisionNegative");
		assertEquals("negative input get empty list", PrimeCalc.getPrimesByDivision(-10).getPrimes().size(), 0);
		assertEquals("zero get empty list", PrimeCalc.getPrimesByDivision(0).getPrimes().size(), 0);
		assertEquals("one get empty list", PrimeCalc.getPrimesByDivision(1).getPrimes().size(), 0);
	}

	@Test
	public void testSieve() {
		log.info("testDivision");
		assertEquals("ten gives four primes", PrimeCalc.getPrimesBySieve(10).getPrimes().size(), 4);
		assertEquals("max 100", PrimeCalc.getPrimesBySieve(100).getPrimes().size(), 25);
		assertEquals("max 1000", PrimeCalc.getPrimesBySieve(1000).getPrimes().size(), 168);
		assertEquals("max 1000000", PrimeCalc.getPrimesBySieve(1000000).getPrimes().size(), 78498);
	}

	@Test
	public void testSieveNegative() {
		log.info("testDivisionNegative");
		assertEquals("negative input get empty list", PrimeCalc.getPrimesBySieve(-10).getPrimes().size(), 0);
		assertEquals("zero get empty list", PrimeCalc.getPrimesBySieve(0).getPrimes().size(), 0);
		assertEquals("one get empty list", PrimeCalc.getPrimesBySieve(1).getPrimes().size(), 0);
	}

	@Test
	public void testDivisionConcurrent() throws Exception {
		log.info("testDivisionConcurrent");
		assertEquals("ten gives four primes", PrimeCalc.getPrimesByDivisionConcurrent(10).getPrimes().size(), 4);
		assertEquals("max 100", PrimeCalc.getPrimesByDivisionConcurrent(100).getPrimes().size(), 25);
		assertEquals("max 1000", PrimeCalc.getPrimesByDivisionConcurrent(1000).getPrimes().size(), 168);
		assertEquals("max 1000000", PrimeCalc.getPrimesByDivisionConcurrent(1000000).getPrimes().size(), 78498);
	}

	@Test
	public void testDivisionConcurrentNegative() throws Exception {
		log.info("testDivisionConcurrentNegative");
		assertEquals("negative input - empty list", PrimeCalc.getPrimesByDivisionConcurrent(-10).getPrimes().size(), 0);
		assertEquals("zero get empty list", PrimeCalc.getPrimesByDivisionConcurrent(0).getPrimes().size(), 0);
		assertEquals("one get empty list", PrimeCalc.getPrimesByDivisionConcurrent(1).getPrimes().size(), 0);
	}

	@Test
	public void testValidationSieve() {
		log.info("testValidation");
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Overflow - Sieve algorithm structures too small");
		PrimeCalc.getPrimesBySieve(1000000000000L);
	}

	@Test
	public void testValidationDivision() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Overflow - estimated number of primes exceed list capacity");
		PrimeCalc.getPrimesByDivision(4000000000000000000L);
	}

}
