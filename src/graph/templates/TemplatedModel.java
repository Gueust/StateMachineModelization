package graph.templates;

import java.util.LinkedList;

public class TemplatedModel {

  private String target;
  private String file_FCI_list;
  private LinkedList<Instanciation> content = new LinkedList<Instanciation>();
  private LinkedList<LinkedList<String>> inputs =
      new LinkedList<LinkedList<String>>();

  public String getFile_FCI_list() {
    return file_FCI_list;
  }

  public void setFile_FCI_list(String file_FCI_list) {
    this.file_FCI_list = file_FCI_list;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public LinkedList<Instanciation> getContent() {
    if (content == null) {
      return new LinkedList<>();
    }
    return content;
  }

  public void setContent(LinkedList<Instanciation> content) {
    this.content = content;
  }

  public LinkedList<LinkedList<String>> getInputs() {
    return inputs;
  }

  public void setInputs(LinkedList<LinkedList<String>> inputs) {
    this.inputs = inputs;
  }
}
