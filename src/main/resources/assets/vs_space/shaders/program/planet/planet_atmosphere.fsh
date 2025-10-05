#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D depth;

in vec2 texCoord;

out vec4 fragColor;

uniform mat4 iProjMat;
uniform mat4 iModelViewMat;

vec3 ScreenToWorld(vec2 screenPos) {
    vec4 view = iProjMat * vec4(screenPos * 2.0 - 1.0, texture(depth, screenPos).r * 2.0 - 1.0, 1.0);
    view /= view.w;
    return vec3(vec4(iModelViewMat * view).xyz);
}

void main() {
    vec3 WorldPos = ScreenToWorld(texCoord);
    fragColor = vec4(WorldPos.xyz, 1.0f);
}