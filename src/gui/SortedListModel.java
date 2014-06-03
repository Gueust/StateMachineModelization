package gui;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

@SuppressWarnings("serial")
public class SortedListModel extends AbstractListModel<String> {

  SortedSet<String> model = new TreeSet<String>();
  String[] array = null;

  @Override
  public int getSize() {
    return model.size();
  }

  @Override
  public String getElementAt(int index) {
    if (array == null) {
      array = new String[model.size()];
      array = model.toArray(array);
    }
    return array[index];
  }

  /**
   * MUST be called when modifying the model.
   */
  private void setModified() {
    array = null;
  }

  public void addElement(String element) {
    if (model.add(element)) {
      setModified();
      fireContentsChanged(this, 0, getSize());
    }
  }

  public void removeAllElements() {
    model.clear();
    setModified();
    fireContentsChanged(this, 0, getSize());
  }

  public boolean contains(Object element) {
    return model.contains(element);
  }

  public Iterator<String> iterator() {
    return model.iterator();
  }

  public boolean removeElement(Object element) {
    boolean removed = model.remove(element);
    if (removed) {
      setModified();
      fireContentsChanged(this, 0, getSize());
    }
    return removed;
  }
}
