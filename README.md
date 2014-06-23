StateMachineModelization
========================


Development
------------

The development is done using Eclipse Kepler Service Release 2.

Dependencies
------------

This software uses SAT4J which is embedded in the github repository (in lib/).

It also uses ANTLRV4 : antlr-4.2.2-complete.jar
This one should be download at <a>http://www.antlr.org/download.html</a>
or <a>https://github.com/antlr/website-antlr4/tree/gh-pages/download</a> and
then added in the /lib directory.

- swingx-all-1.6.4.jar

To build automaton for a horrible proprietary XLS file:
poi-3.10-FINAL-20140208.jar

which uses :
- commons-codec-1.5.jar
- commons-logging-1.1.jar
- log4j-1.2.13.jar


Java Agent
----------

To get a JVM implementation dependent size, a java agent is used. To build the
jar file, an eclipse configuration file exist in java_agent.jardesc.

Build
-------------

To build the project, it is required to:
- build the ANTLR v4 parsers for the files AEFDBooleanExpression.g4 and 
BooleanExpression.g4. The -visitor option must be used. The files are then to be
added respectively in graph.conditions.aefdParser and 
abstractGraph.conditions.parser.

Documentation
-------------

Javadoc Guidelines
<a>http://www.liferay.com/fr/community/wiki/-/wiki/Main/Javadoc+Guidelines</a>