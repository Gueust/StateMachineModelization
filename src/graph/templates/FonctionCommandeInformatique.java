package graph.templates;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class FonctionCommandeInformatique {

  private HashMap<String, LinkedList<HashMap<String, LinkedList<String>>>> FCI_list;

  public HashMap<String, LinkedList<HashMap<String, LinkedList<String>>>> getFCI_list() {
    return FCI_list;
  }

  public void setFCI_list(
      HashMap<String, LinkedList<HashMap<String, LinkedList<String>>>> fCI_list) {
    FCI_list = fCI_list;
  }

  public static FonctionCommandeInformatique load(String file_name)
      throws IOException {
    File file = new File(file_name);
    return load(file);
  }

  /**
   * 
   * @param file
   * @return The list of the FCI.
   * @throws IOException
   */
  public static FonctionCommandeInformatique load(File file) throws IOException {

    InputStream input_stream = new FileInputStream(file);

    Constructor constructor = new Constructor(
        FonctionCommandeInformatique.class);

    Yaml yaml = new Yaml(constructor);

    FonctionCommandeInformatique result =
        (FonctionCommandeInformatique) yaml.load(input_stream);
    return result;
  }

  @Test
  public void loadTest() throws IOException {
    String file_name = "src/test/resources/Miscellaneous/FCI_ACT.yaml";
    System.out.println(load(file_name).FCI_list);
    assertEquals(
        load(file_name).FCI_list.toString(),
        "{FCI_1=[{true=[A, B, C]}, {false=B}, {IND_A_Actif=C}, {true=D}], FCI_2=[{false=E}, {true=F}, {true=H}, {IND_B_Inactif=G}]}");
  }
}
