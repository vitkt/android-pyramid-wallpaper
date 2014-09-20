package ru.vitkt.pyramidwallpaper;

import java.util.ArrayList;
import java.util.Stack;

import android.util.Log;

public class FrameStorage {
	Stack<Frame> stack = new Stack<Frame>();
	Frame currentFrame = null;
	Frame previousFrame = null;
	private boolean enabled = true;

	void BeginPushFrame() {
		if (enabled)
			currentFrame = new Frame();
	}

	void AddFigureRect(float left, float top, float right, float bottom, int c) {
		if (enabled)
			currentFrame.AddFigureRect(left, top, right, bottom, c);
	}

	void EndPushFrame() {
		if (enabled) {
			
			stack.push(currentFrame);
			
//			if (previousFrame != null) {
//				if (!previousFrame.equals(currentFrame)) {
//					stack.push(currentFrame);
//					previousFrame = currentFrame;
//				}
//			}
//			else
//			{
//				stack.push(currentFrame);
//				previousFrame = currentFrame;
//			}
		}
	}

	void PopFrame() {
		if (!enabled)
			return;
		if (stack.size() == 0)
			return;
		stack.pop();
		if (stack.size() == 0)
			return;
		currentFrame = stack.lastElement();
	}

	boolean isBegin() {
		if (!enabled)
			return false;
		return stack.size() == 0;
	}

	Frame GetFrame() {
		if (!enabled)
			return null;
		return currentFrame;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public class Frame {
		Frame() {
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Frame) {
				
				Frame another = (Frame) o;
				if (another.figures.size() != figures.size())
					return false;
				for (int i = 0; i < figures.size(); i++) {
					//Log.d("pyramid","comp cycle");
					float[] figure1 = figures.get(i);
					float[] figure2 = another.figures.get(i);
					if (figure1.length != figure2.length)
						return false;
					for (int j = 0; j < figure1.length; j++) {
						//Log.d("pyramid","comp cycle2");
						if (figure1[j] != figure2[j])
							return false;
					}
				}
				//Log.d("pyramid","comp true");
				return true;
			} else
			{Log.d("pyramid","comp eq super");
				return super.equals(o);
			}
		}

		public void AddFigureRect(float left, float top, float right,
				float bottom, int c) {
			float[] rect = new float[5];
			rect[0] = left;
			rect[1] = top;
			rect[2] = right;
			rect[3] = bottom;
			rect[4] = c;
			figures.add(rect);
		}

		ArrayList<float[]> figures = new ArrayList<float[]>();
		float[] currentFigure;
	}
}
