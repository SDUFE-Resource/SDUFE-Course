SRCS = DEScalc.java DES.java GenDES.java Util.java
OBJS = DEScalc.class DES.class GenDES.class Util.class
EXTRAS = DEScalc.html index.html lab_des.html README Makefile manifest.txt \
	DEScalc check_triples nist-triples

# assorted Makefile targets

all: DEScalc.jar

clean:
	-rm *.class
	-rm -rf doc

doc: $(SRCS) Makefile
	@test -d doc || mkdir doc
	javadoc -author -private -nodeprecatedlist -d doc $(SRCS)

jars: DEScalc.jar

test: $(OBJS)
	java DES

# detailed Makefile dependencies

DEScalc.jar: $(OBJS) $(SRCS) $(EXTRAS) Makefile manifest.txt
	jar cvmf manifest.txt DEScalc.jar $(OBJS) $(SRCS) $(EXTRAS)

.SUFFIXES: .class .java

.java.class:
	javac $<

