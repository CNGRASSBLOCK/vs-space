#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D depth;

in vec2 texCoord;

out vec4 fragColor;

uniform mat4 iProjMat;
uniform mat4 iModelViewMat;
uniform vec3 CameraPos;

struct Star {
    vec3 Pos;   //恒星位置
    vec4 Color; //恒星颜色（RGBA）
    float R;    //恒心半径
};
struct Planet {
    vec3 Pos;   //行星位置
    float R;    //行星半径
    float AtmosphericHeight; //大气高度
    vec4 AtmosphericColor;   //大气颜色
};

layout(std140) uniform CelestialBodyData {
    int StarCount;
    Star starlist[16];

    int PlanetCount;
    Planet planetlist[64];
};

vec3 ScreenToWorld(vec2 screenPos) {
    vec4 view = iProjMat * vec4(screenPos * 2.0 - 1.0, texture(depth, screenPos).r * 2.0 - 1.0, 1.0);
    view.w = max(view.w, 1.0e-8f);
    view /= view.w;
    return vec3(vec4(iModelViewMat * view).xyz);
}

void main() {
    vec3 Ray = vec3(ScreenToWorld(texCoord) - CameraPos); //像素发出的光线

    vec4 brightness = vec4(0, 0, 0, 0);
    for (int i = 0; i < StarCount; i++) {
        Star star = starlist[i];
        vec3 starToCamera = star.Pos - CameraPos;
        if (dot(Ray, starToCamera) < 0.0 || dot(Ray, Ray) < pow(length(starToCamera) - star.R - 1, 2.0)) continue; //防止层错误和背景投射
        vec3 rayDir = normalize(Ray);

        float distance = length(CameraPos + rayDir * dot(starToCamera, rayDir) - star.Pos);
        brightness += star.Color * exp(-distance * distance / pow(star.R, 2.0));
    }

    brightness *= 5; //亮度超级加倍

    fragColor = texture(DiffuseSampler,texCoord) + brightness;
}