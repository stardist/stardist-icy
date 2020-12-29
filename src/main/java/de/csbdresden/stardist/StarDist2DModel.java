package de.csbdresden.stardist;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class StarDist2DModel {
    
    public final URL url;
    public final double probThresh;
    public final double nmsThresh;
    public final int sizeDivBy;
    public final int tileOverlap;
    private final String protocol;
    
    public StarDist2DModel(URL url, double probThresh, double nmsThresh, int sizeDivBy, int tileOverlap) {
        this.url = url;
        this.protocol = url.getProtocol().toLowerCase();
        this.probThresh = probThresh;
        this.nmsThresh = nmsThresh;
        this.sizeDivBy = sizeDivBy;
        this.tileOverlap = tileOverlap;
    }
    
    public boolean canGetFile() {
        return protocol.equals("file") || protocol.equals("jar");
    }

    public boolean isTempFile() {
        return protocol.equals("jar");
    }
    
    public File getFile() throws IOException {
        switch (protocol) {
        case "file":
            try {
                return new File(url.toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            case "jar":
            final File tmpModelFile = File.createTempFile("stardist_model_", ".zip");
            Files.copy(url.openStream(), tmpModelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return tmpModelFile;            
        default:
            return null;
        }
    }


}
