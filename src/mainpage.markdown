
General Presentation {#mainpage}
====================

## What can this software do ?

This program is base on the modelization of system based on communicating
automatons. The exact semantic is defined at {TODO: put the report there ?}.

This program is able to load models from different formats. In particular, a
Domain Specific Language has been defined and should be used for new models.

It can run several verification tests on the model to check that it
respects some constraints to ensure its correctness.

Then, it can execute all the possibles executions of the model. A model is
composed of a functional model, and a proof model that models the
properties that should be respected during all executions.
During the processing of all executions, it will compare the execution of the
functional model with the proof model and detect any discrepancy.

Nothing clever (i.e. no symbolic execution, no use of BDD etc) is done. This
method is thus facing easily combinatorial explosion.

## Different entries of the software

The default package contains several classes containing the possible
main functions of the program.

- {@link AEFDFormatToACIFERFormat} : This allows to translate an AEFD file
("6 lines format") into the "ACIFER" format. Some manual modifications may
still be necessary after the generation (in particular in case of the use of
"NON"/"NOT" in the AEFD format.

- {@link CTLAutomatonGeneration} : This main function generates the AEFD
format of a CTL automata.

- {@link ExcelFormatToAEDFFormat} : This functions allows to translate the
transitions written in an Excel Format into the AEFD format. The format is
not given here, and this is only for internal use.

- {@link LaunchDSLSimulatorGUI} : Launches the simulator for files written
using the Domain Specific languages.

- {@link LaunchSimulatorGUI} : Same as above, but using AEFD files.

- {@link LaunchProofFromAEFDFormat} : Allows to launch the parsing,
verification and proof of AEFD files without the use of the graphical
interface. This is much faster to use (just modify the name of the files to
load and launch).

- {@link LauchProofFromAEFD} : same as above, but it uses the developped DSL
langauge for the description of the automata.

- {@link LaunchProofFromYAMLFormat} : Same as above, but it uses the custom
YAML format. The YAML format has been used by the authors of this tool to
generate faster the AEFD files. The custom YAML format is thus not
specified, but looking at the examples should be enough.

## Installation

The software has been developed using Eclipse, and it is advised to use it
throught Eclipse and not to use only the Graphical User Interface.

In the case you are cloning the project from Github, you will need to get the
dependencies manually (no project management software as Maven is used to
manage the dependencies). The details are given in the README.
You will also need to generate files using ANTLR.

For instance to generate the files associated with
\VerificationTools\src\domainSpecificLanguage\parser\FSM_Language.g4 , 
one need to call ANTL with the following options :
-listener -visitor -encoding UTF-8 -o src/domainSpecificLanguage/parser

If you get the full project, you will not have to do these installations steps.

## Configuration

When launching a proof, you may need to increase the memory of the Java Virtual
Machine.
For instance, to give 1Go of RAM memory during,
one need to give the following arguments to the JVM : -Xms1000m -Xmx1000m

In Eclipse, this can be done with "Run" > "Run Configurations..." > "Arguments"
tab, in the "VM Arguments" textarea.

Moreover, one may want to use the "enable assertions" option by giving the -ea
option to the JVM. It may slow down the runtime. At least, in case of an error,
enabling this option may give you the exact location of the error.

## Organization of the software

See \ref


Documentation that will appear on the main page
