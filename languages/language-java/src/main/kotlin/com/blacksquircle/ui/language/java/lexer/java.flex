package com.blacksquircle.ui.language.java.lexer;

@SuppressWarnings("all")
%%

%public
%class JavaLexer
%type JavaToken
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

ANNOTATION = ({WHITESPACE}?"@"[a-zA-Z]+([^\r\n\(\)])*)

LINE_COMMENT = "//".*
BLOCK_COMMENT = "/"\*([^*] | \*+[^*/])*(\*+"/")?

%%

<YYINITIAL> {

  {LONG_LITERAL} { return JavaToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return JavaToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return JavaToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return JavaToken.DOUBLE_LITERAL; }

  "abstract" { return JavaToken.ABSTRACT; }
  "assert" { return JavaToken.ASSERT; }
  "break" { return JavaToken.BREAK; }
  "case" { return JavaToken.CASE; }
  "catch" { return JavaToken.CATCH; }
  "class" { return JavaToken.CLASS; }
  "const" { return JavaToken.CONST; }
  "continue" { return JavaToken.CONTINUE; }
  "default" { return JavaToken.DEFAULT; }
  "do" { return JavaToken.DO; }
  "else" { return JavaToken.ELSE; }
  "enum" { return JavaToken.ENUM; }
  "extends" { return JavaToken.EXTENDS; }
  "final" { return JavaToken.FINAL; }
  "finally" { return JavaToken.FINALLY; }
  "for" { return JavaToken.FOR; }
  "goto" { return JavaToken.GOTO; }
  "if" { return JavaToken.IF; }
  "implements" { return JavaToken.IMPLEMENTS; }
  "import" { return JavaToken.IMPORT; }
  "instanceof" { return JavaToken.INSTANCEOF; }
  "interface" { return JavaToken.INTERFACE; }
  "native" { return JavaToken.NATIVE; }
  "new" { return JavaToken.NEW; }
  "package" { return JavaToken.PACKAGE; }
  "private" { return JavaToken.PRIVATE; }
  "protected" { return JavaToken.PROTECTED; }
  "public" { return JavaToken.PUBLIC; }
  "static" { return JavaToken.STATIC; }
  "strictfp" { return JavaToken.STRICTFP; }
  "super" { return JavaToken.SUPER; }
  "switch" { return JavaToken.SWITCH; }
  "synchronized" { return JavaToken.SYNCHRONIZED; }
  "this" { return JavaToken.THIS; }
  "throw" { return JavaToken.THROW; }
  "throws" { return JavaToken.THROWS; }
  "transient" { return JavaToken.TRANSIENT; }
  "try" { return JavaToken.TRY; }
  "void" { return JavaToken.VOID; }
  "volatile" { return JavaToken.VOLATILE; }
  "while" { return JavaToken.WHILE; }
  "return" { return JavaToken.RETURN; }

  "boolean" { return JavaToken.BOOLEAN; }
  "byte" { return JavaToken.BYTE; }
  "char" { return JavaToken.CHAR; }
  "double" { return JavaToken.DOUBLE; }
  "float" { return JavaToken.FLOAT; }
  "int" { return JavaToken.INT; }
  "long" { return JavaToken.LONG; }
  "short" { return JavaToken.SHORT; }

  "true" { return JavaToken.TRUE; }
  "false" { return JavaToken.FALSE; }
  "null" { return JavaToken.NULL; }

  "==" { return JavaToken.EQEQ; }
  "!=" { return JavaToken.NOTEQ; }
  "||" { return JavaToken.OROR; }
  "++" { return JavaToken.PLUSPLUS; }
  "--" { return JavaToken.MINUSMINUS; }

  "<" { return JavaToken.LT; }
  "<<" { return JavaToken.LTLT; }
  "<=" { return JavaToken.LTEQ; }
  "<<=" { return JavaToken.LTLTEQ; }

  ">" { return JavaToken.GT; }
  ">>" { return JavaToken.GTGT; }
  ">>>" { return JavaToken.GTGTGT; }
  ">=" { return JavaToken.GTEQ; }
  ">>=" { return JavaToken.GTGTEQ; }

  "&" { return JavaToken.AND; }
  "&&" { return JavaToken.ANDAND; }

  "+=" { return JavaToken.PLUSEQ; }
  "-=" { return JavaToken.MINUSEQ; }
  "*=" { return JavaToken.MULTEQ; }
  "/=" { return JavaToken.DIVEQ; }
  "&=" { return JavaToken.ANDEQ; }
  "|=" { return JavaToken.OREQ; }
  "^=" { return JavaToken.XOREQ; }
  "%=" { return JavaToken.MODEQ; }

  "(" { return JavaToken.LPAREN; }
  ")" { return JavaToken.RPAREN; }
  "{" { return JavaToken.LBRACE; }
  "}" { return JavaToken.RBRACE; }
  "[" { return JavaToken.LBRACK; }
  "]" { return JavaToken.RBRACK; }
  ";" { return JavaToken.SEMICOLON; }
  "," { return JavaToken.COMMA; }
  "." { return JavaToken.DOT; }
  "..." { return JavaToken.ELLIPSIS; }

  "=" { return JavaToken.EQ; }
  "!" { return JavaToken.NOT; }
  "~" { return JavaToken.TILDE; }
  "?" { return JavaToken.QUEST; }
  ":" { return JavaToken.COLON; }
  "+" { return JavaToken.PLUS; }
  "-" { return JavaToken.MINUS; }
  "*" { return JavaToken.MULT; }
  "/" { return JavaToken.DIV; }
  "|" { return JavaToken.OR; }
  "^" { return JavaToken.XOR; }
  "%" { return JavaToken.MOD; }

  "::" { return JavaToken.DOUBLE_COLON; }
  "->" { return JavaToken.ARROW; }

  {ANNOTATION} { return JavaToken.ANNOTATION; }

  {LINE_COMMENT} { return JavaToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return JavaToken.BLOCK_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return JavaToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return JavaToken.SINGLE_QUOTED_STRING; }

  {IDENTIFIER} { return JavaToken.IDENTIFIER; }
  {WHITESPACE} { return JavaToken.WHITESPACE; }
}

[^] { return JavaToken.BAD_CHARACTER; }

<<EOF>> { return JavaToken.EOF; }