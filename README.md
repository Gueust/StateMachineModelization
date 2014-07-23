StateMachineModelization
========================

Development
------------

The development is done using Eclipse Kepler Service Release 2.

Launching the tools
-------------------

All the main functions are in separated classes in the default package.
It is advised to enable assertions in the JVM (-ea option).

Build
-----

To build the project, it is required to:
- for the AEFD format parsing:
build the ANTLR v4 parsers for the files AEFDBooleanExpression.g4 and 
BooleanExpression.g4. The -visitor option *must* be used. The files are then to 
be added respectively in graph.conditions.aefdParser and 
abstractGraph.conditions.parser.
- for the DSL parsing: same operation with FSM_language.g4 in 
domainSpecificLanguage/parser.

Dependencies to run the project
-------------------------------

This software uses SAT4J which is embedded in the github repository (in lib/).

It also uses ANTLRV4 : antlr-4.2.2-complete.jar
This one should be download at <a>http://www.antlr.org/download.html</a>
or <a>https://github.com/antlr/website-antlr4/tree/gh-pages/download</a> and
then added in the /lib directory.

- swingx-all-1.6.4.jar (can be download at https://java.net/downloads/swingx/releases/)

- To build automaton for a horrible proprietary XLS file:
poi-3.10-FINAL-20140208.jar (see http://poi.apache.org/download.html)

which uses:
- commons-codec-1.5.jar
- commons-logging-1.1.jar
- log4j-1.2.13.jar

- graphviz-2.38 is used in a test to display the activation graph.
- mapdb-1.0.3.jar can be used to have a HashMap backed up with disk memory
during the model checking.

- snakeyaml-1.13.jar is used when using yaml files
The final arborescence is:

lib/
  graphviz-2.38/
  POI HSSF/
    commons-codec-1.5.jar
    commons-logging-1.1.jar
    log4j-1.2.13.jar
    poi-3.10-FINAL-20140208.jar
  sat4j-core-v20130525/
    org.sat4j.core.jar
  antlr-4.2.2-complete.jar
  snakeyaml-1.13.jar
  swingx-all-1.6.4.jar

Java Agent
----------

To get a JVM implementation dependent size, a java agent is used. To build the
jar file, an eclipse configuration file exist in java_agent.jardesc.
This Java Agent is not used anymore but kept for debug purposes.

Documentation
-------------

Javadoc Guidelines
<a>http://www.liferay.com/fr/community/wiki/-/wiki/Main/Javadoc+Guidelines</a>