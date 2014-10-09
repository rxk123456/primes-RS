package com.assigment.primes.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.assigment.primes.model.PrimesList;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class PrimeServiceIT {
	private static final Logger log = LoggerFactory.getLogger(PrimeServiceIT.class);

	private WebTarget primesTarget = null;

	@Before
	public void setUp() throws Exception {
		Client client = ClientBuilder.newClient();
		primesTarget = client.target("http://localhost:8080").path("primes");
		XMLUnit.setIgnoreWhitespace(true);
	}

	@Test
	public void testJSON() throws Exception {
		log.info("testJSON");
		validate(100, PrimesService.ALGO_DIVISION, MediaType.APPLICATION_JSON_TYPE, "expected/primes100.json");
		validate(100, PrimesService.ALGO_SIEVE, MediaType.APPLICATION_JSON_TYPE, "expected/primes100.json");
		validate(100, PrimesService.ALGO_CONCURRENT, MediaType.APPLICATION_JSON_TYPE, "expected/primes100.json");
		Response response = getAndValidateResponse(100, PrimesService.ALGO_DIVISION, MediaType.APPLICATION_JSON_TYPE);
		PrimesList resultDivision = response.readEntity(PrimesList.class);
		log.debug("result division={}", resultDivision);
		assertEquals("max 100 gives 25 primes", resultDivision.getPrimes().size(), 25);
		response = getAndValidateResponse(100, PrimesService.ALGO_SIEVE, MediaType.APPLICATION_JSON_TYPE);
		PrimesList resultSieve = response.readEntity(PrimesList.class);
		log.debug("result sieve={}", resultSieve);
		assertEquals("PrimeList from divide and sieve the same", resultSieve, resultDivision);
	}

	@Test
	public void testXML() throws Exception {
		log.info("testXML");

		validate(127, PrimesService.ALGO_DIVISION, MediaType.APPLICATION_XML_TYPE, "expected/primes127.xml");
		validate(127, PrimesService.ALGO_SIEVE, MediaType.APPLICATION_XML_TYPE, "expected/primes127.xml");
		validate(127, PrimesService.ALGO_CONCURRENT, MediaType.APPLICATION_XML_TYPE, "expected/primes127.xml");
		Response response = getAndValidateResponse(127, PrimesService.ALGO_DIVISION, MediaType.APPLICATION_XML_TYPE);
		PrimesList resultDivision = response.readEntity(PrimesList.class);
		log.debug("result division={}", resultDivision);
		assertEquals("max 127 gives 31 primes", resultDivision.getPrimes().size(), 31);
		response = getAndValidateResponse(127, PrimesService.ALGO_DIVISION, MediaType.APPLICATION_XML_TYPE);
		PrimesList resultSieve = response.readEntity(PrimesList.class);
		log.debug("result sieve={}", resultSieve);
		assertEquals("PrimeList from divide and sieve the same", resultSieve, resultDivision);
	}

	@Test
	public void testText() throws Exception {
		log.info("testText");
		validate(173, PrimesService.ALGO_DIVISION, MediaType.TEXT_PLAIN_TYPE, "expected/primes173.txt");
		validate(173, PrimesService.ALGO_SIEVE, MediaType.TEXT_PLAIN_TYPE, "expected/primes173.txt");
		validate(173, PrimesService.ALGO_CONCURRENT, MediaType.TEXT_PLAIN_TYPE, "expected/primes173.txt");
	}

	@Test
	public void testHtml() throws Exception {
		log.info("testHtml");
		validate(83, PrimesService.ALGO_DIVISION, MediaType.TEXT_HTML_TYPE, "expected/primes83.html");
		validate(83, PrimesService.ALGO_SIEVE, MediaType.TEXT_HTML_TYPE, "expected/primes83.html");
		validate(83, PrimesService.ALGO_CONCURRENT, MediaType.TEXT_HTML_TYPE, "expected/primes83.html");
	}

	@Test
	public void testNegative() throws Exception {
		log.info("testNegative");
		validate(83, PrimesService.ALGO_DIVISION, MediaType.TEXT_HTML_TYPE, "expected/primes83.html");
		validate(83, PrimesService.ALGO_SIEVE, MediaType.TEXT_HTML_TYPE, "expected/primes83.html");
		validate(83, PrimesService.ALGO_CONCURRENT, MediaType.TEXT_HTML_TYPE, "expected/primes83.html");
	}
	
	
	private Response getAndValidateResponse(long maxNum, String algorithm, MediaType mediaType) throws Exception {
		WebTarget target = primesTarget.path(String.valueOf(maxNum)).queryParam(PrimesService.ALGO_PARAM_NAME,
				algorithm);
		Invocation.Builder invocationBuilder = target.request(mediaType);
		Response response = invocationBuilder.get();
		log.debug("{} response={}", algorithm, response);
		assertEquals(algorithm + " status OK", response.getStatus(), 200);
		log.debug("responseMedia={}", response.getMediaType());
		return response;
	}

	private void validate(long maxNum, String algorithm, MediaType mediaType, String expectedFile) throws Exception {
		Response response = getAndValidateResponse(maxNum, algorithm, mediaType);
		String responseText = response.readEntity(String.class);
		log.debug("{} responseText={}", algorithm, responseText);
		String expectedText = getFileContent(expectedFile);
		if (mediaType.equals(MediaType.APPLICATION_XML)) {
			Diff d = new Diff(expectedText, responseText);
			assertTrue(d.toString(), d.identical());
		} else {
			assertEquals(algorithm + " result as expected", responseText.replaceAll("\\s+", ""),
					expectedText.replaceAll("\\s+", ""));
		}
	}

	private String getFileContent(String fileName) throws IOException {
		URL fileURL = Resources.getResource(fileName);
		return Resources.toString(fileURL, Charsets.UTF_8);
	}
}
