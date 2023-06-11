package com.osroyale;

public final class Animation {

    private int[] frameSounds;

    static void unpackConfig(StreamLoader streamLoader) {
        Buffer stream = new Buffer(streamLoader.getFile("seq.dat"));
        int length = stream.readUShort();

        if (animations == null) {
            animations = new Animation[length + 5000];
        }

        for (int index = 0; index < length; index++) {
            if (animations[index] == null) {
                animations[index] = new Animation();
            }

            animations[index].decode(stream);
        }
    }

    int method258(int i) {
        int j = duration[i];
        if (j == 0) {
            Frame class36 = Frame.method531(primary[i]);
            if (class36 != null)
                j = duration[i] = class36.anInt636;
        }
        if (j == 0)
            j = 1;
        return j;
    }

    public void decode(Buffer stream) {
        int i;
        while ((i = stream.readUByte()) != 0) {

            if (i == 1) {
                length = stream.readUShort();
                primary = new int[length];
                secondary = new int[length];
                duration = new int[length];

                for (int j = 0; j < length; j++)
                    duration[j] = stream.readUShort();


                for (int j = 0; j < length; j++) {
                    primary[j] = stream.readUShort();
                    secondary[j] = -1;
                }

                for (int j = 0; j < length; j++) {
                    primary[j] += stream.readUShort() << 16;
                }
            }
            else if (i == 2) {
                padding = stream.readShort2();
            }
            else if (i == 3) {
                int length = stream.readUByte();
                interleaveOrder = new int[length + 1];
                for (int k = 0; k < length; k++) {
                    interleaveOrder[k] = stream.readUByte();
                }
                interleaveOrder[length] = 9999999;
            }
            else if (i == 4) {
                allowsRotation = true;
            }
            else if (i == 5) {
                priority = stream.readUByte();
            }
            else if (i == 6) {
                shield = stream.readShort2();
            }
            else if (i == 7) {
                weapon = stream.readShort2();
            }
            else if (i == 8) {
                resetCycle = stream.readUByte();
            }
            else if (i == 9) {
                runFlag = stream.readUByte();
            }
            else if (i == 10) {
                walkFlag = stream.readUByte();
            }
            else if (i == 11) {
                type = stream.readUByte();
            }
         else if (i == 12) {
            int len = stream.readUByte();

            for (int i1 = 0; i1 < len; i1++) {
                stream.readUShort();
            }

            for (int i1 = 0; i1 < len; i1++) {
                stream.readUShort();
            }
        } else if (i == 13) {
            int var3 = stream.readUByte();
            frameSounds = new int[var3];
            for (int var4 = 0; var4 < var3; ++var4) {
                frameSounds[var4] = stream.read24BitInt();
                if (0 != frameSounds[var4]) {
                    int var6 = frameSounds[var4] >> 8;
                    int var8 = frameSounds[var4] >> 4 & 7;
                    int var9 = frameSounds[var4] & 15;
                    frameSounds[var4] = var6;
                }
            }
            }
            else {
//                Utility.print("Error unrecognised seq config code: " + i);
            }
        }
        if (length == 0) {
            length = 1;
            primary = new int[1];
            primary[0] = -1;
            secondary = new int[1];
            secondary[0] = -1;
            duration = new int[1];
            duration[0] = -1;
        }
        if (runFlag == -1)
            if (interleaveOrder != null)
                runFlag = 2;
            else
                runFlag = 0;
        if (walkFlag == -1) {
            if (interleaveOrder != null) {
                walkFlag = 2;
                return;
            }
            walkFlag = 0;
        }
    }

//    private void decode(Buffer buffer) {
//        int opcode;
//
//        while ((opcode = buffer.readUnsignedByte()) != 0) {
//            if (opcode == 1) {
//                length = buffer.readUShort();
//                primary = new int[length];
//                secondary = new int[length];
//                duration = new int[length];
//                for (int i = 0; i < length; i++) {
//                    primary[i] = buffer.readInt();
//                    secondary[i] = -1;
//                }
//                for (int i = 0; i < length; i++) {
//                    duration[i] = buffer.readUnsignedByte();
//                }
//            } else if (opcode == 2) {
//                padding = buffer.readUShort();
//            } else if (opcode == 3) {
//                int length = buffer.readUnsignedByte();
//                interleaveOrder = new int[length + 1];
//                for (int i = 0; i < length; i++) {
//                    interleaveOrder[i] = buffer.readUnsignedByte();
//                }
//                interleaveOrder[length] = 9999999;
//            } else if (opcode == 4) {
//                allowsRotation = true;
//            } else if (opcode == 5) {
//                priority = buffer.readUnsignedByte();
//            } else if (opcode == 6) {
//                shield = buffer.readUShort();
//            } else if (opcode == 7) {
//                weapon = buffer.readUShort();
//            } else if (opcode == 8) {
//                resetCycle = buffer.readUnsignedByte();
//            } else if (opcode == 9) {
//                runFlag = buffer.readUnsignedByte();
//            } else if (opcode == 10) {
//                priority = buffer.readUnsignedByte();
//            } else if (opcode == 11) {
//                type = buffer.readUnsignedByte();
//            } else if (opcode == 12) {
//                buffer.readInt();
//            } else {
//                System.out.println("Error unrecognised seq config code: " + opcode);
//            }
//        }
//    }

    private Animation() {
        padding = -1;
        allowsRotation = false;
        priority = 5;
        shield = -1;
        weapon = -1;
        resetCycle = 99;
        runFlag = -1;
        walkFlag = -1;
        type = 1;
    }

    static Animation animations[];
    public int length;
    int primary[];
    int secondary[];
    int[] duration;
    int padding;
    int interleaveOrder[];
    boolean allowsRotation;
    int priority;
    int shield;
    int weapon;
    int resetCycle;
    int runFlag;
    int walkFlag;
    public int type;
}
