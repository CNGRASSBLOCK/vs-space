#version 150

in vec3 Position;     // 顶点位置

//输出到片段着色器
out vec4 OutStarColor;

//统一变量
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform vec4 StarColor;

void main() {
    // 顶点变换
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    // 传递颜色和纹理坐标
    OutStarColor = StarColor;
}
