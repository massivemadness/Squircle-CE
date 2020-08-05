/*
 * Copyright (c) 2019-2020 gzu-liyujiang <1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v1.
 * You can use this software according to the terms and conditions of the Mulan PSL v1.
 * You may obtain a copy of Mulan PSL v1 at:
 *     http://license.coscl.org.cn/MulanPSL
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v1 for more details.
 *
 */

package org.angellee.utf8;

import org.angellee.CharsetDetector;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Adapted from https://github.com/angelLYK/smartCharsetDetector
 * <p>
 * UTF-8 编码相关资料，参阅：
 * https://baike.baidu.com/item/UTF-8
 * https://tools.ietf.org/html/rfc2277
 */
public class UTF8CharsetDetector implements CharsetDetector {

    @Override
    public String getCharsetName() {
        return "UTF-8";
    }

    /**
     * utf-8编码规则
     * <p>
     * 0xxxxxxx
     * 110xxxxx 10xxxxxx
     * 1110xxxx 10xxxxxx 10xxxxxx
     * 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     * 111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
     * 1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
     */
    @Override
    public boolean detect(ByteArrayInputStream in) {
        if (in == null) {
            return false;
        }

        byte[] buf = new byte[12288];
        int readCount;
        if ((readCount = in.read(buf, 0, buf.length)) != -1) {
            int i = 0;
            for (; i < readCount; ) {
                int b_0 = buf[i] & 0xff; //转换成无符号数
                if (b_0 <= 0x7f) {//ascii, 单字节
                    i = i + 1;
                    continue;
                }

                if (0xc0 <= b_0 && b_0 <= 0xdf) {//两个字节
                    if ((i + 1) < buf.length) {
                        int b_1 = buf[i + 1] & 0xff;//获取第二个字节
                        if (0x80 <= b_1 && b_1 <= 0xbf) {
                            i = i + 2;
                            continue;
                        }
                    }
                    return false;
                }

                if (0xe0 <= b_0 && b_0 <= 0xef) {//三个字节
                    if ((i + 2) < buf.length) {
                        int b_1 = buf[i + 1] & 0xff;
                        int b_2 = buf[i + 2] & 0xff;

                        if ((0x80 <= b_1 && b_1 <= 0xbf) && (0x80 <= b_2 && b_2 <= 0xbf)) {
                            i = i + 3;
                            continue;
                        }
                    }

                    return false;
                }

                if (0xf0 <= b_0 && b_0 <= 0xf7) {//四个字节
                    if ((i + 3) < buf.length) {
                        int b_1 = buf[i + 1] & 0xff;
                        int b_2 = buf[i + 2] & 0xff;
                        int b_3 = buf[i + 3] & 0xff;

                        if ((0x80 <= b_1 && b_1 <= 0xbf) && (0x80 <= b_2 && b_2 <= 0xbf) && (0x80 <= b_3 && b_3 <= 0xbf)) {
                            i = i + 3;
                            continue;
                        }
                    }
                    return false;
                }

                if (0xf8 <= b_0 && b_0 <= 0xfb) {//五个字节
                    if ((i + 4) < buf.length) {
                        int b_1 = buf[i + 1] & 0xff;
                        int b_2 = buf[i + 2] & 0xff;
                        int b_3 = buf[i + 3] & 0xff;
                        int b_4 = buf[i + 4] & 0xff;

                        if ((0x80 <= b_1 && b_1 <= 0xbf) &&
                                (0x80 <= b_2 && b_2 <= 0xbf) &&
                                (0x80 <= b_3 && b_3 <= 0xbf) &&
                                (0x80 <= b_4 && b_4 <= 0xbf)) {

                            i = i + 4;
                            continue;
                        }
                    }
                    return false;
                }

                if (0xfc <= b_0 && b_0 <= 0xfd) {//六个字节
                    if ((i + 5) < buf.length) {
                        int b_1 = buf[i + 1] & 0xff;
                        int b_2 = buf[i + 2] & 0xff;
                        int b_3 = buf[i + 3] & 0xff;
                        int b_4 = buf[i + 4] & 0xff;
                        int b_5 = buf[i + 4] & 0xff;

                        if ((0x80 <= b_1 && b_1 <= 0xbf) &&
                                (0x80 <= b_2 && b_2 <= 0xbf) &&
                                (0x80 <= b_3 && b_3 <= 0xbf) &&
                                (0x80 <= b_4 && b_4 <= 0xbf) &&
                                (0x80 <= b_5 && b_5 <= 0xbf)) {

                            i = i + 5;
                            continue;
                        }
                    }
                    return false;
                }

                return false;
            }

            if (i >= 12282) {//防止最后结尾的时候出现错位
                return true;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        String s = "1a12cvfgas我爱中国";
        ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        UTF8CharsetDetector detector = new UTF8CharsetDetector();
        System.out.println(detector.detect(in));
    }

}
