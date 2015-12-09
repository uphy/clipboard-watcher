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

import java.util.ArrayList;
import java.util.List;


/**
 * @author Yuhi Ishikura
 */
public class ClipboardWatcher {

  private static final ClipboardWatcher sharedInstance = new ClipboardWatcher();

  public static ClipboardWatcher getSharedInstance() {
    return sharedInstance;
  }

  public static void setWatchInterval(long interval) {
    ClipboardWatcherManager.getInstance().setWatchInterval(interval);
  }

  private boolean started = false;
  private List<ClipboardListener> listeners = new ArrayList<>();

  private final ClipboardListener listener = c -> {
    if (isStarted() == false) {
      return;
    }
    synchronized (this.listeners) {
      listeners.forEach(l -> l.clipboardChanged(c));
    }
  };

  public void addListener(ClipboardListener listener) {
    synchronized (this.listeners) {
      this.listeners.add(listener);
    }
  }

  public void removeListener(ClipboardListener listener) {
    synchronized (this.listeners) {
      this.listeners.remove(listener);
    }
  }

  public synchronized void start() {
    if (isStarted()) {
      return;
    }
    ClipboardWatcherManager manager = ClipboardWatcherManager.getInstance();
    manager.addListener(this.listener);
    this.started = true;
  }

  public boolean isStarted() {
    return started;
  }

  public synchronized void stop() {
    if (isStarted() == false) {
      return;
    }
    ClipboardWatcherManager manager = ClipboardWatcherManager.getInstance();
    manager.removeListener(this.listener);
  }

}
