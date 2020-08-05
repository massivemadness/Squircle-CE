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
package org.angellee.gbk

import java.io.ByteArrayInputStream
import java.io.IOException
import kotlin.experimental.and
import org.angellee.CharsetDetector

/**
 * Adapted from https://github.com/angelLYK/smartCharsetDetector
 *
 * GB2312 或 GB2312-80 是中国国家标准简体中文字符集，共收录 6763 个简体汉字。
 * GBK 即汉字内码扩展规范，共收入 21886 个汉字和图形符号，包括 GB2312 中的简体汉字及 BIG5 中的繁体汉字。
 * GB18030 是中华人民共和国现时最新的内码字集，支持中国国内少数民族的文字，收录范围包含简体汉字、繁体汉字以及日韩汉字。
 */
class GBKCharsetDetector : CharsetDetector {
    override val charsetName: String
        get() = "GBK"

    /**
     * 范围	    第1字节	 第2字节	                       编码数 	字数                内容
     * GBK/1	 A1–A9	 A1–FE	        846	    717     GB2312非汉字符号
     * GBK/2	 B0–F7	 A1–FE	        6,768	6,763   GB2312 汉字
     * GBK/3	 81–A0	 40–FE(7F除外)	6,080	6,080         扩充汉字
     * GBK/4	 AA–FE	 40–A0(7F除外)	8,160	8,160         扩充汉字
     * GBK/5	 A8–A9	 40–A0(7F除外)	192	    166                扩充非汉字
     *
     *
     * 参考连接: http://www.bo56.com/gbk%E7%BC%96%E7%A0%81%E8%A1%A8%E4%B8%8E%E7%BC%96%E7%A0%81%E5%AD%97%E5%BA%93/
     * http://www.bo56.com/gbk%E6%B1%89%E5%AD%97%E5%86%85%E7%A0%81%E6%89%A9%E5%B1%95%E8%A7%84%E8%8C%83%E7%BC%96%E7%A0%81%E8%A1%A8/
     */
    override fun detect(inputStream: ByteArrayInputStream?): Boolean {
        if (inputStream == null) {
            return false
        }
        val buf = ByteArray(12288) // 12k
        var readCount: Int
        if (inputStream.read(buf, 0, buf.size).also { readCount = it } != -1) {
            var i = 0
            while (i < readCount) {
                val b0: Byte = buf[i] and 0xff.toByte() // 转换成无符号数
                if (b0 <= 0x7f) { // ascii, 单字节
                    i += 1
                    continue
                }

                // 以下都是双字节
                return if (i + 1 < buf.size) {
                    val b1: Byte = buf[i + 1] and 0xff.toByte() // 转换成无符号数
                    if (b0 in 0xa1..0xa9 &&
                        0xa1 <= b1 && b1 <= 0xfe
                    ) { // -> GBK/1
                        i += 2
                        continue
                    }
                    if (b0 in 0xb0..0xf7 &&
                        0xa1 <= b1 && b1 <= 0xfe
                    ) { // -> GBK/2
                        i += 2
                        continue
                    }
                    if (b0 in 0x81..0xa0 &&
                        0x40 <= b1 && b1 <= 0xfe
                    ) { // -> GBK/3
                        i += 2
                        continue
                    }
                    if (b0 in 0xaa..0xfe &&
                        0x40 <= b1 && b1 <= 0xa0
                    ) { // -> GBK/4
                        i += 2
                        continue
                    }
                    if (b0 in 0xa8..0xa9 &&
                        0x40 <= b1 && b1 <= 0xa0
                    ) { // -> GBK/5
                        i += 2
                        continue
                    }
                    false
                } else {
                    false
                }
            }
            if (i >= 12286) { // 防止最后结尾的时候出现错位
                return true
            }
        }
        return true
    }

    companion object {

        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val str = "1a12cvfgas我爱中国"
            val inputStream = ByteArrayInputStream(str.toByteArray(charset("GBK")))
            val detector = GBKCharsetDetector()
            println(detector.detect(inputStream))
        }
    }
}