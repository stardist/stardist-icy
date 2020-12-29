package de.csbdresden.stardist;

import java.util.LinkedHashMap;
import java.util.Map;

public class AvailableModels {

	public enum Model2D {
		MODEL_DSB2018_HEAVY_AUGMENTATION("Versatile (fluorescent nuclei)"),
		MODEL_HE_HEAVY_AUGMENTATION("DSB 2018 (from StarDist 2D paper)"),
		MODEL_DSB2018_PAPER("Versatile (H&E nuclei)");

		private final String name;

		Model2D(String name) {
			this.name = name;
		}


		@Override
		public String toString() {
			return name;
		}
	}

	public static Map<String, StarDist2DModel> get() {
		final Map<String, StarDist2DModel> models = new LinkedHashMap<>();
		models.put(Model2D.MODEL_DSB2018_PAPER.toString(), new StarDist2DModel(StarDist2DModel.class.getResource("/models/2D/dsb2018_paper.zip"), 0.417819, 0.5, 8, 48));
		models.put(Model2D.MODEL_DSB2018_HEAVY_AUGMENTATION.toString(), new StarDist2DModel(StarDist2DModel.class.getResource("/models/2D/dsb2018_heavy_augment.zip"), 0.479071, 0.3, 16, 96));
		models.put(Model2D.MODEL_HE_HEAVY_AUGMENTATION.toString(), new StarDist2DModel(StarDist2DModel.class.getResource("/models/2D/he_heavy_augment.zip"), 0.692478, 0.3, 16, 96));
		return models;
	}
}
