package org.cn_grass_block.space.client.render.shader.UBO;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

//这个类大部分都是gpt5写的
public class UniformBuffer {
    private final int uboId;
    private final int bindingPoint;
    private final int size;

    public UniformBuffer(int bindingPoint, int size) {
        this.bindingPoint = bindingPoint;
        this.size = size;

        uboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, uboId);
        GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, size, GL15.GL_DYNAMIC_DRAW);
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);

        GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, bindingPoint, uboId);
    }

    public void update(FloatBuffer data) {
        update(data,false);
    }
    public void update(ByteBuffer data) {
        update(data,false);
    }

    public void update(FloatBuffer data,boolean autoFree) {
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, uboId);
        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, data);
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
        if (autoFree) MemoryUtil.memFree(data);
    }
    public void update(ByteBuffer data,boolean autoFree) {
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, uboId);
        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, data);
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
        if (autoFree) MemoryUtil.memFree(data);
    }

    public void bindToShader(int shaderProgramId, String blockName) {
        int blockIndex = GL31.glGetUniformBlockIndex(shaderProgramId, blockName);
        if (blockIndex < 0) {
            throw new IllegalStateException("Uniform block not found: " + blockName);
        }
        GL31.glUniformBlockBinding(shaderProgramId, blockIndex, bindingPoint);
    }

    public void delete() {
        GL15.glDeleteBuffers(uboId);
    }
    public int getUboId() { return uboId; }
    public int getBindingPoint() { return bindingPoint; }
    public int getSize() { return size; }
}
