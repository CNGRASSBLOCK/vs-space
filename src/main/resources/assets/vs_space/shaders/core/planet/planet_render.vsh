#version 410

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

uniform vec3 PlanetPos;

struct Star {
    vec3 Pos;   //恒星位置
    vec4 Color; //恒星颜色（RGBA）
};
struct Planet {
    vec3 Pos;   //行星位置
    float R;    //行星半径
};

layout(std140) uniform CelestialBodyData {
    int StarCount;
    Star starlist[16];

    int PlanetCount;
    Planet planetlist[64];
};

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0); //顶点变换

    float brightness = 0.0f;
    for (int i = 0; i < StarCount; i++) {
        vec3 StarPos = vec3(starlist[i].Pos);
        vec3 LightDirection = vec3(StarPos - PlanetPos + Position);
        float this_brightness = dot(Normal, LightDirection) / length(LightDirection); //向量投影
        if (this_brightness > 0.0f) brightness += this_brightness;
    }

    Light = brightness;
    UV = UV0;
}
