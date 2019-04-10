#!/bin/bash

antlr4 Tex_grammar.g4 #anlt4 should be installed in the system
javac *.java
java Main $1 $2
