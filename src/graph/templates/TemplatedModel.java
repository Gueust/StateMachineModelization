package graph.templates;

import java.util.LinkedList;

public class TemplatedModel {

  private String target;
  private LinkedList<Instanciation> content = new LinkedList<Instanciation>();
  private LinkedList<LinkedList<String>> inputs =
      new LinkedList<LinkedList<String>>();

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public LinkedList<Instanciation> getContent() {
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
