package com.mapgeneration.mapgen;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class MapGenMain extends ApplicationAdapter {
	ShapeRenderer sr;
	Float[][] noise;
	int width = 0;
	int height = 0;

	@Override
	public void create() {
		sr = new ShapeRenderer();
		Perlin perlin = new Perlin();

		width = (int) (Gdx.graphics.getWidth() * 1.2);
		height = (int) (Gdx.graphics.getHeight() * 1.2);
		// noise = perlin.getSmoothNoise(width, height, 8);
		noise = perlin.getPerlin(width, height, 7, 0.1f);
		noise = perlin.Equalize(noise, width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		sr.begin(ShapeType.Filled);
		Float max = 0.0f, min = 1.0f;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				float buffer = noise[x][y];
				max = max > buffer ? max : buffer;
				min = min < buffer ? min : buffer;
				Color color = new Color(buffer, buffer, buffer, 1.0f);
				 if (buffer < 0.3) {
				 color = new Color(1.0f, buffer*2.5f,1.0f, 0.5f);
				 } else if (buffer < 0.5 && buffer >=0.3) {
				 color = new Color(1.0f, buffer, 1.0f, 0.5f);
				 } else {
				 color = new Color(1.0f, 1.0f, buffer, 0.5f);
				 }

				sr.rect(x, y, 1.0f, 1.0f, color, color, color, color);
			}
		}
		sr.end();
		Gdx.app.log("min max", min + " " + max);
	}
}
