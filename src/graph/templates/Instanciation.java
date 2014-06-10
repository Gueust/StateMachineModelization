package graph.templates;

import java.util.HashMap;

public class Instanciation {

  private String instanciate;
  private HashMap<String, String> with = new HashMap<String, String>();

  public String getInstanciate() {
    return instanciate;
  }

  public void setInstanciate(String instanciate) {
    this.instanciate = instanciate;
  }

  public HashMap<String, String> getWith() {
    return with;
  }

  public void setWith(HashMap<String, String> with) {
    this.with = with;
  }
}
