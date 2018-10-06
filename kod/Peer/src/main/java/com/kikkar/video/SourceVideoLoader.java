package com.kikkar.video;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface SourceVideoLoader {
	void loadVideo(String inputVideoPath, String outputVideoPath) throws FileNotFoundException;
	
	void readChunk(InputStream is, int chunkNum) throws IOException;
}
