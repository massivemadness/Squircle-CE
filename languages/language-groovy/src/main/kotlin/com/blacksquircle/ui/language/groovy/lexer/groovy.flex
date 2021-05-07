package com.blacksquircle.ui.language.groovy.lexer;

@SuppressWarnings("all")
%%

%public
%class GroovyLexer
%type GroovyToken
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

LINE_TERMINATOR = \r|\n|\r\n
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]

COMMENT_TAIL = ( [^"*"]* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?
SHEBANG_COMMENT = "#!"[^\r\n]*
LINE_COMMENT = "/""/"[^\r\n]*
BLOCK_COMMENT = ("/*" [^"*"] {COMMENT_TAIL} ) | "/*"
DOC_COMMENT = "/*" "*"+ ( "/" | ( [^"/""*"] {COMMENT_TAIL} ) )?

HEX_DIGIT = [0-9A-Fa-f]
DIGIT = [0-9]
BIG_SUFFIX = g | G
FLOAT_SUFFIX = f | F
LONG_SUFFIX = l | L
INT_SUFFIX = i | I
DOUBLE_SUFFIX = d | D
EXPONENT = (e | E)("+" | "-")? [0-9] ("_"? [0-9])*

NUM_BIN = 0 (b | B) [0-1] ("_"* [0-1])*
NUM_HEX = 0(x | X) {HEX_DIGIT} ("_"* {HEX_DIGIT})*
NUM_OCT = 0[0-7] ("_"* [0-7])*
NUM_DEC = {DIGIT} ("_"* {DIGIT})*

NUM_INT_PART = {NUM_BIN} | {NUM_HEX} | {NUM_OCT} | {NUM_DEC}
NUM_INT = {NUM_INT_PART} {INT_SUFFIX}?
NUM_LONG = {NUM_INT_PART} {LONG_SUFFIX}
NUM_BIG_INT = {NUM_INT_PART} {BIG_SUFFIX}
NUM_FLOAT = {NUM_DEC} ("." {NUM_DEC})? {EXPONENT}? {FLOAT_SUFFIX}
NUM_DOUBLE = {NUM_DEC} ("." {NUM_DEC})? {EXPONENT}? {DOUBLE_SUFFIX}
NUM_BIG_DECIMAL = {NUM_DEC} (
  ({EXPONENT} {BIG_SUFFIX}?) |
  ("." {NUM_DEC} {EXPONENT}? {BIG_SUFFIX}?) |
  {BIG_SUFFIX}
)

LETTER = [:letter:] | "_"
IDENTIFIER = ({LETTER}|\$) ({LETTER} | {DIGIT} | \$)*
NOT_IDENT_PART = [^_[:letter:]0-9$]

ANNOTATION = ({WHITESPACE}?"@"[a-zA-Z]+([^\r\n\(\)])*)

STRING_ESC = \\ [^] | \\ ({WHITESPACE})+ (\n|\r)

SINGLE_QUOTED_CONTENT = {STRING_ESC} | [^'\\\r\n]
SINGLE_QUOTED_LITERAL = \' {SINGLE_QUOTED_CONTENT}* \'?

DOUBLE_QUOTED_CONTENT = {STRING_ESC} | [^\"\\$\n\r]
DOUBLE_QUOTED_LITERAL = \" {DOUBLE_QUOTED_CONTENT}* \"

TRIPLE_QUOTED_CONTENT = {DOUBLE_QUOTED_CONTENT} | \R | \"(\")?[^\"\\$]
TRIPLE_QUOTED_LITERAL = \"\"\" {TRIPLE_QUOTED_CONTENT}* \"\"\"

%%

<YYINITIAL> {

  {NUM_INT}       { return GroovyToken.INTEGER_LITERAL; }
  {NUM_BIG_INT}   { return GroovyToken.INTEGER_LITERAL; }
  {NUM_BIG_DECIMAL} { return GroovyToken.DOUBLE_LITERAL; }
  {NUM_FLOAT}     { return GroovyToken.FLOAT_LITERAL; }
  {NUM_DOUBLE}    { return GroovyToken.DOUBLE_LITERAL; }
  {NUM_LONG}      { return GroovyToken.LONG_LITERAL; }

  "package"       { return GroovyToken.PACKAGE; }
  "strictfp"      { return GroovyToken.STRICTFP; }
  "import"        { return GroovyToken.IMPORT; }
  "static"        { return GroovyToken.STATIC; }
  "def"           { return GroovyToken.DEF; }
  "var"           { return GroovyToken.VAR; }
  "class"         { return GroovyToken.CLASS; }
  "interface"     { return GroovyToken.INTERFACE; }
  "enum"          { return GroovyToken.ENUM; }
  "trait"         { return GroovyToken.TRAIT; }
  "extends"       { return GroovyToken.EXTENDS; }
  "super"         { return GroovyToken.SUPER; }
  "void"          { return GroovyToken.VOID; }
  "as"            { return GroovyToken.AS; }
  "private"       { return GroovyToken.PRIVATE; }
  "abstract"      { return GroovyToken.ABSTRACT; }
  "public"        { return GroovyToken.PUBLIC; }
  "protected"     { return GroovyToken.PROTECTED; }
  "transient"     { return GroovyToken.TRANSIENT; }
  "native"        { return GroovyToken.NATIVE; }
  "synchronized"  { return GroovyToken.SYNCHRONIZED; }
  "volatile"      { return GroovyToken.VOLATILE; }
  "default"       { return GroovyToken.DEFAULT; }
  "do"            { return GroovyToken.DO; }
  "throws"        { return GroovyToken.THROWS; }
  "implements"    { return GroovyToken.IMPLEMENTS; }
  "this"          { return GroovyToken.THIS; }
  "if"            { return GroovyToken.IF; }
  "else"          { return GroovyToken.ELSE; }
  "while"         { return GroovyToken.WHILE; }
  "switch"        { return GroovyToken.SWITCH; }
  "for"           { return GroovyToken.FOR; }
  "in"            { return GroovyToken.IN; }
  "return"        { return GroovyToken.RETURN; }
  "break"         { return GroovyToken.BREAK; }
  "continue"      { return GroovyToken.CONTINUE; }
  "throw"         { return GroovyToken.THROW; }
  "assert"        { return GroovyToken.ASSERT; }
  "case"          { return GroovyToken.CASE; }
  "try"           { return GroovyToken.TRY; }
  "finally"       { return GroovyToken.FINALLY; }
  "catch"         { return GroovyToken.CATCH; }
  "instanceof"    { return GroovyToken.INSTANCEOF; }
  "new"           { return GroovyToken.NEW; }
  "final"         { return GroovyToken.FINAL; }

  "!in"/{NOT_IDENT_PART} { return GroovyToken.NOT_IN; }
  "!instanceof"/{NOT_IDENT_PART} { return GroovyToken.NOT_INSTANCEOF; }

  "boolean"       { return GroovyToken.BOOLEAN; }
  "byte"          { return GroovyToken.BYTE; }
  "char"          { return GroovyToken.CHAR; }
  "double"        { return GroovyToken.DOUBLE; }
  "float"         { return GroovyToken.FLOAT; }
  "int"           { return GroovyToken.INT; }
  "long"          { return GroovyToken.LONG; }
  "short"         { return GroovyToken.SHORT; }

  "true"          { return GroovyToken.TRUE; }
  "false"         { return GroovyToken.FALSE; }
  "null"          { return GroovyToken.NULL; }

  "=="            { return GroovyToken.EQEQ; }
  "!="            { return GroovyToken.NOTEQ; }
  "==="           { return GroovyToken.EQEQEQ; }
  "!=="           { return GroovyToken.NOTEQEQEQ; }
  "||"            { return GroovyToken.OROR; }
  "++"            { return GroovyToken.PLUSPLUS; }
  "--"            { return GroovyToken.MINUSMINUS; }
  "**"            { return GroovyToken.POW; }
  "<=>"           { return GroovyToken.LTEQGT; }

  "<"             { return GroovyToken.LT; }
  "<="            { return GroovyToken.LTEQ; }
  "<<="           { return GroovyToken.LTLTEQ; }

  ">"             { return GroovyToken.GT; }
  ">="            { return GroovyToken.GTEQ; }
  ">>="           { return GroovyToken.GTGTEQ; }
  ">>>="          { return GroovyToken.GTGTGTEQ; }

  "&"             { return GroovyToken.AND; }
  "&&"            { return GroovyToken.ANDAND; }

  "+="            { return GroovyToken.PLUSEQ; }
  "-="            { return GroovyToken.MINUSEQ; }
  "*="            { return GroovyToken.MULTEQ; }
  "/="            { return GroovyToken.DIVEQ; }
  "&="            { return GroovyToken.ANDEQ; }
  "|="            { return GroovyToken.OREQ; }
  "^="            { return GroovyToken.XOREQ; }
  "%="            { return GroovyToken.MODEQ; }
  "?="            { return GroovyToken.QUESTEQ; }
  "**="           { return GroovyToken.POWEQ; }

  "("             { return GroovyToken.LPAREN; }
  ")"             { return GroovyToken.RPAREN; }
  "{"             { return GroovyToken.LBRACE; }
  "}"             { return GroovyToken.RBRACE; }
  "["             { return GroovyToken.LBRACK; }
  "]"             { return GroovyToken.RBRACK; }
  ";"             { return GroovyToken.SEMICOLON; }
  ","             { return GroovyToken.COMMA; }
  "."             { return GroovyToken.DOT; }
  "..."           { return GroovyToken.ELLIPSIS; }
  ".."            { return GroovyToken.RANGE; }

  "="             { return GroovyToken.EQ; }
  "!"             { return GroovyToken.NOT; }
  "~"             { return GroovyToken.TILDE; }
  "?"             { return GroovyToken.QUEST; }
  ":"             { return GroovyToken.COLON; }
  "+"             { return GroovyToken.PLUS; }
  "-"             { return GroovyToken.MINUS; }
  "*"             { return GroovyToken.MULT; }
  "/"             { return GroovyToken.DIV; }
  "|"             { return GroovyToken.OR; }
  "^"             { return GroovyToken.XOR; }
  "%"             { return GroovyToken.MOD; }

  "?:"            { return GroovyToken.ELVIS; }
  "*."            { return GroovyToken.SPREAD_DOT; }
  "?."            { return GroovyToken.SAFE_DOT; }
  ".&"            { return GroovyToken.METHOD_CLOSURE; }
  "=~"            { return GroovyToken.REGEX_FIND; }
  "==~"           { return GroovyToken.REGEX_MATCH; }
  "::"            { return GroovyToken.DOUBLE_COLON; }
  "->"            { return GroovyToken.ARROW; }

  {ANNOTATION}    { return GroovyToken.ANNOTATION; }

  {SINGLE_QUOTED_LITERAL} { return GroovyToken.SINGLE_QUOTED_STRING; }
  {DOUBLE_QUOTED_LITERAL} { return GroovyToken.DOUBLE_QUOTED_STRING; }
  {TRIPLE_QUOTED_LITERAL} { return GroovyToken.TRIPLE_QUOTED_STRING; }

  \"              { return GroovyToken.DOUBLE_QUOTED_STRING; }
  \'\'\'          { return GroovyToken.TRIPLE_QUOTED_STRING; }
  \"\"\"          { return GroovyToken.TRIPLE_QUOTED_STRING; }

  {SHEBANG_COMMENT} { return GroovyToken.SHEBANG_COMMENT; }
  {LINE_COMMENT}  { return GroovyToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return GroovyToken.BLOCK_COMMENT; }
  {DOC_COMMENT}   { return GroovyToken.DOC_COMMENT; }

  {IDENTIFIER}    { return GroovyToken.IDENTIFIER; }
  {WHITESPACE}    { return GroovyToken.WHITESPACE; }
}

[^] { return GroovyToken.BAD_CHARACTER; }

<<EOF>> { return GroovyToken.EOF; }