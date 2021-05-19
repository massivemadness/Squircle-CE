package com.blacksquircle.ui.language.typescript.lexer;

@SuppressWarnings("all")
%%

%public
%class TypeScriptLexer
%type TypeScriptToken
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
SINGLE_BACKTICK_STRING = `([^\\`\r\n] | \\[^\r\n] | \\{CRLF})*`?

LINE_TERMINATOR = \r|\n|\r\n
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]

LINE_COMMENT = "//".*
BLOCK_COMMENT = "/"\*([^*] | \*+[^*/])*(\*+"/")?

%%

<YYINITIAL> {

  {LONG_LITERAL} { return TypeScriptToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return TypeScriptToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return TypeScriptToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return TypeScriptToken.DOUBLE_LITERAL; }

  "function" { return TypeScriptToken.FUNCTION; }
  "prototype" { return TypeScriptToken.PROTOTYPE; }
  "debugger" { return TypeScriptToken.DEBUGGER; }
  "super" { return TypeScriptToken.SUPER; }
  "any" { return TypeScriptToken.ANY; }
  "this" { return TypeScriptToken.THIS; }
  "async" { return TypeScriptToken.ASYNC; }
  "await" { return TypeScriptToken.AWAIT; }
  "export" { return TypeScriptToken.EXPORT; }
  "from" { return TypeScriptToken.FROM; }
  "extends" { return TypeScriptToken.EXTENDS; }
  "declare" { return TypeScriptToken.DECLARE; }
  "final" { return TypeScriptToken.FINAL; }
  "implements" { return TypeScriptToken.IMPLEMENTS; }
  "native" { return TypeScriptToken.NATIVE; }
  "private" { return TypeScriptToken.PRIVATE; }
  "protected" { return TypeScriptToken.PROTECTED; }
  "public" { return TypeScriptToken.PUBLIC; }
  "static" { return TypeScriptToken.STATIC; }
  "synchronized" { return TypeScriptToken.SYNCHRONIZED; }
  "constructor" { return TypeScriptToken.CONSTRUCTOR; }
  "throws" { return TypeScriptToken.THROWS; }
  "transient" { return TypeScriptToken.TRANSIENT; }
  "volatile" { return TypeScriptToken.VOLATILE; }
  "yield" { return TypeScriptToken.YIELD; }
  "delete" { return TypeScriptToken.DELETE; }
  "new" { return TypeScriptToken.NEW; }
  "in" { return TypeScriptToken.IN; }
  "instanceof" { return TypeScriptToken.INSTANCEOF; }
  "typeof" { return TypeScriptToken.TYPEOF; }
  "of" { return TypeScriptToken.OF; }
  "keyof" { return TypeScriptToken.KEYOF; }
  "type" { return TypeScriptToken.TYPE; }
  "with" { return TypeScriptToken.WITH; }
  "as" { return TypeScriptToken.AS; }
  "is" { return TypeScriptToken.IS; }
  "break" { return TypeScriptToken.BREAK; }
  "case" { return TypeScriptToken.CASE; }
  "catch" { return TypeScriptToken.CATCH; }
  "continue" { return TypeScriptToken.CONTINUE; }
  "default" { return TypeScriptToken.DEFAULT; }
  "do" { return TypeScriptToken.DO; }
  "else" { return TypeScriptToken.ELSE; }
  "finally" { return TypeScriptToken.FINALLY; }
  "for" { return TypeScriptToken.FOR; }
  "goto" { return TypeScriptToken.GOTO; }
  "if" { return TypeScriptToken.IF; }
  "import" { return TypeScriptToken.IMPORT; }
  "package" { return TypeScriptToken.PACKAGE; }
  "readonly" { return TypeScriptToken.READONLY; }
  "return" { return TypeScriptToken.RETURN; }
  "switch" { return TypeScriptToken.SWITCH; }
  "throw" { return TypeScriptToken.THROW; }
  "try" { return TypeScriptToken.TRY; }
  "while" { return TypeScriptToken.WHILE; }

  "class" { return TypeScriptToken.CLASS; }
  "interface" { return TypeScriptToken.INTERFACE; }
  "enum" { return TypeScriptToken.ENUM; }
  "module" { return TypeScriptToken.MODULE; }
  "unknown" { return TypeScriptToken.UNKNOWN; }
  "object" { return TypeScriptToken.OBJECT; }
  "boolean" { return TypeScriptToken.BOOLEAN; }
  "string" { return TypeScriptToken.STRING; }
  "number" { return TypeScriptToken.NUMBER; }
  "bigint" { return TypeScriptToken.BIGINT; }
  "void" { return TypeScriptToken.VOID; }
  "const" { return TypeScriptToken.CONST; }
  "var" { return TypeScriptToken.VAR; }
  "let" { return TypeScriptToken.LET; }

  "true" { return TypeScriptToken.TRUE; }
  "false" { return TypeScriptToken.FALSE; }
  "null" { return TypeScriptToken.NULL; }
  "NaN" { return TypeScriptToken.NAN; }
  "undefined" { return TypeScriptToken.UNDEFINED; }

  "==" { return TypeScriptToken.EQEQ; }
  "!=" { return TypeScriptToken.NOTEQ; }
  "||" { return TypeScriptToken.OROR; }
  "++" { return TypeScriptToken.PLUSPLUS; }
  "--" { return TypeScriptToken.MINUSMINUS; }

  "<" { return TypeScriptToken.LT; }
  "<<" { return TypeScriptToken.LTLT; }
  "<=" { return TypeScriptToken.LTEQ; }
  "<<=" { return TypeScriptToken.LTLTEQ; }

  ">" { return TypeScriptToken.GT; }
  ">>" { return TypeScriptToken.GTGT; }
  ">>>" { return TypeScriptToken.GTGTGT; }
  ">=" { return TypeScriptToken.GTEQ; }
  ">>=" { return TypeScriptToken.GTGTEQ; }
  ">>>=" { return TypeScriptToken.GTGTGTEQ; }

  "&" { return TypeScriptToken.AND; }
  "&&" { return TypeScriptToken.ANDAND; }

  "+=" { return TypeScriptToken.PLUSEQ; }
  "-=" { return TypeScriptToken.MINUSEQ; }
  "*=" { return TypeScriptToken.MULTEQ; }
  "/=" { return TypeScriptToken.DIVEQ; }
  "&=" { return TypeScriptToken.ANDEQ; }
  "|=" { return TypeScriptToken.OREQ; }
  "^=" { return TypeScriptToken.XOREQ; }
  "%=" { return TypeScriptToken.MODEQ; }

  "(" { return TypeScriptToken.LPAREN; }
  ")" { return TypeScriptToken.RPAREN; }
  "{" { return TypeScriptToken.LBRACE; }
  "}" { return TypeScriptToken.RBRACE; }
  "[" { return TypeScriptToken.LBRACK; }
  "]" { return TypeScriptToken.RBRACK; }
  ";" { return TypeScriptToken.SEMICOLON; }
  "," { return TypeScriptToken.COMMA; }
  "." { return TypeScriptToken.DOT; }
  "..." { return TypeScriptToken.ELLIPSIS; }

  "=" { return TypeScriptToken.EQ; }
  "!" { return TypeScriptToken.NOT; }
  "~" { return TypeScriptToken.TILDE; }
  "?" { return TypeScriptToken.QUEST; }
  ":" { return TypeScriptToken.COLON; }
  "+" { return TypeScriptToken.PLUS; }
  "-" { return TypeScriptToken.MINUS; }
  "*" { return TypeScriptToken.MULT; }
  "/" { return TypeScriptToken.DIV; }
  "|" { return TypeScriptToken.OR; }
  "^" { return TypeScriptToken.XOR; }
  "%" { return TypeScriptToken.MOD; }

  "=>" { return TypeScriptToken.ARROW; }

  "require" { return TypeScriptToken.REQUIRE; }

  {LINE_COMMENT} { return TypeScriptToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return TypeScriptToken.BLOCK_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return TypeScriptToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return TypeScriptToken.SINGLE_QUOTED_STRING; }
  {SINGLE_BACKTICK_STRING} { return TypeScriptToken.SINGLE_BACKTICK_STRING; }

  {IDENTIFIER} { return TypeScriptToken.IDENTIFIER; }
  {WHITESPACE} { return TypeScriptToken.WHITESPACE; }
}

[^] { return TypeScriptToken.BAD_CHARACTER; }

<<EOF>> { return TypeScriptToken.EOF; }