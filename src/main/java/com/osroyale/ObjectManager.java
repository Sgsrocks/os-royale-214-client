package com.osroyale;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.

// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

import java.util.ArrayList;

final class ObjectManager {

	public ObjectManager(byte[][][] flags, int[][][] heights) {
		min_plane = 99;
		region_size_x = 104;
		region_size_y = 104;
		vertex_heights = heights;
		tile_flags = flags;
		underlay_floor_id = new short[4][region_size_x][region_size_y];
		overlay_floor_id = new short[4][region_size_x][region_size_y];
		overlay_clipping_paths = new byte[4][region_size_x][region_size_y];
		overlay_clipping_path_rotations = new byte[4][region_size_x][region_size_y];
		tile_culling_bitsets = new int[4][region_size_x + 1][region_size_y + 1];
		tile_shadow_intensity = new byte[4][region_size_x + 1][region_size_y + 1];
		tile_light_intensity = new int[region_size_x + 1][region_size_y + 1];
		blended_hue = new int[region_size_y];
		blended_saturation = new int[region_size_y];
		blended_lightness = new int[region_size_y];
		blended_hue_factor = new int[region_size_y];
		blend_direction_tracker = new int[region_size_y];
	}

	private static int method170(int i, int j) {
		int k = i + j * 57;
		k = k << 13 ^ k;
		int l = k * (k * k * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
		return l >> 19 & 0xff;
	}

	public final void method171(CollisionMap[] collisions, SceneGraph region) {
		for (int height = 0; height < 4; height++) {
			for (int mapX = 0; mapX < 104; mapX++) {
				for (int mapY = 0; mapY < 104; mapY++) {

					if ((tile_flags[height][mapX][mapY] & 1) == 1) {
						int plane = height;

						if ((tile_flags[1][mapX][mapY] & 2) == 2) {
							plane--;
						}

						if (plane >= 0) {
							collisions[plane].block(mapX, mapY);
						}
					}

				}
			}
		}

		for (int plane = 0; plane < 4; plane++) {
			byte abyte0[][] = tile_shadow_intensity[plane];
			byte byte0 = 96;
			char c = '\u0300';
			byte byte1 = -50;
			byte byte2 = -10;
			byte byte3 = -50;
			int j3 = (int) Math.sqrt(byte1 * byte1 + byte2 * byte2 + byte3 * byte3);
			int l3 = c * j3 >> 8;
			for (int j4 = 1; j4 < region_size_y - 1; j4++) {
				for (int j5 = 1; j5 < region_size_x - 1; j5++) {
					int k6 = vertex_heights[plane][j5 + 1][j4] - vertex_heights[plane][j5 - 1][j4];
					int l7 = vertex_heights[plane][j5][j4 + 1] - vertex_heights[plane][j5][j4 - 1];
					int j9 = (int) Math.sqrt(k6 * k6 + 0x10000 + l7 * l7);
					int k12 = (k6 << 8) / j9;
					int l13 = 0x10000 / j9;
					int j15 = (l7 << 8) / j9;
					int j16 = byte0 + (byte1 * k12 + byte2 * l13 + byte3 * j15) / l3;
					int j17 = (abyte0[j5 - 1][j4] >> 2) + (abyte0[j5 + 1][j4] >> 3) + (abyte0[j5][j4 - 1] >> 2) + (abyte0[j5][j4 + 1] >> 3) + (abyte0[j5][j4] >> 1);
					tile_light_intensity[j5][j4] = j16 - j17;
				}

			}

			for (int k5 = 0; k5 < region_size_y; k5++) {
				blended_hue[k5] = 0;
				blended_saturation[k5] = 0;
				blended_lightness[k5] = 0;
				blended_hue_factor[k5] = 0;
				blend_direction_tracker[k5] = 0;
			}

			for (int x = -5; x < region_size_x; x++) {
				for (int y = 0; y < region_size_y; y++) {
					int x_positive_offset = x + 5;
					if (x_positive_offset >= 0 && x_positive_offset < region_size_x) {
						int l12 = underlay_floor_id[plane][x_positive_offset][y] & 0x3FFF;
						if (l12 > 0) {
							FloorUnderlayDefinition flo = FloorUnderlayDefinition.underlays[l12 - 1];
							blended_hue[y] += flo.blendHue;
							blended_saturation[y] += flo.saturation;
							blended_lightness[y] += flo.luminance;
							blended_hue_factor[y] += flo.blendHueMultiplier;
							blend_direction_tracker[y]++;
						}
					}
					int i13 = x - 5;
					if (i13 >= 0 && i13 < region_size_x) {
						int i14 = underlay_floor_id[plane][i13][y] & 0x3FFF;
						if (i14 > 0) {
							FloorUnderlayDefinition flo_1 = FloorUnderlayDefinition.underlays[i14 - 1];
							blended_hue[y] -= flo_1.blendHue;
							blended_saturation[y] -= flo_1.saturation;
							blended_lightness[y] -= flo_1.luminance;
							blended_hue_factor[y] -= flo_1.blendHueMultiplier;
							blend_direction_tracker[y]--;
						}
					}
				}

				if (x >= 1 && x < region_size_x - 1) {
					int l9 = 0;
					int j13 = 0;
					int j14 = 0;
					int k15 = 0;
					int k16 = 0;
					for (int k17 = -5; k17 < region_size_y + 5; k17++) {
						int j18 = k17 + 5;
						if (j18 >= 0 && j18 < region_size_y) {
							l9 += blended_hue[j18];
							j13 += blended_saturation[j18];
							j14 += blended_lightness[j18];
							k15 += blended_hue_factor[j18];
							k16 += blend_direction_tracker[j18];
						}
						int k18 = k17 - 5;
						if (k18 >= 0 && k18 < region_size_y) {
							l9 -= blended_hue[k18];
							j13 -= blended_saturation[k18];
							j14 -= blended_lightness[k18];
							k15 -= blended_hue_factor[k18];
							k16 -= blend_direction_tracker[k18];
						}
						if (k17 >= 1 && k17 < region_size_y - 1 && (!lowMem || (tile_flags[0][x][k17] & 2) != 0 || (tile_flags[plane][x][k17] & 0x10) == 0 && method182(k17, plane, x) == anInt131)) {
							if (plane < min_plane)
								min_plane = plane;
							int l18 = underlay_floor_id[plane][x][k17] & 0x3FFF;
							int i19 = overlay_floor_id[plane][x][k17] & 0x3FFF;
							if (l18 > 0 || i19 > 0) {
								int j19 = vertex_heights[plane][x][k17];
								int k19 = vertex_heights[plane][x + 1][k17];
								int l19 = vertex_heights[plane][x + 1][k17 + 1];
								int i20 = vertex_heights[plane][x][k17 + 1];
								int j20 = tile_light_intensity[x][k17];
								int k20 = tile_light_intensity[x + 1][k17];
								int l20 = tile_light_intensity[x + 1][k17 + 1];
								int i21 = tile_light_intensity[x][k17 + 1];
								int j21 = -1;
								int k21 = -1;
								if (l18 > 0) {
									int l21 = (l9 * 256) / k15;
									int j22 = j13 / k16;
									int l22 = j14 / k16;
									j21 = method177(l21, j22, l22);
									k21 = method177(l21, j22, l22);
								}
								if (plane > 0) {
									boolean flag = true;
									if (l18 == 0 && overlay_clipping_paths[plane][x][k17] != 0)
										flag = false;
									if (i19 > 0 && !FloorOverlayDefinition.overlays[i19 - 1].occlude)
										flag = false;
									if (flag && j19 == k19 && j19 == l19 && j19 == i20)
										tile_culling_bitsets[plane][x][k17] |= 0x924;
								}
								int i22 = 0;
								if (j21 != -1)
									i22 = Rasterizer.anIntArray1482[method187(k21, 96)];
								if (i19 == 0) {
									region.method279(plane, x, k17, 0, 0, -1, j19, k19, l19, i20, method187(j21, j20), method187(j21, k20), method187(j21, l20), method187(j21, i21), 0, 0, 0, 0, i22, 0);
								} else {
									int k22 = overlay_clipping_paths[plane][x][k17] + 1;
									byte byte4 = overlay_clipping_path_rotations[plane][x][k17];
									if (i19  - 1 > FloorOverlayDefinition.overlays.length) {
										i19  = FloorOverlayDefinition.overlays.length;
									}
									FloorOverlayDefinition overlay = FloorOverlayDefinition.overlays[i19 - 1];
									int textureId = overlay.texture;
									int j23;
									int minimapColor;
									if (textureId == 51) {
										textureId = 3;
									}
									if (textureId >= 0) {
										minimapColor = Rasterizer.method369(textureId);
										j23 = -1;
									} else if (overlay.rgb == 0xff00ff) {
										minimapColor = 0;
										j23 = -2;
										textureId = -1;
									} else if (overlay.rgb == 0x333333) {
										minimapColor = Rasterizer.anIntArray1482[method185(overlay.hsl16, 96)];
										j23 = -2;
										textureId = -1;
									} else {
										j23 = method177(overlay.hue, overlay.saturation, overlay.luminance);
										minimapColor = Rasterizer.anIntArray1482[method185(overlay.hsl16, 96)];
									}
									fogColorList.add(minimapColor);
									if (minimapColor == 0x000000 && overlay.anotherRgb != -1) {
										int newMinimapColor = method177(overlay.hue, overlay.saturation, overlay.luminance);
										minimapColor = Rasterizer.anIntArray1482[method185(newMinimapColor, 96)];
									}
									region.method279(plane, x, k17, k22, byte4, textureId, j19, k19, l19, i20, method187(j21, j20), method187(j21, k20), method187(j21, l20), method187(j21, i21), method185(j23, j20), method185(j23, k20), method185(j23, l20), method185(j23, i21), i22, minimapColor);
								}
							}
						}
					}
				}
			}

			for (int y = 1; y < region_size_y - 1; y++) {
				for (int x = 1; x < region_size_x - 1; x++) {
					region.method278(plane, x, y, method182(y, plane, x));
				}
			}

		}

		region.method305(-10, -50, -50);
		for (int j1 = 0; j1 < region_size_x; j1++) {
			for (int l1 = 0; l1 < region_size_y; l1++)
				if ((tile_flags[1][j1][l1] & 2) == 2)
					region.method276(l1, j1);

		}

		int i2 = 1;
		int j2 = 2;
		int k2 = 4;
		for (int l2 = 0; l2 < 4; l2++) {
			if (l2 > 0) {
				i2 <<= 3;
				j2 <<= 3;
				k2 <<= 3;
			}
			for (int i3 = 0; i3 <= l2; i3++) {
				for (int k3 = 0; k3 <= region_size_y; k3++) {
					for (int i4 = 0; i4 <= region_size_x; i4++) {
						if ((tile_culling_bitsets[i3][i4][k3] & i2) != 0) {
							int k4 = k3;
							int l5 = k3;
							int i7 = i3;
							int k8 = i3;
							for (; k4 > 0 && (tile_culling_bitsets[i3][i4][k4 - 1] & i2) != 0; k4--)
								;
							for (; l5 < region_size_y && (tile_culling_bitsets[i3][i4][l5 + 1] & i2) != 0; l5++)
								;
							label0:
							for (; i7 > 0; i7--) {
								for (int j10 = k4; j10 <= l5; j10++)
									if ((tile_culling_bitsets[i7 - 1][i4][j10] & i2) == 0)
										break label0;

							}

							label1:
							for (; k8 < l2; k8++) {
								for (int k10 = k4; k10 <= l5; k10++)
									if ((tile_culling_bitsets[k8 + 1][i4][k10] & i2) == 0)
										break label1;

							}

							int l10 = ((k8 + 1) - i7) * ((l5 - k4) + 1);
							if (l10 >= 8) {
								char c1 = '\360';
								int k14 = vertex_heights[k8][i4][k4] - c1;
								int l15 = vertex_heights[i7][i4][k4];
								SceneGraph.method277(l2, i4 * 128, l15, i4 * 128, l5 * 128 + 128, k14, k4 * 128, 1);
								for (int l16 = i7; l16 <= k8; l16++) {
									for (int l17 = k4; l17 <= l5; l17++)
										tile_culling_bitsets[l16][i4][l17] &= ~i2;

								}

							}
						}
						if ((tile_culling_bitsets[i3][i4][k3] & j2) != 0) {
							int l4 = i4;
							int i6 = i4;
							int j7 = i3;
							int l8 = i3;
							for (; l4 > 0 && (tile_culling_bitsets[i3][l4 - 1][k3] & j2) != 0; l4--)
								;
							for (; i6 < region_size_x && (tile_culling_bitsets[i3][i6 + 1][k3] & j2) != 0; i6++)
								;
							label2:
							for (; j7 > 0; j7--) {
								for (int i11 = l4; i11 <= i6; i11++)
									if ((tile_culling_bitsets[j7 - 1][i11][k3] & j2) == 0)
										break label2;

							}

							label3:
							for (; l8 < l2; l8++) {
								for (int j11 = l4; j11 <= i6; j11++)
									if ((tile_culling_bitsets[l8 + 1][j11][k3] & j2) == 0)
										break label3;

							}

							int k11 = ((l8 + 1) - j7) * ((i6 - l4) + 1);
							if (k11 >= 8) {
								char c2 = '\360';
								int l14 = vertex_heights[l8][l4][k3] - c2;
								int i16 = vertex_heights[j7][l4][k3];
								SceneGraph.method277(l2, l4 * 128, i16, i6 * 128 + 128, k3 * 128, l14, k3 * 128, 2);
								for (int i17 = j7; i17 <= l8; i17++) {
									for (int i18 = l4; i18 <= i6; i18++)
										tile_culling_bitsets[i17][i18][k3] &= ~j2;

								}

							}
						}
						if ((tile_culling_bitsets[i3][i4][k3] & k2) != 0) {
							int i5 = i4;
							int j6 = i4;
							int k7 = k3;
							int i9 = k3;
							for (; k7 > 0 && (tile_culling_bitsets[i3][i4][k7 - 1] & k2) != 0; k7--)
								;
							for (; i9 < region_size_y && (tile_culling_bitsets[i3][i4][i9 + 1] & k2) != 0; i9++)
								;
							label4:
							for (; i5 > 0; i5--) {
								for (int l11 = k7; l11 <= i9; l11++)
									if ((tile_culling_bitsets[i3][i5 - 1][l11] & k2) == 0)
										break label4;

							}

							label5:
							for (; j6 < region_size_x; j6++) {
								for (int i12 = k7; i12 <= i9; i12++)
									if ((tile_culling_bitsets[i3][j6 + 1][i12] & k2) == 0)
										break label5;

							}

							if (((j6 - i5) + 1) * ((i9 - k7) + 1) >= 4) {
								int j12 = vertex_heights[i3][i5][k7];
								SceneGraph.method277(l2, i5 * 128, j12, j6 * 128 + 128, i9 * 128 + 128, j12, k7 * 128, 4);
								for (int k13 = i5; k13 <= j6; k13++) {
									for (int i15 = k7; i15 <= i9; i15++)
										tile_culling_bitsets[i3][k13][i15] &= ~k2;

								}
							}
						}
					}
				}
			}
		}
	}

	private static int calc_heights(int i, int j) {
		int k = (method176(i + 45365, j + 0x16713, 4) - 128) + (method176(i + 10294, j + 37821, 2) - 128 >> 1) + (method176(i, j, 1) - 128 >> 2);
		k = (int) ((double) k * 0.29999999999999999D) + 35;
		if (k < 10)
			k = 10;
		else if (k > 60)
			k = 60;
		return k;
	}

	public static void method173(Buffer stream, ResourceProvider class42_sub1) {
		label0:
		{
			int i = -1;
			do {
				int j = stream.readUSmart2();
				if (j == 0)
					break label0;
				i += j;
				ObjectDefinition class46 = ObjectDefinition.forID(i);
				class46.method574(class42_sub1);
				do {
					int k = stream.method422();
					if (k == 0)
						break;
					stream.readUByte();
				} while (true);
			} while (true);
		}
	}

	public final void method174(int i, int j, int l, int i1) {
		for (int j1 = i; j1 <= i + j; j1++) {
			for (int k1 = i1; k1 <= i1 + l; k1++)
				if (k1 >= 0 && k1 < region_size_x && j1 >= 0 && j1 < region_size_y) {
					tile_shadow_intensity[0][k1][j1] = 127;
					if (k1 == i1 && k1 > 0)
						vertex_heights[0][k1][j1] = vertex_heights[0][k1 - 1][j1];
					if (k1 == i1 + l && k1 < region_size_x - 1)
						vertex_heights[0][k1][j1] = vertex_heights[0][k1 + 1][j1];
					if (j1 == i && j1 > 0)
						vertex_heights[0][k1][j1] = vertex_heights[0][k1][j1 - 1];
					if (j1 == i + j && j1 < region_size_y - 1)
						vertex_heights[0][k1][j1] = vertex_heights[0][k1][j1 + 1];
				}

		}
	}

	private void method175(int y, SceneGraph region, CollisionMap collisionMap, int type, int z, int x, int id, int orientation) {
		if (lowMem && (tile_flags[0][x][y] & 2) == 0) {
			if ((tile_flags[z][x][y] & 0x10) != 0) {
				return;
			}

			if (method182(y, z, x) != anInt131) {
				return;
			}
		}

		if (z < min_plane) {
			min_plane = z;
		}

		ObjectDefinition objectDefinition = ObjectDefinition.forID(id);

		int sizeY;
		int sizeX;
		if (orientation != 1 && orientation != 3) {
			sizeX = objectDefinition.width;
			sizeY = objectDefinition.length;
		} else {
			sizeX = objectDefinition.length;
			sizeY = objectDefinition.width;
		}

		int editX;
		int editX2;
		if (x + sizeX <= 104) {
			editX = x + (sizeX >> 1);
			editX2 = x + (1 + sizeX >> 1);
		} else {
			editX = x;
			editX2 = 1 + x;
		}

		int editY;
		int editY2;
		if (sizeY + y <= 104) {
			editY = (sizeY >> 1) + y;
			editY2 = y + (1 + sizeY >> 1);
		} else {
			editY = y;
			editY2 = 1 + y;
		}

		int k1 = vertex_heights[z][editX][editY];
		int l1 = vertex_heights[z][editX2][editY];
		int i2 = vertex_heights[z][editX2][editY2];
		int j2 = vertex_heights[z][editX][editY2];
		int k2 = k1 + l1 + i2 + j2 >> 2;

		long key = (long) (orientation << 20 | type << 14 | (y << 7 | x) + 0x40000000);

		if (!objectDefinition.interactive) {
			key |= ~0x7fffffffffffffffL;
		}
		if(objectDefinition.anInt760 == 1) {
			key |= 0x400000L;
		}
		key |= (long) id << 32;
		byte byte0 = (byte) ((orientation << 6) + type);
		if (type == 22) {
			if (!Settings.GROUND_DECORATIONS && !objectDefinition.interactive && !objectDefinition.obstructsGround) {
				return;
			}

			Renderable obj;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null) {
				obj = objectDefinition.method578(22, orientation, k1, l1, i2, j2, -1);
			} else {
				obj = new SceneObject(id, orientation, 22, l1, i2, k1, j2, objectDefinition.anInt781, true);
			}

			region.method280(z, k2, y, obj, byte0, key, x);

			if (objectDefinition.solid && objectDefinition.interactive && collisionMap != null) {
				collisionMap.block(x, y);
			}
			return;
		}

		if (type == 10 || type == 11) {
			Object obj1;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj1 = objectDefinition.method578(10, orientation, k1, l1, i2, j2, -1);
			else
				obj1 = new SceneObject(id, orientation, 10, l1, i2, k1, j2, objectDefinition.anInt781, true);
			if (obj1 != null) {
				int i5 = 0;
				if (type == 11)
					i5 += 256;
				int width;
				int length;
				if (orientation == 1 || orientation == 3) {
					width = objectDefinition.length;
					length = objectDefinition.width;
				} else {
					width = objectDefinition.width;
					length = objectDefinition.length;
				}
				if (region.method284(key, byte0, k2, length, ((Renderable) (obj1)), width, z, i5, y, x) && objectDefinition.aBoolean779) {
					Model model;
					if (obj1 instanceof Model)
						model = (Model) obj1;
					else
						model = objectDefinition.method578(10, orientation, k1, l1, i2, j2, -1);
					if (model != null) {
						for (int xx = 0; xx <= width; xx++) {
							for (int yy = 0; yy <= length; yy++) {
								int l5 = model.XYZMag / 4;
								if (l5 > 30)
									l5 = 30;
								if (l5 > tile_shadow_intensity[z][x + xx][y + yy])
									tile_shadow_intensity[z][x + xx][y + yy] = (byte) l5;
							}

						}

					}
				}
			}
			if (objectDefinition.solid && collisionMap != null)
				collisionMap.clipObject(x, y, objectDefinition.width, objectDefinition.length, orientation, objectDefinition.impenetrable);
			return;
		}
		if (type >= 12) {
			Object obj2;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj2 = objectDefinition.method578(type, orientation, k1, l1, i2, j2, -1);
			else
				obj2 = new SceneObject(id, orientation, type, l1, i2, k1, j2, objectDefinition.anInt781, true);
			region.method284(key, byte0, k2, 1, ((Renderable) (obj2)), 1, z, 0, y, x);
			if (type >= 12 && type <= 17 && type != 13 && z > 0)
				tile_culling_bitsets[z][x][y] |= 0x924;
			if (objectDefinition.solid && collisionMap != null)
				collisionMap.clipObject(x, y, objectDefinition.width, objectDefinition.length, orientation, objectDefinition.impenetrable);
			return;
		}
		if (type == 0) {
			Object obj3;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj3 = objectDefinition.method578(0, orientation, k1, l1, i2, j2, -1);
			else
				obj3 = new SceneObject(id, orientation, 0, l1, i2, k1, j2, objectDefinition.anInt781, true);
			region.method282(anIntArray152[orientation], ((Renderable) (obj3)), key, y, byte0, x, null, k2, 0, z);
			if (orientation == 0) {
				if (objectDefinition.aBoolean779) {
					tile_shadow_intensity[z][x][y] = 50;
					tile_shadow_intensity[z][x][y + 1] = 50;
				}
				if (objectDefinition.aBoolean764)
					tile_culling_bitsets[z][x][y] |= 0x249;
			} else if (orientation == 1) {
				if (objectDefinition.aBoolean779) {
					tile_shadow_intensity[z][x][y + 1] = 50;
					tile_shadow_intensity[z][x + 1][y + 1] = 50;
				}
				if (objectDefinition.aBoolean764)
					tile_culling_bitsets[z][x][y + 1] |= 0x492;
			} else if (orientation == 2) {
				if (objectDefinition.aBoolean779) {
					tile_shadow_intensity[z][x + 1][y] = 50;
					tile_shadow_intensity[z][x + 1][y + 1] = 50;
				}
				if (objectDefinition.aBoolean764)
					tile_culling_bitsets[z][x + 1][y] |= 0x249;
			} else if (orientation == 3) {
				if (objectDefinition.aBoolean779) {
					tile_shadow_intensity[z][x][y] = 50;
					tile_shadow_intensity[z][x + 1][y] = 50;
				}
				if (objectDefinition.aBoolean764)
					tile_culling_bitsets[z][x][y] |= 0x492;
			}
			if (objectDefinition.solid && collisionMap != null)
				collisionMap.clip(x, y, type, orientation, objectDefinition.impenetrable);
			if (objectDefinition.anInt775 != 16)
				region.method290(y, objectDefinition.anInt775, x, z);
			return;
		}
		if (type == 1) {
			Object obj4;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj4 = objectDefinition.method578(1, orientation, k1, l1, i2, j2, -1);
			else
				obj4 = new SceneObject(id, orientation, 1, l1, i2, k1, j2, objectDefinition.anInt781, true);
			region.method282(anIntArray140[orientation], ((Renderable) (obj4)), key, y, byte0, x, null, k2, 0, z);
			if (objectDefinition.aBoolean779)
				if (orientation == 0)
					tile_shadow_intensity[z][x][y + 1] = 50;
				else if (orientation == 1)
					tile_shadow_intensity[z][x + 1][y + 1] = 50;
				else if (orientation == 2)
					tile_shadow_intensity[z][x + 1][y] = 50;
				else if (orientation == 3)
					tile_shadow_intensity[z][x][y] = 50;
			if (objectDefinition.solid && collisionMap != null)
				collisionMap.clip(x, y, type, orientation, objectDefinition.impenetrable);
			return;
		}
		if (type == 2) {
			int i3 = orientation + 1 & 3;
			Object obj11;
			Object obj12;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null) {
				obj11 = objectDefinition.method578(2, 4 + orientation, k1, l1, i2, j2, -1);
				obj12 = objectDefinition.method578(2, i3, k1, l1, i2, j2, -1);
			} else {
				obj11 = new SceneObject(id, 4 + orientation, 2, l1, i2, k1, j2, objectDefinition.anInt781, true);
				obj12 = new SceneObject(id, i3, 2, l1, i2, k1, j2, objectDefinition.anInt781, true);
			}
			region.method282(anIntArray152[orientation], ((Renderable) (obj11)), key, y, byte0, x, ((Renderable) (obj12)), k2, anIntArray152[i3], z);
			if (objectDefinition.aBoolean764)
				if (orientation == 0) {
					tile_culling_bitsets[z][x][y] |= 0x249;
					tile_culling_bitsets[z][x][y + 1] |= 0x492;
				} else if (orientation == 1) {
					tile_culling_bitsets[z][x][y + 1] |= 0x492;
					tile_culling_bitsets[z][x + 1][y] |= 0x249;
				} else if (orientation == 2) {
					tile_culling_bitsets[z][x + 1][y] |= 0x249;
					tile_culling_bitsets[z][x][y] |= 0x492;
				} else if (orientation == 3) {
					tile_culling_bitsets[z][x][y] |= 0x492;
					tile_culling_bitsets[z][x][y] |= 0x249;
				}
			if (objectDefinition.solid && collisionMap != null)
				collisionMap.clip(x, y, type, orientation, objectDefinition.impenetrable);
			if (objectDefinition.anInt775 != 16)
				region.method290(y, objectDefinition.anInt775, x, z);
			return;
		}
		if (type == 3) {
			Object obj5;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj5 = objectDefinition.method578(3, orientation, k1, l1, i2, j2, -1);
			else
				obj5 = new SceneObject(id, orientation, 3, l1, i2, k1, j2, objectDefinition.anInt781, true);
			region.method282(anIntArray140[orientation], ((Renderable) (obj5)), key, y, byte0, x, null, k2, 0, z);
			if (objectDefinition.aBoolean779)
				if (orientation == 0)
					tile_shadow_intensity[z][x][y + 1] = 50;
				else if (orientation == 1)
					tile_shadow_intensity[z][x + 1][y + 1] = 50;
				else if (orientation == 2)
					tile_shadow_intensity[z][x + 1][y] = 50;
				else if (orientation == 3)
					tile_shadow_intensity[z][x][y] = 50;
			if (objectDefinition.solid && collisionMap != null)
				collisionMap.clip(x, y, type, orientation, objectDefinition.impenetrable);
			return;
		}
		if (type == 9) {
			Object obj6;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj6 = objectDefinition.method578(type, orientation, k1, l1, i2, j2, -1);
			else
				obj6 = new SceneObject(id, orientation, type, l1, i2, k1, j2, objectDefinition.anInt781, true);
			region.method284(key, byte0, k2, 1, ((Renderable) (obj6)), 1, z, 0, y, x);
			if (objectDefinition.solid && collisionMap != null)
				collisionMap.clipObject(x, y, objectDefinition.width, objectDefinition.length, orientation, objectDefinition.impenetrable);
			return;
		}
		if (objectDefinition.aBoolean762)
			if (orientation == 1) {
				int j3 = j2;
				j2 = i2;
				i2 = l1;
				l1 = k1;
				k1 = j3;
			} else if (orientation == 2) {
				int k3 = j2;
				j2 = l1;
				l1 = k3;
				k3 = i2;
				i2 = k1;
				k1 = k3;
			} else if (orientation == 3) {
				int l3 = j2;
				j2 = k1;
				k1 = l1;
				l1 = i2;
				i2 = l3;
			}
		if (type == 4) {
			Object obj7;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj7 = objectDefinition.method578(4, 0, k1, l1, i2, j2, -1);
			else
				obj7 = new SceneObject(id, 0, 4, l1, i2, k1, j2, objectDefinition.anInt781, true);
			region.method283(key, y, orientation * 512, z, 0, k2, ((Renderable) (obj7)), x, byte0, 0, anIntArray152[orientation]);
			return;
		}
		if (type == 5) {
			int i4 = 16;
			long k4 = region.method300(z, x, y);
			if (k4 > 0)
				i4 = ObjectDefinition.forID(ObjectKey.getObjectId(k4)).anInt775;
			Object obj13;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj13 = objectDefinition.method578(4, 0, k1, l1, i2, j2, -1);
			else
				obj13 = new SceneObject(id, 0, 4, l1, i2, k1, j2, objectDefinition.anInt781, true);
			region.method283(key, y, orientation * 512, z, anIntArray137[orientation] * i4, k2, ((Renderable) (obj13)), x, byte0, anIntArray144[orientation] * i4, anIntArray152[orientation]);
			return;
		}
		if (type == 6) {
			Object obj8;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj8 = objectDefinition.method578(4, 0, k1, l1, i2, j2, -1);
			else
				obj8 = new SceneObject(id, 0, 4, l1, i2, k1, j2, objectDefinition.anInt781, true);
			region.method283(key, y, orientation, z, 0, k2, ((Renderable) (obj8)), x, byte0, 0, 256);
			return;
		}
		if (type == 7) {
			Object obj9;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj9 = objectDefinition.method578(4, 0, k1, l1, i2, j2, -1);
			else
				obj9 = new SceneObject(id, 0, 4, l1, i2, k1, j2, objectDefinition.anInt781, true);
			region.method283(key, y, orientation, z, 0, k2, ((Renderable) (obj9)), x, byte0, 0, 512);
			return;
		}
		if (type == 8) {
			Object obj10;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj10 = objectDefinition.method578(4, 0, k1, l1, i2, j2, -1);
			else
				obj10 = new SceneObject(id, 0, 4, l1, i2, k1, j2, objectDefinition.anInt781, true);
			region.method283(key, y, orientation, z, 0, k2, ((Renderable) (obj10)), x, byte0, 0, 768);
		}
	}

	private static int method176(int i, int j, int k) {
		int l = i / k;
		int i1 = i & k - 1;
		int j1 = j / k;
		int k1 = j & k - 1;
		int l1 = method186(l, j1);
		int i2 = method186(l + 1, j1);
		int j2 = method186(l, j1 + 1);
		int k2 = method186(l + 1, j1 + 1);
		int l2 = method184(l1, i2, i1, k);
		int i3 = method184(j2, k2, i1, k);
		return method184(l2, i3, k1, k);
	}

	private int method177(int i, int j, int k) {
		if (k > 179)
			j /= 2;
		if (k > 192)
			j /= 2;
		if (k > 217)
			j /= 2;
		if (k > 243)
			j /= 2;
		return (i / 4 << 10) + (j / 32 << 7) + k / 2;
	}

	public static boolean method178(int i, int j) {
		ObjectDefinition class46 = ObjectDefinition.forID(i);
		if (j == 11)
			j = 10;
		if (j >= 5 && j <= 8)
			j = 4;
		return class46.method577(j);
	}

	public final void method179(int i, int height, CollisionMap collisionMap[], int x, int i1, byte data[], int j1, int collisionMapIndex, int y) {
		for (int localX = 0; localX < 8; localX++) {
			for (int localY = 0; localY < 8; localY++) {

				if (x + localX > 0 && x + localX < 103 && y + localY > 0 && y + localY < 103) {
					collisionMap[collisionMapIndex].adjacencies[x + localX][y + localY] &= 0xfeffffff;
				}

			}
		}

		Buffer stream = new Buffer(data);

		for (int plane = 0; plane < 4; plane++) {
			for (int yy = 0; yy < 64; yy++) {
				for (int xx = 0; xx < 64; xx++) {

					if (plane == i && yy >= i1 && yy < i1 + 8 && xx >= j1 && xx < j1 + 8) {
						load_terrain_tile(y + ChunkUtil.method156(xx & 7, height, yy & 7), 0, stream, x + ChunkUtil.method155(height, xx & 7, yy & 7), collisionMapIndex, height, 0);
					} else {
						load_terrain_tile(-1, 0, stream, -1, 0, 0, 0);
					}

				}
			}
		}

	}

	public final void method180(byte data[], int regionY, int regionX, int regionChunkX, int regionChunkY, CollisionMap aclass11[]) {
		for (int i1 = 0; i1 < 4; i1++) {
			for (int x = 0; x < 64; x++) {
				for (int y = 0; y < 64; y++)
					if (regionX + x > 0 && regionX + x < 103 && regionY + y > 0 && regionY + y < 103)
						aclass11[i1].adjacencies[regionX + x][regionY + y] &= 0xfeffffff;

			}

		}

		Buffer stream = new Buffer(data);
		for (int plane = 0; plane < 4; plane++) {
			for (int x = 0; x < 64; x++) {
				for (int y = 0; y < 64; y++)
					load_terrain_tile(y + regionY, regionChunkY, stream, x + regionX, plane, 0, regionChunkX);

			}

		}
	}

	private void load_terrain_tile(int y, int y_offset, Buffer buffer, int x, int plane, int orientation, int x_offset) {
		try {
			if (x >= 0 && x < 104 && y >= 0 && y < 104) {
				int absX = (x_offset + x);
				int absY = (y_offset + y);
				int absZ = (orientation + plane);
				tile_flags[plane][x][y] = 0;
				do {
					int index = buffer.readUShort();
					if (index == 0)
						if (plane == 0) {
							vertex_heights[0][x][y] = -calc_heights(0xe3b7b + x + x_offset, 0x87cce + y + y_offset) * 8;
							return;
						} else {
							vertex_heights[plane][x][y] = vertex_heights[plane - 1][x][y] - 240;
							return;
						}
					if (index == 1) {
						int height = buffer.readUByte();
						if (height == 1)
							height = 0;
						if (plane == 0) {
							vertex_heights[0][x][y] = -height * 8;
							return;
						} else {
							vertex_heights[plane][x][y] = vertex_heights[plane - 1][x][y] - height * 8;
							return;
						}
					}
					if (index <= 49) {
						overlay_floor_id[plane][x][y] = (short) buffer.readSignedShort();
						overlay_clipping_paths[plane][x][y] = (byte) ((index - 2) / 4);
						overlay_clipping_path_rotations[plane][x][y] = (byte) ((index - 2) + orientation & 3);
					} else if (index <= 81)
						tile_flags[plane][x][y] = (byte) (index - 49);
					else
						underlay_floor_id[plane][x][y] = (short) (index - 81);
				} while (true);
			}
			do {
				int index = buffer.readUShort();
				if (index == 0)
					break;

				if (index == 1) {
					buffer.readUByte();
					return;
				}
				if (index <= 49)
					buffer.readSignedShort();

			} while (true);
		} catch (Exception e) {
		}
	}
	private int method182(int y, int plane, int x) {
		if ((tile_flags[plane][x][y] & 8) != 0) {
			return 0;
		}

		if (plane > 0 && (tile_flags[1][x][y] & 2) != 0) {
			return plane - 1;
		}

		return plane;
	}

	public final void method183(CollisionMap[] matrix, SceneGraph region, int i, int j, int k, int plane, byte abyte0[], int i1, int j1, int k1) {
		label0:
		{
			Buffer stream = new Buffer(abyte0);
			int x = -1;
			do {
				int i2 = stream.readUSmart2();
				if (i2 == 0)
					break label0;
				x += i2;
				int packed = 0;
				do {
					int k2 = stream.method422();
					if (k2 == 0)
						break;
					packed += k2 - 1;
					int localY = packed & 0x3f;
					int localX = packed >> 6 & 0x3f;
					int _plane = packed >> 12;
					int more = stream.readUByte();
					int type = more >> 2;
					int rotation = more & 3;
					if (_plane == i && localX >= i1 && localX < i1 + 8 && localY >= k && localY < k + 8) {
						ObjectDefinition class46 = ObjectDefinition.forID(x);
						int id = j + ChunkUtil.method157(j1, class46.length, localX & 7, localY & 7, class46.width);
						int y = k1 + ChunkUtil.method158(localY & 7, class46.length, j1, class46.width, localX & 7);
						if (id > 0 && y > 0 && id < 103 && y < 103) {
							int height = _plane;
							if ((tile_flags[1][id][y] & 2) == 2) {
								height--;
							}
							CollisionMap collisions = null;
							if (height >= 0) {
								collisions = matrix[height];
							}
							method175(y, region, collisions, type, plane, id, x, rotation + j1 & 3);
						}
					}
				} while (true);
			} while (true);
		}
	}

	private static int method184(int i, int j, int k, int l) {
		int i1 = 0x10000 - Rasterizer.anIntArray1471[(k * 1024) / l] >> 1;
		return (i * (0x10000 - i1) >> 16) + (j * i1 >> 16);
	}

	private int method185(int i, int j) {
		if (i == -2)
			return 0xbc614e;
		if (i == -1) {
			if (j < 0)
				j = 0;
			else if (j > 127)
				j = 127;
			j = 127 - j;
			return j;
		}
		j = (j * (i & 0x7f)) / 128;
		if (j < 2)
			j = 2;
		else if (j > 126)
			j = 126;
		return (i & 0xff80) + j;
	}

	private static int method186(int i, int j) {
		int k = method170(i - 1, j - 1) + method170(i + 1, j - 1) + method170(i - 1, j + 1) + method170(i + 1, j + 1);
		int l = method170(i - 1, j) + method170(i + 1, j) + method170(i, j - 1) + method170(i, j + 1);
		int i1 = method170(i, j);
		return k / 16 + l / 8 + i1 / 4;
	}

	private static int method187(int i, int j) {
		if (i == -1)
			return 0xbc614e;
		j = (j * (i & 0x7f)) / 128;
		if (j < 2)
			j = 2;
		else if (j > 126)
			j = 126;
		return (i & 0xff80) + j;
	}

	public static void method188(SceneGraph worldController, int orientation, int y, int type, int plane, CollisionMap matrix, int ai[][][], int x, int id, int k1) {
		int l1 = ai[plane][x][y];
		int i2 = ai[plane][x + 1][y];
		int j2 = ai[plane][x + 1][y + 1];
		int k2 = ai[plane][x][y + 1];
		int l2 = l1 + i2 + j2 + k2 >> 2;
		ObjectDefinition objectDefinition = ObjectDefinition.forID(id);
		long key = (long) (orientation << 20 | type << 14 | (y << 7 | x) + 0x40000000);
		if (!objectDefinition.interactive) {
			key |= ~0x7fffffffffffffffL;
		}
		if(objectDefinition.anInt760 == 1) {
			key |= 0x400000L;
		}
		key |= (long) id << 32;
		byte byte1 = (byte) ((orientation << 6) + type);
		if (type == 22) {
			Object obj;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj = objectDefinition.method578(22, orientation, l1, i2, j2, k2, -1);
			else
				obj = new SceneObject(id, orientation, 22, i2, j2, l1, k2, objectDefinition.anInt781, true);
			worldController.method280(k1, l2, y, ((Renderable) (obj)), byte1, key, x);
			if (objectDefinition.solid && objectDefinition.interactive)
				matrix.block(x, y);
			return;
		}
		if (type == 10 || type == 11) {
			Object obj1;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj1 = objectDefinition.method578(10, orientation, l1, i2, j2, k2, -1);
			else
				obj1 = new SceneObject(id, orientation, 10, i2, j2, l1, k2, objectDefinition.anInt781, true);
			if (obj1 != null) {
				int j5 = 0;
				if (type == 11)
					j5 += 256;
				int k4;
				int i5;
				if (orientation == 1 || orientation == 3) {
					k4 = objectDefinition.length;
					i5 = objectDefinition.width;
				} else {
					k4 = objectDefinition.width;
					i5 = objectDefinition.length;
				}
				worldController.method284(key, byte1, l2, i5, ((Renderable) (obj1)), k4, k1, j5, y, x);
			}
			if (objectDefinition.solid)
				matrix.clipObject(x, y, objectDefinition.width, objectDefinition.length, orientation, objectDefinition.impenetrable);
			return;
		}
		if (type >= 12) {
			Object obj2;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj2 = objectDefinition.method578(type, orientation, l1, i2, j2, k2, -1);
			else
				obj2 = new SceneObject(id, orientation, type, i2, j2, l1, k2, objectDefinition.anInt781, true);
			worldController.method284(key, byte1, l2, 1, ((Renderable) (obj2)), 1, k1, 0, y, x);
			if (objectDefinition.solid)
				matrix.clipObject(x, y, objectDefinition.width, objectDefinition.length, orientation, objectDefinition.impenetrable);
			return;
		}
		if (type == 0) {
			Object obj3;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj3 = objectDefinition.method578(0, orientation, l1, i2, j2, k2, -1);
			else
				obj3 = new SceneObject(id, orientation, 0, i2, j2, l1, k2, objectDefinition.anInt781, true);
			worldController.method282(anIntArray152[orientation], ((Renderable) (obj3)), key, y, byte1, x, null, l2, 0, k1);
			if (objectDefinition.solid)
				matrix.clip(x, y, type, orientation, objectDefinition.impenetrable);
			return;
		}
		if (type == 1) {
			Object obj4;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj4 = objectDefinition.method578(1, orientation, l1, i2, j2, k2, -1);
			else
				obj4 = new SceneObject(id, orientation, 1, i2, j2, l1, k2, objectDefinition.anInt781, true);
			worldController.method282(anIntArray140[orientation], ((Renderable) (obj4)), key, y, byte1, x, null, l2, 0, k1);
			if (objectDefinition.solid)
				matrix.clip(x, y, type, orientation, objectDefinition.impenetrable);
			return;
		}
		if (type == 2) {
			int j3 = orientation + 1 & 3;
			Object obj11;
			Object obj12;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null) {
				obj11 = objectDefinition.method578(2, 4 + orientation, l1, i2, j2, k2, -1);
				obj12 = objectDefinition.method578(2, j3, l1, i2, j2, k2, -1);
			} else {
				obj11 = new SceneObject(id, 4 + orientation, 2, i2, j2, l1, k2, objectDefinition.anInt781, true);
				obj12 = new SceneObject(id, j3, 2, i2, j2, l1, k2, objectDefinition.anInt781, true);
			}
			worldController.method282(anIntArray152[orientation], ((Renderable) (obj11)), key, y, byte1, x, ((Renderable) (obj12)), l2, anIntArray152[j3], k1);
			if (objectDefinition.solid)
				matrix.clip(x, y, type, orientation, objectDefinition.impenetrable);
			return;
		}
		if (type == 3) {
			Object obj5;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj5 = objectDefinition.method578(3, orientation, l1, i2, j2, k2, -1);
			else
				obj5 = new SceneObject(id, orientation, 3, i2, j2, l1, k2, objectDefinition.anInt781, true);
			worldController.method282(anIntArray140[orientation], ((Renderable) (obj5)), key, y, byte1, x, null, l2, 0, k1);
			if (objectDefinition.solid)
				matrix.clip(x, y, type, orientation, objectDefinition.impenetrable);
			return;
		}
		if (type == 9) {
			Object obj6;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj6 = objectDefinition.method578(type, orientation, l1, i2, j2, k2, -1);
			else
				obj6 = new SceneObject(id, orientation, type, i2, j2, l1, k2, objectDefinition.anInt781, true);
			worldController.method284(key, byte1, l2, 1, ((Renderable) (obj6)), 1, k1, 0, y, x);
			if (objectDefinition.solid)
				matrix.clipObject(x, y, objectDefinition.width, objectDefinition.length, orientation, objectDefinition.impenetrable);
			return;
		}
		if (objectDefinition.aBoolean762)
			if (orientation == 1) {
				int k3 = k2;
				k2 = j2;
				j2 = i2;
				i2 = l1;
				l1 = k3;
			} else if (orientation == 2) {
				int l3 = k2;
				k2 = i2;
				i2 = l3;
				l3 = j2;
				j2 = l1;
				l1 = l3;
			} else if (orientation == 3) {
				int i4 = k2;
				k2 = l1;
				l1 = i2;
				i2 = j2;
				j2 = i4;
			}
		if (type == 4) {
			Object obj7;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj7 = objectDefinition.method578(4, 0, l1, i2, j2, k2, -1);
			else
				obj7 = new SceneObject(id, 0, 4, i2, j2, l1, k2, objectDefinition.anInt781, true);
			worldController.method283(key, y, orientation * 512, k1, 0, l2, ((Renderable) (obj7)), x, byte1, 0, anIntArray152[orientation]);
			return;
		}
		if (type == 5) {
			int j4 = 16;
			long l4 = worldController.method300(k1, x, y);
			if (l4 > 0)
				j4 = ObjectDefinition.forID(ObjectKey.getObjectId(l4)).anInt775;
			Object obj13;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj13 = objectDefinition.method578(4, 0, l1, i2, j2, k2, -1);
			else
				obj13 = new SceneObject(id, 0, 4, i2, j2, l1, k2, objectDefinition.anInt781, true);
			worldController.method283(key, y, orientation * 512, k1, anIntArray137[orientation] * j4, l2, ((Renderable) (obj13)), x, byte1, anIntArray144[orientation] * j4, anIntArray152[orientation]);
			return;
		}
		if (type == 6) {
			Object obj8;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj8 = objectDefinition.method578(4, 0, l1, i2, j2, k2, -1);
			else
				obj8 = new SceneObject(id, 0, 4, i2, j2, l1, k2, objectDefinition.anInt781, true);
			worldController.method283(key, y, orientation, k1, 0, l2, ((Renderable) (obj8)), x, byte1, 0, 256);
			return;
		}
		if (type == 7) {
			Object obj9;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj9 = objectDefinition.method578(4, 0, l1, i2, j2, k2, -1);
			else
				obj9 = new SceneObject(id, 0, 4, i2, j2, l1, k2, objectDefinition.anInt781, true);
			worldController.method283(key, y, orientation, k1, 0, l2, ((Renderable) (obj9)), x, byte1, 0, 512);
			return;
		}
		if (type == 8) {
			Object obj10;
			if (objectDefinition.anInt781 == -1 && objectDefinition.childrenIDs == null)
				obj10 = objectDefinition.method578(4, 0, l1, i2, j2, k2, -1);
			else
				obj10 = new SceneObject(id, 0, 4, i2, j2, l1, k2, objectDefinition.anInt781, true);
			worldController.method283(key, y, orientation, k1, 0, l2, ((Renderable) (obj10)), x, byte1, 0, 768);
		}
	}

	public static boolean method189(int x, byte[] is, int y) {
		boolean bool = true;
		Buffer stream = new Buffer(is);
		int objId = -1;
		for (; ; ) {
			int objIdOffset = stream.method1606();
			if (objIdOffset == 0)
				break;
			objId += objIdOffset;
			int objPosInfo = 0;
			boolean bool_255_ = false;
			for (; ; ) {
				if (bool_255_) {
					int objPosInfoOffset = stream.readUShortSmart();
					if (objPosInfoOffset == 0) {
						break;
					}
					stream.readUByte();
				} else {
					int objPosInfoOffset = stream.readUShortSmart();
					if (objPosInfoOffset == 0) {
						break;
					}
					objPosInfo += objPosInfoOffset - 1;
					int localY = objPosInfo & 0x3f;
					int localX = objPosInfo >> 6 & 0x3f;
					int plane = stream.readUByte() >> 2;
					int mapX = localX + x;
					int mapY = localY + y;

					if (mapX > 0 && mapY > 0 && mapX < 103 && mapY < 103) {
						ObjectDefinition definition = ObjectDefinition.forID(objId);

						if (plane != 22 || Settings.GROUND_DECORATIONS || definition.interactive || definition.obstructsGround) {//!lowMem
							bool &= definition.method579();
							bool_255_ = true;
						}
					}
				}
			}
		}
		return bool;
	}

	public final void method190(int x, CollisionMap[] collisions, int y, SceneGraph worldController, byte[] data) {
		main_loop:
		{
			Buffer stream = new Buffer(data);
			int objId = -1;
			do {
				int objIdOffset = stream.method1606();
				if (objIdOffset == 0) {
					break main_loop;
				}
				objId += objIdOffset;
				int objPosInfo = 0;
				do {
					int objPosInfoOffset = stream.readUShortSmart();
					if (objPosInfoOffset == 0) {
						break;
					}
					objPosInfo += objPosInfoOffset - 1;
					int localY = objPosInfo & 0x3f;
					int localX = objPosInfo >> 6 & 0x3f;
					int height = objPosInfo >> 12;
					int objOtherInfo = stream.readUByte();
					int type = objOtherInfo >> 2;
					int face = objOtherInfo & 3;
					int mapX = localX + x;
					int mapY = localY + y;
					if (mapX > 0 && mapY > 0 && mapX < 103 && mapY < 103 && height >= 0 && height < 4) {
						int plane = height;

						if ((tile_flags[1][mapX][mapY] & 2) == 2) {
							plane--;
						}

						CollisionMap collision = null;

						if (plane >= 0) {
							collision = collisions[plane];
						}

						method175(mapY, worldController, collision, type, height, mapX, objId, face);
					}
				} while (true);
			} while (true);
		}
	}

	public ArrayList<Integer> fogColorList = new ArrayList<Integer>();
	private final int[] blended_hue;
	private final int[] blended_saturation;
	private final int[] blended_lightness;
	private final int[] blended_hue_factor;
	private final int[] blend_direction_tracker;
	private final int[][][] vertex_heights;
	private final short[][][] overlay_floor_id;
	static int anInt131;
	private final byte[][][] tile_shadow_intensity;
	private final int[][][] tile_culling_bitsets;
	private final byte[][][] overlay_clipping_paths;
	private static final int anIntArray137[] = {1, 0, -1, 0};
	private final int[][] tile_light_intensity;
	private static final int anIntArray140[] = {16, 32, 64, 128};
	private final short[][][] underlay_floor_id;
	private static final int anIntArray144[] = {0, -1, 0, 1};
	static int min_plane = 99;
	private final int region_size_x;
	private final int region_size_y;
	private final byte[][][] overlay_clipping_path_rotations;
	private final byte[][][] tile_flags;
	static boolean lowMem = true;
	private static final int anIntArray152[] = {1, 2, 4, 8};

}
