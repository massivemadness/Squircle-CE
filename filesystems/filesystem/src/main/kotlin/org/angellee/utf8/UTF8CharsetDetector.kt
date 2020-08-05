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
package org.angellee.utf8

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import kotlin.experimental.and
import org.angellee.CharsetDetector

/**
 * Adapted from https://github.com/angelLYK/smartCharsetDetector
 *
 * UTF-8 编码相关资料，参阅：
 * https://baike.baidu.com/item/UTF-8
 * https://tools.ietf.org/html/rfc2277
 */
class UTF8CharsetDetector : CharsetDetector {
    override val charsetName: String
        get() = "UTF-8"

    /**
     * utf-8编码规则
     *
     *
     * 0xxxxxxx
     * 110xxxxx 10xxxxxx
     * 1110xxxx 10xxxxxx 10xxxxxx
     * 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     * 111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
     * 1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
     */
    override fun detect(inputStream: ByteArrayInputStream?): Boolean {
        if (inputStream == null) {
            return false
        }
        val buf = ByteArray(12288)
        var readCount: Int
        if (inputStream.read(buf, 0, buf.size).also { readCount = it } != -1) {
            var i = 0
            while (i < readCount) {
                val b0: Byte = buf[i] and 0xff.toByte() // 转换成无符号数
                if (b0 <= 0x7f) { // ascii, 单字节
                    i += 1
                    continue
                }
                if (b0 in 0xc0..0xdf) { // 两个字节
                    if (i + 1 < buf.size) {
                        val b1: Byte = buf[i + 1] and 0xff.toByte() // 获取第二个字节
                        if (b1 in 0x80..0xbf) {
                            i += 2
                            continue
                        }
                    }
                    return false
                }
                if (b0 in 0xe0..0xef) { // 三个字节
                    if (i + 2 < buf.size) {
                        val b1: Byte = buf[i + 1] and 0xff.toByte()
                        val b2: Byte = buf[i + 2] and 0xff.toByte()
                        if (b1 in 0x80..0xbf && 0x80 <= b2 && b2 <= 0xbf) {
                            i += 3
                            continue
                        }
                    }
                    return false
                }
                if (b0 in 0xf0..0xf7) { // 四个字节
                    if (i + 3 < buf.size) {
                        val b1: Byte = buf[i + 1] and 0xff.toByte()
                        val b2: Byte = buf[i + 2] and 0xff.toByte()
                        val b3: Byte = buf[i + 3] and 0xff.toByte()
                        if (b1 in 0x80..0xbf && 0x80 <= b2 && b2 <= 0xbf && 0x80 <= b3 && b3 <= 0xbf) {
                            i += 3
                            continue
                        }
                    }
                    return false
                }
                if (b0 in 0xf8..0xfb) { // 五个字节
                    if (i + 4 < buf.size) {
                        val b1: Byte = buf[i + 1] and 0xff.toByte()
                        val b2: Byte = buf[i + 2] and 0xff.toByte()
                        val b3: Byte = buf[i + 3] and 0xff.toByte()
                        val b4: Byte = buf[i + 4] and 0xff.toByte()
                        if (b1 in 0x80..0xbf &&
                            0x80 <= b2 && b2 <= 0xbf &&
                            0x80 <= b3 && b3 <= 0xbf &&
                            0x80 <= b4 && b4 <= 0xbf
                        ) {
                            i += 4
                            continue
                        }
                    }
                    return false
                }
                if (b0 in 0xfc..0xfd) { // 六个字节
                    if (i + 5 < buf.size) {
                        val b1: Byte = buf[i + 1] and 0xff.toByte()
                        val b2: Byte = buf[i + 2] and 0xff.toByte()
                        val b3: Byte = buf[i + 3] and 0xff.toByte()
                        val b4: Byte = buf[i + 4] and 0xff.toByte()
                        val b5: Byte = buf[i + 4] and 0xff.toByte()
                        if (b1 in 0x80..0xbf &&
                            0x80 <= b2 && b2 <= 0xbf &&
                            0x80 <= b3 && b3 <= 0xbf &&
                            0x80 <= b4 && b4 <= 0xbf &&
                            0x80 <= b5 && b5 <= 0xbf
                        ) {
                            i += 5
                            continue
                        }
                    }
                    return false
                }
                return false
            }
            if (i >= 12282) { // 防止最后结尾的时候出现错位
                return true
            }
        }
        return true
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val str = "1a12cvfgas我爱中国"
            val inputStream = ByteArrayInputStream(str.toByteArray(StandardCharsets.UTF_8))
            val detector = UTF8CharsetDetector()
            println(detector.detect(inputStream))
        }
    }
}