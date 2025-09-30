#version 410

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec3 Normal;

//输出到片段着色器
out float Light;
out vec2 UV;
out vec4 lightMapColor;
out vec4 overlayColor;

//统一变量
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform vec3 PlanetPos;

uniform int StarNumber;
uniform vec3 StarPos_0;
uniform vec3 StarPos_1;
uniform vec3 StarPos_2;
uniform vec3 StarPos_3;
uniform vec3 StarPos_4;
uniform vec3 StarPos_5;
uniform vec3 StarPos_6;
uniform vec3 StarPos_7;

uniform int PlanetNumber;
uniform vec3 PlanetPos_0;
uniform vec3 PlanetPos_1;
uniform vec3 PlanetPos_2;
uniform vec3 PlanetPos_3;
uniform vec3 PlanetPos_4;
uniform vec3 PlanetPos_5;
uniform vec3 PlanetPos_6;
uniform vec3 PlanetPos_7;
uniform vec3 PlanetPos_8;
uniform vec3 PlanetPos_9;
uniform vec3 PlanetPos_10;
uniform vec3 PlanetPos_11;
uniform vec3 PlanetPos_12;
uniform vec3 PlanetPos_13;
uniform vec3 PlanetPos_14;
uniform vec3 PlanetPos_15;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0); //顶点变换

    vec3[8] StarPosList;
    StarPosList[0] = StarPos_0;
    StarPosList[1] = StarPos_1;
    StarPosList[2] = StarPos_2;
    StarPosList[3] = StarPos_3;
    StarPosList[4] = StarPos_4;
    StarPosList[5] = StarPos_5;
    StarPosList[6] = StarPos_6;
    StarPosList[7] = StarPos_7;

    float brightness = 0.0f;
    for (int i = 0; i < StarNumber; i++) {
        vec3 StarPos = vec3(StarPosList[i]);
        vec3 LightDirection = vec3(StarPos - PlanetPos + Position);
        float this_brightness = dot(Normal, LightDirection) / length(LightDirection); //向量投影
        if (this_brightness > 0.0f) brightness += this_brightness;
    }

    Light = brightness;
    UV = UV0;
}