/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iotdb.tsfile.read.common;

import java.io.Serializable;
import org.apache.iotdb.tsfile.common.constant.TsFileConstant;

/**
 * This class define an Object named Path to represent a series in IoTDB.
 * AndExpression in batch read, this definition is also used in query
 * processing. Note that, Path is unmodified after a new object has been
 * created.
 */
public class Path implements Serializable, Comparable<Path> {

  private static final long serialVersionUID = 3405277066329298200L;
  private String measurement;
  private String alias = null;
  private String device;
  private String fullPath;
  private static final String illegalPathArgument = "Path parameter is null";

  /**
   * this constructor doesn't split the path, only useful for table header.
   * @param pathSc a path that wouldn't  be split.
   */
  @Deprecated
  public Path(String pathSc) {
    if (pathSc == null) {
      throw new IllegalArgumentException(illegalPathArgument);
    }
    fullPath = pathSc;
    device = "";
    measurement = pathSc;
  }

  /**
   * @param pathSc path
   * @param needSplit whether need to be split to device and measurement, doesn't support escape character yet.
   */
  public Path(String pathSc, boolean needSplit) {
    if(!needSplit) {
      fullPath = pathSc;
      device = "";
      measurement = pathSc;
    } else {
      if (pathSc.length() > 0) {
        if (pathSc.charAt(pathSc.length() - 1) == TsFileConstant.DOUBLE_QUOTE) {
          int endIndex = pathSc.lastIndexOf('"', pathSc.length() - 2);
          if (endIndex != -1 && (endIndex == 0 || pathSc.charAt(endIndex - 1) == '.')) {
            fullPath = pathSc;
            device = pathSc.substring(0, endIndex - 1);
            measurement = pathSc.substring(endIndex);
          } else {
            throw new IllegalArgumentException(illegalPathArgument);
          }
        } else if (pathSc.charAt(pathSc.length() - 1) != TsFileConstant.DOUBLE_QUOTE
            && pathSc.charAt(pathSc.length() - 1) != TsFileConstant.PATH_SEPARATOR_CHAR) {
          int endIndex = pathSc.lastIndexOf(TsFileConstant.PATH_SEPARATOR_CHAR);
          if (endIndex < 0) {
            fullPath = pathSc;
            device = "";
            measurement = pathSc;
          } else {
            fullPath = pathSc;
            device = pathSc.substring(0, endIndex);
            measurement = pathSc.substring(endIndex + 1);
          }
        } else {
          throw new IllegalArgumentException(illegalPathArgument);
        }
      } else {
        fullPath = pathSc;
        device = "";
        measurement = pathSc;
      }
    }
  }

  /**
   * construct a Path directly using device and measurement, no need to reformat
   * the path
   *
   * @param device      root.deviceType.d1
   * @param measurement s1 , does not contain TsFileConstant.PATH_SEPARATOR
   */
  public Path(String device, String measurement) {
    if (device == null || measurement == null) {
      throw new IllegalArgumentException(illegalPathArgument);
    }
    this.device = device;
    this.measurement = measurement;
    if(!device.equals("")) {
      this.fullPath = device + TsFileConstant.PATH_SEPARATOR + measurement;
    } else {
      fullPath = measurement;
    }
  }

  public String getFullPath() {
    return fullPath;
  }

  public String getDevice() {
    return device;
  }

  public String getMeasurement() {
    return measurement;
  }

  public String getAlias() { return alias; }

  public void setAlias(String alias) { this.alias = alias; }

  public String getFullPathWithAlias() { return device + TsFileConstant.PATH_SEPARATOR + alias; }

  @Override
  public int hashCode() {
    return fullPath.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Path && this.fullPath.equals(((Path) obj).fullPath);
  }

  public boolean equals(String obj) {
    return this.fullPath.equals(obj);
  }

  @Override
  public int compareTo(Path path) {
    return fullPath.compareTo(path.getFullPath());
  }

  @Override
  public String toString() {
    return fullPath;
  }

  @Override
  public Path clone() {
    return new Path(fullPath);
  }

}
