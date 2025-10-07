#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D main_screen;
uniform vec2 ScreenSize;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 texelSize = 1.0 / ScreenSize;

    vec4 result = vec4(0.0);

    result += texture(DiffuseSampler, texCoord + vec2(3, 0) * texelSize);
    result += texture(DiffuseSampler, texCoord + vec2(0, 3) * texelSize);
    result += texture(DiffuseSampler, texCoord + vec2(0, 0) * texelSize);
    result += texture(DiffuseSampler, texCoord + vec2(-3, 3) * texelSize);


    fragColor = result / 9.0 + texture(main_screen, texCoord);
}