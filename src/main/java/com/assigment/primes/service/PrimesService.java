package com.assigment.primes.service;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.assigment.primes.model.PrimesList;

/**
 * Service class used by JAX-RS to get WebService responses. It supports different Media Types based on the request. All
 * the GET methods support optional algorithm parameter used to decide which algorithm to use. If algorithm parameter is
 * empty it defaults to trial division algorithm. All the methods will throw IllegalArgumentException for invalid
 * algorithm names.
 */
@Path("")
public class PrimesService {

	/**
	 * name of the optional query parameter to choose the algorithm used
	 */
	public static final String ALGO_PARAM_NAME = "algo";
	/**
	 * Trial division algorithm, if algo parameter is not specified this is the default
	 */
	public static final String ALGO_DIVISION = "division";
	/**
	 * Trial division algorithm running on multiple threads
	 */
	public static final String ALGO_CONCURRENT = "concurrent";
	/**
	 * Optimised Sieve of Eratosthenes algorithm
	 */
	public static final String ALGO_SIEVE = "sieve";

	private static final Logger log = LoggerFactory.getLogger(PrimesService.class);

	@GET
	@Path("/{max}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public PrimesList getPrimes(@PathParam("max") long max,
			@QueryParam(ALGO_PARAM_NAME) @DefaultValue(ALGO_DIVISION) String algorithm) {
		log.debug("starting getPrimes for max={} algorithm={}", max, algorithm);
		PrimesList res = null;
		if (algorithm.equals(ALGO_DIVISION)) {
			res = PrimeCalc.getPrimesByDivision(max);
		} else if (algorithm.equals(ALGO_CONCURRENT)) {
			res = PrimeCalc.getPrimesByDivisionConcurrent(max);
		} else if (algorithm.equals(ALGO_SIEVE)) {
			res = PrimeCalc.getPrimesBySieve(max);
		} else {
			log.error("Unknown algorithm {}", algorithm);
			throw new IllegalArgumentException("Unknown algorithm " + algorithm);
		}
		log.info("getPrimes returns {}", res);
		return res;
	}

	@GET
	@Path("/{max}")
	@Produces(MediaType.TEXT_HTML)
	public String getPrimesAsHtml(@PathParam("max") long max,
			@QueryParam(ALGO_PARAM_NAME) @DefaultValue(ALGO_DIVISION) String algorithm) {
		PrimesList res = getPrimes(max, algorithm);
		StringBuilder buf = new StringBuilder("<html><head><title>Primes ").append(res.getInitial())
				.append("</title></head><body><p>Initial:").append(res.getInitial()).append("</p>");
		buf.append("Primes:<br/><ul>");
		for (long num : res.getPrimes()) {
			buf.append("<li>").append(num).append("</li>");
		}
		buf.append("</ul></body></html>");
		return buf.toString();
	}

	@GET
	@Path("/{max}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPrimesAsText(@PathParam("max") long max,
			@QueryParam(ALGO_PARAM_NAME) @DefaultValue(ALGO_DIVISION) String algorithm) {
		PrimesList res = getPrimes(max, algorithm);
		StringBuilder buf = new StringBuilder("Initial: ").append(res.getInitial());
		buf.append("\nPrimes: ").append(res.getPrimes());
		return buf.toString();
	}
}
