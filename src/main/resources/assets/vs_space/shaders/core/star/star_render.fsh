#version 150

in vec4 OutStarColor;
//输出颜色
out vec4 fragColor;

uniform vec4 StarColor;

void main() {
    fragColor = OutStarColor * vec4(1.25, 1.25, 1.25, 1);
}
