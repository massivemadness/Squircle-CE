package com.blacksquircle.ui.language.shell.lexer;

@SuppressWarnings("all")
%%

%public
%class ShellLexer
%type ShellToken
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

DOUBLE_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) | {DIGITS}
DEC_FP_LITERAL = {DIGITS} {DEC_EXPONENT} | {DEC_SIGNIFICAND} {DEC_EXPONENT}?
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT_OR_UNDERSCORE}*
DEC_EXPONENT = [Ee] [+-]? {DIGIT_OR_UNDERSCORE}*
HEX_FP_LITERAL = {HEX_SIGNIFICAND} {HEX_EXPONENT}
HEX_SIGNIFICAND = 0 [Xx] ({HEX_DIGIT_OR_UNDERSCORE}+ "."? | {HEX_DIGIT_OR_UNDERSCORE}* "." {HEX_DIGIT_OR_UNDERSCORE}+)
HEX_EXPONENT = [Pp] [+-]? {DIGIT_OR_UNDERSCORE}*

CRLF = [\ \t \f]* \R
DOUBLE_QUOTED_STRING = \"([^\\\"\r\n] | \\[^\r\n] | \\{CRLF})*\"?
SINGLE_QUOTED_STRING = '([^\\'\r\n] | \\[^\r\n] | \\{CRLF})*'?

INPUT_CHARACTER = [^\r\n]
LINE_TERMINATOR = \r|\n|\r\n
LINE_CONTINUATION = "\\" {LINE_TERMINATOR}
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]

SHEBANG = #\! {INPUT_CHARACTER}*
COMMENT = # {INPUT_CHARACTER}*

ESCAPED_CHAR = "\\" [^\n]
ESCAPED_ANY_CHAR = {ESCAPED_CHAR} | {LINE_CONTINUATION}
EVAL_CONTENT = [^\r\n$\"`'() ;] | {ESCAPED_ANY_CHAR}

%%

<YYINITIAL> {

  {INTEGER_LITERAL} { return ShellToken.INTEGER_LITERAL; }
  {DOUBLE_LITERAL} { return ShellToken.DOUBLE_LITERAL; }

  "break" { return ShellToken.BREAK; }
  "case" { return ShellToken.CASE; }
  "continue" { return ShellToken.CONTINUE; }
  "echo" { return ShellToken.ECHO; }
  "esac" { return ShellToken.ESAC; }
  "eval" { return ShellToken.EVAL; }
  "elif" { return ShellToken.ELIF; }
  "else" { return ShellToken.ELSE; }
  "exit" { return ShellToken.EXIT; }
  "exec" { return ShellToken.EXEC; }
  "export" { return ShellToken.EXPORT; }
  "done" { return ShellToken.DONE; }
  "do" { return ShellToken.DO; }
  "fi" { return ShellToken.FI; }
  "for" { return ShellToken.FOR; }
  "in" { return ShellToken.IN; }
  "function" { return ShellToken.FUNCTION; }
  "if" { return ShellToken.IF; }
  "set" { return ShellToken.SET; }
  "select" { return ShellToken.SELECT; }
  "shift" { return ShellToken.SHIFT; }
  "trap" { return ShellToken.TRAP; }
  "then" { return ShellToken.THEN; }
  "ulimit" { return ShellToken.ULIMIT; }
  "umask" { return ShellToken.UMASK; }
  "unset" { return ShellToken.UNSET; }
  "until" { return ShellToken.UNTIL; }
  "wait" { return ShellToken.WAIT; }
  "while" { return ShellToken.WHILE; }
  "let" { return ShellToken.LET; }
  "local" { return ShellToken.LOCAL; }
  "read" { return ShellToken.READ; }
  "readonly" { return ShellToken.READONLY; }
  "return" { return ShellToken.RETURN; }
  "test" { return ShellToken.TEST; }

  "true" { return ShellToken.TRUE; }
  "false" { return ShellToken.FALSE; }

  "*=" { return ShellToken.MULTEQ; }
  "/=" { return ShellToken.DIVEQ; }
  "%=" { return ShellToken.MODEQ; }
  "+=" { return ShellToken.PLUSEQ; }
  "-=" { return ShellToken.MINUSEQ; }
  ">>=" { return ShellToken.SHIFT_RIGHT_EQ; }
  "<<=" { return ShellToken.SHIFT_LEFT_EQ; }
  "&=" { return ShellToken.BIT_AND_EQ; }
  "|=" { return ShellToken.BIT_OR_EQ; }
  "^=" { return ShellToken.BIT_XOR_EQ; }
  "!=" { return ShellToken.NOTEQ; }
  "==" { return ShellToken.EQEQ; }
  "=~" { return ShellToken.REGEXP; }
  ">=" { return ShellToken.GTEQ; }
  "<=" { return ShellToken.LTEQ; }

  "++" { return ShellToken.PLUS_PLUS; }
  "--" { return ShellToken.MINUS_MINUS; }
  "**" { return ShellToken.EXPONENT; }

  "!" { return ShellToken.BANG; }
  "~" { return ShellToken.TILDE; }
  "+" { return ShellToken.PLUS; }
  "-" { return ShellToken.MINUS; }
  "*" { return ShellToken.MULT; }
  "/" { return ShellToken.DIV; }
  "%" { return ShellToken.MOD; }

  "<<" { return ShellToken.SHIFT_LEFT; }
  ">>" { return ShellToken.SHIFT_RIGHT; }
  "<" { return ShellToken.LT; }
  ">" { return ShellToken.GT; }

  "&&" { return ShellToken.AND_AND; }
  "||" { return ShellToken.OR_OR; }
  "&" { return ShellToken.AND; }
  "^" { return ShellToken.XOR; }
  "|" { return ShellToken.OR; }
  "$" { return ShellToken.DOLLAR; }
  "=" { return ShellToken.EQ; }
  "`" { return ShellToken.BACKTICK; }
  "?" { return ShellToken.QUEST; }
  ":" { return ShellToken.COLON; }

  "(" { return ShellToken.LPAREN; }
  ")" { return ShellToken.RPAREN; }
  "{" { return ShellToken.LBRACE; }
  "}" { return ShellToken.RBRACE; }
  "[" { return ShellToken.LBRACK; }
  "]" { return ShellToken.RBRACK; }
  ";" { return ShellToken.SEMICOLON; }
  "," { return ShellToken.COMMA; }
  "." { return ShellToken.DOT; }

  {EVAL_CONTENT} { return ShellToken.EVAL_CONTENT; }

  {SHEBANG} { return ShellToken.SHEBANG; }
  {COMMENT} { return ShellToken.COMMENT; }

  {DOUBLE_QUOTED_STRING} { return ShellToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return ShellToken.SINGLE_QUOTED_STRING; }

  {IDENTIFIER} { return ShellToken.IDENTIFIER; }
  {WHITESPACE} { return ShellToken.WHITESPACE; }
}

[^] { return ShellToken.BAD_CHARACTER; }

<<EOF>> { return ShellToken.EOF; }