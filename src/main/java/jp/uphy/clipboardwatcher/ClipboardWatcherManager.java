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

import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


/**
 * @author Yuhi Ishikura
 */
class ClipboardWatcherManager {

  private final DataFormat alreadyReplaced = new DataFormat(ClipboardWatcherManager.class.getName() + "/alreadyproceeded/" + System.currentTimeMillis());
  private static ClipboardWatcherManager instance;
  private static final Logger logger = Logger.getLogger(ClipboardWatcherManager.class.getName());

  static synchronized ClipboardWatcherManager getInstance() {
    if (instance == null) {
      instance = new ClipboardWatcherManager(JavaFXUtilities.getSystemClipboard());
    }
    return instance;
  }

  private Clipboard clipboard;
  private List<ClipboardListener> listeners = new ArrayList<>();
  private WatchThread watchThread;
  private long watchInterval = TimeUnit.SECONDS.toMillis(1);

  private ClipboardWatcherManager(Clipboard clipboard) {
    this.clipboard = Objects.requireNonNull(clipboard);
  }

  void addListener(ClipboardListener listener) {
    synchronized (this.listeners) {
      this.listeners.add(listener);
      logger.fine("ClipboardListener added: " + listener);
      updateState();
    }
  }

  void removeListener(ClipboardListener listener) {
    synchronized (this.listeners) {
      this.listeners.remove(listener);
      logger.fine("ClipboardListener removed: " + listener);
      updateState();
    }
  }

  private void updateState() {
    if (this.listeners.isEmpty()) {
      stop();
    } else {
      start();
    }
  }

  void setWatchInterval(final long watchInterval) {
    this.watchInterval = watchInterval;
  }

  private boolean isRunning() {
    return this.watchThread != null;
  }

  private synchronized void start() {
    if (isRunning()) {
      stop();
    }
    this.watchThread = new WatchThread();
    this.watchThread.start();
    logger.fine("ClipboardWatcherManager started.");
  }

  private synchronized void stop() {
    if (isRunning() == false) {
      return;
    }
    this.watchThread.interrupt();
    this.watchThread = null;
    logger.fine("ClipboardWatcherManager stopped.");
  }

  class WatchThread extends Thread {

    @Override
    public void run() {
      final jp.uphy.clipboardwatcher.Clipboard clipboardWrapper = new jp.uphy.clipboardwatcher.Clipboard(clipboard);
      while (true) {
        Platform.runLater(() -> {
          final Boolean replaced = (Boolean)clipboard.getContent(alreadyReplaced);
          if (replaced == null || replaced.booleanValue() == false) {
            clipboardWrapper.set(alreadyReplaced, true);
            synchronized (listeners) {
              for (ClipboardListener replacer : listeners) {
                replacer.clipboardChanged(clipboardWrapper);
              }
            }
          }
        });
        try {
          Thread.sleep(watchInterval);
        } catch (InterruptedException e) {
          break;
        }
      }
    }
  }

}
