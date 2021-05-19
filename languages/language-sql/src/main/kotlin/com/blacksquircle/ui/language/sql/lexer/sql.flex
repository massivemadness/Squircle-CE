package com.blacksquircle.ui.language.sql.lexer;

@SuppressWarnings("all")
%%

%public
%class SqlLexer
%type SqlToken
%function advance
%caseless
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

INTEGER_LITERAL = {DIGITS}

FLOAT_LITERAL = ({DEC_FP_LITERAL}) [Ff] | {DIGITS} [Ff]
DOUBLE_LITERAL = ({DEC_FP_LITERAL}) [Dd]? | {DIGITS} [Dd]
DEC_FP_LITERAL = {DIGITS} {DEC_EXPONENT} | {DEC_SIGNIFICAND} {DEC_EXPONENT}?
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT_OR_UNDERSCORE}*
DEC_EXPONENT = [Ee] [+-]? {DIGIT_OR_UNDERSCORE}*

CRLF = [\ \t \f]* \R
DOUBLE_QUOTED_STRING = \"([^\\\"\r\n] | \\[^\r\n] | \\{CRLF})*\"?
SINGLE_QUOTED_STRING = '([^\\'\r\n] | \\[^\r\n] | \\{CRLF})*'?

LINE_TERMINATOR = \r|\n|\r\n
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]

LINE_COMMENT = "--".*
BLOCK_COMMENT = "/*" ~"*/"

%%

