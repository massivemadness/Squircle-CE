package com.blacksquircle.ui.language.lua.lexer;

@SuppressWarnings("all")
%%

%public
%class LuaLexer
%type LuaToken
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

  private int nBrackets = 0;

  private boolean checkAhead(char c, int offset) {
      return this.zzMarkedPos + offset < this.zzBuffer.length && this.zzBuffer[(this.zzMarkedPos + offset)] == c;
  }

  private boolean checkBlock() {
      nBrackets = 0;
      if (checkAhead('[', 0)) {
          int n = 0;
          while (checkAhead('=', n + 1)) n++;
          if (checkAhead('[', n + 1)) {
              nBrackets = n;
              return true;
          }
      }
      return false;
  }

  private int checkBlockEnd() {
      int pos = zzMarkedPos;
      int end = zzEndRead;
      while(pos < end) {
          char c = zzBuffer[pos];
          if (c == ']') {
              pos++;
              int size = 0;
              while (pos < zzEndRead && zzBuffer[pos] == '=') {
                  size++;
                  pos++;
              }
              if (size == nBrackets && pos < zzEndRead && zzBuffer[pos] == ']') {
                  pos++;
                  break;
              }
              continue;
          }
          pos++;
      }
      return pos - zzMarkedPos;
  }
%}

IDENTIFIER = [:jletter:] [:jletterdigit:]*

DIGIT = [0-9]
DIGIT_OR_UNDERSCORE = [_0-9]
DIGITS = {DIGIT} | {DIGIT} {DIGIT_OR_UNDERSCORE}*
HEX_DIGIT_OR_UNDERSCORE = [_0-9A-Fa-f]

INTEGER_LITERAL = {DIGITS} | {HEX_INTEGER_LITERAL} | {BIN_INTEGER_LITERAL}
LONG_LITERAL = {INTEGER_LITERAL} [Ll]
HEX_INTEGER_LITERAL = 0 [Xx] {HEX_DIGIT_OR_UNDERSCORE}*
BIN_INTEGER_LITERAL = 0 [Bb] {DIGIT_OR_UNDERSCORE}*

FLOAT_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Ff] | {DIGITS} [Ff]
DOUBLE_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Dd]? | {DIGITS} [Dd]
DEC_FP_LITERAL = {DIGITS} {DEC_EXPONENT} | {DEC_SIGNIFICAND} {DEC_EXPONENT}?
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT_OR_UNDERSCORE}*
DEC_EXPONENT = [Ee] [+-]? {DIGIT_OR_UNDERSCORE}*
HEX_FP_LITERAL = {HEX_SIGNIFICAND} {HEX_EXPONENT}
HEX_SIGNIFICAND = 0 [Xx] ({HEX_DIGIT_OR_UNDERSCORE}+ "."? | {HEX_DIGIT_OR_UNDERSCORE}* "." {HEX_DIGIT_OR_UNDERSCORE}+)
HEX_EXPONENT = [Pp] [+-]? {DIGIT_OR_UNDERSCORE}*

CRLF = [\ \t \f]* \R
DOUBLE_QUOTED_STRING = \"([^\\\"\r\n] | \\[^\r\n] | \\{CRLF})*\"?
SINGLE_QUOTED_STRING = '([^\\'\r\n] | \\[^\r\n] | \\{CRLF})*'?

LINE_TERMINATOR = \r|\n|\r\n
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]

LINE_COMMENT = "--".*
BLOCK_COMMENT = --\[=*\[[\s\S]*(\]=*\])?

%state COMMENT

%%

