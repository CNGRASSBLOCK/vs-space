#version 410

in float Light;
in vec2 UV;

out vec4 fragColor; //输出颜色

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

void main() {
    vec4 color = texture(Sampler0, UV);
    vec4 night_color = texture(Sampler1, UV);

    color *= Light;
    color.a = 1.0f;

    fragColor = color + night_color;
}
