package com.weilay.pos2.util;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.weilay.pos2.R;

import java.util.List;

public class KeyboardUtil {
 public abstract static class OnFinishListener{
       public abstract void  onFinish();
    }
    private OnFinishListener onFinishListener;

    public OnFinishListener getOnFinishListener() {
        return onFinishListener;
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }
	private Context ctx;
	private Activity act;

	private Keyboard k1;// 字母键盘
	private Keyboard k2;// 数字键盘
	public boolean isnun = false;// 是否数据键盘
	public boolean isupper = false;// 是否大写
	private KeyboardView keyboardView;
	private EditText ed;

	public EditText getEdit() {
		return ed;
	}

	/**
	 * 
	 * @param act
	 * @param ctx
	 * @param edit
	 * @param keyboardView
	 * @param number
	 *            是否为纯数字键盘
	 */
	public KeyboardUtil(Activity act, Context ctx, EditText edit, KeyboardView keyboardView, boolean number) {
		if (keyboardView == null) {
			return;
		}
		this.act = act;
		this.ctx = ctx;
		this.ed = edit;
		this.keyboardView = keyboardView;
		if (number) {
			k2 = new Keyboard(ctx, R.xml.number);
			this.keyboardView.setKeyboard(k2);
		} else {
			k1 = new Keyboard(ctx, R.xml.qwerty);
			k2 = new Keyboard(ctx, R.xml.symbols1);
			this.keyboardView.setKeyboard(k1);

		}
		DeviceUtil.closeSystemInput(edit, act);

		this.keyboardView.setEnabled(true);
		this.keyboardView.setPreviewEnabled(false);// 取消点击时弹出文字提示
		this.keyboardView.setOnKeyboardActionListener(listener);
	}

	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			Editable editable = ed.getText();
			int start = ed.getSelectionStart();
			if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
				hideKeyboard();
				  if(onFinishListener!=null){
	                    onFinishListener.onFinish();
	                }
			} else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			} else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
				changeKey();
				keyboardView.setKeyboard(k1);

			} else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {// 数字键盘切换
				if (isnun) {
					isnun = false;
					keyboardView.setKeyboard(k1);
				} else {
					isnun = true;
					keyboardView.setKeyboard(k2);
				}
			} else if (primaryCode == 57419) { // go left
				if (start > 0) {
					ed.setSelection(start - 1);
				}
			} else if (primaryCode == 57421) { // go right
				if (start < ed.length()) {
					ed.setSelection(start + 1);
				}
			} else {
				editable.insert(start, Character.toString((char) primaryCode));
			}
		}
	};

	/**
	 * 键盘大小写切换
	 */
	private void changeKey() {
		List<Key> keylist = k1.getKeys();
		if (isupper) {// 大写切换小写
			isupper = false;
			for (Key key : keylist) {
				if (key.label != null && isword(key.label.toString())) {
					key.label = key.label.toString().toLowerCase();
					key.codes[0] = key.codes[0] + 32;
				}
			}
		} else {// 小写切换大写
			isupper = true;
			for (Key key : keylist) {
				if (key.label != null && isword(key.label.toString())) {
					key.label = key.label.toString().toUpperCase();
					key.codes[0] = key.codes[0] - 32;
				}
			}
		}
	}

	public void showKeyboard() {
		if (keyboardView == null) {
			return;
		}
		int visibility = keyboardView.getVisibility();
		if (visibility == View.GONE || visibility == View.INVISIBLE) {
			keyboardView.setVisibility(View.VISIBLE);
		}
	}

	public void hideKeyboard() {
		int visibility = keyboardView.getVisibility();
		if (visibility == View.VISIBLE) {
			keyboardView.setVisibility(View.INVISIBLE);
		}
	}

	private boolean isword(String str) {
		String wordstr = "abcdefghijklmnopqrstuvwxyz";
		if (wordstr.indexOf(str.toLowerCase()) > -1) {
			return true;
		}
		return false;
	}
}
