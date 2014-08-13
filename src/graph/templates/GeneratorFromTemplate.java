package graph.templates;

import graph.GraphFactoryAEFD;
import graph.Model;
import graph.conditions.aefdParser.AEFDFormulaFactory;
import graph.conditions.aefdParser.GenerateFormulaAEFD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class GeneratorFromTemplate {

  public static String load(String file_name) throws IOException {
    return load(file_name, true);
  }

  public static String load(String file_name, boolean verify_instanciation)
      throws IOException {
    File file = new File(file_name);
    return load(file, verify_instanciation);
  }

  /**
   * 
   * @param file
   * @return The name of the file where the model has be generated.
   * @throws IOException
   */
  public static String load(File file, boolean verify_instanciation)
      throws IOException {

    String default_path = file.getParent();
    if (default_path == null) {
      default_path = "";
    } else {
      default_path += "/";
    }

    InputStream input_stream = new FileInputStream(file);

    Constructor constructor = new Constructor(TemplatedModel.class);
    TypeDescription model_descr = new TypeDescription(TemplatedModel.class);
    model_descr.putListPropertyType("content", Instanciation.class);
    // model_descr.putListPropertyType("inputs", LinkedList.class);
    constructor.addTypeDescription(model_descr);

    Yaml yaml = new Yaml(constructor);

    TemplatedModel templated_model = (TemplatedModel) yaml.load(input_stream);
    assert (templated_model != null);

    System.out.println(yaml.dump(templated_model));
    input_stream.close();

    String output_path = default_path + templated_model.getTarget();
    BufferedWriter out =
        new BufferedWriter(new FileWriter(output_path));

    boolean first = true;
    /* We generate the CLT-IND graphs */
    LinkedList<LinkedList<String>> inputs = templated_model.getInputs();
    if (inputs != null) {
      for (LinkedList<String> list : inputs) {
        if (list.size() != 2) {
          out.close();
          throw new Error("The data within 'inputs' must be pairs.");
        }

        GraphFactoryAEFD factory = new GraphFactoryAEFD(null);

        String graph =
            factory.generateAutomateForCTL(list.get(0), list.get(1));

        if (first) {
          first = false;
        } else {
          out.write("\r\n");
        }
        out.write(graph);
        out.flush();
      }
    }

    /* We instanciate the templated graphs */
    for (Instanciation instanciation : templated_model.getContent()) {
      if (first) {
        first = false;
      } else {
        out.write("\r\n");
      }
      String file_name = instanciation.getInstanciate();

      byte[] encoded = Files.readAllBytes(Paths.get(default_path + file_name));
      String template = new String(encoded, StandardCharsets.UTF_8);

      for (Entry<String, String> entry : instanciation.getWith().entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        template = template.replaceAll("<" + key + ">", value);
      }

      /* We verify that the result is not a template */
      if (verify_instanciation && template.indexOf('<') != -1) {
        out.close();
        System.err.println(template);
        throw new Error("The instanciation is not correct since there still "
            + "exists <variables>.");
      }
      out.write(template);

    }

    out.close();

    if (verify_instanciation) {
      GraphFactoryAEFD factory = new GraphFactoryAEFD(null);
      Model result = factory.buildModel(output_path,
          "Merge instanciated model");
      result.build();
    }
    return output_path;

  }

  public static String load(File file) throws IOException {
    return load(file, true);
  }

  public static void main(String[] args) throws IOException {
    GeneratorFromTemplate.load("fonctionnel1voie.yaml");
    GeneratorFromTemplate.load("PN/PN_JB_1_voie.yaml");
    GeneratorFromTemplate.load("PN/PN_P6_Template_JB.yaml", false);
    GeneratorFromTemplate.load("PN/PN_JB_1_voie_preuve.yaml");
  }
}
