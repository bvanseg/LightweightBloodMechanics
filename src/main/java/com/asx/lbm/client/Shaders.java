package com.asx.lbm.client;

import java.util.ArrayList;
import java.util.List;

import com.asx.lbm.LBM;
import com.asx.lbm.util.Reflection;
import com.asx.mdx.lib.util.Game;
import com.asx.mdx.lib.util.Reflect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.Shader;
import net.minecraft.util.ResourceLocation;

public class Shaders
{
    public static final ResourceLocation LIGHTHEADED = new ResourceLocation(LBM.Properties.ID, "shaders/post/lightheaded.json");
    public static final Minecraft        MC          = Game.minecraft();

    public static void enableLightheaded(EntityRenderer renderer)
    {
        if (renderer.getShaderGroup() == null || renderer.getShaderGroup() != null && !renderer.getShaderGroup().getShaderGroupName().equalsIgnoreCase(LIGHTHEADED.toString()))
        {
            renderer.loadShader(Shaders.LIGHTHEADED);

            if (!LBM.settings().isPhosphorBlurringEnabled())
            {
                Object obj = Reflect.get(renderer.getShaderGroup(), Reflection.Fields.ShaderGroup.listShadersDeobf, Reflection.Fields.ShaderGroup.listShadersObf);

                if (obj instanceof List)
                {
                    List<Shader> listShaders = (List<Shader>) obj;

                    for (Shader shader : new ArrayList<Shader>(listShaders))
                    {
                        String programFilename = (String) Reflect.get(shader.getShaderManager(), Reflection.Fields.ShaderManager.programFilenameDeobf, Reflection.Fields.ShaderManager.programFilenameObf);

                        if (programFilename.equalsIgnoreCase("phosphor"))
                        {
                            listShaders.remove(shader);
                        }
                    }

                    Reflect.set(renderer.getShaderGroup(), Reflection.Fields.ShaderGroup.listShadersDeobf, Reflection.Fields.ShaderGroup.listShadersObf, listShaders);
                }
            }
        }
    }

    public static void disable()
    {
        MC.entityRenderer.stopUseShader();
    }
}
