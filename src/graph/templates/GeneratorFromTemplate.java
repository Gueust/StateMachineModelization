package graph.templates;

import graph.GraphFactoryAEFD;
import graph.Model;

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

  public static Model load(String file_name) throws IOException {
    File file = new File(file_name);
    return load(file);
  }

  public static Model load(File file) throws IOException {

    InputStream input = new FileInputStream(file);

    Constructor constructor = new Constructor(TemplatedModel.class);
    TypeDescription model_descr = new TypeDescription(TemplatedModel.class);
    model_descr.putListPropertyType("content", Instanciation.class);
    // model_descr.putListPropertyType("inputs", LinkedList.class);
    constructor.addTypeDescription(model_descr);

    Yaml yaml = new Yaml(constructor);

    TemplatedModel templated_model = (TemplatedModel) yaml.load(input);
    System.out.println(yaml.dump(templated_model));
    input.close();

    BufferedWriter out =
        new BufferedWriter(new FileWriter(templated_model.getTarget()));

    boolean first = true;
    /* We generate the CLT-IND graphs */
    for (LinkedList<String> list : templated_model.getInputs()) {
      if (list.size() != 2) {
        out.close();
        throw new Error("The data within 'inputs' must be pairs.");
      }

      String graph =
          GraphFactoryAEFD.generateAutomateForCTL(list.get(0), list.get(1));

      if (first) {
        first = false;
      } else {
        out.write("\r\n");
      }
      out.write(graph);
      out.flush();
    }

    /* We instanciate the templated graphs */
    for (Instanciation instanciation : templated_model.getContent()) {
      if (first) {
        first = false;
      } else {
        out.write("\r\n");
      }
      String file_name = instanciation.getInstanciate();

      byte[] encoded = Files.readAllBytes(Paths.get(file_name));
      String template = new String(encoded, StandardCharsets.UTF_8);

      for (Entry<String, String> entry : instanciation.getWith().entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        template = template.replaceAll("<" + key + ">", value);
      }

      if (template.indexOf('<') != -1) {
        out.close();
        System.err.println(template);
        throw new Error("The instanciation is not correct since there still "
            + "exists <variables>.");
      }
      out.write(template);

    }

    out.close();

    GraphFactoryAEFD factory = new GraphFactoryAEFD();
    Model result = factory.buildModel(templated_model.getTarget(),
        "Merge instanciated model");
    result.build();
    return result;

  }

  public static void main(String[] args) throws IOException {
    GeneratorFromTemplate.load("test.yaml");
  }
}