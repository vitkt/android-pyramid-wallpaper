package ru.vitkt.pyramidwallpaper;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import ru.vitkt.pyramidwallpaper.FrameStorage.Frame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class WallpaperScene {

	private int width;
	private int height;
	Paint p = new Paint();
	FrameStorage _storage;

	public WallpaperScene(FrameStorage storage) {
		_storage = storage;
		p.setColor(Color.BLUE);
		p.setStrokeWidth(10.0f);
	}

	public void draw(Canvas canvas) {

	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setWorldWidth(float _worldWidth) {
		worldWidth = _worldWidth;
	}

	private float worldWidth;
	private float xoff = 0f;//40;
	private float yoff = 0f;//30;

	private void drawCubeFromBoxToViewWithLine(Canvas canvas, Vec2[] vertices,
			int vertexCount, int color) {
		p.setColor(color);
		int width = getWidth();
		int height = getHeight();
		float wk = width / worldWidth;

		// RectF rf = new RectF();
		//
		// rf.left = (vertices[0].x + xoff) * wk;
		// rf.top = height - (vertices[0].y + yoff) * wk;
		//
		// rf.right = (vertices[3].x + xoff) * wk;
		// rf.bottom = height - (vertices[3].y + yoff) * wk;

		// _storage.AddFigureRect(rf.left, rf.top, rf.right, rf.bottom, color);

		// canvas.drawRect(rf, p);
		for (int i = 0; i < vertexCount - 1; i++) {
			float sx, sy, ex, ey;

			sx = (vertices[i].x + xoff) * wk;
			sy = height - (vertices[i].y + yoff) * wk;

			ex = (vertices[i + 1].x + xoff) * wk;
			ey = height - (vertices[i + 1].y + yoff) * wk;

			canvas.drawLine(sx, sy, ex, ey, p);

		}

		float sx, sy, ex, ey;

		sx = (vertices[vertexCount - 1].x + xoff) * wk;
		sy = height - (vertices[vertexCount - 1].y + yoff) * wk;

		ex = (vertices[0].x + xoff) * wk;
		ey = height - (vertices[0].y + yoff) * wk;

		canvas.drawLine(sx, sy, ex, ey, p);
		// FrameStorage.AddFigureLine(sx, sy, ex, ey, color);
		// FrameStorage.EndAddFigure();

	}

	private void drawCubeFromBoxToView(Canvas canvas, Vec2[] vertices,
			int vertexCount, int color) {

		p.setColor(color);
		int width = getWidth();
		int height = getHeight();
		float wk = width / worldWidth;

		RectF rf = new RectF();

		rf.left = (vertices[0].x + xoff) * wk;
		rf.top = height - (vertices[0].y + yoff) * wk;

		rf.right = (vertices[3].x + xoff) * wk;
		rf.bottom = height - (vertices[3].y + yoff) * wk;

		_storage.AddFigureRect(rf.left, rf.top, rf.right, rf.bottom, color);

		canvas.drawRect(rf, p);
		for (int i = 0; i < vertexCount - 1; i++) {
			float sx, sy, ex, ey;

			sx = (vertices[i].x + xoff) * wk;
			sy = height - (vertices[i].y + yoff) * wk;

			ex = (vertices[i + 1].x + xoff) * wk;
			ey = height - (vertices[i + 1].y + yoff) * wk;

			// canvas.drawLine(sx, sy, ex, ey, p);

		}

		float sx, sy, ex, ey;

		sx = (vertices[vertexCount - 1].x + xoff) * wk;
		sy = height - (vertices[vertexCount - 1].y + yoff) * wk;

		ex = (vertices[0].x + xoff) * wk;
		ey = height - (vertices[0].y + yoff) * wk;

		// canvas.drawLine(sx, sy, ex, ey, p);
		// FrameStorage.AddFigureLine(sx, sy, ex, ey, color);
		// FrameStorage.EndAddFigure();

	}

	public void drawCube(Canvas canvas, BoxGuiWrapper _box, boolean linesMode) {
		Transform t = _box.getBody().getTransform();
		Fixture f = _box.getBody().getFixtureList();
		drawShape(canvas, t, f, _box.getColor(), linesMode);

	}

	public void drawTimeLapse(Canvas canvas) {

		Frame frame = _storage.GetFrame();
		for (float[] rect : frame.figures) {

			p.setColor((int) rect[4]);

			canvas.drawRect(rect[0], rect[1], rect[2], rect[3], p);

		}

	}

	private void drawShape(Canvas canvas, Transform t, Fixture f, int color,
			boolean linesMode) {
		PolygonShape poly = (PolygonShape) f.getShape();
		int vertexCount = poly.m_count;
		Vec2[] vertices = new Vec2[vertexCount];

		for (int i = 0; i < vertexCount; ++i) {
			vertices[i] = Transform.mul(t, poly.m_vertices[i]);

		}
		if (linesMode)
			drawCubeFromBoxToViewWithLine(canvas, vertices, vertexCount, color);
		else
			drawCubeFromBoxToView(canvas, vertices, vertexCount, color);
	}

	public Vec2 ConvertViewToWorld(float x, float y) {
		float wk = getWidth() / worldWidth;
		// sx = (vertices[i].x + xoff) * wk;
		// sy = height - (vertices[i].y + yoff) * wk;
		return new Vec2((x / wk) - xoff, ((y - getHeight()) / (-wk)) - yoff);
	}
}
