package de.csbdresden.stardist;

import icy.file.Loader;
import icy.sequence.Sequence;
import org.junit.Test;
import plugins.frauzufall.AbstractIcyTest;
import plugins.frauzufall.deeplearning.tensorflow.TensorFlowModelPrediction;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StarDist2DTest extends AbstractIcyTest {
	@Test
	public void runStarDistOnBlobs() throws IOException {
		// load input image
		System.out.println("Loading input image..");
		String imagePath = "samples/blobs.png";
		final Sequence input = Loader.loadSequence( imagePath, 0, true );
		assertNotNull(input);

		// locate model
		System.out.println("Locating model file..");
		final StarDist2DModel pretrainedModel = AvailableModels.get().get(AvailableModels.Model2D.MODEL_DSB2018_PAPER.toString());
		File modelFile = pretrainedModel.getFile();
		assertNotNull(modelFile);
		assertTrue(modelFile.exists());

		// setup prediction
		System.out.println("Setup prediction..");
		TensorFlowModelPrediction prediction = new TensorFlowModelPrediction();
		prediction.getOptions().setNormalizeInput(true);
		prediction.getOptions().setModelFile(modelFile);
		prediction.getOptions().setBlockMultiple(pretrainedModel.sizeDivBy);
		prediction.getOptions().setHalo(pretrainedModel.tileOverlap);

		// initialize prediction
		System.out.println("Initialize prediction..");
		prediction.initialize();

		// predict
		System.out.println("Run prediction..");
		Sequence predictionResult = prediction.predict(input);
		assertNotNull(predictionResult);

		// convert prediction result into polygons
		System.out.println("Calculate polygons..");
		StarDist2DNMS nms = new StarDist2DNMS();
		Candidates polygons = nms.run(predictionResult, 0);

		// check result
		assertNotNull(polygons);
		assertNotNull(polygons.getWinner());
		assertEquals(62, polygons.getWinner().size());
	}
}
