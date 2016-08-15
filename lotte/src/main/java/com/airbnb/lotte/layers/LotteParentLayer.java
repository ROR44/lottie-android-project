package com.airbnb.lotte.layers;

import android.graphics.Camera;

import com.airbnb.lotte.model.LotteComposition;
import com.airbnb.lotte.utils.LotteAnimationGroup;

public class LotteParentLayer extends LotteAnimatableLayer {

    private LotteLayer parent;
    private LotteAnimationGroup animation;

    public LotteParentLayer(LotteLayer parent, LotteComposition composition) {
        super(composition.getDuration());
        this.parent = parent;
        setupLayerFromModel();
    }

    private void setupLayerFromModel() {
        position = parent.getPosition().getInitialPoint();
        anchorPoint = parent.getAnchor().getInitialPoint();
        transform = parent.getScale().getInitialScale();
        sublayerTransform = new Camera();
        sublayerTransform.rotateZ(parent.getRotation().getInitialValue());
        buildAnimations();
    }

    private void buildAnimations() {
        // TODO
    }
}
