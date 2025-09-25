#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec3 Normal;

//输出到片段着色器
out float Light;
out vec2 UV;

//统一变量
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 CameraPos;

uniform vec3 PlanetPos;

uniform int StarPosListSize;


void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0); //顶点变换

    float brightness = 0.0f;
    for (int i = 0; i < StarPosListSize; i++) {
        vec3 StarPos = vec3(10000.0f,10000.0f,0.0f);
        vec3 LightDirection = vec3(StarPos - PlanetPos + Position);
        float this_brightness = dot(Normal, LightDirection) / length(LightDirection); //向量投影
        if (this_brightness > 0.0f) brightness += this_brightness;
    }

    Light = brightness;
    UV = UV0;
}
