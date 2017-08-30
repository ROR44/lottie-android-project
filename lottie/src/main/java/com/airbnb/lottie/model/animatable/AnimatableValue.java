package com.airbnb.lottie.model.animatable;

import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;

public interface AnimatableValue<K, A> {
  BaseKeyframeAnimation<K, A> createAnimation();
  boolean hasAnimation();

  interface Factory<V> {
    V valueFromObject(Object object, float scale);
  }
}
