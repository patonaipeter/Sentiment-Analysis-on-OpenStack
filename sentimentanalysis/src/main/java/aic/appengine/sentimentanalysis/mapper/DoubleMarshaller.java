package aic.appengine.sentimentanalysis.mapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.appengine.tools.mapreduce.*;

public class DoubleMarshaller extends Marshaller<Double>{
	private static final long serialVersionUID = 7392342684406274177L;

	@Override
	public ByteBuffer toBytes(Double x) {
		ByteBuffer out = ByteBuffer.allocate(8).putDouble(x);
		out.rewind();
		return out;
	}

	@Override
	public Double fromBytes(ByteBuffer in) throws IOException {
		if (in.remaining() != 8) {
			throw new IOException("Expected 8 bytes, not " + in.remaining());
		}
		in.order(ByteOrder.BIG_ENDIAN);
		return in.getDouble();
	}
}
