package aic.data;

import java.io.IOException;

import aic.data.dto.Tweet;

public interface ITweetReader {
	public Tweet read() throws IOException;
}
