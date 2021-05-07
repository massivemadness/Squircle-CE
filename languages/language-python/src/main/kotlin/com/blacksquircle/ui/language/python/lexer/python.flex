package com.blacksquircle.ui.language.python.lexer;

@SuppressWarnings("all")
%%

%public
%class PythonLexer
%type PythonToken
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
NONZERODIGIT = [1-9]
OCTDIGIT = [0-7]
HEXDIGIT = [0-9A-Fa-f]
BINDIGIT = [01]

HEXINTEGER = 0[Xx]("_"?{HEXDIGIT})+
OCTINTEGER = 0[Oo]?("_"?{OCTDIGIT})+
BININTEGER = 0[Bb]("_"?{BINDIGIT})+
DECIMALINTEGER = (({NONZERODIGIT}("_"?{DIGIT})*)|0)
INTEGER = {DECIMALINTEGER}|{OCTINTEGER}|{HEXINTEGER}|{BININTEGER}
INTEGER_SUFFIX = u|l|ll|U|L|LL|ul|ull|lu|llu|uL|Ul|UL|uLL|Ull|ULL|lU|Lu|LU|llU|LLu|LLU
LONGINTEGER = {INTEGER}{INTEGER_SUFFIX}

FLOATNUMBER = ({POINTFLOAT})|({EXPONENTFLOAT})
POINTFLOAT = (({INTPART})?{FRACTION})|({INTPART}\.)
EXPONENTFLOAT = (({INTPART})|({POINTFLOAT})){EXPONENT}
INTPART = {DIGIT}("_"?{DIGIT})*
FRACTION = \.{INTPART}
EXPONENT = [eE][+\-]?{INTPART}

IMAGNUMBER = (({FLOATNUMBER})|({INTPART}))[Jj]

CRLF = [\ \t \f]* \R
DOUBLE_QUOTED_STRING = \"([^\\\"\r\n] | \\[^\r\n] | \\{CRLF})*\"?
SINGLE_QUOTED_STRING = '([^\\'\r\n] | \\[^\r\n] | \\{CRLF})*'?
LONG_SINGLE_QUOTED_STRING = "'''" ~"'''"
LONG_DOUBLE_QUOTED_STRING = \"\"\" ~\"\"\"

LINE_TERMINATOR = \r|\n|\r\n
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]
DECORATOR = ({WHITESPACE}?"@"{WHITESPACE}?[a-zA-Z]+([^\n])*)

LINE_COMMENT = "#".*

%%

