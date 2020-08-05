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

package org.angellee.gbk;

import org.angellee.CharsetDetector;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Adapted from https://github.com/angelLYK/smartCharsetDetector
 * <p>
 * GB2312 或 GB2312-80 是中国国家标准简体中文字符集，共收录 6763 个简体汉字。
 * GBK 即汉字内码扩展规范，共收入 21886 个汉字和图形符号，包括 GB2312 中的简体汉字及 BIG5 中的繁体汉字。
 * GB18030 是中华人民共和国现时最新的内码字集，支持中国国内少数民族的文字，收录范围包含简体汉字、繁体汉字以及日韩汉字。
 */
public class GBKCharsetDetector implements CharsetDetector {

    @Override
    public String getCharsetName() {
        return "GBK";
    }

    /**
     * 范围	    第1字节	 第2字节	                       编码数 	字数                内容
     * GBK/1	 A1–A9	 A1–FE	        846	    717     GB2312非汉字符号
     * GBK/2	 B0–F7	 A1–FE	        6,768	6,763   GB2312 汉字
     * GBK/3	 81–A0	 40–FE(7F除外)	6,080	6,080         扩充汉字
     * GBK/4	 AA–FE	 40–A0(7F除外)	8,160	8,160         扩充汉字
     * GBK/5	 A8–A9	 40–A0(7F除外)	192	    166                扩充非汉字
     * <p>
     * 参考连接: http://www.bo56.com/gbk%E7%BC%96%E7%A0%81%E8%A1%A8%E4%B8%8E%E7%BC%96%E7%A0%81%E5%AD%97%E5%BA%93/
     * http://www.bo56.com/gbk%E6%B1%89%E5%AD%97%E5%86%85%E7%A0%81%E6%89%A9%E5%B1%95%E8%A7%84%E8%8C%83%E7%BC%96%E7%A0%81%E8%A1%A8/
     */
    @Override
    public boolean detect(ByteArrayInputStream in) {
        if (in == null) {
            return false;
        }

        byte[] buf = new byte[12288];//12k
        int readCount;
        if ((readCount = in.read(buf, 0, buf.length)) != -1) {
            int i = 0;
            for (; i < readCount; ) {
                int b_0 = buf[i] & 0xff; //转换成无符号数
                if (b_0 <= 0x7f) {//ascii, 单字节
                    i = i + 1;
                    continue;
                }

                //以下都是双字节
                if ((i + 1) < buf.length) {
                    int b_1 = buf[i + 1] & 0xff; //转换成无符号数
                    if ((0xa1 <= b_0 && b_0 <= 0xa9) &&
                            (0xa1 <= b_1 && b_1 <= 0xfe)) {//-> GBK/1

                        i = i + 2;
                        continue;
                    }

                    if ((0xb0 <= b_0 && b_0 <= 0xf7) &&
                            (0xa1 <= b_1 && b_1 <= 0xfe)) {//-> GBK/2

                        i = i + 2;
                        continue;
                    }

                    if ((0x81 <= b_0 && b_0 <= 0xa0) &&
                            (0x40 <= b_1 && b_1 <= 0xfe)) {//-> GBK/3

                        i = i + 2;
                        continue;
                    }

                    if ((0xaa <= b_0 && b_0 <= 0xfe) &&
                            (0x40 <= b_1 && b_1 <= 0xa0)) {//-> GBK/4

                        i = i + 2;
                        continue;
                    }

                    if ((0xa8 <= b_0 && b_0 <= 0xa9) &&
                            (0x40 <= b_1 && b_1 <= 0xa0)) {//-> GBK/5

                        i = i + 2;
                        continue;
                    }

                    return false;
                } else {
                    return false;
                }
            }

            if (i >= 12286) {//防止最后结尾的时候出现错位
                return true;
            }
        }

        return true;
    }

    public static void main(String[] args) throws IOException {
        String s = "1a12cvfgas我爱中国";
        ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes("gbk"));
        GBKCharsetDetector detector = new GBKCharsetDetector();
        System.out.println(detector.detect(in));
    }

}
