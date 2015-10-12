package jp.uphy.clipboardwatcher;

/**
 * @author Yuhi Ishikura
 */
@FunctionalInterface
public interface ClipboardListener {

  void clipboardChanged(Clipboard clipboard);

}
