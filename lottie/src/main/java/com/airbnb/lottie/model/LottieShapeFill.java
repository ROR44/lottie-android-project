package com.airbnb.lottie.model;

import android.util.Log;

import com.airbnb.lottie.L;
import com.airbnb.lottie.animation.AnimatableColorValue;
import com.airbnb.lottie.animation.AnimatableIntegerValue;
import com.airbnb.lottie.animation.AnimationGroup;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings({"EmptyCatchBlock", "WeakerAccess", "FieldCanBeLocal"})
public class LottieShapeFill {
    private static final String TAG = LottieShapeFill.class.getSimpleName();

    private boolean fillEnabled;
    private AnimatableColorValue color;
    private AnimatableIntegerValue opacity;

    public LottieShapeFill(JSONObject json, int frameRate, long compDuration) {
        JSONObject jsonColor = null;
        try {
            jsonColor = json.getJSONObject("c");
        } catch (JSONException e) { }
        if (jsonColor != null) {
            color = new AnimatableColorValue(jsonColor, frameRate, compDuration);
        }

        JSONObject jsonOpacity = null;
        try {
            jsonOpacity = json.getJSONObject("o");
        } catch (JSONException e) { }
        if (jsonOpacity != null) {
            opacity = new AnimatableIntegerValue(jsonOpacity, frameRate, compDuration, false);
            opacity.remap100To255();
        }

        try {
            fillEnabled = json.getBoolean("fillEnabled");
        } catch (JSONException e) { }
        if (L.DBG) Log.d(TAG, "Parsed new shape fill " + toString());
    }

    public AnimatableColorValue getColor() {
        return color;
    }

    public AnimatableIntegerValue getOpacity() {
        return opacity;
    }

    public AnimationGroup createAnimation() {
        return AnimationGroup.forAnimatableValues(getColor(), getOpacity());
    }

    @Override
    public String toString() {
        return "LottieShapeFill{" + "color=" + Integer.toHexString(color.getInitialValue()) +
                ", fillEnabled=" + fillEnabled +
                ", opacity=" + opacity.getInitialValue() +
                '}';
    }
}