<YYINITIAL> {

  {LONGINTEGER} { return PythonToken.LONG_LITERAL; }
  {INTEGER} { return PythonToken.INTEGER_LITERAL; }
  {FLOATNUMBER} { return PythonToken.FLOAT_LITERAL; }
  {IMAGNUMBER} { return PythonToken.IMAGINARY_LITERAL; }

  "and" { return PythonToken.AND_KEYWORD; }
  "as" { return PythonToken.AS; }
  "assert" { return PythonToken.ASSERT; }
  "break" { return PythonToken.BREAK; }
  "class" { return PythonToken.CLASS; }
  "continue" { return PythonToken.CONTINUE; }
  "def" { return PythonToken.DEF; }
  "del" { return PythonToken.DEL; }
  "elif" { return PythonToken.ELIF; }
  "else" { return PythonToken.ELSE; }
  "except" { return PythonToken.EXCEPT; }
  "exec" { return PythonToken.EXEC; }
  "finally" { return PythonToken.FINALLY; }
  "for" { return PythonToken.FOR; }
  "from" { return PythonToken.FROM; }
  "global" { return PythonToken.GLOBAL; }
  "if" { return PythonToken.IF; }
  "import" { return PythonToken.IMPORT; }
  "in" { return PythonToken.IN; }
  "is" { return PythonToken.IS; }
  "lambda" { return PythonToken.LAMBDA; }
  "not" { return PythonToken.NOT_KEYWORD; }
  "or" { return PythonToken.OR_KEYWORD; }
  "pass" { return PythonToken.PASS; }
  "print" { return PythonToken.PRINT; }
  "raise" { return PythonToken.RAISE; }
  "return" { return PythonToken.RETURN; }
  "try" { return PythonToken.TRY; }
  "while" { return PythonToken.WHILE; }
  "yield" { return PythonToken.YIELD; }

  "char" { return PythonToken.CHAR; }
  "double" { return PythonToken.DOUBLE; }
  "float" { return PythonToken.FLOAT; }
  "int" { return PythonToken.INT; }
  "long" { return PythonToken.LONG; }
  "short" { return PythonToken.SHORT; }
  "signed" { return PythonToken.SIGNED; }
  "unsigned" { return PythonToken.UNSIGNED; }
  "void" { return PythonToken.VOID; }

  "abs" |
  "apply" |
  "bool" |
  "buffer" |
  "callable" |
  "chr" |
  "classmethod" |
  "cmp" |
  "coerce" |
  "compile" |
  "complex" |
  "delattr" |
  "dict" |
  "dir" |
  "divmod" |
  "enumerate" |
  "eval" |
  "execfile" |
  "file" |
  "filter" |
  "float" |
  "getattr" |
  "globals" |
  "hasattr" |
  "hash" |
  "hex" |
  "id" |
  "input" |
  "int" |
  "intern" |
  "isinstance" |
  "issubclass" |
  "iter" |
  "len" |
  "list" |
  "locals" |
  "long" |
  "map" |
  "max" |
  "min" |
  "object" |
  "oct" |
  "open" |
  "ord" |
  "pow" |
  "property" |
  "range" |
  "raw_input" |
  "reduce" |
  "reload" |
  "repr" |
  "round" |
  "setattr" |
  "slice" |
  "staticmethod" |
  "str" |
  "sum" |
  "super" |
  "tuple" |
  "type" |
  "unichr" |
  "unicode" |
  "vars" |
  "xrange" |
  "zip" { return PythonToken.METHOD; }

  "True" { return PythonToken.TRUE; }
  "False" { return PythonToken.FALSE; }
  "None" { return PythonToken.NONE; }

  "+=" { return PythonToken.PLUSEQ; }
  "-=" { return PythonToken.MINUSEQ; }
  "**=" { return PythonToken.EXPEQ; }
  "*=" { return PythonToken.MULTEQ; }
  "@=" { return PythonToken.ATEQ; }
  "//=" { return PythonToken.FLOORDIVEQ; }
  "/=" { return PythonToken.DIVEQ; }
  "%=" { return PythonToken.MODEQ; }
  "&=" { return PythonToken.ANDEQ; }
  "|=" { return PythonToken.OREQ; }
  "^=" { return PythonToken.XOREQ; }
  ">>=" { return PythonToken.GTGTEQ; }
  "<<=" { return PythonToken.LTLTEQ; }
  "<<" { return PythonToken.LTLT; }
  ">>" { return PythonToken.GTGT; }
  "**" { return PythonToken.EXP; }
  "//" { return PythonToken.FLOORDIV; }
  "<=" { return PythonToken.LTEQ; }
  ">=" { return PythonToken.GTEQ; }
  "==" { return PythonToken.EQEQ; }
  "!=" { return PythonToken.NOTEQ; }
  "<>" { return PythonToken.NOTEQ_OLD; }
  "->" { return PythonToken.RARROW; }
  "+" { return PythonToken.PLUS; }
  "-" { return PythonToken.MINUS; }
  "*" { return PythonToken.MULT; }
  "/" { return PythonToken.DIV; }
  "%" { return PythonToken.MOD; }
  "&" { return PythonToken.AND; }
  "|" { return PythonToken.OR; }
  "^" { return PythonToken.XOR; }
  "~" { return PythonToken.TILDE; }
  "<" { return PythonToken.LT; }
  ">" { return PythonToken.GT; }
  "@" { return PythonToken.AT; }
  ":" { return PythonToken.COLON; }
  "`" { return PythonToken.TICK; }
  "=" { return PythonToken.EQ; }
  ":=" { return PythonToken.COLONEQ; }
      
  "(" { return PythonToken.LPAREN; }
  ")" { return PythonToken.RPAREN; }
  "{" { return PythonToken.LBRACE; }
  "}" { return PythonToken.RBRACE; }
  "[" { return PythonToken.LBRACK; }
  "]" { return PythonToken.RBRACK; }
  ";" { return PythonToken.SEMICOLON; }
  "," { return PythonToken.COMMA; }
  "." { return PythonToken.DOT; }

  {DECORATOR} { return PythonToken.DECORATOR; }

  {LINE_COMMENT} { return PythonToken.LINE_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return PythonToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return PythonToken.SINGLE_QUOTED_STRING; }
  {LONG_DOUBLE_QUOTED_STRING} { return PythonToken.LONG_DOUBLE_QUOTED_STRING; }
  {LONG_SINGLE_QUOTED_STRING} { return PythonToken.LONG_SINGLE_QUOTED_STRING; }

  {IDENTIFIER} { return PythonToken.IDENTIFIER; }
  {WHITESPACE} { return PythonToken.WHITESPACE; }
}

[^] { return PythonToken.BAD_CHARACTER; }

<<EOF>> { return PythonToken.EOF; }