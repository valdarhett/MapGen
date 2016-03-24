package com.mapgeneration.mapgen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;

public class Perlin {

	/**
	 * normal noise
	 * 
	 * @param width
	 * @param height
	 * @return float[][] containing noise info
	 */
	public Float[][] getWhiteNoise(int width, int height) {
		Float[][] noise = new Float[width][height];
		Random random = new Random(System.currentTimeMillis());
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				noise[x][y] = random.nextFloat();
			}
		}
		return noise;
	}

	public Float[][] getSmoothNoise(int width, int height, int octave) {
		int samplePeriod = (int) Math.pow(2, octave - 1);
		Float[][] baseNoise = getWhiteNoise(width, height);
		float sampleFrequency = 1.0f / samplePeriod;
		Gdx.app.log("frequency", sampleFrequency + "");
		Float[][] smoothNoise = new Float[width][height];
		for (int y = 0; y < height; y++) {
			int sampley0 = (y / samplePeriod) * samplePeriod;
			int sampley1 = (sampley0 + samplePeriod) % height;
			float verticalBlend = (y - sampley0) * sampleFrequency;
			for (int x = 0; x < width; x++) {
				int samplex0 = (x / samplePeriod) * samplePeriod;
				int samplex1 = (samplex0 + samplePeriod) % width;
				float horizontalBlend = (x - samplex0) * sampleFrequency;

				float top = interpolate(baseNoise[samplex0][sampley0], baseNoise[samplex0][sampley1], verticalBlend);
				float bottom = interpolate(baseNoise[samplex1][sampley0], baseNoise[samplex1][sampley1], verticalBlend);

				// smoothNoise[x][y] = verticalBlend;
				smoothNoise[x][y] = interpolate(top, bottom, horizontalBlend);
			}
		}
		return smoothNoise;
	}

	private float interpolate(float x0, float x1, float t) {
		return x0 * (1 - t) + t * x1;
	}

	public Float[][] getPerlin(int width, int height, int octaveCount, float persistance) {
		float amplitude = 1.0f;
		float totalAmplitude = 0.0f;
		Float[][][] noise = new Float[octaveCount][][];
		Float[][] perlin = new Float[width][height];

		for (Float[] row : perlin) {
			Arrays.fill(row, 0.0f);
		}

		for (int i = 1; i < octaveCount; i++) {
			noise[i] = getSmoothNoise(width, height, i);
		}

		for (int i = octaveCount - 1; i > 0; i--) {
			amplitude *= persistance;
			totalAmplitude += amplitude;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					perlin[x][y] += noise[i][x][y] * amplitude;
				}
			}
		}
		// normalization
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				perlin[x][y] /= totalAmplitude;
			}
		}

		return perlin;
	}

	/**
	 * normalizing function
	 * 
	 * @param source
	 * @param width
	 * @param height
	 * @return
	 */
	public Float[][] Equalize(Float[][] source, int width, int height) {
		HashMap<Float, Integer> pCount = new HashMap<Float, Integer>();
		Float[][] buffer = new Float[width][height];
		Float max = 0.0f, min = 1.0f;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (!pCount.containsKey(source[x][y])) {
					pCount.put(source[x][y], 1);
				} else {
					pCount.put(source[x][y], pCount.get(source[x][y]) + 1);
				}
				min = min < source[x][y] ? min : source[x][y];
				max = max > source[x][y] ? max : source[x][y];
			}
		}

		Float dynamic = max - min;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				buffer[x][y] = ((source[x][y] - min) / dynamic);
			}
		}

		return buffer;
	}
}
