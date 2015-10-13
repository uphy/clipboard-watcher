/**
 * Copyright (C) 2015 uphy.jp
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.uphy.clipboardwatcher;

import javafx.scene.image.Image;
import javafx.scene.input.DataFormat;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author Yuhi Ishikura
 */
public class Clipboard {

  private final javafx.scene.input.Clipboard clipboard;

  Clipboard(javafx.scene.input.Clipboard clipboard) {
    this.clipboard = clipboard;
  }

  public Set<DataFormat> getContentTypes() {
    return this.clipboard.getContentTypes();
  }

  public Object get(DataFormat dataFormat) {
    return this.clipboard.getContent(dataFormat);
  }

  public void set(DataFormat dataFormat, Object value) {
    final Map<DataFormat, Object> map = getContents();
    map.put(dataFormat, fixValue(value));
    this.clipboard.setContent(map);
  }

  private Map<DataFormat, Object> getContents() {
    Map<DataFormat, Object> map = new HashMap<>();
    for (DataFormat df : getContentTypes()) {
      Object v = clipboard.getContent(df);
      if (v == null) {
        continue;
      }
      map.put(df, fixValue(v));
    }
    return map;
  }

  private static Object fixValue(Object value) {
    if (value instanceof String) {
      // JavaFXのバグか、クリップボードに改行コードがCRLFの文字列を値として設定すると、改行が２つになる。
      return ((String)value).replaceAll("\r\n", "\n");
    }
    return value;
  }

  public void remove(DataFormat dataFormat) {
    final Map<DataFormat, Object> map = getContents();
    map.remove(dataFormat);
    this.clipboard.setContent(map);
  }

  public void clear() {
    clipboard.clear();
  }

  public boolean has(final DataFormat dataFormat) {
    return clipboard.hasContent(dataFormat);
  }

  public List<Path> getPaths() {
    final List<File> files = getFiles();
    final List<Path> paths = new ArrayList<>(files.size());
    for (File file : files) {
      paths.add(file.toPath());
    }
    return paths;
  }

  public boolean hasPaths() {
    return hasFiles();
  }

  public void setPaths(List<Path> paths) {
    final List<File> files = new ArrayList<>(paths.size());
    for (Path path : paths) {
      files.add(path.toFile());
    }
    setFiles(files);
  }

  public List<File> getFiles() {
    return clipboard.getFiles();
  }

  public boolean hasFiles() {
    return clipboard.hasFiles();
  }

  public void setFiles(List<File> files) {
    set(DataFormat.FILES, files);
  }

  public boolean hasString() {
    return clipboard.hasString();
  }

  public String getString() {
    return clipboard.getString();
  }

  public void setString(String s) {
    set(DataFormat.PLAIN_TEXT, s);
  }

  public boolean hasUrl() {
    return clipboard.hasUrl();
  }

  public String getUrl() {
    return clipboard.getUrl();
  }

  public void setUrl(String url) {
    set(DataFormat.URL, url);
  }

  public boolean hasHtml() {
    return clipboard.hasHtml();
  }

  public String getHtml() {
    return clipboard.getHtml();
  }

  public void setHtml(String html) {
    set(DataFormat.HTML, html);
  }

  public boolean hasRtf() {
    return clipboard.hasRtf();
  }

  public String getRtf() {
    return clipboard.getRtf();
  }

  public void setRtf(String rtf) {
    set(DataFormat.RTF, rtf);
  }

  public boolean hasImage() {
    return clipboard.hasImage();
  }

  public Image getImage() {
    return clipboard.getImage();
  }

  public void setImage(Image image) {
    set(DataFormat.IMAGE, image);
  }


}
