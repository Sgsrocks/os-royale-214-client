package com.osroyale;

import java.math.BigInteger;

public final class Buffer extends Cacheable {

	public static Buffer create() {
		synchronized (nodeList) {
			Buffer stream = null;
			if (anInt1412 > 0) {
				anInt1412--;
				stream = (Buffer) nodeList.popHead();
			}
			if (stream != null) {
				stream.currentOffset = 0;
				return stream;
			}
		}
		Buffer stream_1 = new Buffer();
		stream_1.currentOffset = 0;
		stream_1.buffer = new byte[5000];
		return stream_1;
	}

	public String readStringNew() {
		int i = currentOffset;
		while (buffer[currentOffset++] != 0)
			;
		return new String(buffer, i, currentOffset - i - 1);
	}

	public int readShort2() {
		currentOffset += 2;
		int i = ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
		if (i > 60000)
			i = -65535 + i;
		return i;

	}

	final int v(int i) {
		currentOffset += 3;
		return (0xff & buffer[currentOffset - 3] << 16) + (0xff & buffer[currentOffset - 2] << 8) + (0xff & buffer[currentOffset - 1]);
	}

	private Buffer() {
	}

	public Buffer(byte[] abyte0) {
		buffer = abyte0;
		currentOffset = 0;
	}

	public int readUSmart2() {
		int baseVal = 0;
		int lastVal = 0;
		while ((lastVal = method422()) == 32767) {
			baseVal += 32767;
		}
		return baseVal + lastVal;
	}

	public String readNewString() {
		int i = currentOffset;
		while (buffer[currentOffset++] != 0)
			;
		return new String(buffer, i, currentOffset - i - 1);
	}

	public void writeOpcode(int i) {
		// System.out.println("Frame: " + i);
		buffer[currentOffset++] = (byte) (i + encryption.getNextKey());
	}

	public void writeByte(int i) {
		buffer[currentOffset++] = (byte) i;
	}

