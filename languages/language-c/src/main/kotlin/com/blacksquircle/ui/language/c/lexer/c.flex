package com.blacksquircle.ui.language.c.lexer;

@SuppressWarnings("all")
%%

%public
%class CLexer
%type CToken
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

TRIGRAPH = ("??="|"??("|"??)"|"??/"|"??'"|"??<"|"??>"|"??!"|"??-")
PREPROCESSOR = ({WHITESPACE}?"#"{WHITESPACE}?[a-zA-Z]+([^\n])*)

LINE_COMMENT = "//".*
BLOCK_COMMENT = "/"\*([^*] | \*+[^*/])*(\*+"/")?

%%

<YYINITIAL> {

  {LONG_LITERAL} { return CToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return CToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return CToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return CToken.DOUBLE_LITERAL; }

  "auto" { return CToken.AUTO; }
  "break" { return CToken.BREAK; }
  "case" { return CToken.CASE; }
  "const" { return CToken.CONST; }
  "continue" { return CToken.CONTINUE; }
  "default" { return CToken.DEFAULT; }
  "do" { return CToken.DO; }
  "else" { return CToken.ELSE; }
  "enum" { return CToken.ENUM; }
  "extern" { return CToken.EXTERN; }
  "for" { return CToken.FOR; }
  "goto" { return CToken.GOTO; }
  "if" { return CToken.IF; }
  "register" { return CToken.REGISTER; }
  "sizeof" { return CToken.SIZEOF; }
  "static" { return CToken.STATIC; }
  "struct" { return CToken.STRUCT; }
  "switch" { return CToken.SWITCH; }
  "typedef" { return CToken.TYPEDEF; }
  "union" { return CToken.UNION; }
  "volatile" { return CToken.VOLATILE; }
  "while" { return CToken.WHILE; }
  "return" { return CToken.RETURN; }

  "NULL" { return CToken.NULL; }
  "null" { return CToken.NULL; }
  "true" { return CToken.TRUE; }
  "false" { return CToken.FALSE; }

  "bool" { return CToken.BOOL; }
  "char" { return CToken.CHAR; }
  "div_t" { return CToken.DIV_T; }
  "double" { return CToken.DOUBLE; }
  "float" { return CToken.FLOAT; }
  "int" { return CToken.INT; }
  "ldiv_t" { return CToken.LDIV_T; }
  "long" { return CToken.LONG; }
  "short" { return CToken.SHORT; }
  "signed" { return CToken.SIGNED; }
  "size_t" { return CToken.SIZE_T; }
  "unsigned" { return CToken.UNSIGNED; }
  "void" { return CToken.VOID; }
  "wchar_t"	{ return CToken.WCHAR_T; }

  "abort" |
  "abs" |
  "acos" |
  "asctime" |
  "asin" |
  "assert" |
  "atan2" |
  "atan" |
  "atexit" |
  "atof" |
  "atoi" |
  "atol" |
  "bsearch" |
  "btowc" |
  "calloc" |
  "ceil" |
  "clearerr" |
  "clock" |
  "cosh" |
  "cos" |
  "ctime" |
  "difftime" |
  "div" |
  "errno" |
  "exit" |
  "exp" |
  "fabs" |
  "fclose" |
  "feof" |
  "ferror" |
  "fflush" |
  "fgetc" |
  "fgetpos" |
  "fgetwc" |
  "fgets" |
  "fgetws" |
  "floor" |
  "fmod" |
  "fopen" |
  "fprintf" |
  "fputc" |
  "fputs" |
  "fputwc" |
  "fputws" |
  "fread" |
  "free" |
  "freopen" |
  "frexp" |
  "fscanf" |
  "fseek" |
  "fsetpos" |
  "ftell" |
  "fwprintf" |
  "fwrite" |
  "fwscanf" |
  "getchar" |
  "getc" |
  "getenv" |
  "gets" |
  "getwc" |
  "getwchar" |
  "gmtime" |
  "isalnum" |
  "isalpha" |
  "iscntrl" |
  "isdigit" |
  "isgraph" |
  "islower" |
  "isprint" |
  "ispunct" |
  "isspace" |
  "isupper" |
  "isxdigit" |
  "labs" |
  "ldexp" |
  "ldiv" |
  "localeconv" |
  "localtime" |
  "log10" |
  "log" |
  "longjmp" |
  "malloc" |
  "mblen" |
  "mbrlen" |
  "mbrtowc" |
  "mbsinit" |
  "mbsrtowcs" |
  "mbstowcs" |
  "mbtowc" |
  "memchr" |
  "memcmp" |
  "memcpy" |
  "memmove" |
  "memset" |
  "mktime" |
  "modf" |
  "offsetof" |
  "perror" |
  "pow" |
  "printf" |
  "putchar" |
  "putc" |
  "puts" |
  "putwc" |
  "putwchar" |
  "qsort" |
  "raise" |
  "rand" |
  "realloc" |
  "remove" |
  "rename" |
  "rewind" |
  "scanf" |
  "setbuf" |
  "setjmp" |
  "setlocale" |
  "setvbuf" |
  "setvbuf" |
  "signal" |
  "sinh" |
  "sin" |
  "sprintf" |
  "sqrt" |
  "srand" |
  "sscanf" |
  "strcat" |
  "strchr" |
  "strcmp" |
  "strcmp" |
  "strcoll" |
  "strcpy" |
  "strcspn" |
  "strerror" |
  "strftime" |
  "strlen" |
  "strncat" |
  "strncmp" |
  "strncpy" |
  "strpbrk" |
  "strrchr" |
  "strspn" |
  "strstr" |
  "strtod" |
  "strtok" |
  "strtol" |
  "strtoul" |
  "strxfrm" |
  "swprintf" |
  "swscanf" |
  "system" |
  "tanh" |
  "tan" |
  "time" |
  "tmpfile" |
  "tmpnam" |
  "tolower" |
  "toupper" |
  "ungetc" |
  "ungetwc" |
  "va_arg" |
  "va_end" |
  "va_start" |
  "vfprintf" |
  "vfwprintf" |
  "vprintf" |
  "vsprintf" |
  "vswprintf" |
  "vwprintf" |
  "wcrtomb" |
  "wcscat" |
  "wcschr" |
  "wcscmp" |
  "wcscoll" |
  "wcscpy" |
  "wcscspn" |
  "wcsftime" |
  "wcslen" |
  "wcsncat" |
  "wcsncmp" |
  "wcsncpy" |
  "wcspbrk" |
  "wcsrchr" |
  "wcsrtombs" |
  "wcsspn" |
  "wcsstr" |
  "wcstod" |
  "wcstok" |
  "wcstol" |
  "wcstombs" |
  "wcstoul" |
  "wcsxfrm" |
  "wctob" |
  "wctomb" |
  "wmemchr" |
  "wmemcmp" |
  "wmemcpy" |
  "wmemmove" |
  "wmemset" |
  "wprintf" |
  "wscanf" { return CToken.FUNCTION; }

  {TRIGRAPH} { return CToken.TRIGRAPH; }
  "=" { return CToken.EQ; }
  "+" { return CToken.PLUS; }
  "-" { return CToken.MINUS; }
  "*" { return CToken.MULT; }
  "/" { return CToken.DIV; }
  "%" { return CToken.MOD; }
  "~" { return CToken.TILDA; }
  "<" { return CToken.LT; }
  ">" { return CToken.GT; }
  "<<" { return CToken.LTLT; }
  ">>" { return CToken.GTGT; }
  "==" { return CToken.EQEQ; }
  "+=" { return CToken.PLUSEQ; }
  "-=" { return CToken.MINUSEQ; }
  "*=" { return CToken.MULTEQ; }
  "/=" { return CToken.DIVEQ; }
  "%=" { return CToken.MODEQ; }
  "&=" { return CToken.ANDEQ; }
  "|=" { return CToken.OREQ; }
  "^=" { return CToken.XOREQ; }
  ">=" { return CToken.GTEQ; }
  "<=" { return CToken.LTEQ; }
  "!=" { return CToken.NOTEQ; }
  ">>=" { return CToken.GTGTEQ; }
  "<<=" { return CToken.LTLTEQ; }
  "^" { return CToken.XOR; }
  "&" { return CToken.AND; }
  "&&" { return CToken.ANDAND; }
  "|" { return CToken.OR; }
  "||" { return CToken.OROR; }
  "?" { return CToken.QUEST; }
  ":" { return CToken.COLON; }
  "," { return CToken.COMMA; }
  "!" { return CToken.NOT; }
  "++" { return CToken.PLUSPLUS; }
  "--" { return CToken.MINUSMINUS; }
  ";" { return CToken.SEMICOLON; }
  "(" { return CToken.LPAREN; }
  ")" { return CToken.RPAREN; }
  "{" { return CToken.LBRACE; }
  "}" { return CToken.RBRACE; }
  "[" { return CToken.LBRACK; }
  "]" { return CToken.RBRACK; }

  "__DATE__" { return CToken.__DATE__; }
  "__TIME__" { return CToken.__TIME__; }
  "__FILE__" { return CToken.__FILE__; }
  "__LINE__" { return CToken.__LINE__; }
  "__STDC__" { return CToken.__STDC__; }

  {PREPROCESSOR} { return CToken.PREPROCESSOR; }

  {LINE_COMMENT} { return CToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return CToken.BLOCK_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return CToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return CToken.SINGLE_QUOTED_STRING; }

  {IDENTIFIER} { return CToken.IDENTIFIER; }
  {WHITESPACE} { return CToken.WHITESPACE; }
}

[^] { return CToken.BAD_CHARACTER; }

<<EOF>> { return CToken.EOF; }