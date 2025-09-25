#version 150

in float Light;
in vec2 UV;
in vec4 lightMapColor;
in vec4 overlayColor;

out vec4 fragColor; //输出颜色

uniform sampler2D Sampler0;

void main() {
    vec4 color = texture(Sampler0, UV);

    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor * Light;

    color.a = 1.0f;
    fragColor = color;
}
