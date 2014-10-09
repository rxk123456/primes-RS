package com.assigment.primes.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.assigment.primes.model.PrimesList;

public class PrimeServiceTest {
	private static final Logger log = LoggerFactory.getLogger(PrimeCalcTest.class);

	PrimesService primesService = null;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		primesService = new PrimesService();
	}

	@Test
	public void testAlgorithms() {
		log.info("testAlgorithms");
		PrimesList div100 = primesService.getPrimes(100, PrimesService.ALGO_DIVISION);
		PrimesList sieve100 = primesService.getPrimes(100, PrimesService.ALGO_SIEVE);
		PrimesList conc100 = primesService.getPrimes(100, PrimesService.ALGO_CONCURRENT);
		assertEquals("max 100 division and sieve return the same values", div100, sieve100);
		assertEquals("max 100 division and concurrent return the same values", div100, conc100);
		
		PrimesList div9999 = primesService.getPrimes(9999, PrimesService.ALGO_DIVISION);
		PrimesList sieve9999 = primesService.getPrimes(9999, PrimesService.ALGO_SIEVE);
		PrimesList conc9999 = primesService.getPrimes(9999, PrimesService.ALGO_CONCURRENT);
		assertEquals("max 9999 division and sieve return the same values", div9999, sieve9999);
		assertEquals("max 9999 division and concurrent return the same values", div9999, conc9999);
	}
	
	@Test
	public void testAlgoValidation() {
		log.info("testAlgoValidation");
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Unknown algorithm none");
		primesService.getPrimes(100, "none");
	}
}
