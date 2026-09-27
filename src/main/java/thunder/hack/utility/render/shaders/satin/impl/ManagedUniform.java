/*
 * Satin
 * Copyright (C) 2019-2024 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package thunder.hack.utility.render.shaders.satin.impl;

import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform1f;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform1i;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform2f;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform2i;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform3f;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform3i;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform4f;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform4i;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.UniformMat4;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import java.util.List;

public final class ManagedUniform extends ManagedUniformBase implements
        Uniform1i, Uniform2i, Uniform3i, Uniform4i,
        Uniform1f, Uniform2f, Uniform3f, Uniform4f,
        UniformMat4 {

    private final int count;
    private int i0, i1, i2, i3;
    private float f0, f1, f2, f3;

    public ManagedUniform(String name, int count) {
        super(name);
        this.count = count;
    }

    @Override
    public boolean findUniformTargets(List<?> shaders) {
        return false;
    }

    @Override
    public boolean findUniformTarget(Object shader) {
        return false;
    }

    @Override
    public void set(int value) {
        i0 = value;
    }

    @Override
    public void set(int value0, int value1) {
        i0 = value0;
        i1 = value1;
    }

    @Override
    public void set(int value0, int value1, int value2) {
        i0 = value0;
        i1 = value1;
        i2 = value2;
    }

    @Override
    public void set(int value0, int value1, int value2, int value3) {
        i0 = value0;
        i1 = value1;
        i2 = value2;
        i3 = value3;
    }

    @Override
    public void set(float value) {
        f0 = value;
    }

    @Override
    public void set(float value0, float value1) {
        f0 = value0;
        f1 = value1;
    }

    @Override
    public void set(Vector2f value) {
        set(value.x(), value.y());
    }

    @Override
    public void set(float value0, float value1, float value2) {
        f0 = value0;
        f1 = value1;
        f2 = value2;
    }

    @Override
    public void set(Vector3f value) {
        set(value.x(), value.y(), value.z());
    }

    @Override
    public void set(float value0, float value1, float value2, float value3) {
        f0 = value0;
        f1 = value1;
        f2 = value2;
        f3 = value3;
    }

    @Override
    public void set(Vector4f value) {
        set(value.x(), value.y(), value.z(), value.w());
    }

    @Override
    public void set(Matrix4f value) {
    }

    @Override
    public void setFromArray(float[] values) {
        if (this.count != values.length) {
            throw new IllegalArgumentException("Mismatched values size, expected " + count + " but got " + values.length);
        }
    }
}