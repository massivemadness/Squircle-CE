/*
 * Copyright (c) 2019-2020 gzu-liyujiang <1032694760@qq.com>
 *
 * This file is licensed under the Apache License, Version 2.0
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
@file:Suppress("unused")

package org.angellee

import java.io.ByteArrayInputStream
import java.util.*
import org.angellee.gbk.GBKCharsetDetector
import org.angellee.utf8.UTF8CharsetDetector

/**
 * Auto-detect GB2312/GBK and UTF-8
 * Adapted from https://github.com/angelLYK/smartCharsetDetector
 */
object SmartCharsetDetector {
    private val detectors: ArrayList<CharsetDetector>? =
        createDetectors()

    /**
     * For extended charset detection program, you need to customize your own
     */
    @Synchronized
    fun registerDetector(detector: CharsetDetector) {
        if (detectors == null) {
            createDetectors()
        }
        detectors!!.add(detector)
    }

    fun detect(inputStream: ByteArrayInputStream): String? {
        try {
            for (detector in detectors!!) {
                if (detector.detect(inputStream)) {
                    try {
                        inputStream.close()
                    } catch (ignore: Throwable) {
                    }
                    return detector.charsetName
                }
            }
        } catch (ignore: Throwable) {
        }
        return null
    }

    private fun createDetectors(): ArrayList<CharsetDetector> {
        val detectors = ArrayList<CharsetDetector>()
        detectors.add(UTF8CharsetDetector())
        detectors.add(GBKCharsetDetector())
        return detectors
    }
}