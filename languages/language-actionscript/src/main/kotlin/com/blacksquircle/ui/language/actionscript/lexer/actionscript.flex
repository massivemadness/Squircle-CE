package com.blacksquircle.ui.language.actionscript.lexer;

@SuppressWarnings("all")
%%

%public
%class ActionScriptLexer
%type ActionScriptToken
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

PREPROCESSOR = ({WHITESPACE}?"#"{WHITESPACE}?[a-zA-Z]+([^\n])*)

LINE_COMMENT = "//".*
BLOCK_COMMENT = "/"\*([^*] | \*+[^*/])*(\*+"/")?

%%

<YYINITIAL> {

  {LONG_LITERAL} { return ActionScriptToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return ActionScriptToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return ActionScriptToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return ActionScriptToken.DOUBLE_LITERAL; }

  "break" { return ActionScriptToken.BREAK; }
  "case" { return ActionScriptToken.CASE; }
  "continue" { return ActionScriptToken.CONTINUE; }
  "default" { return ActionScriptToken.DEFAULT; }
  "do" { return ActionScriptToken.DO; }
  "while" { return ActionScriptToken.WHILE; }
  "else" { return ActionScriptToken.ELSE; }
  "for" { return ActionScriptToken.FOR; }
  "in" { return ActionScriptToken.IN; }
  "each" { return ActionScriptToken.EACH; }
  "if" { return ActionScriptToken.IF; }
  "label" { return ActionScriptToken.LABEL; }
  "return" { return ActionScriptToken.RETURN; }
  "super" { return ActionScriptToken.SUPER; }
  "switch" { return ActionScriptToken.SWITCH; }
  "throw" { return ActionScriptToken.THROW; }
  "try" { return ActionScriptToken.TRY; }
  "catch" { return ActionScriptToken.CATCH; }
  "finally" { return ActionScriptToken.FINALLY; }
  "with" { return ActionScriptToken.WITH; }
  "dynamic" { return ActionScriptToken.DEFAULT; }
  "final" { return ActionScriptToken.FINAL; }
  "internal" { return ActionScriptToken.INTERNAL; }
  "native" { return ActionScriptToken.NATIVE; }
  "override" { return ActionScriptToken.OVERRIDE; }
  "private" { return ActionScriptToken.PRIVATE; }
  "protected" { return ActionScriptToken.PROTECTED; }
  "public" { return ActionScriptToken.PUBLIC; }
  "static" { return ActionScriptToken.STATIC; }
  "parameter" { return ActionScriptToken.PARAMETER; }
  "class" { return ActionScriptToken.CLASS; }
  "const" { return ActionScriptToken.CONST; }
  "extends" { return ActionScriptToken.EXTENDS; }
  "function" { return ActionScriptToken.FUNCTION; }
  "get" { return ActionScriptToken.GET; }
  "implements" { return ActionScriptToken.IMPLEMENTS; }
  "interface" { return ActionScriptToken.INTERFACE; }
  "namespace" { return ActionScriptToken.NAMESPACE; }
  "package" { return ActionScriptToken.PACKAGE; }
  "typeof" { return ActionScriptToken.TYPEOF; }
  "set" { return ActionScriptToken.SET; }
  "this" { return ActionScriptToken.THIS; }
  "include" { return ActionScriptToken.INCLUDE; }
  "instanceof" { return ActionScriptToken.INSTANCEOF; }
  "import" { return ActionScriptToken.IMPORT; }
  "use" { return ActionScriptToken.USE; }
  "as" { return ActionScriptToken.AS; }
  "new" { return ActionScriptToken.NEW; }
  "var" { return ActionScriptToken.VAR; }

  "Array" { return ActionScriptToken.ARRAY; }
  "Object" { return ActionScriptToken.OBJECT; }
  "Boolean" { return ActionScriptToken.BOOLEAN; }
  "Number" { return ActionScriptToken.NUMBER; }
  "String" { return ActionScriptToken.STRING; }
  ([vV]oid) { return ActionScriptToken.VOID; }
  "Vector" { return ActionScriptToken.VECTOR; }
  "int" { return ActionScriptToken.INT; }
  "uint" { return ActionScriptToken.UINT; }

  "true" { return ActionScriptToken.TRUE; }
  "false" { return ActionScriptToken.FALSE; }
  "null" { return ActionScriptToken.NULL; }
  "undefined" { return ActionScriptToken.UNDEFINED; }
  "NaN" { return ActionScriptToken.NAN; }

  "+" { return ActionScriptToken.PLUS; }
  "--" { return ActionScriptToken.MINUSMINUS; }
  "/" { return ActionScriptToken.DIV; }
  "++" { return ActionScriptToken.PLUSPLUS; }
  "%" { return ActionScriptToken.MOD; }
  "*" { return ActionScriptToken.MULT; }
  "-" { return ActionScriptToken.MINUS; }

  "+=" { return ActionScriptToken.PLUSEQ; }
  "/=" { return ActionScriptToken.DIVEQ; }
  "%=" { return ActionScriptToken.MODEQ; }
  "*=" { return ActionScriptToken.MULTEQ; }
  "-=" { return ActionScriptToken.MINUSEQ; }

  "=" { return ActionScriptToken.EQ; }

  "&" { return ActionScriptToken.AND; }
  "<<" { return ActionScriptToken.LTLT; }
  "~" { return ActionScriptToken.TILDE; }
  "|" { return ActionScriptToken.OR; }
  ">>" { return ActionScriptToken.GTGT; }
  ">>>" { return ActionScriptToken.GTGTGT; }
  "^" { return ActionScriptToken.XOR; }

  "&=" { return ActionScriptToken.ANDEQ; }
  "<<=" { return ActionScriptToken.LTLTEQ; }
  "|=" { return ActionScriptToken.OREQ; }
  ">>=" { return ActionScriptToken.GTGTEQ; }
  ">>>=" { return ActionScriptToken.GTGTGTEQ; }
  "^=" { return ActionScriptToken.XOREQ; }

  "==" { return ActionScriptToken.EQEQ; }
  ">" { return ActionScriptToken.GT; }
  ">=" { return ActionScriptToken.GTEQ; }
  "!=" { return ActionScriptToken.NOTEQ; }
  "<" { return ActionScriptToken.LT; }
  "<=" { return ActionScriptToken.LTEQ; }
  "===" { return ActionScriptToken.EQEQEQ; }
  "!==" { return ActionScriptToken.NOTEQEQ; }

  "&&" { return ActionScriptToken.ANDAND; }
  "&&=" { return ActionScriptToken.ANDANDEQ; }
  "!" { return ActionScriptToken.NOT; }
  "||" { return ActionScriptToken.OROR; }
  "||=" { return ActionScriptToken.OROREQ; }

  "(" { return ActionScriptToken.LPAREN; }
  ")" { return ActionScriptToken.RPAREN; }
  "{" { return ActionScriptToken.LBRACE; }
  "}" { return ActionScriptToken.RBRACE; }
  "[" { return ActionScriptToken.LBRACK; }
  "]" { return ActionScriptToken.RBRACK; }
  ";" { return ActionScriptToken.SEMICOLON; }
  "," { return ActionScriptToken.COMMA; }
  "." { return ActionScriptToken.DOT; }
  "?" { return ActionScriptToken.QUEST; }
  ":" { return ActionScriptToken.COLON; }

  {PREPROCESSOR} { return ActionScriptToken.PREPROCESSOR; }

  {LINE_COMMENT} { return ActionScriptToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return ActionScriptToken.BLOCK_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return ActionScriptToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return ActionScriptToken.SINGLE_QUOTED_STRING; }

  {IDENTIFIER} { return ActionScriptToken.IDENTIFIER; }
  {WHITESPACE} { return ActionScriptToken.WHITESPACE; }
}

[^] { return ActionScriptToken.BAD_CHARACTER; }

<<EOF>> { return ActionScriptToken.EOF; }