<YYINITIAL> {

  {INTEGER_LITERAL} { return SqlToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return SqlToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return SqlToken.DOUBLE_LITERAL; }

  "ADD" { return SqlToken.ADD; }
  "ALL" { return SqlToken.ALL; }
  "ALTER" { return SqlToken.ALTER; }
  "AND" { return SqlToken.AND; }
  "ANY" { return SqlToken.ANY; }
  "AS" { return SqlToken.AS; }
  "ASC" { return SqlToken.ASC; }
  "AUTOINCREMENT" { return SqlToken.AUTOINCREMENT; }
  "AVA" { return SqlToken.AVA; }
  "BETWEEN" { return SqlToken.BETWEEN; }
  "BINARY" { return SqlToken.BINARY; }
  "BIT" { return SqlToken.BIT; }
  "BLOB" { return SqlToken.BLOB; }
  "BOOLEAN" { return SqlToken.BOOLEAN; }
  "BY" { return SqlToken.BY; }
  "BYTE" { return SqlToken.BYTE; }
  "CHAR" { return SqlToken.CHAR; }
  "CHARACTER" { return SqlToken.CHARACTER; }
  "COLUMN" { return SqlToken.COLUMN; }
  "CONSTRAINT" { return SqlToken.CONSTRAINT; }
  "COUNT" { return SqlToken.COUNT; }
  "COUNTER" { return SqlToken.COUNTER; }
  "CREATE" { return SqlToken.CREATE; }
  "CURRENCY" { return SqlToken.CURRENCY; }
  "DATABASE" { return SqlToken.DATABASE; }
  "DATE" { return SqlToken.DATE; }
  "DATETIME" { return SqlToken.DATETIME; }
  "DELETE" { return SqlToken.DELETE; }
  "DESC" { return SqlToken.DESC; }
  "DISALLOW" { return SqlToken.DISALLOW; }
  "DISTINCT" { return SqlToken.DISTINCT; }
  "DISTINCTROW" { return SqlToken.DISTINCTROW; }
  "DOUBLE" { return SqlToken.DOUBLE; }
  "DROP" { return SqlToken.DROP; }
  "EXISTS" { return SqlToken.EXISTS; }
  "FLOAT" { return SqlToken.FLOAT; }
  "FLOAT4" { return SqlToken.FLOAT4; }
  "FLOAT8" { return SqlToken.FLOAT8; }
  "FOREIGN" { return SqlToken.FOREIGN; }
  "FROM" { return SqlToken.FROM; }
  "GENERAL" { return SqlToken.GENERAL; }
  "GROUP" { return SqlToken.GROUP; }
  "GUID" { return SqlToken.GUID; }
  "HAVING" { return SqlToken.HAVING; }
  "INNER" { return SqlToken.INNER; }
  "INSERT" { return SqlToken.INSERT; }
  "IGNORE" { return SqlToken.IGNORE; }
  "IF" { return SqlToken.IF; }
  "IMP" { return SqlToken.IMP; }
  "IN" { return SqlToken.IN; }
  "INDEX" { return SqlToken.INDEX; }
  "INT" { return SqlToken.INT; }
  "INTEGER" { return SqlToken.INTEGER; }
  "INTEGER1" { return SqlToken.INTEGER1; }
  "INTEGER2" { return SqlToken.INTEGER2; }
  "INTEGER4" { return SqlToken.INTEGER4; }
  "INTO" { return SqlToken.INTO; }
  "IS" { return SqlToken.IS; }
  "JOIN" { return SqlToken.JOIN; }
  "KEY" { return SqlToken.KEY; }
  "LEFT" { return SqlToken.LEFT; }
  "LEVEL" { return SqlToken.LEVEL; }
  "LIKE" { return SqlToken.LIKE; }
  "LOGICAL" { return SqlToken.LOGICAL; }
  "LONG" { return SqlToken.LONG; }
  "LONGBINARY" { return SqlToken.LONGBINARY; }
  "LONGTEXT" { return SqlToken.LONGTEXT; }
  "MAX" { return SqlToken.MAX; }
  "MEMO" { return SqlToken.MEMO; }
  "MIN" { return SqlToken.MIN; }
  "MOD" { return SqlToken.MOD; }
  "MONEY" { return SqlToken.MONEY; }
  "NOT" { return SqlToken.NOT; }
  "NULL" { return SqlToken.NULL; }
  "NUMBER" { return SqlToken.NUMBER; }
  "NUMERIC" { return SqlToken.NUMERIC; }
  "OLEOBJECT" { return SqlToken.OLEOBJECT; }
  "ON" { return SqlToken.ON; }
  "OPTION" { return SqlToken.OPTION; }
  "OR" { return SqlToken.OR; }
  "ORDER" { return SqlToken.ORDER; }
  "OUTER" { return SqlToken.OUTER; }
  "OWNERACCESS" { return SqlToken.OWNERACCESS; }
  "PARAMETERS" { return SqlToken.PARAMETERS; }
  "PASSWORD" { return SqlToken.PASSWORD; }
  "PERCENT" { return SqlToken.PERCENT; }
  "PIVOT" { return SqlToken.PIVOT; }
  "PRIMARY" { return SqlToken.PRIMARY; }
  "REAL" { return SqlToken.REAL; }
  "REFERENCES" { return SqlToken.REFERENCES; }
  "RIGHT" { return SqlToken.RIGHT; }
  "SELECT" { return SqlToken.SELECT; }
  "SET" { return SqlToken.SET; }
  "SHORT" { return SqlToken.SHORT; }
  "SINGLE" { return SqlToken.SINGLE; }
  "SMALLINT" { return SqlToken.SMALLINT; }
  "SOME" { return SqlToken.SOME; }
  "STDEV" { return SqlToken.STDEV; }
  "STDEVP" { return SqlToken.STDEVP; }
  "STRING" { return SqlToken.STRING; }
  "SUM" { return SqlToken.SUM; }
  "TABLE" { return SqlToken.TABLE; }
  "TABLEID" { return SqlToken.TABLEID; }
  "TEXT" { return SqlToken.TEXT; }
  "TIME" { return SqlToken.TIME; }
  "TIMESTAMP" { return SqlToken.TIMESTAMP; }
  "TOP" { return SqlToken.TOP; }
  "TRANSFORM" { return SqlToken.TRANSFORM; }
  "TYPE" { return SqlToken.TYPE; }
  "UNION" { return SqlToken.UNION; }
  "UNIQUE" { return SqlToken.UNIQUE; }
  "UPDATE" { return SqlToken.UPDATE; }
  "USER" { return SqlToken.USER; }
  "VALUE" { return SqlToken.VALUE; }
  "VALUES" { return SqlToken.VALUES; }
  "VAR" { return SqlToken.VAR; }
  "VARBINARY" { return SqlToken.VARBINARY; }
  "VARCHAR" { return SqlToken.VARCHAR; }
  "VARP" { return SqlToken.VARP; }
  "VIEW" { return SqlToken.VIEW; }
  "WHERE" { return SqlToken.WHERE; }
  "WITH" { return SqlToken.WITH; }
  "YESNO" { return SqlToken.YESNO; }
  "AVG" { return SqlToken.AVG; }
  "CURRENT_DATE" { return SqlToken.CURRENT_DATE; }
  "CURRENT_TIME" { return SqlToken.CURRENT_TIME; }
  "CURRENT_TIMESTAMP" { return SqlToken.CURRENT_TIMESTAMP; }
  "CURRENT_USER" { return SqlToken.CURRENT_USER; }
  "SESSION_USER" { return SqlToken.SESSION_USER; }
  "SYSTEM_USER" { return SqlToken.SYSTEM_USER; }
  "BIT_LENGTH" { return SqlToken.BIT_LENGTH; }
  "CHAR_LENGTH" { return SqlToken.CHAR_LENGTH; }
  "EXTRACT" { return SqlToken.EXTRACT; }
  "OCTET_LENGTH" { return SqlToken.OCTET_LENGTH; }
  "POSITION" { return SqlToken.POSITION; }
  "CONCATENATE" { return SqlToken.CONCATENATE; }
  "CONVERT" { return SqlToken.CONVERT; }
  "LOWER" { return SqlToken.LOWER; }
  "SUBSTRING" { return SqlToken.SUBSTRING; }
  "TRANSLATE" { return SqlToken.TRANSLATE; }
  "TRIM" { return SqlToken.TRIM; }
  "UPPER" { return SqlToken.UPPER; }

  ">=" { return SqlToken.GTEQ; }
  "<=" { return SqlToken.LTEQ; }
  "<>" { return SqlToken.LTGT; }
  ">" { return SqlToken.GT; }
  "<" { return SqlToken.LT; }
  "=" { return SqlToken.EQ; }
  "+" { return SqlToken.PLUS; }
  "-" { return SqlToken.MINUS; }
  "*" { return SqlToken.MULT; }
  "/" { return SqlToken.DIV; }
  "`" { return SqlToken.BACKTICK; }
      
  "(" { return SqlToken.LPAREN; }
  ")" { return SqlToken.RPAREN; }
  "[" { return SqlToken.LBRACK; }
  "]" { return SqlToken.RBRACK; }
  ";" { return SqlToken.SEMICOLON; }
  "," { return SqlToken.COMMA; }
  "." { return SqlToken.DOT; }

  {LINE_COMMENT} { return SqlToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return SqlToken.BLOCK_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return SqlToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return SqlToken.SINGLE_QUOTED_STRING; }

  {IDENTIFIER} { return SqlToken.IDENTIFIER; }
  {WHITESPACE} { return SqlToken.WHITESPACE; }
}

[^] { return SqlToken.BAD_CHARACTER; }

<<EOF>> { return SqlToken.EOF; }