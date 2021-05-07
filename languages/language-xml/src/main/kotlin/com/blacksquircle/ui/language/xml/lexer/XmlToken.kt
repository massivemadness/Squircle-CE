/*
 * Copyright 2021 Squircle IDE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.language.xml.lexer

enum class XmlToken {
    XML_TAG_NAME,
    XML_ATTR_NAME,

    XML_DOCTYPE_PUBLIC,
    XML_DOCTYPE_SYSTEM,
    XML_DOCTYPE_START,
    XML_DOCTYPE_END,

    XML_PI_START,
    XML_PI_END,
    XML_PI_TARGET,

    XML_DATA_CHARACTERS,
    XML_TAG_CHARACTERS,

    XML_EMPTY_ELEMENT_END,
    XML_TAG_END,

    XML_START_TAG_START,
    XML_END_TAG_START,

    XML_ATTRIBUTE_VALUE_TOKEN,
    XML_ATTRIBUTE_VALUE_START_DELIMITER,
    XML_ATTRIBUTE_VALUE_END_DELIMITER,

    XML_COMMENT_START,
    XML_COMMENT_END,
    XML_CONDITIONAL_COMMENT_START,
    XML_CONDITIONAL_COMMENT_START_END,
    XML_CONDITIONAL_COMMENT_END,
    XML_CONDITIONAL_COMMENT_END_START,
    XML_COMMENT_CHARACTERS,

    XML_CDATA_START,
    XML_CDATA_END,

    XML_CHAR_ENTITY_REF,
    XML_ENTITY_REF_TOKEN,

    WHITESPACE,
    BAD_CHARACTER,
    EOF
}