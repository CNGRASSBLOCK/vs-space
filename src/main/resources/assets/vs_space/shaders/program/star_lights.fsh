#version 410
//输出颜色
uniform sampler2D DiffuseSampler;
in vec2 texCoord;
out vec3 fragColor;

void main() {
    // 原始颜色 + 漫光
    fragColor = vec3(1.0f, 1.0f, 1.0f);
}

