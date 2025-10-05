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

struct IntersectionData { vec3 nearPoint; vec3 farPoint;};
IntersectionData getIntersectSphereData(vec3 rayOrigin, vec3 rayDir, vec3 sphereCenter, float sphereRadius) {
    IntersectionData result;
    vec3 toOrigin = rayOrigin - sphereCenter;
    float b = dot(rayDir, toOrigin);
    float c = dot(toOrigin, toOrigin) - sphereRadius * sphereRadius;
    float discriminant = b*b - c;
    if (discriminant < 0.0) return result;
    float s = sqrt(discriminant);
    float tNear = -b - s;
    float tFar  = -b + s;
    if (tFar < 0.0) return result;
    if (tNear > 0.0) {
        result.nearPoint = rayOrigin + tNear * rayDir;
        result.farPoint  = rayOrigin + tFar  * rayDir;
    } else {
        result.nearPoint = rayOrigin; // 从相机位置开始
        result.farPoint  = rayOrigin + tFar * rayDir;
    }
    return result;
}

bool Occlusion(vec3 Origin, vec3 LightPos) {
    vec3 Ray = LightPos - Origin;
    float RayLen2 = dot(Ray, Ray);
    for (int i = 0; i < PlanetCount; i++) {
        Planet planet = planetlist[i];
        float t = dot(planet.Pos - Origin, Ray) / RayLen2;
        if (t >= 0.0 && t <= 1.0) {
            vec3 ClosestPoint = Origin + t * Ray;
            if (length(planet.Pos - ClosestPoint) <= planet.R) return true;
        }
    }
    return false;
}

vec3 ScreenToWorld(vec2 screenPos) {
    vec4 view = iProjMat * vec4(screenPos * 2.0 - 1.0, texture(depth, screenPos).r * 2.0 - 1.0, 1.0);
    view.w = max(view.w, 1.0e-8f);
    view /= view.w;
    return vec3(vec4(iModelViewMat * view).xyz);
}

void main() {
    vec3 Ray = vec3(ScreenToWorld(texCoord) - CameraPos); //像素发出的光线

    vec4 brightness = vec4(0, 0, 0, 0);
    for (int i = 0; i < PlanetCount; i++) {
        Planet planet = planetlist[i];
        if (dot(Ray, Ray) < pow(length(planet.Pos - CameraPos) - planet.R - planet.AtmosphericHeight, 2.0)) continue; //防止层错误和背景投射

        IntersectionData AtmosphereIntersection = getIntersectSphereData(CameraPos, normalize(Ray), planet.Pos, planet.R + planet.AtmosphericHeight);
        if (length(AtmosphereIntersection.farPoint - AtmosphereIntersection.nearPoint) <= 0) continue;
        vec3 UnitLight = (AtmosphereIntersection.farPoint - AtmosphereIntersection.nearPoint) * 0.005;
        for (int j = 0; j < 200; j++) {
            vec3 LightPos = AtmosphereIntersection.nearPoint + UnitLight * j;
            if (Occlusion(LightPos, CameraPos) || distance(LightPos, planet.Pos) <= planet.R) continue;
            float p = 1024 * exp(-0.275 * (length(LightPos - planet.Pos) - planet.AtmosphericHeight));
            for (int x = 0; x < StarCount; x++) if (!Occlusion(LightPos, starlist[x].Pos)) brightness += vec4(0.52734,0.8043,0.91796,1) * length(UnitLight) * p;
        }
    }

    fragColor = texture(DiffuseSampler, texCoord) + vec4(brightness.xyz, 1.0f);
}