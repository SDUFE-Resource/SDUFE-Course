DES Calculator

The DES Calculator applet is used to encrypt or decrypt test data values
using DES block cipher.  It takes a 64-bit (16 hex digit) data value
and a 128-bit (32 hex digit) key.  It can optionally provide a trace
of the calculations performed, with varying degrees of detail.

This source jarfile contains the source distribution for this program.
It contains the following files:

DES.java	- class implementing the DES cipher.
		  It contains a self-test main routine, which can test
		  using an internally coded triple, tracing at the 
		  specified level, or with an explicitly sprecified triple,
		  by invoking using one of the following forms:
		      java DES
		      java DES level
		      java DES key plain cipher level
DEScalc.java	- class implementing the calculator GUI (as an applet)
		  and main program for when run as an application.
		  Can be run either as an applet (using DEScalc.html),
		  as a GUI by running as:
		      java -jar DEScalc.jar [-t level]
		  or in command-line to generate en/decrypt traces as:
		      java -jar DEScalc.jar [-e|-d] [-tlevel] hexkey hexdata
GenDES.java	- a program to generate random DES (key,plain,cipher)
		  triples. eg for use in student labs.
		  To generate n triples run as:
		      java GenDES n
Util.java	- utility routines for converting and displaying binary data

index.html	- overview HTML file describing this program
DEScalc.html	- HTML file to run DEScalc as an applet.

check_triples	- Unix shellscript to read and verify a file of test triples
nist-triples	- a set of test triples to validate the implementation
manifest.txt	- JAR manifest file specifying the main class for jar execution
DEScalc	- Unix shell script to run DEScalc (wraps java command)
Makefile	- Unix makefile to compile the code and create the jar
		  file using the JDK utilities
README		- this README file

You can build the class files and main program jar file using:
	make
You can test the DES class using:
	make test
You can build the source file distribution using:
	make jars
You can construct a javadoc tree in directory doc using:
	make doc
You can remove the assorted class files and doc using:
	make clean

Lawrie Brown / 11 Feb 2005

