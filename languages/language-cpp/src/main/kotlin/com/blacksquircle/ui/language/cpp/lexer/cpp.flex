package com.blacksquircle.ui.language.cpp.lexer;

@SuppressWarnings("all")
%%

%public
%class CppLexer
%type CppToken
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

  {LONG_LITERAL} { return CppToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return CppToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return CppToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return CppToken.DOUBLE_LITERAL; }

  "auto" { return CppToken.AUTO; }
  "break" { return CppToken.BREAK; }
  "case" { return CppToken.CASE; }
  "catch" { return CppToken.CATCH; }
  "class" { return CppToken.CLASS; }
  "const" { return CppToken.CONST; }
  "const_cast" { return CppToken.CONST_CAST; }
  "continue" { return CppToken.CONTINUE; }
  "default" { return CppToken.DEFAULT; }
  "delete" { return CppToken.DELETE; }
  "do" { return CppToken.DO; }
  "dynamic_cast" { return CppToken.DYNAMIC_CAST; }
  "else" { return CppToken.ELSE; }
  "enum" { return CppToken.ENUM; }
  "explicit" { return CppToken.EXPLICIT; }
  "extern" { return CppToken.EXTERN; }
  "for" { return CppToken.FOR; }
  "friend" { return CppToken.FOR; }
  "goto" { return CppToken.GOTO; }
  "if" { return CppToken.IF; }
  "inline" { return CppToken.IF; }
  "mutable" { return CppToken.MUTABLE; }
  "namespace" { return CppToken.NAMESPACE; }
  "new" { return CppToken.NEW; }
  "operator" { return CppToken.OPERATOR; }
  "private" { return CppToken.PRIVATE; }
  "protected" { return CppToken.PROTECTED; }
  "public" { return CppToken.PUBLIC; }
  "register" { return CppToken.REGISTER; }
  "reinterpret_cast" { return CppToken.REINTERPRET_CAST; }
  "sizeof" { return CppToken.SIZEOF; }
  "static" { return CppToken.STATIC; }
  "static_cast" { return CppToken.STATIC_CAST; }
  "struct" { return CppToken.STRUCT; }
  "switch" { return CppToken.SWITCH; }
  "template" { return CppToken.TEMPLATE; }
  "this" { return CppToken.THIS; }
  "throw" { return CppToken.THROW; }
  "try" { return CppToken.TRY; }
  "typedef" { return CppToken.TYPEDEF; }
  "typeid" { return CppToken.TYPEID; }
  "typename" { return CppToken.TYPENAME; }
  "union" { return CppToken.UNION; }
  "using" { return CppToken.USING; }
  "virtual" { return CppToken.VIRTUAL; }
  "volatile" { return CppToken.VOLATILE; }
  "while" { return CppToken.WHILE; }
  "return" { return CppToken.RETURN; }

  "NULL" { return CppToken.NULL; }
  "null" { return CppToken.NULL; }
  "true" { return CppToken.TRUE; }
  "false" { return CppToken.FALSE; }

  "bool" { return CppToken.BOOL; }
  "char" { return CppToken.CHAR; }
  "div_t" { return CppToken.DIV_T; }
  "double" { return CppToken.DOUBLE; }
  "float" { return CppToken.FLOAT; }
  "int" { return CppToken.INT; }
  "ldiv_t" { return CppToken.LDIV_T; }
  "long" { return CppToken.LONG; }
  "short" { return CppToken.SHORT; }
  "signed" { return CppToken.SIGNED; }
  "size_t" { return CppToken.SIZE_T; }
  "unsigned" { return CppToken.UNSIGNED; }
  "void" { return CppToken.VOID; }
  "wchar_t"	{ return CppToken.WCHAR_T; }

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
  "wscanf" { return CppToken.FUNCTION; }

  {TRIGRAPH} { return CppToken.TRIGRAPH; }
  "=" { return CppToken.EQ; }
  "+" { return CppToken.PLUS; }
  "-" { return CppToken.MINUS; }
  "*" { return CppToken.MULT; }
  "/" { return CppToken.DIV; }
  "%" { return CppToken.MOD; }
  "~" { return CppToken.TILDA; }
  "<" { return CppToken.LT; }
  ">" { return CppToken.GT; }
  "<<" { return CppToken.LTLT; }
  ">>" { return CppToken.GTGT; }
  "==" { return CppToken.EQEQ; }
  "+=" { return CppToken.PLUSEQ; }
  "-=" { return CppToken.MINUSEQ; }
  "*=" { return CppToken.MULTEQ; }
  "/=" { return CppToken.DIVEQ; }
  "%=" { return CppToken.MODEQ; }
  "&=" { return CppToken.ANDEQ; }
  "|=" { return CppToken.OREQ; }
  "^=" { return CppToken.XOREQ; }
  ">=" { return CppToken.GTEQ; }
  "<=" { return CppToken.LTEQ; }
  "!=" { return CppToken.NOTEQ; }
  ">>=" { return CppToken.GTGTEQ; }
  "<<=" { return CppToken.LTLTEQ; }
  "^" { return CppToken.XOR; }
  "&" { return CppToken.AND; }
  "&&" { return CppToken.ANDAND; }
  "|" { return CppToken.OR; }
  "||" { return CppToken.OROR; }
  "?" { return CppToken.QUEST; }
  ":" { return CppToken.COLON; }
  "," { return CppToken.COMMA; }
  "!" { return CppToken.NOT; }
  "++" { return CppToken.PLUSPLUS; }
  "--" { return CppToken.MINUSMINUS; }
  ";" { return CppToken.SEMICOLON; }
  "(" { return CppToken.LPAREN; }
  ")" { return CppToken.RPAREN; }
  "{" { return CppToken.LBRACE; }
  "}" { return CppToken.RBRACE; }
  "[" { return CppToken.LBRACK; }
  "]" { return CppToken.RBRACK; }

  "__DATE__" { return CppToken.__DATE__; }
  "__TIME__" { return CppToken.__TIME__; }
  "__FILE__" { return CppToken.__FILE__; }
  "__LINE__" { return CppToken.__LINE__; }
  "__STDC__" { return CppToken.__STDC__; }

  {PREPROCESSOR} { return CppToken.PREPROCESSOR; }

  {LINE_COMMENT} { return CppToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return CppToken.BLOCK_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return CppToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return CppToken.SINGLE_QUOTED_STRING; }

  {IDENTIFIER} { return CppToken.IDENTIFIER; }
  {WHITESPACE} { return CppToken.WHITESPACE; }
}

[^] { return CppToken.BAD_CHARACTER; }

<<EOF>> { return CppToken.EOF; }