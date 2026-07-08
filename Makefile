.PHONY: test test-java8 compile package clean verify

test:
	mvn test

test-java8:
	mvn -DskipTests=false test

compile:
	mvn -DskipTests compile

package:
	mvn -DskipTests package

verify:
	mvn verify

clean:
	mvn clean
