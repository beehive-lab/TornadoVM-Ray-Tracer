package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.bodies.Body;
import com.vinhderful.raytracer.utils.Color;
import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;
import uk.ac.manchester.tornado.api.collections.types.VectorInt;

import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.max;
import static uk.ac.manchester.tornado.api.collections.math.TornadoMath.pow;

public class Shader {

    public static Float4 getAmbient(Float4 bodyColor, Float4 lightColor) {

        final float AMBIENT_STRENGTH = 0.05F;
        return Color.mult(Color.mult(bodyColor, lightColor), AMBIENT_STRENGTH);
    }

    public static Float4 getDiffuse(int bodyType, Float4 hitPosition,
                                    Float4 bodyPosition, Float4 bodyColor,
                                    Float4 lightPosition, Float4 lightColor) {
        float diffuseBrightness = max(0, Float4.dot(Body.getNormal(bodyType, hitPosition, bodyPosition), Float4.normalise(Float4.sub(lightPosition, hitPosition))));
        return Color.mult(Color.mult(bodyColor, lightColor), diffuseBrightness);
    }

    public static Float4 getSpecular(Float4 cameraPosition, int bodyType, Float4 hitPosition,
                                     Float4 bodyPosition, float bodyReflectivity,
                                     Float4 lightPosition, Float4 lightColor) {

        final float SPECULAR_STRENGTH = 0.5F;

        Float4 rayDirection = Float4.normalise(Float4.sub(hitPosition, cameraPosition));
        Float4 lightDirection = Float4.normalise(Float4.sub(lightPosition, hitPosition));

        Float4 reflectionVector = Float4.sub(lightDirection,
                Float4.mult(Body.getNormal(bodyType, hitPosition, bodyPosition),
                        2 * Float4.dot(lightDirection, Body.getNormal(bodyType, hitPosition, bodyPosition))));

        float specularFactor = max(0, Float4.dot(reflectionVector, rayDirection));
        float specularBrightness = pow(specularFactor, bodyReflectivity);

        return Color.mult(Color.mult(lightColor, specularBrightness), SPECULAR_STRENGTH);
    }

    public static float getShadow(int sampleSize, Float4 hitPosition,
                                  VectorInt bodyTypes, VectorFloat4 bodyPositions, VectorFloat bodySizes,
                                  Float4 lightPosition, float lightSize) {

        float uniform = lightSize * 2 / (sampleSize - 1);

        int raysHit = 0;
        int totalRays = sampleSize * sampleSize;

        for (float i = lightPosition.getX() - lightSize; i <= lightPosition.getX() + lightSize + 0.01F; i += uniform) {
            for (float j = lightPosition.getZ() - lightSize; j <= lightPosition.getZ() + lightSize + 0.01F; j += uniform) {
                Float4 samplePoint = new Float4(i, lightPosition.getY(), j, 0);
                Float4 rayDir = Float4.normalise(Float4.sub(samplePoint, hitPosition));
                Float4 rayOrigin = Float4.add(hitPosition, Float4.mult(rayDir, 0.001F));

                Float4 closestHit = Renderer.getClosestHit(bodyTypes, bodyPositions, bodySizes, rayOrigin, rayDir);

                if (closestHit.getW() != -1000F)
                    raysHit++;
            }
        }

        if (raysHit == 0) return 1;
        else return 1 + -raysHit / (totalRays * 1.3F);
    }
}