package com.blacksquircle.ui.language.ruby.lexer;

@SuppressWarnings("all")
%%

%public
%class RubyLexer
%type RubyToken
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

INSTANCE_VARIABLE = "@" \w*?

EMBEDDED_LITERAL = \#\{[^}]*\}

LINE_COMMENT = "#".*
BLOCK_COMMENT = "=begin"\s([^=begin])*"=end"?

%% 

<YYINITIAL> {

  {LONG_LITERAL} { return RubyToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return RubyToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return RubyToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return RubyToken.DOUBLE_LITERAL; }
  {INSTANCE_VARIABLE} { return RubyToken.INSTANCE_VARIABLE; }

  "alias" { return RubyToken.ALIAS; }
  "super" { return RubyToken.SUPER; }
  "self" { return RubyToken.SELF; }
  "undef" { return RubyToken.UNDEF; }
  "class" { return RubyToken.CLASS; }
  "def" { return RubyToken.DEF; }
  "end" { return RubyToken.END; }
  "module" { return RubyToken.MODULE; }
  "return" { return RubyToken.RETURN; }
  "and" { return RubyToken.AND; }
  "begin" { return RubyToken.BEGIN; }
  "break" { return RubyToken.BREAK; }
  "do" { return RubyToken.DO; }
  "ensure" { return RubyToken.ENSURE; }
  "for" { return RubyToken.FOR; }
  "in" { return RubyToken.IN; }
  "next" { return RubyToken.NEXT; }
  "not" { return RubyToken.NOT; }
  "or" { return RubyToken.OR; }
  "redo" { return RubyToken.REDO; }
  "rescue" { return RubyToken.RESCUE; }
  "retry" { return RubyToken.RETRY; }
  "yield" { return RubyToken.YIELD; }
  "until" { return RubyToken.UNTIL; }
  "unless" { return RubyToken.UNLESS; }
  "while" { return RubyToken.WHILE; }
  "if" { return RubyToken.IF; }
  "case" { return RubyToken.CASE; }
  "when" { return RubyToken.WHEN; }
  "then" { return RubyToken.THEN; }
  "else" { return RubyToken.ELSE; }
  "elsif" { return RubyToken.ELSIF; }
  "end" { return RubyToken.END; }
  "until" { return RubyToken.UNTIL; }

  "abort" |
  "catch" |
  "chomp" |
  "chop" |
  "eval" |
  "exec" |
  "exit" |
  "fail" |
  "fork" |
  "format" |
  "gets" |
  "gsub" |
  "Integer" |
  "proc" |
  "lambda" |
  "load" |
  "loop" |
  "open" |
  "p" |
  "print" |
  "proc" |
  "puts" |
  "putc" |
  "pp" |
  "raise" |
  "fail" |
  "rand" |
  "readline" |
  "readlines" |
  "require" |
  "scan" |
  "select" |
  "sleep" |
  "srand" |
  "system" |
  "sub" |
  "throw" { return RubyToken.METHOD; }
  "nil" { return RubyToken.NIL; }
  "true" { return RubyToken.TRUE; }
  "false" { return RubyToken.FALSE; }

  "__ENCODING__" { return RubyToken._ENCODING; }
  "__LINE__" { return RubyToken._LINE; }
  "__FILE__" { return RubyToken._FILE; }
  "defined?" { return RubyToken.DEFINED; }

  "(" { return RubyToken.LPAREN; }
  ")" { return RubyToken.RPAREN; }
  "{" { return RubyToken.LBRACE; }
  "}" { return RubyToken.RBRACE; }
  "[" { return RubyToken.LBRACK; }
  "]" { return RubyToken.RBRACK; }
  "+" { return RubyToken.PLUS; }
  "-" { return RubyToken.MINUS; }
  "*" { return RubyToken.MULT; }
  "**" { return RubyToken.POW; }
  
  "/" { return RubyToken.DIV; }
  "%" { return RubyToken.MOD; }                            
  "<<" { return RubyToken.LTLT; }                          
  ">>" { return RubyToken.GTGT; }                         
  "&" { return RubyToken.AND; }       
  "|" { return RubyToken.OR; }     
  
  "!" { return RubyToken.NOT; }
  "NOT" { return RubyToken.NOT_KEYWORD; }
  "&&" { return RubyToken.ANDAND; }
  "AND" { return RubyToken.AND_KEYWORD; }         
  "||" { return RubyToken.OROR; }
  "OR" { return RubyToken.OR_KEYWORD; }
                              
  "^"  { return RubyToken.XOR; }                          
  "~"  { return RubyToken.TILDE; }                          
  "<"  { return RubyToken.LT; }                          
  ">"  { return RubyToken.GT; }                          
  "<=" { return RubyToken.LTEQ; }                         
  ">=" { return RubyToken.GTEQ; }
  "==" { return RubyToken.EQEQ; }
  "!=" { return RubyToken.NOTEQ; }                        
  "<>" { return RubyToken.LTGT; }                                                
  "," { return RubyToken.COMMA; }
  ":" { return RubyToken.COLON; }
  "." { return RubyToken.DOT; }
  ".." { return RubyToken.RANGE; }
  "`"  { return RubyToken.BACKTICK; }                          
  "=" { return RubyToken.EQ; }
  ";" { return RubyToken.SEMICOLON; }                           
  "+=" { return RubyToken.PLUSEQ; }                           
  "-=" { return RubyToken.MINUSEQ; }                         
  "*=" { return RubyToken.MULTEQ; }                          
  "/="  { return RubyToken.DIVEQ; }                         
  "%="  { return RubyToken.MODEQ; }                         
  "&="  { return RubyToken.ANDEQ; }                         
  "|="  { return RubyToken.OREQ; }                         
  "^="  { return RubyToken.XOREQ; }                         
  ">>="  { return RubyToken.GTGTEQ; }                        
  "<<="  { return RubyToken.LTLTEQ; }                        
  "**="  { return RubyToken.POWEQ; }

  {DOUBLE_QUOTED_STRING} { return RubyToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return RubyToken.SINGLE_QUOTED_STRING; }
  {EMBEDDED_LITERAL} { return RubyToken.EMBEDDED_LITERAL; }
  {IDENTIFIER} { return RubyToken.IDENTIFIER; }
  {WHITESPACE} { return RubyToken.WHITESPACE; }
  {LINE_COMMENT}  { return RubyToken.LINE_COMMENT; }
  {BLOCK_COMMENT} { return RubyToken.BLOCK_COMMENT; }
}

[^] { return RubyToken.BAD_CHARACTER; }

<<EOF>> { return RubyToken.EOF; }