/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.KillerBLS.modpeide.adapter.model;

import android.support.annotation.NonNull;

/**
 * @author Trần Lê Duy
 */
public class SuggestionItem implements Comparable<String> {

    @NonNull
    private String name;

    public SuggestionItem(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public int compareTo(@NonNull String o) {
        String s = o.toLowerCase();
        return getName().toLowerCase().startsWith(s) ? 0 : -1;
    }

    @Override
    public String toString() {
        return getName();
    }
}