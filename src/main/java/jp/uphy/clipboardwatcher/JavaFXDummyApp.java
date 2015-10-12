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

import javafx.application.Application;
import javafx.stage.Stage;


/**
 * JavaFX Event Threadが起動していない場合に、起動するために利用する、ダミーアプリケーションです。
 *
 * @author Yuhi Ishikura
 */
public class JavaFXDummyApp extends Application {

  @Override
  public void start(final Stage primaryStage) throws Exception {
    // do nothing
  }

  public static void main() {
    final String caller = Thread.currentThread().getStackTrace()[1].getClassName();
    if (caller.startsWith("jp.uphy.clipboardwatcher") == false) {
      throw new IllegalStateException("Unexpected invocation.  Do not use this class from the out of this package.");
    }
    System.out.println(caller);
    launch();
  }

}
