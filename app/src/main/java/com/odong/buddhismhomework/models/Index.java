package com.odong.buddhismhomework.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flamen on 15-2-21.
 */
public class Index implements Serializable {
    public Index() {
        positions = new ArrayList<>();
    }

    public int size() {
        return positions.size();
    }

    private List<Long> positions;

    public List<Long> getPositions() {
        return positions;
    }
}
