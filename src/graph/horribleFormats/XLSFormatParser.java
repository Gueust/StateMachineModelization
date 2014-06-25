package graph.horribleFormats;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class XLSFormatParser {

  private static final int AUTOMATA = 0;
  private static final int FROM = 1;
  private static final int TO = 2;
  private static final int EVENTS = 3;
  private static final int CONDITION = 4;
  private static final int ACTIONS = 5;

  private static final String NEWLINE = "\r\n";

  public static void ParseXLSFormat(String file_name, String output_file_name)
      throws IOException {

    FileInputStream fileInputStream = new FileInputStream(file_name);
    HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
    HSSFSheet worksheet = workbook.getSheet("T_Auto");

    BufferedWriter out =
        new BufferedWriter(new FileWriter(output_file_name));

    boolean first = true;
    StringBuffer result = new StringBuffer();

    Iterator<Row> rowIterator = worksheet.iterator();
    rowIterator.next();
    while (rowIterator.hasNext()) {
      Row row = rowIterator.next();

      String automaton = row.getCell(AUTOMATA).getStringCellValue();

      Cell from_cell = row.getCell(FROM);
      int from;
      if (from_cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
        from = (int) from_cell.getNumericCellValue();
      } else {
        from = Integer.parseInt(from_cell.getStringCellValue());
      }

      Cell to_cell = row.getCell(TO);
      int to;
      if (to_cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
        to = (int) to_cell.getNumericCellValue();
      } else {
        to = Integer.parseInt(to_cell.getStringCellValue());
      }

      String events = row.getCell(EVENTS).getStringCellValue();
      String condition = row.getCell(CONDITION).getStringCellValue();
      String actions = row.getCell(ACTIONS).getStringCellValue();

      if (first) {
        first = false;
      } else {
        result.append(NEWLINE);
      }

      result.append(automaton + NEWLINE
          + from + NEWLINE
          + to + NEWLINE
          + events + NEWLINE
          + condition + NEWLINE
          + actions);
    }
    fileInputStream.close();
    String string_to_write = result.toString();
    /* Replacing some occurences */
    string_to_write = string_to_write.replaceAll("ACT_Init[a-zA-Z0-9_]*",
        "ACT_Init");
    string_to_write = string_to_write
        .replaceAll("Occupe(?=[^e])", "Occupee");
    string_to_write = string_to_write
        .replaceAll("_Decondamnee", "_Decondamne");
    string_to_write = string_to_write
        .replaceAll("_Condamnee", "_Condamne");
    string_to_write = string_to_write
        .replaceAll("_Controlee", "_Controle");
    string_to_write = string_to_write
        .replaceAll("_Restituee", "_non_Prise");
    string_to_write = string_to_write
        .replaceAll("_non_Libere", "_en_Action");
    string_to_write = string_to_write
        .replaceAll("_non_en_Action", "_Libere");
    out.write(string_to_write);
    out.close();
    CTLReplacer ctl_replacer = new CTLReplacer(output_file_name,
        "Graph_with_corrected_CTL.txt", false);
    ctl_replacer = new CTLReplacer("Nurieux/NurieuxTestJLPreuv_Auto.txt",
        "Proof_with_corrected_CTL.txt", true);

  }

  public static void main(String[] args) throws IOException {
    ParseXLSFormat("Nurieux/Nurieux_Donnees.xls",
        "Nurieux/generated_nurieux.txt");
    System.out.println("Finished");
  }
}
