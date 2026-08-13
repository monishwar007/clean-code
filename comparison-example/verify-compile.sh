#!/usr/bin/env bash
# verify-compile.sh
#
# Compiles both the without-skill/ and with-skill/ versions against a
# minimal local stub of the Spring/JPA/JUnit/Mockito API surface they use
# (see _stub-spring-api/), and runs the pure-JUnit test (OrderPolicyTest)
# for real.
#
# This is a lightweight sanity check for environments without internet
# access to Maven Central — it is NOT a substitute for a real
# `mvn clean verify` in a normal project setup, since the stub API only
# implements enough of Spring/JPA/Mockito's surface to type-check, not
# their runtime behavior (Mockito mocking in particular is not
# functionally implemented — OrderServiceTest / OrderControllerTest are
# verified to *compile* correctly here, and would need real Mockito/JUnit
# to actually execute).
#
# Usage: ./verify-compile.sh

set -e
cd "$(dirname "$0")"

echo "== Compiling without-skill/ =="
rm -rf /tmp/out-without-skill && mkdir -p /tmp/out-without-skill
find _stub-spring-api without-skill/src/main -name "*.java" > /tmp/without-skill-files.txt
javac -d /tmp/out-without-skill @/tmp/without-skill-files.txt
echo "OK - $(wc -l < /tmp/without-skill-files.txt) files compiled clean"
echo

echo "== Compiling with-skill/ (main + test) =="
rm -rf /tmp/out-with-skill && mkdir -p /tmp/out-with-skill
find _stub-spring-api with-skill/src/main with-skill/src/test -name "*.java" > /tmp/with-skill-files.txt
echo MiniTestRunner.java >> /tmp/with-skill-files.txt
javac -d /tmp/out-with-skill @/tmp/with-skill-files.txt
echo "OK - $(wc -l < /tmp/with-skill-files.txt) files compiled clean"
echo

echo "== Running OrderPolicyTest for real (pure JUnit, no Mockito needed) =="
java -cp /tmp/out-with-skill MiniTestRunner com.example.orders.service.OrderPolicyTest
echo
echo "Note: OrderServiceTest and OrderControllerTest use Mockito, which this"
echo "lightweight stub does not functionally implement (no bytecode mocking)."
echo "They are verified to compile/type-check correctly above; run them with"
echo "a real 'mvn test' (internet access to Maven Central required) to"
echo "execute them."
