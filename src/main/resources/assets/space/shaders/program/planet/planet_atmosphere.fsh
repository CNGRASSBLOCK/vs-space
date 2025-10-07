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

//球射线相交
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

//遮挡检测
bool Occlusion(vec3 Origin, vec3 LightPos) {
    vec3 Ray = LightPos - Origin;
    float RayLen2 = dot(Ray, Ray);
    for (int i = 0; i < PlanetCount; ++i) {
        Planet planet = planetlist[i];
        float t = dot(planet.Pos - Origin, Ray) / RayLen2;
        if (t >= 0.0 && t <= 1.0) {
            vec3 ClosestPoint = Origin + t * Ray;
            if (length(planet.Pos - ClosestPoint) <= planet.R) return true;
        }
    }
    return false;
}

//散射
float computeAtmosphericReflectance(float totalHeight, float currentHeight) {
    const float scaleHeight   = 2;   // 尺度高度 H ~ 8 km
    const float rho0          = 1.0;      // 归一化地表密度
    const float sigmaRayleigh = 0.025;   // Rayleigh 有效散射截面 (近似值)
    const float baseReflect   = 0.3;      // 地表附近的最大反射率

    float h = clamp(currentHeight, 0.0, totalHeight);
    return baseReflect * (1.0 - exp(-sigmaRayleigh * rho0 * exp(-h / scaleHeight) * (totalHeight - h)));
}

//色散
vec3 computeDispersionColor(vec3 incident, vec3 normal) {
    float r = dot(incident, normal) * 0.65 * 0.65;
    float g = dot(incident, normal) * 0.51 * 0.51;
    float b = dot(incident, normal) * 0.45 * 0.45;

    return clamp(vec3(r, g, b), 0.0, 1.0);
}

//随机数
float rand(vec2 co) { return fract(sin(dot(co, vec2(127.1, 311.7))) * 43758.5453 + sin(dot(co, vec2(269.5, 183.3))) * 12345.6789); }

//屏幕坐标到世界坐标
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
        vec3 UnitLight = (AtmosphereIntersection.farPoint - AtmosphereIntersection.nearPoint) * 0.02;
        for (int j = 0; j < 20; ++j) {
            vec3 LightPos = AtmosphereIntersection.nearPoint + UnitLight * (j + rand(texCoord));
            if (Occlusion(LightPos, CameraPos) || distance(LightPos, planet.Pos) <= planet.R) continue;
            float ReflectionCoefficient = computeAtmosphericReflectance(planet.AtmosphericHeight, length(LightPos - planet.Pos) - planet.R);
            for (int x = 0; x < StarCount; ++x) if (!Occlusion(LightPos, starlist[x].Pos)) {
                vec4 dspersion_color = vec4(computeDispersionColor(normalize(LightPos - starlist[x].Pos), normalize(LightPos - planet.Pos)), 1.0f);
                brightness += vec4(1,1,1,1) * length(UnitLight) * ReflectionCoefficient;
            }
        }
    }

    fragColor = vec4(brightness.xyz, 1.0f);
}