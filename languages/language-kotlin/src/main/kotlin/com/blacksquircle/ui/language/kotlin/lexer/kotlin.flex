package com.blacksquircle.ui.language.kotlin.lexer;

@SuppressWarnings("all")
%%

%public
%class KotlinLexer
%type KotlinToken
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
LONG_DOUBLE_QUOTED_STRING = \"\"\" ~\"\"\"

LINE_TERMINATOR = \r|\n|\r\n
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]

ANNOTATION = ({WHITESPACE}?"@"[a-zA-Z]+([^\r\n\(\)])*)

LINE_COMMENT = "//".*
BLOCK_COMMENT = "/*" ~"*/"

%%

<YYINITIAL> {

  {LONG_LITERAL} { return KotlinToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return KotlinToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return KotlinToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return KotlinToken.DOUBLE_LITERAL; }

  "abstract" { return KotlinToken.ABSTRACT; }
  "actual" { return KotlinToken.ACTUAL; }
  "annotation" { return KotlinToken.ANNOTATION_KEYWORD; }
  "as" { return KotlinToken.AS; }
  "as?" { return KotlinToken.AS_QUEST; }
  "assert" { return KotlinToken.ASSERT; }
  "break" { return KotlinToken.BREAK; }
  "by" { return KotlinToken.BY; }
  "catch" { return KotlinToken.CATCH; }
  "class" { return KotlinToken.CLASS; }
  "companion" { return KotlinToken.COMPANION; }
  "const" { return KotlinToken.CONST; }
  "constuctor" { return KotlinToken.CONSTRUCTOR; }
  "continue" { return KotlinToken.CONTINUE; }
  "data" { return KotlinToken.DATA; }
  "do" { return KotlinToken.DO; }
  "else" { return KotlinToken.ELSE; }
  "enum" { return KotlinToken.ENUM; }
  "expect" { return KotlinToken.EXPECT; }
  "finally" { return KotlinToken.FINALLY; }
  "for" { return KotlinToken.FOR; }
  "fun" { return KotlinToken.FUN; }
  "get" { return KotlinToken.GET; }
  "if" { return KotlinToken.IF; }
  "implements" { return KotlinToken.IMPLEMENTS; }
  "import" { return KotlinToken.IMPORT; }
  "interface" { return KotlinToken.INTERFACE; }
  "in" { return KotlinToken.IN; }
  "infix" { return KotlinToken.INFIX; }
  "init" { return KotlinToken.INIT; }
  "internal" { return KotlinToken.INTERNAL; }
  "inline" { return KotlinToken.INLINE; }
  "is" { return KotlinToken.IS; }
  "lateinit" { return KotlinToken.LATEINIT; }
  "native" { return KotlinToken.NATIVE; }
  "object" { return KotlinToken.OBJECT; }
  "open" { return KotlinToken.OPEN; }
  "operator" { return KotlinToken.OPERATOR; }
  "or" { return KotlinToken.OR_KEYWORD; }
  "out" { return KotlinToken.OUT; }
  "override" { return KotlinToken.OVERRIDE; }
  "package" { return KotlinToken.PACKAGE; }
  "private" { return KotlinToken.PRIVATE; }
  "protected" { return KotlinToken.PROTECTED; }
  "public" { return KotlinToken.PUBLIC; }
  "reified" { return KotlinToken.REIFIED; }
  "return" { return KotlinToken.RETURN; }
  "sealed" { return KotlinToken.SEALED; }
  "set" { return KotlinToken.SET; }
  "super" { return KotlinToken.SUPER; }
  "this" { return KotlinToken.THIS; }
  "throw" { return KotlinToken.THROW; }
  "try" { return KotlinToken.TRY; }
  "typealias" { return KotlinToken.TYPEALIAS; }
  "val" { return KotlinToken.VAL; }
  "var" { return KotlinToken.VAR; }
  "vararg" { return KotlinToken.VARARGS; }
  "when" { return KotlinToken.WHEN; }
  "where" { return KotlinToken.WHERE; }
  "while" { return KotlinToken.WHILE; }

  "true" { return KotlinToken.TRUE; }
  "false" { return KotlinToken.FALSE; }
  "null" { return KotlinToken.NULL; }

  "==" { return KotlinToken.EQEQ; }
  "!=" { return KotlinToken.NOTEQ; }
  "||" { return KotlinToken.OROR; }
  "++" { return KotlinToken.PLUSPLUS; }
  "--" { return KotlinToken.MINUSMINUS; }

  "<" { return KotlinToken.LT; }
  "<<" { return KotlinToken.LTLT; }
  "<=" { return KotlinToken.LTEQ; }
  "<<=" { return KotlinToken.LTLTEQ; }

  ">" { return KotlinToken.GT; }
  ">>" { return KotlinToken.GTGT; }
  ">>>" { return KotlinToken.GTGTGT; }
  ">=" { return KotlinToken.GTEQ; }
  ">>=" { return KotlinToken.GTGTEQ; }

  "&" { return KotlinToken.AND; }
  "&&" { return KotlinToken.ANDAND; }

  "+=" { return KotlinToken.PLUSEQ; }
  "-=" { return KotlinToken.MINUSEQ; }
  "*=" { return KotlinToken.MULTEQ; }
  "/=" { return KotlinToken.DIVEQ; }
  "&=" { return KotlinToken.ANDEQ; }
  "|=" { return KotlinToken.OREQ; }
  "^=" { return KotlinToken.XOREQ; }
  "%=" { return KotlinToken.MODEQ; }

  "(" { return KotlinToken.LPAREN; }
  ")" { return KotlinToken.RPAREN; }
  "{" { return KotlinToken.LBRACE; }
  "}" { return KotlinToken.RBRACE; }
  "[" { return KotlinToken.LBRACK; }
  "]" { return KotlinToken.RBRACK; }
  ";" { return KotlinToken.SEMICOLON; }
  "," { return KotlinToken.COMMA; }
  "." { return KotlinToken.DOT; }

  "=" { return KotlinToken.EQ; }
  "!" { return KotlinToken.NOT; }
  "~" { return KotlinToken.TILDE; }
  "?" { return KotlinToken.QUEST; }
  ":" { return KotlinToken.COLON; }
  "+" { return KotlinToken.PLUS; }
  "-" { return KotlinToken.MINUS; }
  "*" { return KotlinToken.MULT; }
  "/" { return KotlinToken.DIV; }
  "|" { return KotlinToken.OR; }
  "^" { return KotlinToken.XOR; }
  "%" { return KotlinToken.MOD; }

  "?:" { return KotlinToken.ELVIS; }
  "..." { return KotlinToken.ELLIPSIS; }
  "::" { return KotlinToken.DOUBLE_COLON; }
  "->" { return KotlinToken.ARROW; }

  {ANNOTATION} { return KotlinToken.ANNOTATION; }

  {LINE_COMMENT} { return KotlinToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return KotlinToken.BLOCK_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return KotlinToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return KotlinToken.SINGLE_QUOTED_STRING; }
  {LONG_DOUBLE_QUOTED_STRING} { return KotlinToken.SINGLE_QUOTED_STRING; }

  {IDENTIFIER} { return KotlinToken.IDENTIFIER; }
  {WHITESPACE} { return KotlinToken.WHITESPACE; }
}

[^] { return KotlinToken.BAD_CHARACTER; }

<<EOF>> { return KotlinToken.EOF; }