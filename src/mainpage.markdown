/**
 * General Presentation {#mainpage}
 * ====================
 * 
 * ## What can this software do ?
 * 
 * This program is base on the modelization of system based on communicating
 * automatons. The exact semantic is defined at {TODO: put the report there ?}.
 * 
 * This program is able to load models from different formats. In particular, a
 * Domain Specific Language has been defined and should be used for new models.
 * 
 * Then, it can run several verification tests on the model to check that it
 * respects some constraints to ensure its correctness.
 * 
 * Finally, it can execute all the possibles executions of the model. A model is
 * composed of a functional model, and a proof model that models the
 * properties that should be respected during all executions.
 * During the processing of all executions, it will compare the execution of the
 * functional model with the proof model and detect any discrepancy.
 * 
 * Nothing clever (i.e. no symbolic execution, no use of BDD etc) is done. This
 * method is thus facing easily combinatorial explosion.
 * 
 * ## Different entries of the software
 * 
 * The default package contains several classes containing the possible
 * main functions of the program.
 * 
 * - {@link AEFDFormatToACIFERFormat} : This allows to translate an AEFD file
 * ("6 lines format") into the "ACIFER" format. Some manual modifications may
 * still be necessary after the generation (in particular in case of the use of
 * "NON"/"NOT" in the AEFD format.
 * 
 * - {@link CTLAutomatonGeneration} : This main function generates the AEFD
 * format of a CTL automata.
 * 
 * - {@link ExcelFormatToAEDFFormat} : This functions allows to translate the
 * transitions written in an Excel Format into the AEFD format. The format is
 * not given here, and this is only for internal use.
 * 
 * - {@link LaunchDSLSimulatorGUI} : Launches the simulator for files written
 * using the Domain Specific languages.
 * 
 * - {@link LaunchSimulatorGUI} : Same as above, but using AEFD files.
 * 
 * - {@link LaunchProofFromAEFDFormat} : Allows to launch the parsing,
 * verification and proof of AEFD files without the use of the graphical
 * interface. This is much faster to use (just modify the name of the files to
 * load and launch).
 * 
 * - {@link LaunchProofFromYAMLFormat} : Same as above, but it uses the custom
 * YAML format. The YAML format has been used by the authors of this tool to
 * generate faster the AEFD files. The custom YAML format is thus not
 * specified, but looking at the examples should be enough.
 * 
 * ## Organization of the software
 * 
 * See \ref
 * 
 * 
 * Documentation that will appear on the main page
 */