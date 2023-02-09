package com.practice.openglpractice

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

open class Image(var context: Context) {

    // defining the shape to show the texture into (vertices region)
    private val vertices = floatArrayOf(
        //x,    y
        -0.5f, 0.5f,
        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,
    )

    private val vericesCoordinatesBuffer: FloatBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(vertices)
            position(0)
        }
    }

    private val COORDINATES_PER_VERTEX = 2
    private val VERTEX_STRIDE: Int = COORDINATES_PER_VERTEX * 4

    // defining the coordinates of the texture
    private val TEXTURE_COORDINATES = floatArrayOf(
        //x,    y
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
    )

    private val textureCoordinatesBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(TEXTURE_COORDINATES.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(TEXTURE_COORDINATES)
                position(0)
            }
        }

    private val vertexShadeCode = "uniform mat4 uVPMatrix;\n" +
            "attribute vec4 a_Position;\n" +
            "attribute vec2 a_TexCoord;\n" +
            "varying vec2 v_TexCoord;\n" +
            "\n" +
            "void main(void)\n" +
            "{\n" +
            "    gl_Position = a_Position;\n" +
            "    v_TexCoord = vec2(a_TexCoord.x, 1.0 - a_TexCoord.y);\n" +
            "}"
    private val fragmentShaderCode = "precision highp float;\n" +
            "\n" +
            "uniform sampler2D u_Texture;\n" +
            "varying vec2 v_TexCoord;\n" +
            "\n" +
            "void main(void){\n" +
            "    gl_FragColor = texture2D(u_Texture, v_TexCoord);\n" +
            "    gl_FragColor = texture2D(u_Texture, v_TexCoord);\n" +
            "}"
    private val vertexShader =
        ShaderUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShadeCode)
    private val fragmentShader =
        ShaderUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

    private val indices = shortArrayOf(0, 1, 2, 0, 2, 3)

    private val indicesBuffer: ShortBuffer = ByteBuffer.allocateDirect(indices.size * 2).run {
        order(ByteOrder.nativeOrder())
        asShortBuffer().apply {
            put(indices)
            position(0)
        }
    }

    private var verticesPositionHandle = -1
    private var texPositionHandle = -1
    private var textureUniformHandle: Int = -1
    private var viewProjectionMatrixHandle: Int = -1
    private var textureBitmap: Bitmap
    private final var program: Int


    init {
        // create the program
        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        textureBitmap = BitmapFactory.decodeStream(context.assets.open("images/spiderman.png"))
    }

    fun draw() {
        GLES20.glUseProgram(program)



        // View projection transformation matrix handler
//        viewProjectionMatrixHandle = GLES20.glGetUniformLocation(program, "uVPMatrix")
//        GLES20.glEnableVertexAttribArray(viewProjectionMatrixHandle)

        //Texture uniform handler (used for sampler2D in fragment shader)
        textureUniformHandle = GLES20.glGetUniformLocation(program, "u_Texture")


        val textureUnit = IntArray(1)
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glGenTextures(textureUnit.size, textureUnit, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureUnit[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glUniform1i(textureUniformHandle, 0)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap, 0)



        // Enable a handle to the cube vertices position
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

//        GLES20.glUniformMatrix4fv(viewProjectionMatrixHandle, 1, false, mvpMatrix, 0)

        // getting handler for all the attributes and uniforms defined in the shaders.

        // vertices position handler
        verticesPositionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        //Texture position handler
        texPositionHandle = GLES20.glGetAttribLocation(program, "a_TexCoord")

        GLES20.glVertexAttribPointer(
            verticesPositionHandle,
            COORDINATES_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            vericesCoordinatesBuffer
        )
        GLES20.glVertexAttribPointer(
            texPositionHandle,
            COORDINATES_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            textureCoordinatesBuffer
        )
        GLES20.glEnableVertexAttribArray(verticesPositionHandle)
        GLES20.glEnableVertexAttribArray(texPositionHandle)
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.size,
            GLES20.GL_UNSIGNED_SHORT,
            indicesBuffer
        )

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(verticesPositionHandle)
        GLES20.glDisableVertexAttribArray(texPositionHandle)
        GLES20.glDeleteProgram(program)
        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)

    }
}