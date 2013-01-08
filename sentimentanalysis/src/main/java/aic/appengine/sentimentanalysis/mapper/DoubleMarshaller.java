package aic.appengine.sentimentanalysis.mapper;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.appengine.tools.mapreduce.*;

public class DoubleMarshaller extends Marshaller<Double>{
	private static final long serialVersionUID = 7392342684406274177L;

	@Override
	public ByteBuffer toBytes(Double object) {
		ByteBuffer buf=ByteBuffer.allocate(8);
		buf.putDouble(object);
		return buf;
	}

	@Override
	public Double fromBytes(ByteBuffer b) throws IOException {
		return b.getDouble();
	}

}
