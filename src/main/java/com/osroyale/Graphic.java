package com.osroyale;

public final class Graphic {

	static void unpackConfig(StreamLoader streamLoader) {
		Buffer stream = new Buffer(streamLoader.getFile("spotanim.dat"));
		int length = stream.readUShort();
		if (cache == null)
			cache = new Graphic[length + 50000];
		for (int j = 0; j < length; j++) {
			if (cache[j] == null)
				cache[j] = new Graphic();
			cache[j].anInt404 = j;
			cache[j].readValues(stream);

			switch (j) {
			}
		}
	}

	private void readValues(Buffer stream) {
		while(true) {
			int i = stream.readUByte();
			if (i == 0) {
				return;
			}
			if (i == 1) {
				modelId = stream.readUShort();
			} else if (i == 2) {
				anInt406 = stream.readUShort();
				if (Animation.animations != null) {
					aAnimation_407 = Animation.animations[anInt406];
				}
			} else if (i == 4) {
				anInt410 = stream.readUShort();
			} else if (i == 5) {
				anInt411 = stream.readUShort();
			} else if (i == 6) {
				anInt412 = stream.readUShort();
			} else if (i == 7) {
				anInt413 = stream.readUByte();
			} else if (i == 8) {
				anInt414 = stream.readUByte();
			} else if (i == 40) {
				int j = stream.readUByte();
				for (int k = 0; k < j; k++) {
					anIntArray408[k] = stream.readUShort();
					anIntArray409[k] = stream.readUShort();
				}
			} else if (i == 41) {
				final int len = stream.readUByte();
				textureToFind = new short[len];
				textureToReplace = new short[len];
				for (int k = 0; k < len; k++) {
					textureToFind[k] = (short) stream.readUShort();
					textureToReplace[k] = (short) stream.readUShort();
				}
			} else {
				System.out.println("Error unrecognised spotanim config code: " + i);
			}
		}
	}

	public Model getModel() {
		Model model = (Model) aMRUNodes_415.get(anInt404);
		if (model != null)
			return model;
		model = Model.getModel(modelId);
		if (model == null)
			return null;
		for (int i = 0; i < 6; i++)
			if (anIntArray408[0] != 0)
				model.recolor(anIntArray408[i], anIntArray409[i]);

		aMRUNodes_415.put(model, anInt404);
		return model;
	}

	private Graphic() {
		anInt406 = -1;
		anIntArray408 = new int[6];
		anIntArray409 = new int[6];
		anInt410 = 128;
		anInt411 = 128;
	}

	public static Graphic cache[];
	private int anInt404;
	int modelId;
	private int anInt406;
	Animation aAnimation_407;
	private final int[] anIntArray408;
	private final int[] anIntArray409;
	private short[] textureToFind;
	private short[] textureToReplace;
	int anInt410;
	int anInt411;
	int anInt412;
	int anInt413;
	int anInt414;
	static Cache aMRUNodes_415 = new Cache(30);

}
