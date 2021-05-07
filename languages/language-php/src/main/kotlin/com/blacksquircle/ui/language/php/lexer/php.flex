package com.blacksquircle.ui.language.php.lexer;

@SuppressWarnings("all")
%%

%public
%class PhpLexer
%type PhpToken
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

LETTER = [A-Za-z]
LETTER_OR_UNDERSCORE = ({LETTER}|"_")
LETTER_OR_UNDERSCORE_OR_DASH = ({LETTER_OR_UNDERSCORE}|[\-])
LETTER_OR_UNDERSCORE_OR_DIGIT = ({LETTER_OR_UNDERSCORE}|{DIGIT})

VARIABLE_LITERAL = ("$"{LETTER_OR_UNDERSCORE}{LETTER_OR_UNDERSCORE_OR_DIGIT}*)

LINE_COMMENT = ("//"|[#]).*
BLOCK_COMMENT = "/"\*([^*] | \*+[^*/])*(\*+"/")?

%%

<YYINITIAL> {

  {INTEGER_LITERAL} { return PhpToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return PhpToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return PhpToken.DOUBLE_LITERAL; }
  {VARIABLE_LITERAL}  { return PhpToken.VARIABLE_LITERAL; }

  "abstract"      { return PhpToken.ABSTRACT; }
  "as"            { return PhpToken.AS; }
  "break"         { return PhpToken.BREAK; }
  "case"          { return PhpToken.CASE; }
  "catch"         { return PhpToken.CATCH; }
  "const"         { return PhpToken.CONST; }
  "class"         { return PhpToken.CLASS; }
  "const"         { return PhpToken.CONST; }
  "continue"      { return PhpToken.CONTINUE; }
  "debugger"      { return PhpToken.DEBUGGER; }
  "default"       { return PhpToken.DEFAULT; }
  "delete"        { return PhpToken.DELETE; }
  "do"            { return PhpToken.DO; }
  "each"          { return PhpToken.EACH; }
  "else"          { return PhpToken.ELSE; }
  "elseif"        { return PhpToken.ELSEIF; }
  "enum"          { return PhpToken.ENUM; }
  "export"        { return PhpToken.EXPORT; }
  "extends"       { return PhpToken.EXTENDS; }
  "final"         { return PhpToken.FINAL; }
  "finally"       { return PhpToken.FINALLY; }
  "fn"            { return PhpToken.FN; }
  "for"           { return PhpToken.FOR; }
  "foreach"       { return PhpToken.FOREACH; }
  "function"      { return PhpToken.FUNCTION; }
  "goto"          { return PhpToken.GOTO; }
  "global"        { return PhpToken.GLOBAL; }
  "if"            { return PhpToken.IF; }
  "implements"    { return PhpToken.IMPLEMENTS; }
  "import"        { return PhpToken.IMPORT; }
  "in"            { return PhpToken.IN; }
  "include"       { return PhpToken.INCLUDE; }
  "include_once"  { return PhpToken.INCLUDE_ONCE; }
  "instanceof"    { return PhpToken.INSTANCEOF; }
  "insteadof"     { return PhpToken.INSTEADOF; }
  "interface"     { return PhpToken.INTERFACE; }
  "let"           { return PhpToken.LET; }
  "namespace"     { return PhpToken.NAMESPACE; }
  "native"        { return PhpToken.NATIVE; }
  "new"           { return PhpToken.NEW; }
  "package"       { return PhpToken.PACKAGE; }
  "parent"        { return PhpToken.PARENT; }
  "private"       { return PhpToken.PRIVATE; }
  "protected"     { return PhpToken.PROTECTED; }
  "public"        { return PhpToken.PUBLIC; }
  "return"        { return PhpToken.RETURN; }
  "self"          { return PhpToken.SELF; }
  "static"        { return PhpToken.STATIC; }
  "super"         { return PhpToken.SUPER; }
  "switch"        { return PhpToken.SWITCH; }
  "synchronized"  { return PhpToken.SYNCHRONIZED; }
  "this"          { return PhpToken.THIS; }
  "throw"         { return PhpToken.THROW; }
  "throws"        { return PhpToken.THROWS; }
  "typeof"        { return PhpToken.TYPEOF; }
  "transient"     { return PhpToken.TRANSIENT; }
  "try"           { return PhpToken.TRY; }
  "var"           { return PhpToken.VAR; }
  "void"          { return PhpToken.VOID; }
  "volatile"      { return PhpToken.VOLATILE; }
  "while"         { return PhpToken.WHILE; }
  "with"          { return PhpToken.WITH; }

  "boolean"       { return PhpToken.BOOLEAN; }
  "byte"          { return PhpToken.BYTE; }
  "char"          { return PhpToken.CHAR; }
  "double"        { return PhpToken.DOUBLE; }
  "float"         { return PhpToken.FLOAT; }
  "int"           { return PhpToken.INT; }
  "long"          { return PhpToken.LONG; }
  "short"         { return PhpToken.SHORT; }

  "true"          { return PhpToken.TRUE; }
  "false"         { return PhpToken.FALSE; }
  "null"          { return PhpToken.NULL; }
  "NaN"           { return PhpToken.NAN; }
  "Infinity"      { return PhpToken.INFINITY; }

  "array"         { return PhpToken.ARRAY; }
  "die"           { return PhpToken.DIE; }
  "echo"          { return PhpToken.ECHO; }
  "empty"         { return PhpToken.EMPTY; }
  "eval"          { return PhpToken.EVAL; }
  "exit"          { return PhpToken.EXIT; }
  "parseInt"      { return PhpToken.PARSEINT; }
  "parseFloat"    { return PhpToken.PARSEFLOAT; }
  "print"         { return PhpToken.PRINT; }
  "escape"        { return PhpToken.ESCAPE; }
  "unescape"      { return PhpToken.UNESCAPE; }
  "isNaN"         { return PhpToken.ISNAN; }
  "isFinite"      { return PhpToken.ISFINITE; }

  "+"             { return PhpToken.PLUS; }
  "-"             { return PhpToken.MINUS; }
  "<="            { return PhpToken.LTEQ; }
  "^"             { return PhpToken.XOR; }
  "++"            { return PhpToken.PLUSPLUS; }
  "<"             { return PhpToken.LT; }
  "*"             { return PhpToken.MULT; }
  ">="            { return PhpToken.GTEQ; }
  "%"             { return PhpToken.MOD; }
  "--"            { return PhpToken.MINUSMINUS; }
  ">"             { return PhpToken.GT; }
  "/"             { return PhpToken.DIV; }
  "!="            { return PhpToken.NOTEQ; }
  "?"             { return PhpToken.QUEST; }
  ">>"            { return PhpToken.GTGT; }
  "!"             { return PhpToken.NOT; }
  "&"             { return PhpToken.AND; }
  "=="            { return PhpToken.EQEQ; }
  ":"             { return PhpToken.COLON; }
  "~"             { return PhpToken.TILDA; }
  "||"            { return PhpToken.OROR; }
  "&&"            { return PhpToken.ANDAND; }
  ">>>"           { return PhpToken.GTGTGT; }

  "="             { return PhpToken.EQ; }
  "-="            { return PhpToken.MINUSEQ; }
  "*="            { return PhpToken.MULTEQ; }
  "/="            { return PhpToken.DIVEQ; }
  "|="            { return PhpToken.OREQ; }
  "&="            { return PhpToken.ANDEQ; }
  "^="            { return PhpToken.XOREQ; }
  "+="            { return PhpToken.PLUSEQ; }
  "%="            { return PhpToken.MODEQ; }
  "<<="           { return PhpToken.LTLTEQ; }
  ">>="           { return PhpToken.GTGTEQ; }
  ">>>="          { return PhpToken.GTGTGTEQ; }

  "("             { return PhpToken.LPAREN; }
  ")"             { return PhpToken.RPAREN; }
  "{"             { return PhpToken.LBRACE; }
  "}"             { return PhpToken.RBRACE; }
  "["             { return PhpToken.LBRACK; }
  "]"             { return PhpToken.RBRACK; }
  ";"             { return PhpToken.SEMICOLON; }
  ","             { return PhpToken.COMMA; }
  "."             { return PhpToken.DOT; }

  {LINE_COMMENT}  { return PhpToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return PhpToken.BLOCK_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return PhpToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return PhpToken.SINGLE_QUOTED_STRING; }

  {IDENTIFIER}    { return PhpToken.IDENTIFIER; }
  {WHITESPACE}    { return PhpToken.WHITESPACE; }
}

[^] { return PhpToken.BAD_CHARACTER; }

<<EOF>> { return PhpToken.EOF; }