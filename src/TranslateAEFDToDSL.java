import java.io.IOException;

import parserAEFDFormat.AEFDToDSL;

public class TranslateAEFDToDSL {

  public static void main(String[] args) throws IOException {
    System.out.println(
        AEFDToDSL.translate("examples/PN à SAL.txt",
            "examples/PN à SAL Preuve.txt"));

  }

}