	public void writeShort(int i) {
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) i;
	}

	public void writeDWordBigEndian(int i) {
		buffer[currentOffset++] = (byte) (i >> 16);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) i;
	}

	public void writeDWord(int i) {
		buffer[currentOffset++] = (byte) (i >> 24);
		buffer[currentOffset++] = (byte) (i >> 16);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) i;
	}

	public void method403(int j) {
		buffer[currentOffset++] = (byte) j;
		buffer[currentOffset++] = (byte) (j >> 8);
		buffer[currentOffset++] = (byte) (j >> 16);
		buffer[currentOffset++] = (byte) (j >> 24);
	}

	public void writeQWord(long l) {
		try {
			buffer[currentOffset++] = (byte) (int) (l >> 56);
			buffer[currentOffset++] = (byte) (int) (l >> 48);
			buffer[currentOffset++] = (byte) (int) (l >> 40);
			buffer[currentOffset++] = (byte) (int) (l >> 32);
			buffer[currentOffset++] = (byte) (int) (l >> 24);
			buffer[currentOffset++] = (byte) (int) (l >> 16);
			buffer[currentOffset++] = (byte) (int) (l >> 8);
			buffer[currentOffset++] = (byte) (int) l;
		} catch (RuntimeException runtimeexception) {
			Utility.reporterror("14395, " + 5 + ", " + l + ", " + runtimeexception.toString());
			throw new RuntimeException();
		}
	}

	public void writeString(String s) {
		// s.getBytes(0, s.length(), buffer, currentOffset); //deprecated
		System.arraycopy(s.getBytes(), 0, buffer, currentOffset, s.length());
		currentOffset += s.length();
		buffer[currentOffset++] = 10;
	}

	public void writeBytes(byte[] abyte0, int i, int j) {
		for (int k = j; k < j + i; k++)
			buffer[currentOffset++] = abyte0[k];
	}

	public void writeBytes(int i) {
		buffer[currentOffset - i - 1] = (byte) i;
	}

	public int readUByte() {
		return buffer[currentOffset++] & 0xff;
	}

	public byte readSignedByte() {
		return buffer[currentOffset++];
	}

	public int readUShort() {
		currentOffset += 2;
		return ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
	}

	public int readSignedShort() {
		currentOffset += 2;
		int i = ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public int readUnsignedTriByte() {
		currentOffset += 3;
		return ((buffer[currentOffset - 3] & 0xff) << 16) + ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
	}

	public int readUnsignedInt() {
		currentOffset += 4;
		return ((buffer[currentOffset - 4] & 0xff) << 24) + ((buffer[currentOffset - 3] & 0xff) << 16) + ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
	}

	public long readUnsignedLong() {
		long big = (long) readUnsignedInt() & 0xFFFF_FFFFL;
		long little = (long) readUnsignedInt() & 0xFFFF_FFFFL;
		return (big << 32) + little;
	}

	public String readString() {
		int start = currentOffset;
		while (buffer[currentOffset++] != 10);
		return new String(buffer, start, currentOffset - start - 1);
	}

	public byte[] readBytes() {
		int start = currentOffset;
		while (buffer[currentOffset++] != 10);
		byte[] bytes = new byte[currentOffset - start - 1];
		System.arraycopy(buffer, start, bytes, start - start, currentOffset - 1 - start);
		return bytes;
	}

	public void readBytes(int i, int j, byte[] abyte0) {
		for (int l = j; l < j + i; l++)
			abyte0[l] = buffer[currentOffset++];
	}

	public void initBitAccess() {
		bitPosition = currentOffset * 8;
	}

	public int readBits(int amount) {
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		int value = 0;

		bitPosition += amount;

		for (; amount > bitOffset; bitOffset = 8) {
			value += (buffer[bytePos++] & BIT_MASK[bitOffset]) << amount - bitOffset;
			amount -= bitOffset;
		}

		if (amount == bitOffset) {
			value += buffer[bytePos] & BIT_MASK[bitOffset];
		} else {
			value += buffer[bytePos] >> bitOffset - amount & BIT_MASK[amount];
		}

		return value;
	}

	public void finishBitAccess() {
		currentOffset = (bitPosition + 7) / 8;
	}

	public int method421() {
		int i = buffer[currentOffset] & 0xff;
		if (i < 128)
			return readUByte() - 64;
		else
			return readUShort() - 49152;
	}

	public int method422() {
		int i = buffer[currentOffset] & 0xff;
		if (i < 128)
			return readUByte();
		else
			return readUShort() - 32768;
	}

	public void encodeRSA(BigInteger exponent, BigInteger modulus) {
		int i = currentOffset;
		currentOffset = 0;
		byte[] abyte0 = new byte[i];
		readBytes(i, 0, abyte0);

		byte[] rsa = abyte0;

		if (Configuration.ENABLE_RSA) {
			rsa = new BigInteger(abyte0).modPow(exponent, modulus).toByteArray();
		}
		currentOffset = 0;
		writeByte(rsa.length);
		writeBytes(rsa, rsa.length, 0);
	}

	public void writeNegatedByte(int i) {
		buffer[currentOffset++] = (byte) (-i);
	}

	public void method425(int j) {
		buffer[currentOffset++] = (byte) (128 - j);
	}

	public int readUByteA() {
		return buffer[currentOffset++] - 128 & 0xff;
	}

	public int readNegUByte() {
		return -buffer[currentOffset++] & 0xff;
	}

	public int readUByteS() {
		return 128 - buffer[currentOffset++] & 0xff;
	}

	public byte method429() {
		return (byte) (-buffer[currentOffset++]);
	}

	public byte method430() {
		return (byte) (128 - buffer[currentOffset++]);
	}

	public void writeLEShort(int i) {
		buffer[currentOffset++] = (byte) i;
		buffer[currentOffset++] = (byte) (i >> 8);
	}

	public void writeShortA(int j) {
		buffer[currentOffset++] = (byte) (j >> 8);
		buffer[currentOffset++] = (byte) (j + 128);
	}

	public void writeLEShortA(int j) {
		buffer[currentOffset++] = (byte) (j + 128);
		buffer[currentOffset++] = (byte) (j >> 8);
	}

	public int readLEUShort() {
		currentOffset += 2;
		return ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] & 0xff);
	}

	public int readUShortA() {
		currentOffset += 2;
		return ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] - 128 & 0xff);
	}

	public int readLEUShortA() {
		currentOffset += 2;
		return ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] - 128 & 0xff);
	}

	public int readLEShort() {
		currentOffset += 2;
		int j = ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] & 0xff);
		if (j > 32767)
			j -= 0x10000;
		return j;
	}

	public int method438() {
		currentOffset += 2;
		int j = ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] - 128 & 0xff);
		if (j > 32767)
			j -= 0x10000;
		return j;
	}

	public int method439() {
		currentOffset += 4;
		return ((buffer[currentOffset - 2] & 0xff) << 24) + ((buffer[currentOffset - 1] & 0xff) << 16) + ((buffer[currentOffset - 4] & 0xff) << 8) + (buffer[currentOffset - 3] & 0xff);
	}

	public int method440() {
		currentOffset += 4;
		return ((buffer[currentOffset - 3] & 0xff) << 24) + ((buffer[currentOffset - 4] & 0xff) << 16) + ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] & 0xff);
	}

	public void method441(int i, byte[] abyte0, int j) {
		for (int k = (i + j) - 1; k >= i; k--)
			buffer[currentOffset++] = (byte) (abyte0[k] + 128);

	}

	public void readReverseData(int i, int j, byte[] abyte0) {
		for (int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = buffer[currentOffset++];

	}
	public int readUnsignedIntSmartShortCompat() {
		int var1 = 0;

		int var2;
		for (var2 = this.readUSmart(); var2 == 32767; var2 = this.readUSmart()) {
			var1 += 32767;
		}

		var1 += var2;
		return var1;
	}
	public int readInt() {
		currentOffset += 4;
		return ((buffer[currentOffset - 4] & 0xff) << 24) + ((buffer[currentOffset - 3] & 0xff) << 16) + ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
	}
	public int readUSmart() {
		int peek = buffer[currentOffset] & 0xFF;
		return peek < 128 ? this.readUByte() : this.readUShort() - 0x8000;
	}
	public int readSmart() {
		int value = buffer[currentOffset] & 0xFF;
		if (value < 128) {
			return readUByte() - 64;
		} else {
			return readUShort() - 49152;
		}
	}
	public void setOffset(int var11) {
		currentOffset = var11;
	}
	public byte[] buffer;
	public int currentOffset;
	public int bitPosition;
	private static final int[] BIT_MASK = {0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1};
	public ISAACRandomGen encryption;
	private static int anInt1412;
	private static final Deque nodeList = new Deque();

	public byte readByte() {
		return this.buffer[++this.currentOffset - 1];
	}

	public int read24BitInt()
	{
		return (this.readUByte() << 16) + (this.readUByte() << 8) + this.readUByte();
	}

	public void skip(int offset) {
		this.currentOffset += offset;
	}

	public int readTriByte() {
		currentOffset += 3;
		return ((buffer[currentOffset - 3] & 0xff) << 16)
				+ ((buffer[currentOffset - 2] & 0xff) << 8)
				+ (buffer[currentOffset - 1] & 0xff);
	}

	public int method1606() {
		int var2 = 0;

		int var3;
		for (var3 = this.readUShortSmart(); var3 == 32767; var3 = this.readShortSmart()) {
			var2 += 32767;
		}

		var2 += var3;
		return var2;
	}
	public int readShortSmart() {
		int var1 = this.buffer[this.currentOffset] & 255;
		return var1 < 128?this.readUByte() - 64:this.readUShort() - '\uc000';
	}
	public int readUShortSmart() {
		int var1 = this.buffer[this.currentOffset] & 255;
		return var1 < 128?this.readUByte():this.readUShort() - '\u8000';
	}

}