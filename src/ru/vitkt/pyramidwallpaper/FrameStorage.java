package ru.vitkt.pyramidwallpaper;

import java.util.ArrayList;
import java.util.Stack;



public class FrameStorage {
	static Stack<Frame> stack = new Stack<Frame>();
	static Frame currentFrame;

	static void BeginPushFrame() {
		currentFrame = new Frame();
	}

	static void BeginAddFigure() {
		currentFrame.BeginAddFigure();
	}

	static void EndAddFigure() {
		currentFrame.EndAddFigure();
	}

	static void AddFigureLine(float sx, float sy, float ex, float ey) {
		currentFrame.AddFigureLine(sx, sy, ex, ey);
	}

	static void EndPushFrame() {
		stack.push(currentFrame);
	}

	static void PopFrame() {
		if (stack.size()==0)
			return;
		stack.pop();
		if (stack.size()==0)
			return;
		currentFrame = stack.lastElement();
	}
	static boolean isBegin()
	{
		return stack.size()==0;
	}
	static Frame GetFrame() {
		return currentFrame;
	}

	public static class Frame {
		Frame() {
		}

		public void EndAddFigure() {
			figures.add(currentFigure);
		}

		public void BeginAddFigure() {
			currentFigure=new ArrayList<float[]>();
		}

		ArrayList<ArrayList<float[]>> figures = new ArrayList<ArrayList<float[]>>();
		ArrayList<float[]>currentFigure;
		public void AddFigureLine(float sx, float sy, float ex, float ey) {
			float[] arr = new float[4];
			arr[0] = sx;
			arr[1] = sy;
			arr[2] = ex;
			arr[3] = ey;
			currentFigure.add(arr);

		}
	}
}
