/*
 * Copyright (C) 2017 Light Team Software
 *
 * This file is part of ModPE IDE.
 *
 * ModPE IDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ModPE IDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CompletionItem implements Comparable<String> {
    private int type;

    @NonNull
    private String name = "";

    @Nullable
    private String description = null;

    @Nullable
    private CharSequence show = null;

    private String compare = "";

    public CompletionItem(int type, @NonNull String name,
                          @Nullable String description, @Nullable String show) {
        this(type, name, description);
        this.show = show;
    }

    private CompletionItem(int type, @NonNull String name, @Nullable String description) {
        this(type, name);
        this.description = description;
        this.compare = name.toLowerCase();
    }

    public CompletionItem(int type, @NonNull String name) {
        this.name = name;
        this.type = type;
        this.compare = name.toLowerCase();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public CharSequence getShow() {
        return show;
    }

    public void setShow(@Nullable CharSequence show) {
        this.show = show;
    }

    @Override
    public int compareTo(@NonNull String o) {
        String s = o.toLowerCase();
        return compare.startsWith(s) ? 0 : -1;
    }

    @Override
    public String toString() {
        return name + " - " + description + " - " + show;
    }
}

