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

package org.angellee;

import org.angellee.gbk.GBKCharsetDetector;
import org.angellee.utf8.UTF8CharsetDetector;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * 自动识别 GBK 及 UTF-8
 * Adapted from https://github.com/angelLYK/smartCharsetDetector
 */
@SuppressWarnings("unused")
public class SmartCharsetDetector {
    private static ArrayList<CharsetDetector> detectors = createDetectors();

    /**
     * 用于扩展字符集检测程序, 需要定制自己的
     */
    public synchronized static void registerDetector(CharsetDetector detector) {
        if (detectors == null) {
            createDetectors();
        }
        detectors.add(detector);
    }

    public static String detect(ByteArrayInputStream inputStream) {
        try {
            for (CharsetDetector detector : detectors) {
                if (detector.detect(inputStream)) {
                    try {
                        inputStream.close();
                    } catch (Throwable ignore) {
                    }
                    return detector.getCharsetName();
                }
            }
        } catch (Throwable ignore) {
        }
        return "";
    }

    private static ArrayList<CharsetDetector> createDetectors() {
        ArrayList<CharsetDetector> detectors = new ArrayList<>();
        detectors.add(new UTF8CharsetDetector());
        detectors.add(new GBKCharsetDetector());
        return detectors;
    }

}
