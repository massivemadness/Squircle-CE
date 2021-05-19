package com.blacksquircle.ui.language.lisp.lexer;

@SuppressWarnings("all")
%%

%public
%class LispLexer
%type LispToken
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

LINE_TERMINATOR = \r|\n|\r\n
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]

LINE_COMMENT = ";".*
BLOCK_COMMENT = "#|" ~"|#"

%%

<YYINITIAL> {

  {LONG_LITERAL} { return LispToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return LispToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return LispToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return LispToken.DOUBLE_LITERAL; }

  "defclass" { return LispToken.DEFCLASS; }
  "defconstant" { return LispToken.DEFCONSTANT; }
  "defgeneric" { return LispToken.DEFGENERIC; }
  "define-compiler-macro" { return LispToken.DEFINE_COMPILER_MACRO; }
  "define-condition" { return LispToken.DEFINE_CONDITION; }
  "define-method-combination" { return LispToken.DEFINE_METHOD_COMBINATION; }
  "define-modify-macro" { return LispToken.DEFINE_MODIFY_MACRO; }
  "define-setf-expander" { return LispToken.DEFINE_SETF_EXPANDER; }
  "define-symbol-macro" { return LispToken.DEFINE_SYMBOL_MACRO; }
  "defmacro" { return LispToken.DEFMACRO; }
  "defmethod" { return LispToken.DEFMETHOD; }
  "defpackage" { return LispToken.DEFPACKAGE; }
  "defparameter" { return LispToken.DEFPARAMETER; }
  "defsetf" { return LispToken.DEFSETF; }
  "defstruct" { return LispToken.DEFSTRUCT; }
  "deftype" { return LispToken.DEFTYPE; }
  "defun" { return LispToken.DEFUN; }
  "defvar" { return LispToken.DEFVAR; }
  "abort" { return LispToken.ABORT; }
  "assert" { return LispToken.ASSERT; }
  "block" { return LispToken.BLOCK; }
  "break" { return LispToken.BREAK; }
  "case" { return LispToken.CASE; }
  "catch" { return LispToken.CATCH; }
  "ccase" { return LispToken.CCASE; }
  "cerror" { return LispToken.CERROR; }
  "cond" { return LispToken.COND; }
  "ctypecase" { return LispToken.CTYPECASE; }
  "declaim" { return LispToken.DECLAIM; }
  "declare" { return LispToken.DECLARE; }
  "do" { return LispToken.DO; }
  "do*" { return LispToken.DO_S; }
  "do-all-symbols" { return LispToken.DO_ALL_SYMBOLS; }
  "do-external-symbols" { return LispToken.DO_EXTERNAL_SYMBOLS; }
  "do-symbols" { return LispToken.DO_SYMBOLS; }
  "dolist" { return LispToken.DOLIST; }
  "dotimes" { return LispToken.DOTIMES; }
  "ecase" { return LispToken.ECASE; }
  "error" { return LispToken.ERROR; }
  "etypecase" { return LispToken.ETYPECASE; }
  "eval-when" { return LispToken.EVAL_WHEN; }
  "flet" { return LispToken.FLET; }
  "handler-bind" { return LispToken.HANDLER_BIND; }
  "handler-case" { return LispToken.HANDLER_CASE; }
  "if" { return LispToken.IF; }
  "ignore-errors" { return LispToken.IGNORE_ERRORS; }
  "in-package" { return LispToken.IN_PACKAGE; }
  "labels" { return LispToken.LABELS; }
  "lambda" { return LispToken.LAMBDA; }
  "let" { return LispToken.LET; }
  "let*" { return LispToken.LET_S; }
  "locally" { return LispToken.LOCALLY; }
  "loop" { return LispToken.LOOP; }
  "macrolet" { return LispToken.MACROLET; }
  "multiple-value-bind" { return LispToken.MULTIPLE_VALUE_BIND; }
  "proclaim" { return LispToken.PROCLAIM; }
  "prog" { return LispToken.PROG; }
  "prog*" { return LispToken.PROG_S; }
  "prog1" { return LispToken.PROG1; }
  "prog2" { return LispToken.PROG2; }
  "progn" { return LispToken.PROGN; }
  "progv" { return LispToken.PROGV; }
  "provide" { return LispToken.PROVIDE; }
  "require" { return LispToken.REQUIRE; }
  "restart-bind" { return LispToken.RESTART_BIND; }
  "restart-case" { return LispToken.RESTART_CASE; }
  "restart-name" { return LispToken.RESTART_NAME; }
  "return" { return LispToken.RETURN; }
  "return-from" { return LispToken.RETURN_FROM; }
  "signal" { return LispToken.SIGNAL; }
  "symbol-macrolet" { return LispToken.SYMBOL_MACROLET; }
  "tagbody" { return LispToken.TAGBODY; }
  "the" { return LispToken.THE; }
  "throw" { return LispToken.THROW; }
  "typecase" { return LispToken.TYPECASE; }
  "unless" { return LispToken.UNLESS; }
  "unwind-protect" { return LispToken.UNWIND_PROTECT; }
  "when" { return LispToken.WHEN; }
  "with-accessors" { return LispToken.WITH_ACCESSORS; }
  "with-compilation-unit" { return LispToken.WITH_COMPILATION_UNIT; }
  "with-condition-restarts" { return LispToken.WITH_CONDITION_RESTARTS; }
  "with-hash-table-iterator" { return LispToken.WITH_HASH_TABLE_ITERATOR; }
  "with-input-from-string" { return LispToken.WITH_INPUT_FROM_STRING; }
  "with-open-file" { return LispToken.WITH_OPEN_FILE; }
  "with-open-stream" { return LispToken.WITH_OPEN_STREAM; }
  "with-output-to-string" { return LispToken.WITH_OUTPUT_TO_STRING; }
  "with-package-iterator" { return LispToken.WITH_PACKAGE_ITERATOR; }
  "with-simple-restart" { return LispToken.WITH_SIMPLE_RESTART; }
  "with-slots" { return LispToken.WITH_SLOTS; }
  "with-standard-io-syntax" { return LispToken.WITH_STANDARD_IO_SYNTAX; }

  "true" { return LispToken.TRUE; }
  "false" { return LispToken.FALSE; }
  "null" { return LispToken.NULL; }

  "==" { return LispToken.EQEQ; }
  "!=" { return LispToken.NOTEQ; }
  "||" { return LispToken.OROR; }
  "++" { return LispToken.PLUSPLUS; }
  "--" { return LispToken.MINUSMINUS; }

  "<" { return LispToken.LT; }
  "<<" { return LispToken.LTLT; }
  "<=" { return LispToken.LTEQ; }
  "<<=" { return LispToken.LTLTEQ; }

  ">" { return LispToken.GT; }
  ">>" { return LispToken.GTGT; }
  ">>>" { return LispToken.GTGTGT; }
  ">=" { return LispToken.GTEQ; }
  ">>=" { return LispToken.GTGTEQ; }
  ">>>=" { return LispToken.GTGTGTEQ; }

  "&" { return LispToken.AND; }
  "&&" { return LispToken.ANDAND; }

  "+=" { return LispToken.PLUSEQ; }
  "-=" { return LispToken.MINUSEQ; }
  "*=" { return LispToken.MULTEQ; }
  "/=" { return LispToken.DIVEQ; }
  "&=" { return LispToken.ANDEQ; }
  "|=" { return LispToken.OREQ; }
  "^=" { return LispToken.XOREQ; }
  "%=" { return LispToken.MODEQ; }

  "(" { return LispToken.LPAREN; }
  ")" { return LispToken.RPAREN; }
  "{" { return LispToken.LBRACE; }
  "}" { return LispToken.RBRACE; }
  "[" { return LispToken.LBRACK; }
  "]" { return LispToken.RBRACK; }
  "," { return LispToken.COMMA; }
  "." { return LispToken.DOT; }

  "=" { return LispToken.EQ; }
  "!" { return LispToken.NOT; }
  "~" { return LispToken.TILDE; }
  "?" { return LispToken.QUEST; }
  ":" { return LispToken.COLON; }
  "+" { return LispToken.PLUS; }
  "-" { return LispToken.MINUS; }
  "*" { return LispToken.MULT; }
  "/" { return LispToken.DIV; }
  "|" { return LispToken.OR; }
  "^" { return LispToken.XOR; }
  "%" { return LispToken.MOD; }
  "@" { return LispToken.AT; }
  "`" { return LispToken.BACKTICK; }
  "'" { return LispToken.SINGLE_QUOTE; }

  {LINE_COMMENT} { return LispToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return LispToken.BLOCK_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return LispToken.DOUBLE_QUOTED_STRING; }

  {IDENTIFIER} { return LispToken.IDENTIFIER; }
  {WHITESPACE} { return LispToken.WHITESPACE; }
}

[^] { return LispToken.BAD_CHARACTER; }

<<EOF>> { return LispToken.EOF; }