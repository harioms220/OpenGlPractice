package com.practice.openglpractice

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet


class OpenGlView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private lateinit var glRenderer: GlRenderer

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        glRenderer = GlRenderer(context!!)
        // Set the Renderer for drawing on the GLSurfaceView
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(glRenderer)
    }

}