package com.blacksquircle.ui.language.json.lexer;

@SuppressWarnings("all")
%%

%public
%class JsonLexer
%type JsonToken
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

EOL = \R
WHITESPACE = \s+

LINE_COMMENT = "//".*
BLOCK_COMMENT = "/"\*([^*] | \*+[^*/])*(\*+"/")?

LINE_TERMINATOR_SEQUENCE = \R
CRLF = [\ \t \f]* {LINE_TERMINATOR_SEQUENCE}
DOUBLE_QUOTED_STRING = \"([^\\\"\r\n] | \\[^\r\n] | \\{CRLF})*\"?
SINGLE_QUOTED_STRING = '([^\\'\r\n] | \\[^\r\n] | \\{CRLF})*'?

JSON5_NUMBER = (\+|-)?(0|[1-9][0-9]*)?\.?([0-9]+)?([eE][+-]?[0-9]*)?
HEX_DIGIT = [0-9A-Fa-f]
HEX_DIGITS = ({HEX_DIGIT})+
HEX_INTEGER_LITERAL = (\+|-)?0[Xx]({HEX_DIGITS})
NUMBER = {JSON5_NUMBER} | {HEX_INTEGER_LITERAL} | Infinity | -Infinity | \+Infinity | NaN | -NaN | \+NaN

IDENTIFIER = [[:jletterdigit:]~!()*\-."/"@\^<>=]+

%%

<YYINITIAL> {

  {WHITESPACE}                { return JsonToken.WHITESPACE; }

  "{"                         { return JsonToken.LBRACE; }
  "}"                         { return JsonToken.RBRACE; }
  "["                         { return JsonToken.LBRACK; }
  "]"                         { return JsonToken.RBRACK; }
  ","                         { return JsonToken.COMMA; }
  ":"                         { return JsonToken.COLON; }

  "true"                      { return JsonToken.TRUE; }
  "false"                     { return JsonToken.FALSE; }
  "null"                      { return JsonToken.NULL; }

  {LINE_COMMENT}              { return JsonToken.LINE_COMMENT; }
  {BLOCK_COMMENT}             { return JsonToken.BLOCK_COMMENT; }
  {DOUBLE_QUOTED_STRING}      { return JsonToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING}      { return JsonToken.SINGLE_QUOTED_STRING; }
  {NUMBER}                    { return JsonToken.NUMBER; }
  {IDENTIFIER}                { return JsonToken.IDENTIFIER; }
}

[^] { return JsonToken.BAD_CHARACTER; }

<<EOF>> { return JsonToken.EOF; }