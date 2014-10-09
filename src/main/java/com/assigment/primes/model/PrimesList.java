package com.assigment.primes.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bean class to keep calculated primes list and initial upper bound. The fields are JAXB annotated to allow automatic
 * marshaling to/from JSON and XML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class PrimesList {
	/**
	 * Initial number - upper bound of found primes
	 */
	@XmlElement(name = "Initial", required = true)
	private long initial;
	/**
	 * List of all primes up to initial number
	 */
	@XmlElementWrapper(name = "PrimesList")
	@XmlElement(name = "Primes", required = true)
	private List<Long> primes;

	public PrimesList(long initial, List<Long> primes) {
		this.initial = initial;
		this.primes = primes;
	}

	public PrimesList() {
		this.initial = 0;
		this.primes = new ArrayList<>();
	}

	public long getInitial() {
		return initial;
	}

	public void setInitial(long initial) {
		this.initial = initial;
	}

	public List<Long> getPrimes() {
		return primes;
	}

	public void setPrimes(List<Long> primes) {
		this.primes = primes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (initial ^ (initial >>> 32));
		result = prime * result + ((primes == null) ? 0 : primes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrimesList other = (PrimesList) obj;
		if (initial != other.initial)
			return false;
		if (primes == null) {
			if (other.primes != null)
				return false;
		} else if (!primes.equals(other.primes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("PrimesList [initial=%s, primesSize=%s]", initial, primes == null ? 0 : primes.size());
	}

}
