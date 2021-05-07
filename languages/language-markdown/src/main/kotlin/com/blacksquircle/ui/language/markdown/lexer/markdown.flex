package com.blacksquircle.ui.language.markdown.lexer;

@SuppressWarnings("all")
%%

%public
%class MarkdownLexer
%type MarkdownToken
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

CRLF = [\ \t \f]* \R
LINE_TERMINATOR = \r|\n|\r\n
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]

HEADER = "#".*
UNORDERED_LIST_ITEM = [-*+]" ".*
ORDERED_LIST_ITEM = [0-9]+". ".*

BOLDITALIC1 = "***"([^\\"***"\r\n] | \\{CRLF})*"***"
BOLDITALIC2 = "___"([^\\"___"\r\n] | \\{CRLF})*"___"
BOLD1 = "**"([^\\"**"\r\n] | \\{CRLF})*"**"
BOLD2 = "__"([^\\"__"\r\n] | \\{CRLF})*"__"
ITALIC1 = "*"([^\\"*"\r\n] | \\{CRLF})*"*"
ITALIC2 = "_"([^\\"_"\r\n] | \\{CRLF})*"_"
STRIKETHROUGH = "~~"([^\\"~~"\r\n] | \\{CRLF})*"~~"

CODE = "`"([^\\"`"\r\n] | \\{CRLF})*"`"
CODE_BLOCK = "```" ~"```"

URLGenDelim = ([:\/\?#\[\]@])
URLSubDelim = ([\!\$&'\(\)\*\+,;=])
URLUnreserved = (([A-Za-z]|"_")|[0-9]|[\-\.\~])
URLCharacter = ({URLGenDelim}|{URLSubDelim}|{URLUnreserved}|[%])
URLCharacters = ({URLCharacter}*)
URLEndCharacter = ([\/\$]|[A-Za-z]|[0-9])
URL = (((https?|f(tp|ile))"://"|"www.")({URLCharacters}{URLEndCharacter})?)

%%

<YYINITIAL> {

  {HEADER} { return MarkdownToken.HEADER; }
  {UNORDERED_LIST_ITEM} { return MarkdownToken.UNORDERED_LIST_ITEM; }
  {ORDERED_LIST_ITEM} { return MarkdownToken.ORDERED_LIST_ITEM; }

  {BOLDITALIC1} { return MarkdownToken.BOLDITALIC1; }
  {BOLDITALIC2} { return MarkdownToken.BOLDITALIC2; }
  {BOLD1} { return MarkdownToken.BOLD1; }
  {BOLD2} { return MarkdownToken.BOLD2; }
  {ITALIC1} { return MarkdownToken.ITALIC1; }
  {ITALIC2} { return MarkdownToken.ITALIC2; }
  {STRIKETHROUGH} { return MarkdownToken.STRIKETHROUGH; }

  {CODE} { return MarkdownToken.CODE; }
  {CODE_BLOCK} { return MarkdownToken.CODE_BLOCK; }

  "<" { return MarkdownToken.LT; }
  ">" { return MarkdownToken.GT; }
  "=" { return MarkdownToken.EQ; }
  "!" { return MarkdownToken.NOT; }
  "/" { return MarkdownToken.DIV; }
  "-" { return MarkdownToken.MINUS; }

  "(" { return MarkdownToken.LPAREN; }
  ")" { return MarkdownToken.RPAREN; }
  "{" { return MarkdownToken.LBRACE; }
  "}" { return MarkdownToken.RBRACE; }
  "[" { return MarkdownToken.LBRACK; }
  "]" { return MarkdownToken.RBRACK; }

  {URL} { return MarkdownToken.URL; }
  {IDENTIFIER} { return MarkdownToken.IDENTIFIER; }
  {WHITESPACE} { return MarkdownToken.WHITESPACE; }
}

[^] { return MarkdownToken.BAD_CHARACTER; }

<<EOF>> { return MarkdownToken.EOF; }