<YYINITIAL> {

  {LONG_LITERAL} { return LuaToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return LuaToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return LuaToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return LuaToken.DOUBLE_LITERAL; }

  "break" { return LuaToken.BREAK; }
  "do" { return LuaToken.DO; }
  "else" { return LuaToken.ELSE; }
  "elseif" { return LuaToken.ELSEIF; }
  "end" { return LuaToken.END; }
  "for" { return LuaToken.FOR; }
  "function" { return LuaToken.FUNCTION; }
  "goto" { return LuaToken.GOTO; }
  "if" { return LuaToken.IF; }
  "in" { return LuaToken.IN; }
  "local" { return LuaToken.LOCAL; }
  "nil" { return LuaToken.NIL; }
  "repeat" { return LuaToken.REPEAT; }
  "return" { return LuaToken.RETURN; }
  "then" { return LuaToken.THEN; }
  "until" { return LuaToken.UNTIL; }
  "while" { return LuaToken.WHILE; }
  "and" { return LuaToken.AND; }
  "or" { return LuaToken.OR; }
  "not" { return LuaToken.NOT; }

  "true" { return LuaToken.TRUE; }
  "false" { return LuaToken.FALSE; }
  "null" { return LuaToken.NULL; }

  "_G" { return LuaToken._G; }
  "_VERSION" { return LuaToken._VERSION; }
  "assert" { return LuaToken.ASSERT; }
  "collectgarbage" { return LuaToken.COLLECTGARBAGE; }
  "dofile" { return LuaToken.DOFILE; }
  "error" { return LuaToken.ERROR; }
  "getfenv" { return LuaToken.GETFENV; }
  "getmetatable" { return LuaToken.GETMETATABLE; }
  "ipairs" { return LuaToken.IPAIRS; }
  "load" { return LuaToken.LOAD; }
  "loadfile" { return LuaToken.LOADFILE; }
  "loadstring" { return LuaToken.LOADSTRING; }
  "module" { return LuaToken.MODULE; }
  "next" { return LuaToken.NEXT; }
  "pairs" { return LuaToken.PAIRS; }
  "pcall" { return LuaToken.PCALL; }
  "print" { return LuaToken.PRINT; }
  "rawequal" { return LuaToken.RAWEQUAL; }
  "rawget" { return LuaToken.RAWGET; }
  "rawset" { return LuaToken.RAWSET; }
  "require" { return LuaToken.REQUIRE; }
  "select" { return LuaToken.SELECT; }
  "setfenv" { return LuaToken.SETFENV; }
  "setmetatable" { return LuaToken.SETMETATABLE; }
  "tonumber" { return LuaToken.TONUMBER; }
  "tostring" { return LuaToken.TOSTRING; }
  "type" { return LuaToken.TYPE; }
  "unpack" { return LuaToken.UNPACK; }
  "xpcall" { return LuaToken.XPCALL; }

  "(" { return LuaToken.LPAREN; }
  ")" { return LuaToken.RPAREN; }
  "{" { return LuaToken.LBRACE; }
  "}" { return LuaToken.RBRACE; }
  "[" {
        if (checkAhead('=', 0) || checkAhead('[', 0)) {
            yypushback(yylength());
            checkBlock();
            zzMarkedPos += checkBlockEnd();
            return LuaToken.DOUBLE_QUOTED_STRING;
        } else {
            return LuaToken.LBRACK;
        }
      }
  "]" { return LuaToken.RBRACK; }
  ";" { return LuaToken.SEMICOLON; }
  "," { return LuaToken.COMMA; }
  "." { return LuaToken.DOT; }

  "--" {
          boolean block = checkBlock();
          if (block) {
              yypushback(yylength());
              zzMarkedPos += checkBlockEnd();
              return LuaToken.BLOCK_COMMENT;
          } else {
              yypushback(yylength());
              yybegin(COMMENT);
          }
      }

  "<" { return LuaToken.LT; }
  ">" { return LuaToken.GT; }
  "<=" { return LuaToken.LTEQ; }
  ">=" { return LuaToken.GTEQ; }
  "==" { return LuaToken.EQEQ; }
  "~=" { return LuaToken.TILDEEQ; }
  ".." { return LuaToken.CONCAT; }
  "=" { return LuaToken.EQ; }
  "!" { return LuaToken.NOT_OPERATOR; }
  "~" { return LuaToken.TILDE; }
  ":" { return LuaToken.COLON; }
  "+" { return LuaToken.PLUS; }
  "-" { return LuaToken.MINUS; }
  "*" { return LuaToken.MULT; }
  "/" { return LuaToken.DIV; }
  "|" { return LuaToken.OR; }
  "^" { return LuaToken.XOR; }
  "%" { return LuaToken.MOD; }
  "?" { return LuaToken.QUEST; }

  {DOUBLE_QUOTED_STRING} { return LuaToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return LuaToken.SINGLE_QUOTED_STRING; }

  {IDENTIFIER} { return LuaToken.IDENTIFIER; }
  {WHITESPACE} { return LuaToken.WHITESPACE; }
}

<COMMENT> {
    {LINE_COMMENT} { yybegin(YYINITIAL); return LuaToken.LINE_COMMENT; }
    {BLOCK_COMMENT} { yybegin(YYINITIAL); return LuaToken.BLOCK_COMMENT; }
}

[^] { return LuaToken.BAD_CHARACTER; }

<<EOF>> { return LuaToken.EOF; }