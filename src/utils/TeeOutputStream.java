package utils;

/* This file has been modified */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 * Classic splitter of OutputStream. Named after the unix 'tee'
 * command. It allows a stream to be branched off so there
 * are now several streams.
 * 
 * @version $Id: TeeOutputStream.java 610010 2008-01-08 14:50:59Z niallp $
 */
public class TeeOutputStream extends FilterOutputStream {

  /** The others OutputStream to write to */
  protected LinkedList<OutputStream> branches = new LinkedList<OutputStream>();

  /**
   * Constructs a TeeOutputStream.
   * 
   * @param out
   *          the main OutputStream
   * @param branch
   *          the second OutputStream
   */
  public TeeOutputStream(OutputStream out, OutputStream branch) {
    super(out);
    this.branches.add(out);
    this.branches.add(branch);
  }

  /**
   * Add a new OutputStream to write to
   */
  public void add(OutputStream other_branch) {
    branches.add(other_branch);
  }

  /**
   * Write the bytes to both streams.
   * 
   * @param b
   *          the bytes to write
   * @throws IOException
   *           if an I/O error occurs
   */
  public synchronized void write(byte[] b) throws IOException {
    
    for (OutputStream branch : branches) {
      branch.write(b);
    }
  }

  /**
   * Write the specified bytes to both streams.
   * 
   * @param b
   *          the bytes to write
   * @param off
   *          The start offset
   * @param len
   *          The number of bytes to write
   * @throws IOException
   *           if an I/O error occurs
   */
  public synchronized void write(byte[] b, int off, int len) throws IOException {
    for (OutputStream branch : branches) {
      branch.write(b, off, len);
    }
  }

  /**
   * Write a byte to both streams.
   * 
   * @param b
   *          the byte to write
   * @throws IOException
   *           if an I/O error occurs
   */
  public synchronized void write(int b) throws IOException {
    for (OutputStream branch : branches) {
      branch.write(b);
    }
  }

  /**
   * Flushes both streams.
   * 
   * @throws IOException
   *           if an I/O error occurs
   */
  public void flush() throws IOException {
    for (OutputStream branch : branches) {
      branch.flush();
    }
  }

  /**
   * Closes both streams.
   * 
   * @throws IOException
   *           if an I/O error occurs
   */
  public void close() throws IOException {
    for (OutputStream branch : branches) {
      branch.close();
    }
  }

}