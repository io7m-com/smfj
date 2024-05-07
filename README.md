smfj
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.smfj/com.io7m.smfj.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.smfj%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.smfj/com.io7m.smfj?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/smfj/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/smfj.svg?style=flat-square)](https://codecov.io/gh/io7m-com/smfj)
![Java Version](https://img.shields.io/badge/21-java?label=java&color=007fff)

![com.io7m.smfj](./src/site/resources/smfj.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/smfj/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/smfj/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/smfj/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/smfj/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/smfj/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/smfj/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/smfj/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/smfj/actions?query=workflow%3Amain.windows.temurin.lts)|

## smfj

The `smfj` package provides a minimalist format for storing mesh data.

## Features

* Strongly-typed generic non-interleaved storage format.
* Carefully designed binary format that allows for mapping sections of files
  into memory for direct upload to the GPU. All data is strictly aligned for
  performance.
* Rich integer types: Store named arrays of 1-4 component 8/16/32/64-bit signed
  and unsigned integer vectors.
* Rich floating point types: Store named arrays of 1-4 component 16/32/64-bit
  floating-point vectors.
* Text and binary encodings.
* [Blender](http://blender.org) plugin to export directly to the text format.
* Event-based parsers for near zero-allocation parsing.
* Extensible filter system for processing and transforming mesh data.
* High coverage test suite.
* [OSGi-ready](https://www.osgi.org/)
* [JPMS-ready](https://en.wikipedia.org/wiki/Java_Platform_Module_System)
* ISC license.

## Specification

See the [specification](https://www.io7m.com/software/smfj).

