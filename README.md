primes-RS
=========
Simple RESTfull WebService to calculate prime numbers up to a given maximum.
Allows selecting algorithm to calculate primes, currently implemented:

1. Trial Division (single thread)
2. Sieve of Eratosthenes

Supports multiple output formats based on request `Accept` header, currently implemented:

1. application/json
2. application/xml
3. text/html
4. text/plain

Uses [Maven](http://maven.apache.org/) to build and run unit and integration tests. Integration tests are run on embedded [Jetty](http://www.eclipse.org/jetty/) instance.
The result war file can be deployed to any servlet 3.0+ compatible container. It is also possible to run the project using embedded Jetty instance. 

Uses [Jersey](https://jersey.java.net/) as the JAX-RS implementation.

The project uses slf4j/logback for logging. The server log of the application is in the file `logs/primes.log`, the output of unit and integration tests is printed to the console.

### Usage
to package war file and run all unit and integration tests:

`mvn verify`

to start project as a "stand alone" application (uses internal jetty on port 8080):

`mvn jetty:run`

To enable Jersey logging to the console start the server with system property pointing to the log configuration file:

`mvn jetty:run -Djava.util.logging.config.file=src/test/resources/jerseyJUL.properties`

### URL formats to invoke the service
(all examples assume servlet container running on localhost:8080 - as is the case when started embedded Jetty)

The URL template:

`monospace`,~~http://localhost:8080/primes/{upper bound}[?algo={algorithm name}]~~, `monospace`

Get all primes up to maximum 10 - by default use Trial Division algorithm
`monospace`,~~http://localhost:8080/primes/10~~, `monospace`

The above is equivalent to directly specifying the Trial Division algorithm
`monospace`,~~http://localhost:8080/primes/10?algo=division~~, `monospace`

Get all primes up to maximum 100 - use Sieve of Eratosthenes algorithm
`monospace`,~~http://localhost:8080/primes/100?algo=sieve~~, `monospace`
