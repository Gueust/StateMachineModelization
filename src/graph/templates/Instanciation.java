package graph.templates;

import java.util.HashMap;

public class Instanciation {

  private String instanciate;
  private String named_as;

  public String getNamed_as() {
    return named_as;
  }

  public void setNamed_as(String named_as) {
    this.named_as = named_as;
  }

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
