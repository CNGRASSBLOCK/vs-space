#version 150

in float Light;
in vec2 UV;

out vec4 fragColor; //输出颜色

uniform sampler2D DiffuseSampler;

void main() {
    vec4 texColor = texture(DiffuseSampler, UV);

    fragColor = texColor * Light;
    fragColor.a = 1.0f;
}
