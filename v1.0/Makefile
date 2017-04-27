all:
	javac worms/Worms.java

run: all
	java worms.Worms

tidy:
	rm -f $(shell find . -name \*~ -o -name \#\*)

clean: tidy
	rm -f worms/*.class
