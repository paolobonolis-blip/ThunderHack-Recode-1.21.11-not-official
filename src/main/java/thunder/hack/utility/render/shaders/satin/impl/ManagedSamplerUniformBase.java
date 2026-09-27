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

import thunder.hack.utility.render.shaders.satin.api.managed.uniform.SamplerUniform;
import java.util.List;

public abstract class ManagedSamplerUniformBase extends ManagedUniformBase implements SamplerUniform {
    protected Object cachedValue;

    public ManagedSamplerUniformBase(String name) {
        super(name);
    }

    @Override
    public boolean findUniformTargets(List<?> shaders) {
        return false;
    }

    @Override
    public boolean findUniformTarget(Object shader) {
        return false;
    }

    protected abstract void set(Object value);

}
