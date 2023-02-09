package com.practice.openglpractice

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer


class TextureCube {
    private var vertexBuffer: FloatBuffer
    private var indexBuffer: ShortBuffer

    private var program: Int

    // colors array representing color for each face of the cube
    private val colors = arrayOf(
        floatArrayOf(1.0f, 0.5f, 0.0f, 1.0f), // color 1
        floatArrayOf(1.0f, 0.0f, 1.0f, 1.0f), // color 2
        floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f), // color 3
        floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f), // color 4
        floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f), // color 5
        floatArrayOf(1.0f, 1.0f, 0.0f, 1.0f) // color 6
    )
    val COORDS_PER_VERTEX = 3

    // vertices representing each vertex of the cube
    var vertices = floatArrayOf(
        -0.8f, -0.8f, 0.8f,  // 0. left-bottom-front
        0.8f, -0.8f, 0.8f,  // 1. right-bottom-front
        -0.8f, 0.8f, 0.8f,  // 2. left-top-front
        0.8f, 0.8f, 0.8f,  // 3. right-top-front
        // BACK
        0.8f, -0.8f, -0.8f,  // 6. right-bottom-back
        -0.8f, -0.8f, -0.8f,  // 4. left-bottom-back
        0.8f, 0.8f, -0.8f,  // 7. right-top-back
        -0.8f, 0.8f, -0.8f,  // 5. left-top-back
        // LEFT
        -0.8f, -0.8f, -0.8f,  // 4. left-bottom-back
        -0.8f, -0.8f, 0.8f,  // 0. left-bottom-front
        -0.8f, 0.8f, -0.8f,  // 5. left-top-back
        -0.8f, 0.8f, 0.8f,  // 2. left-top-front
        // RIGHT
        0.8f, -0.8f, 0.8f,  // 1. right-bottom-front
        0.8f, -0.8f, -0.8f,  // 6. right-bottom-back
        0.8f, 0.8f, 0.8f,  // 3. right-top-front
        0.8f, 0.8f, -0.8f,  // 7. right-top-back
        // TOP
        -0.8f, 0.8f, 0.8f,  // 2. left-top-front
        0.8f, 0.8f, 0.8f,  // 3. right-top-front
        -0.8f, 0.8f, -0.8f,  // 5. left-top-back
        0.8f, 0.8f, -0.8f,  // 7. right-top-back
        // BOTTOM
        -0.8f, -0.8f, -0.8f,  // 4. left-bottom-back
        0.8f, -0.8f, -0.8f,  // 6. right-bottom-back
        -0.8f, -0.8f, 0.8f,  // 0. left-bottom-front
        0.8f, -0.8f, 0.8f // 1. right-bottom-front
    )

    // indices specifying the order of the drawing of the vertices. Will be used by glDrawElements() to draw the vertices in the order specified in this array
    var indices = shortArrayOf(
        0, 1, 2, 2, 1, 3, // face 1
        5, 4, 7, 7, 4, 6, // face 2
        8, 9, 10, 10, 9, 11, // face 3
        12, 13, 14, 14, 13, 15, // face 4
        16, 17, 18, 18, 17, 19, // face 5
        22, 23, 20, 20, 23, 21 // face 6
    )

    private val vertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"
    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    init {

        // copying arrays(vertices and indices) data stored in vertices indices into buffers.
        // Passing data to GPU memory from RAM and thus to variables and attributes in shaders is supported by many ways.
        // Using buffer is one of those ways.
        val vbb: ByteBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder()) // Use native byte order
        vertexBuffer = vbb.asFloatBuffer() // Convert from byte to float
        vertexBuffer.put(vertices) // Copy data into buffer
        vertexBuffer.position(0) // Rewind

        // initialize byte buffer for the draw list
        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2).order(ByteOrder.nativeOrder()).asShortBuffer()
        indexBuffer.put(indices).position(0)


        val vertexShader: Int = loadShader(
            GLES20.GL_VERTEX_SHADER,
            vertexShaderCode
        )
        val fragmentShader: Int = loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            fragmentShaderCode
        )
        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram()
        // add the vertex shader to program
        GLES20.glAttachShader(program, vertexShader)
        // add the fragment shader to program
        GLES20.glAttachShader(program, fragmentShader)
        // creates OpenGL ES program executables
        GLES20.glLinkProgram(program)
    }


    fun loadShader(type: Int, shaderCode: String?): Int {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        val shader = GLES20.glCreateShader(type)
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }


    private var positionHandle = 0
    private var colorHandle = 0
    private val vertexCount: Int = vertices.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4
    private var vPMatrixHandle = 0
    fun draw() {
        // Adding program to OpenGL ES environment
        GLES20.glUseProgram(program)

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        // Pass the projection and view transformation to the shader
//        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw the triangle
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        // Enable a handle to the cube vertices position
        GLES20.glEnableVertexAttribArray(positionHandle)
        // Prepare the cube coordinate data
        GLES20.glVertexAttribPointer(
            positionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer
        )
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")

        for (face in 0 until 6) {
            // Set the color for each of the faces
            GLES20.glUniform4fv(colorHandle, 1, colors[face], 0)
            indexBuffer.position(face * 6)
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
        }
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}