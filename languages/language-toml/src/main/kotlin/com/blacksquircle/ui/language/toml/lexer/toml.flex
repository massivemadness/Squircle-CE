package com.blacksquircle.ui.language.toml.lexer;

@SuppressWarnings("all")
%%

%public
%class TomlLexer
%type TomlToken
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
LINE_TERMINATOR = \r|\n|\r\n
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]

COMMENT = #[^\n\r]*
BOOLEAN = true|false

BARE_KEY = [_\-a-zA-Z]+
BARE_KEY_OR_NUMBER = -?[0-9]+
BARE_KEY_OR_DATE = {DATE}[Zz]?

DEC_INT = [-+]?(0|[1-9](_?[0-9])*) // no leading zeros
HEX_INT = 0x[0-9a-fA-F](_?[0-9a-fA-F])*
OCT_INT = 0o[0-7](_?[0-7])*
BIN_INT = 0b[01](_?[01])*
INTEGER = {DEC_INT}|{HEX_INT}|{OCT_INT}|{BIN_INT}

EXP = [eE]{DEC_INT}
FRAC = \.[0-9](_?[0-9])*
SPECIAL_FLOAT = [-+]?(inf|nan)
FLOAT = {DEC_INT}({EXP}|{FRAC}{EXP}?)|{SPECIAL_FLOAT}
NUMBER = {FLOAT}|{INTEGER}

DATE = [0-9]{4}-[0-9]{2}-[0-9]{2}
TIME = [0-9]{2}:[0-9]{2}:[0-9]{2}(\.[0-9]+)?
OFFSET = [Zz]|[+-][0-9]{2}:[0-9]{2}
DATE_TIME = ({DATE} ([Tt]{TIME})? | {TIME}) {OFFSET}?

ESCAPE = \\[^]
BASIC_STRING = \"
  ([^\r\n\"] | {ESCAPE})*
(\")?
MULTILINE_BASIC_STRING = (\"\"\")
  ([^\"] | {ESCAPE} | \"[^\"] | \"\"[^\"])*
(\"\"\")?
LITERAL_STRING = \'
  ([^\r\n\'] | {ESCAPE})*
(\')?
MULTILINE_LITERAL_STRING = (\'\'\')
  ([^\'] | {ESCAPE} | \'[^\'] | \'\'[^\'])*
(\'\'\')?

%%

<YYINITIAL> {

  {WHITESPACE} { return TomlToken.WHITESPACE; }
  {COMMENT} { return TomlToken.COMMENT; }
  {BOOLEAN} { return TomlToken.BOOLEAN; }

  {BARE_KEY} { return TomlToken.BARE_KEY; }
  {BARE_KEY_OR_NUMBER} { return TomlToken.BARE_KEY_OR_NUMBER; }
  {BARE_KEY_OR_DATE} { return TomlToken.BARE_KEY_OR_DATE; }
  {NUMBER} { return TomlToken.NUMBER; }
  {DATE_TIME} { return TomlToken.DATE_TIME; }

  {BASIC_STRING} { return TomlToken.BASIC_STRING; }
  {LITERAL_STRING} { return TomlToken.LITERAL_STRING; }
  {MULTILINE_BASIC_STRING} { return TomlToken.MULTILINE_BASIC_STRING; }
  {MULTILINE_LITERAL_STRING} { return TomlToken.MULTILINE_LITERAL_STRING; }

  "=" { return TomlToken.EQ; }
  "," { return TomlToken.COMMA; }
  "." { return TomlToken.DOT; }
  "[" { return TomlToken.LBRACK; }
  "]" { return TomlToken.RBRACK; }
  "{" { return TomlToken.LBRACE; }
  "}" { return TomlToken.RBRACE; }

  {IDENTIFIER} { return TomlToken.IDENTIFIER; }
  {WHITESPACE} { return TomlToken.WHITESPACE; }
}

[^] { return TomlToken.BAD_CHARACTER; }

<<EOF>> { return TomlToken.EOF; }