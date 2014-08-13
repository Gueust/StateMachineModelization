import java.io.IOException;

import parserAEFDFormat.AEFDToDSL;

/**
 * This file allows to translate a 6 lines format into the DSL format: the
 * result is printed on the console.
 * 
 * See {@link AEFDToDSL#translate(String, String)} for the details.
 * 
 * There are still manual modifications to do after the generation:
 * <ol>
 * <li>>at least the initial values of the variables</li>
 * <li>maybe some names of the variables</li>
 * </ol>
 * 
 */
public class TranslateAEFDToDSL {

  public static void main(String[] args) throws IOException {
    System.out.println(
        AEFDToDSL.translate("examples/PN à SAL.txt",
            "examples/PN à SAL Preuve.txt"));

  }

}
