package com.airbnb.lottie

import android.graphics.Bitmap
import android.util.Log
import java.util.*
import java.util.concurrent.Semaphore
import kotlin.collections.HashSet

internal class ObjectPool<T>(private val factory: () -> T) {

    private val semaphore = SuspendingSemaphore(MAX_RELEASED_OBJECTS)
    private val objects = Collections.synchronizedList(ArrayList<T>())
    private val releasedObjects = HashSet<T>()

    fun acquire(): T {
        var blockedStartTime = 0L
        if (semaphore.isFull()) {
            blockedStartTime = System.currentTimeMillis()
        }
        semaphore.acquire()
        if (blockedStartTime > 0) {
            Log.d(L.TAG, "Waited ${System.currentTimeMillis() - blockedStartTime}ms for an object.")
        }

        val obj = synchronized(objects) {
            objects.firstOrNull()?.also { objects.remove(it) }
        } ?: factory()

        releasedObjects += obj
        return obj
    }

    fun release(obj: T) {
        val removed = releasedObjects.remove(obj)
        if (!removed) throw IllegalArgumentException("Unable to find original obj.")

        objects.add(obj)
        semaphore.release()
    }

    companion object {
        // The maximum number of objects that are allowed out at a time.
        // If this limit is reached a thread must wait for another bitmap to be returned.
        // Bitmaps are expensive, and if we aren't careful we can easily allocate too many objects
        // since coroutines run parallelized.
        private const val MAX_RELEASED_OBJECTS = 10
    }
}