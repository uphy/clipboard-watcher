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

import java.util.concurrent.Semaphore;


/**
 * @author Yuhi Ishikura
 */
class JavaFXUtilities {

  static Clipboard getSystemClipboard() {
    final Clipboard[] systemClipboard = new Clipboard[1];
    if (Platform.isFxApplicationThread()) {
      systemClipboard[0] = Clipboard.getSystemClipboard();
    } else {
      if (isJavaFXThreadInitialized() == false) {
        startJavaFXThread();
      }
      final Semaphore s = new Semaphore(0);
      Platform.runLater(() -> {
        systemClipboard[0] = Clipboard.getSystemClipboard();
        s.release();
      });
      try {
        s.acquire();
      } catch (InterruptedException e) {
        throw new IllegalStateException("Can not acquire system clipboard.", e);
      }
    }
    return systemClipboard[0];
  }

  private static void startJavaFXThread() {
    new Thread(() -> {
      JavaFXDummyApp.main();
    }).start();
    while (isJavaFXThreadInitialized() == false) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static boolean isJavaFXThreadInitialized() {
    for (Thread thread : Thread.getAllStackTraces().keySet()) {
      if (thread.getName().equals("JavaFX Application Thread")) {
        return true;
      }
    }
    return false;
  }

}
