import java.io.IOException;

import parserAEFDFormat.AEFDToDSL;

public class TranslateAEFDToDSL {

  public static void main(String[] args) throws IOException {
    System.out.println(
        AEFDToDSL.translate("PN/PN à SAL.txt",
            "PN/PN à SAL Preuve.txt"));

  }

}
