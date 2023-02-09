package com.practice.openglpractice

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GlRenderer(var context: Context) : GLSurfaceView.Renderer {
    private lateinit var textureCube: TextureCube
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)
    private lateinit var image: Image

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onSurfaceChanged(p0: GL10?, w: Int, h: Int) {
        textureCube = TextureCube()
        image = Image(context)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glViewport(0, 0, w, h)
        val ratio: Float = w.toFloat() / h
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
//        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        Log.d("OPENGL::", "onSurfaceChanged: ${projectionMatrix.contentToString()}")
    }

    override fun onDrawFrame(p0: GL10?) {

        // Set the camera position (View matrix)
//        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
//        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

//        val scratch = FloatArray(16)
//        // Create a rotation transformation for the triangle
//        // Create a rotation transformation for the triangle
//        val time = SystemClock.uptimeMillis() % 4000L
//        val angle = 0.090f * time.toInt()
//        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)
//        // Combine the rotation matrix with the projection and camera view
//        // Note that the vPMatrix factor *must be first* in order
//        // for the matrix multiplication product to be correct.
//        // Combine the rotation matrix with the projection and camera view
//        // Note that the vPMatrix factor *must be first* in order
//        // for the matrix multiplication product to be correct.
//        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)
        image.draw()
    }
}