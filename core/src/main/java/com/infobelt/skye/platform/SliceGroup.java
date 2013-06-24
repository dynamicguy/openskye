package com.infobelt.skye.platform;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a logical group of {@link Slice}
 */
public class SliceGroup {

    private List<Slice> slices = new ArrayList<>();

    public List<Slice> getSlices() {
        return slices;
    }

    public void setSlices(List<Slice> slices) {
        this.slices = slices;
    }
}
