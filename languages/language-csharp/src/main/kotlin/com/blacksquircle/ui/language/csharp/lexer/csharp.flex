package com.blacksquircle.ui.language.csharp.lexer;

@SuppressWarnings("all")
%%

%public
%class CSharpLexer
%type CSharpToken
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

  {LONG_LITERAL} { return CSharpToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return CSharpToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return CSharpToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return CSharpToken.DOUBLE_LITERAL; }

  "abstract" { return CSharpToken.ABSTRACT; }
  "as" { return CSharpToken.AS; }
  "async" { return CSharpToken.ASYNC; }
  "await" { return CSharpToken.AWAIT; }
  "base" { return CSharpToken.BASE; }
  "break" { return CSharpToken.BREAK; }
  "case" { return CSharpToken.CASE; }
  "catch" { return CSharpToken.CATCH; }
  "checked" { return CSharpToken.CHECKED; }
  "class" { return CSharpToken.CLASS; }
  "const" { return CSharpToken.CONST; }
  "continue" { return CSharpToken.CONTINUE; }
  "decimal" { return CSharpToken.DECIMAL; }
  "default" { return CSharpToken.DEFAULT; }
  "delegate" { return CSharpToken.DELEGATE; }
  "do" { return CSharpToken.DO; }
  "dynamic" { return CSharpToken.DYNAMIC; }
  "else" { return CSharpToken.ELSE; }
  "enum" { return CSharpToken.ENUM; }
  "event" { return CSharpToken.EVENT; }
  "explicit" { return CSharpToken.EXPLICIT; }
  "extern" { return CSharpToken.EXTERN; }
  "finally" { return CSharpToken.FINALLY; }
  "fixed" { return CSharpToken.FIXED; }
  "for" { return CSharpToken.FOR; }
  "foreach" { return CSharpToken.FOREACH; }
  "goto" { return CSharpToken.GOTO; }
  "if" { return CSharpToken.IF; }
  "implicit" { return CSharpToken.IMPLICIT; }
  "in" { return CSharpToken.IN; }
  "interface" { return CSharpToken.INTERFACE; }
  "internal" { return CSharpToken.INTERNAL; }
  "is" { return CSharpToken.IS; }
  "lock" { return CSharpToken.LOCK; }
  "namespace" { return CSharpToken.NAMESPACE; }
  "new" { return CSharpToken.NEW; }
  "operator" { return CSharpToken.OPERATOR; }
  "out" { return CSharpToken.OUT; }
  "override" { return CSharpToken.OVERRIDE; }
  "params" { return CSharpToken.PARAMS; }
  "private" { return CSharpToken.PRIVATE; }
  "protected" { return CSharpToken.PROTECTED; }
  "public" { return CSharpToken.PUBLIC; }
  "readonly" { return CSharpToken.READONLY; }
  "ref" { return CSharpToken.REF; }
  "return" { return CSharpToken.RETURN; }
  "sealed" { return CSharpToken.SEALED; }
  "sizeof" { return CSharpToken.SIZEOF; }
  "stackalloc" { return CSharpToken.STACKALLOC; }
  "static" { return CSharpToken.STATIC; }
  "struct" { return CSharpToken.STRUCT; }
  "switch" { return CSharpToken.SWITCH; }
  "this" { return CSharpToken.THIS; }
  "throw" { return CSharpToken.THROW; }
  "typeof" { return CSharpToken.TYPEOF; }
  "unchecked" { return CSharpToken.UNCHECKED; }
  "unsafe" { return CSharpToken.UNSAFE; }
  "using" { return CSharpToken.USING; }
  "var" { return CSharpToken.VAR; }
  "virtual" { return CSharpToken.VIRTUAL; }
  "void" { return CSharpToken.VOID; }
  "volatile" { return CSharpToken.VOLATILE; }
  "while" { return CSharpToken.WHILE; }

  "null" { return CSharpToken.NULL; }
  "true" { return CSharpToken.TRUE; }
  "false" { return CSharpToken.FALSE; }

  "bool" { return CSharpToken.BOOL; }
  "byte" { return CSharpToken.BYTE; }
  "char" { return CSharpToken.CHAR; }
  "double" { return CSharpToken.DOUBLE; }
  "float" { return CSharpToken.FLOAT; }
  "int" { return CSharpToken.INT; }
  "long" { return CSharpToken.LONG; }
  "object" { return CSharpToken.OBJECT; }
  "sbyte" { return CSharpToken.SBYTE; }
  "short" { return CSharpToken.SHORT; }
  "string" { return CSharpToken.STRING; }
  "uint" { return CSharpToken.UINT; }
  "ulong" { return CSharpToken.ULONG; }
  "ushort" { return CSharpToken.USHORT; }

  "+" { return CSharpToken.PLUS; }
  "--" { return CSharpToken.MINUSMINUS; }
  "/" { return CSharpToken.DIV; }
  "++" { return CSharpToken.PLUSPLUS; }
  "%" { return CSharpToken.MOD; }
  "*" { return CSharpToken.MULT; }
  "-" { return CSharpToken.MINUS; }

  "+=" { return CSharpToken.PLUSEQ; }
  "/=" { return CSharpToken.DIVEQ; }
  "%=" { return CSharpToken.MODEQ; }
  "*=" { return CSharpToken.MULTEQ; }
  "-=" { return CSharpToken.MINUSEQ; }

  "=" { return CSharpToken.EQ; }

  "&" { return CSharpToken.AND; }
  "<<" { return CSharpToken.LTLT; }
  "~" { return CSharpToken.TILDE; }
  "|" { return CSharpToken.OR; }
  ">>" { return CSharpToken.GTGT; }
  "^" { return CSharpToken.XOR; }

  "&=" { return CSharpToken.ANDEQ; }
  "<<=" { return CSharpToken.LTLTEQ; }
  "|=" { return CSharpToken.OREQ; }
  ">>=" { return CSharpToken.GTGTEQ; }
  "^=" { return CSharpToken.XOREQ; }

  "==" { return CSharpToken.EQEQ; }
  ">" { return CSharpToken.GT; }
  ">=" { return CSharpToken.GTEQ; }
  "!=" { return CSharpToken.NOTEQ; }
  "<" { return CSharpToken.LT; }
  "<=" { return CSharpToken.LTEQ; }

  "&&" { return CSharpToken.ANDAND; }
  "!" { return CSharpToken.NOT; }
  "||" { return CSharpToken.OROR; }

  "(" { return CSharpToken.LPAREN; }
  ")" { return CSharpToken.RPAREN; }
  "{" { return CSharpToken.LBRACE; }
  "}" { return CSharpToken.RBRACE; }
  "[" { return CSharpToken.LBRACK; }
  "]" { return CSharpToken.RBRACK; }
  ";" { return CSharpToken.SEMICOLON; }
  "," { return CSharpToken.COMMA; }
  "." { return CSharpToken.DOT; }
  "?" { return CSharpToken.QUEST; }
  ":" { return CSharpToken.COLON; }

  {PREPROCESSOR} { return CSharpToken.PREPROCESSOR; }

  {LINE_COMMENT} { return CSharpToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return CSharpToken.BLOCK_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return CSharpToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return CSharpToken.SINGLE_QUOTED_STRING; }

  {IDENTIFIER} { return CSharpToken.IDENTIFIER; }
  {WHITESPACE} { return CSharpToken.WHITESPACE; }
}

[^] { return CSharpToken.BAD_CHARACTER; }

<<EOF>> { return CSharpToken.EOF; }