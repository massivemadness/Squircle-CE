package com.blacksquircle.ui.language.html.lexer;

@SuppressWarnings("all")
%%

%public
%class HtmlLexer
%type HtmlToken
%function advance
%unicode
%line
%column
%char

%{
  public final int getTokenStart() {
      return (int) yychar;
  }

  public final int getTokenEnd() {
      return getTokenStart() + yylength();
  }
%}

%state DOC_TYPE
%state COMMENT
%state START_TAG_NAME
%state END_TAG_NAME
%state BEFORE_TAG_ATTRIBUTES
%state TAG_ATTRIBUTES
%state ATTRIBUTE_VALUE_START
%state ATTRIBUTE_VALUE_DQ
%state ATTRIBUTE_VALUE_SQ
%state PROCESSING_INSTRUCTION
%state TAG_CHARACTERS
%state C_COMMENT_START
%state C_COMMENT_END

ALPHA = [:letter:]
DIGIT = [0-9]
WHITESPACE = [ \n\r\t\f\u2028\u2029\u0085]+

TAG_NAME = ({ALPHA}|"_"|":")({ALPHA}|{DIGIT}|"_"|":"|"."|"-")*
ATTRIBUTE_NAME = ([^ \n\r\t\f\"\'<>/])+

DTD_REF = "\"" [^\"]* "\"" | "'" [^']* "'"
DOCTYPE = "<!" (D|d)(O|o)(C|c)(T|t)(Y|y)(P|p)(E|e)
HTML = (H|h)(T|t)(M|m)(L|l)
PUBLIC = (P|p)(U|u)(B|b)(L|l)(I|i)(C|c)
END_COMMENT = "-->"

CONDITIONAL_COMMENT_CONDITION = ({ALPHA})({ALPHA}|{WHITESPACE}|{DIGIT}|"."|"("|")"|"|"|"!"|"&")*

%%

<YYINITIAL> {
    {DOCTYPE} { yybegin(DOC_TYPE); return HtmlToken.XML_DOCTYPE_START; }

    "<?" { yybegin(PROCESSING_INSTRUCTION); return HtmlToken.XML_PI_START; }
    "<" {TAG_NAME} { yybegin(START_TAG_NAME); yypushback(yylength()); }
    "</" {TAG_NAME} { yybegin(END_TAG_NAME); yypushback(yylength()); }

    "<!--" { yybegin(COMMENT); return HtmlToken.XML_COMMENT_START; }
    "</" { return HtmlToken.XML_END_TAG_START; }

    \\\$ { return HtmlToken.XML_DATA_CHARACTERS; }
    ([^<&\$# \n\r\t\f]|(\\\$)|(\\#))* { return HtmlToken.XML_DATA_CHARACTERS; }

    {WHITESPACE} { return HtmlToken.WHITESPACE; }

    [^] { return HtmlToken.XML_DATA_CHARACTERS; }
}

<DOC_TYPE> {
    ">" { yybegin(YYINITIAL); return HtmlToken.XML_DOCTYPE_END; }

    {HTML} { return HtmlToken.XML_TAG_NAME; }
    {PUBLIC} { return HtmlToken.XML_DOCTYPE_PUBLIC; }
    {DTD_REF} { return HtmlToken.XML_ATTRIBUTE_VALUE_TOKEN;}
    {WHITESPACE} { return HtmlToken.WHITESPACE; }
}

<PROCESSING_INSTRUCTION> {
    "?"? ">" { yybegin(YYINITIAL); return HtmlToken.XML_PI_END; }
    ([^\?\>] | (\?[^\>]))* { return HtmlToken.XML_PI_TARGET; }

    {WHITESPACE} { return HtmlToken.WHITESPACE; }
}

<TAG_ATTRIBUTES> {
    ">" { yybegin(YYINITIAL); return HtmlToken.XML_TAG_END; }
    "/>" { yybegin(YYINITIAL); return HtmlToken.XML_EMPTY_ELEMENT_END; }
    \" { yybegin(ATTRIBUTE_VALUE_START); yypushback(1); }
    \' { yybegin(ATTRIBUTE_VALUE_START); yypushback(1); }

    {ATTRIBUTE_NAME} { return HtmlToken.XML_ATTR_NAME; }
    {WHITESPACE} { return HtmlToken.WHITESPACE; }

    [^] { yybegin(YYINITIAL); yypushback(1); break; }
}

<START_TAG_NAME> {
    "<" { return HtmlToken.XML_START_TAG_START; }

    {TAG_NAME} { yybegin(BEFORE_TAG_ATTRIBUTES); return HtmlToken.XML_TAG_NAME; }
    {WHITESPACE} { return HtmlToken.WHITESPACE; }

    [^] { yybegin(YYINITIAL); yypushback(1); break; }
}

<END_TAG_NAME> {
    "</" { return HtmlToken.XML_END_TAG_START; }

     {TAG_NAME} { yybegin(BEFORE_TAG_ATTRIBUTES); return HtmlToken.XML_TAG_NAME; }
     {WHITESPACE} { return HtmlToken.WHITESPACE; }

    [^] { yybegin(YYINITIAL); yypushback(1); break; }
}

<BEFORE_TAG_ATTRIBUTES> {
    ">" { yybegin(YYINITIAL); return HtmlToken.XML_TAG_END; }
    "/>" { yybegin(YYINITIAL); return HtmlToken.XML_EMPTY_ELEMENT_END; }

    {WHITESPACE} { yybegin(TAG_ATTRIBUTES); return HtmlToken.WHITESPACE; }

    [^] { yybegin(YYINITIAL); yypushback(1); break; }
}

<TAG_CHARACTERS> {
    "<" { return HtmlToken.XML_START_TAG_START; }
    ">" { yybegin(YYINITIAL); return HtmlToken.XML_TAG_END; }
    "/>" { yybegin(YYINITIAL); return HtmlToken.XML_EMPTY_ELEMENT_END; }

    {WHITESPACE} { return HtmlToken.WHITESPACE; }

    [^] { return HtmlToken.XML_TAG_CHARACTERS; }
}

<ATTRIBUTE_VALUE_START> {
    ">" { yybegin(YYINITIAL); return HtmlToken.XML_TAG_END; }
    "/>" { yybegin(YYINITIAL); return HtmlToken.XML_EMPTY_ELEMENT_END; }

    [^ \n\r\t\f'\"\>]([^ \n\r\t\f\>]|(\/[^\>]))* { yybegin(TAG_ATTRIBUTES); return HtmlToken.XML_ATTRIBUTE_VALUE_TOKEN; }
    "\"" { yybegin(ATTRIBUTE_VALUE_DQ); return HtmlToken.XML_ATTRIBUTE_VALUE_START_DELIMITER; }
    "'" { yybegin(ATTRIBUTE_VALUE_SQ); return HtmlToken.XML_ATTRIBUTE_VALUE_START_DELIMITER; }

    {WHITESPACE} { return HtmlToken.WHITESPACE; }
}

<ATTRIBUTE_VALUE_DQ> {
    "\"" { yybegin(TAG_ATTRIBUTES); return HtmlToken.XML_ATTRIBUTE_VALUE_END_DELIMITER; }
    \\\$ { return HtmlToken.XML_ATTRIBUTE_VALUE_TOKEN; }

    [^] { return HtmlToken.XML_ATTRIBUTE_VALUE_TOKEN; }
}

<ATTRIBUTE_VALUE_SQ> {
    "'" { yybegin(TAG_ATTRIBUTES); return HtmlToken.XML_ATTRIBUTE_VALUE_END_DELIMITER; }
    \\\$ { return HtmlToken.XML_ATTRIBUTE_VALUE_TOKEN; }

    [^] { return HtmlToken.XML_ATTRIBUTE_VALUE_TOKEN; }
}

<COMMENT> {
    "[" { yybegin(C_COMMENT_START); return HtmlToken.XML_CONDITIONAL_COMMENT_START; }
    "<![" { yybegin(C_COMMENT_END); return HtmlToken.XML_CONDITIONAL_COMMENT_END_START; }
    "<!--" { return HtmlToken.BAD_CHARACTER; }
    "<!--->" | "--!>" { yybegin(YYINITIAL); return HtmlToken.BAD_CHARACTER; }
    ">" {
          int loc = getTokenStart();
          char prev = zzBuffer[loc - 1];
          char prevPrev = zzBuffer[loc - 2];
          if (prev == '-' && prevPrev == '-') {
              yybegin(YYINITIAL); return HtmlToken.BAD_CHARACTER;
          }
          return HtmlToken.XML_COMMENT_CHARACTERS;
      }

    {END_COMMENT} | "<!-->" { yybegin(YYINITIAL); return HtmlToken.XML_COMMENT_END; }

    [^] { return HtmlToken.XML_COMMENT_CHARACTERS; }
}

<C_COMMENT_START> {
    "]>" { yybegin(COMMENT); return HtmlToken.XML_CONDITIONAL_COMMENT_START_END; }

    {CONDITIONAL_COMMENT_CONDITION} { return HtmlToken.XML_COMMENT_CHARACTERS; }
    {END_COMMENT} { yybegin(YYINITIAL); return HtmlToken.XML_COMMENT_END; }

    [^] { yybegin(COMMENT); return HtmlToken.XML_COMMENT_CHARACTERS; }
}

<C_COMMENT_END> {
    "]" { yybegin(COMMENT); return HtmlToken.XML_CONDITIONAL_COMMENT_END; }

    {CONDITIONAL_COMMENT_CONDITION} { return HtmlToken.XML_COMMENT_CHARACTERS; }
    {END_COMMENT} { yybegin(YYINITIAL); return HtmlToken.XML_COMMENT_END; }

    [^] { yybegin(COMMENT); return HtmlToken.XML_COMMENT_CHARACTERS; }
}

"&lt;" { return HtmlToken.XML_CHAR_ENTITY_REF; }
"&gt;" { return HtmlToken.XML_CHAR_ENTITY_REF; }
"&apos;" { return HtmlToken.XML_CHAR_ENTITY_REF; }
"&quot;" { return HtmlToken.XML_CHAR_ENTITY_REF; }
"&nbsp;" { return HtmlToken.XML_CHAR_ENTITY_REF; }
"&amp;" { return HtmlToken.XML_CHAR_ENTITY_REF; }
"&#"{DIGIT}+";" { return HtmlToken.XML_CHAR_ENTITY_REF; }
"&#"[xX]({DIGIT}|[a-fA-F])+";" { return HtmlToken.XML_CHAR_ENTITY_REF; }

"&"{TAG_NAME}";" { return HtmlToken.XML_ENTITY_REF_TOKEN; }

[^] { return HtmlToken.BAD_CHARACTER; }

<<EOF>> { return HtmlToken.EOF; }