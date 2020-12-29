package de.csbdresden.stardist;

import icy.sequence.Sequence;

public class StarDist2DNMS {

    private double probThresh = (double) Opt.getDefault(Opt.PROB_THRESH);

    private double nmsThresh = (double) Opt.getDefault(Opt.NMS_THRESH);

    private int excludeBoundary = (int) Opt.getDefault(Opt.EXCLUDE_BNDRY);

    public Candidates run(Sequence predictionResult, int t) {
        final Candidates polygons = new Candidates(predictionResult, probThresh, excludeBoundary, t);
        polygons.nms(nmsThresh);
        return polygons;
    }


    private boolean checkInputs() {
//        final LinkedHashSet<AxisType> probAxes = Utils.orderedAxesSet(prob);
//        final LinkedHashSet<AxisType> distAxes = Utils.orderedAxesSet(dist);
//
//        if (!( (prob.numDimensions() == 2 && probAxes.containsAll(Arrays.asList(Axes.X, Axes.Y))) ||
//               (prob.numDimensions() == 3 && probAxes.containsAll(Arrays.asList(Axes.X, Axes.Y, Axes.TIME))) ))
//            return showError(String.format("%s must be a 2D image or timelapse.", Opt.PROB_IMAGE));
//
//        if (!( (dist.numDimensions() == 3 && distAxes.containsAll(Arrays.asList(Axes.X, Axes.Y, Axes.CHANNEL))            && dist.getChannels() >= 3) ||
//               (dist.numDimensions() == 4 && distAxes.containsAll(Arrays.asList(Axes.X, Axes.Y, Axes.CHANNEL, Axes.TIME)) && dist.getChannels() >= 3) ))
//            return showError(String.format("%s must be a 2D image or timelapse with at least three channels.", Opt.DIST_IMAGE));
//
//        if ((prob.numDimensions() + 1) != dist.numDimensions())
//            return showError(String.format("Axes of %s and %s not compatible.", Opt.PROB_IMAGE, Opt.DIST_IMAGE));
//
//        if (prob.getWidth() != dist.getWidth() || prob.getHeight() != dist.getHeight())
//            return showError(String.format("Width or height of %s and %s differ.", Opt.PROB_IMAGE, Opt.DIST_IMAGE));
//
//        if (prob.getFrames() != dist.getFrames())
//            return showError(String.format("Number of frames of %s and %s differ.", Opt.PROB_IMAGE, Opt.DIST_IMAGE));
//
//        final AxisType[] probAxesArray = probAxes.stream().toArray(AxisType[]::new);
//        final AxisType[] distAxesArray = distAxes.stream().toArray(AxisType[]::new);
//        if (!( probAxesArray[0] == Axes.X && probAxesArray[1] == Axes.Y ))
//            return showError(String.format("First two axes of %s must be a X and Y.", Opt.PROB_IMAGE));
//        if (!( distAxesArray[0] == Axes.X && distAxesArray[1] == Axes.Y ))
//            return showError(String.format("First two axes of %s must be a X and Y.", Opt.DIST_IMAGE));
//
//        if (!(0 <= nmsThresh && nmsThresh <= 1))
//            return showError(String.format("%s must be between 0 and 1.", Opt.NMS_THRESH));
//
//        if (excludeBoundary < 0)
//            return showError(String.format("%s must be >= 0", Opt.EXCLUDE_BNDRY));
//
//        if (!(outputType.equals(Opt.OUTPUT_ROI_MANAGER) || outputType.equals(Opt.OUTPUT_LABEL_IMAGE) || outputType.equals(Opt.OUTPUT_BOTH) || outputType.equals(Opt.OUTPUT_POLYGONS)))
//            return showError(String.format("%s must be one of {\"%s\", \"%s\", \"%s\"}.", Opt.OUTPUT_TYPE, Opt.OUTPUT_ROI_MANAGER, Opt.OUTPUT_LABEL_IMAGE, Opt.OUTPUT_BOTH));
//
//        if (outputType.equals(Opt.OUTPUT_POLYGONS) && probAxes.contains(Axes.TIME))
//            return showError(String.format("Timelapse not supported for output type \"%s\"", Opt.OUTPUT_POLYGONS));
//
//        if (!(roiPosition.equals(Opt.ROI_POSITION_STACK) || roiPosition.equals(Opt.ROI_POSITION_HYPERSTACK)))
//            return showError(String.format("%s must be one of {\"%s\", \"%s\"}.", Opt.ROI_POSITION, Opt.ROI_POSITION_STACK, Opt.ROI_POSITION_HYPERSTACK));
//
        return true;
    }

    public void setProbThresh(double probThresh) {
        this.probThresh = probThresh;
    }

    public void setNmsThresh(double nmsThresh) {
        this.nmsThresh = nmsThresh;
    }

    public void setExcludeBoundary(int excludeBoundary) {
        this.excludeBoundary = excludeBoundary;
    }

}
