package com.blacksquircle.ui.language.visualbasic.lexer;

@SuppressWarnings("all")
%%

%public
%class VisualBasicLexer
%type VisualBasicToken
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

LINE_COMMENT = "'".*

%%

<YYINITIAL> {

  {LONG_LITERAL} { return VisualBasicToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return VisualBasicToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return VisualBasicToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return VisualBasicToken.DOUBLE_LITERAL; }

  "AddHandler" |
  "AddressOf" |
  "Alias" |
  "And" |
  "AndAlso" |
  "As" |
  "ByRef" |
  "ByVal" |
  "Call" |
  "Case" |
  "Catch" |
  "CBool" |
  "CByte" |
  "CChar" |
  "CDate" |
  "CDbl" |
  "CDec" |
  "CInt" |
  "Class" |
  "CLng" |
  "CObj" |
  "Const" |
  "Continue" |
  "CSByte" |
  "CShort" |
  "CSng" |
  "CStr" |
  "CType" |
  "CUInt" |
  "CULng" |
  "CUShort" |
  "Declare" |
  "Default" |
  "Delegate" |
  "Dim" |
  "DirectCast" |
  "Do" |
  "Each" |
  "Else" |
  "ElseIf" |
  "End" |
  "EndIf" |
  "Enum" |
  "Erase" |
  "Error" |
  "Event" |
  "Exit" |
  "Finally" |
  "For" |
  "Friend" |
  "Function" |
  "Get" |
  "GetType" |
  "GetXMLNamespace" |
  "Global" |
  "GoSub" |
  "GoTo" |
  "Handles" |
  "If" |
  "Implements" |
  "Imports" |
  "In" |
  "Inherits" |
  "Interface" |
  "Is" |
  "IsNot" |
  "Let" |
  "Lib" |
  "Like" |
  "Loop" |
  "Me" |
  "Mod" |
  "Module" |
  "Module Statement" |
  "MustInherit" |
  "MustOverride" |
  "MyBase" |
  "MyClass" |
  "Namespace" |
  "Narrowing" |
  "New" |
  "New" |
  "Next" |
  "Not" |
  "Nothing" |
  "NotInheritable" |
  "NotOverridable" |
  "Of" |
  "On" |
  "Operator" |
  "Option" |
  "Optional" |
  "Or" |
  "OrElse" |
  "Out" |
  "Overloads" |
  "Overridable" |
  "Overrides" |
  "ParamArray" |
  "Partial" |
  "Private" |
  "Property" |
  "Protected" |
  "Public" |
  "RaiseEvent" |
  "ReadOnly" |
  "ReDim" |
  "REM" |
  "RemoveHandler" |
  "Resume" |
  "Select" |
  "Set" |
  "Shadows" |
  "Shared" |
  "Static" |
  "Step" |
  "Stop" |
  "Structure" |
  "Sub" |
  "SyncLock" |
  "Then" |
  "Throw" |
  "To" |
  "Try" |
  "TryCast" |
  "TypeOf" |
  "Using" |
  "Variant" |
  "Wend" |
  "When" |
  "While" |
  "Widening" |
  "With" |
  "WithEvents" |
  "WriteOnly" |
  "Xor" |
  "Return" { return VisualBasicToken.KEYWORD; }

  "Boolean" { return VisualBasicToken.BOOLEAN; }
  "Byte" { return VisualBasicToken.BYTE; }
  "Char" { return VisualBasicToken.CHAR; }
  "Date" { return VisualBasicToken.DATE; }
  "Decimal" { return VisualBasicToken.DECIMAL; }
  "Double" { return VisualBasicToken.DOUBLE; }
  "Integer" { return VisualBasicToken.INTEGER; }
  "Long" { return VisualBasicToken.LONG; }
  "Object" { return VisualBasicToken.OBJECT; }
  "SByte" { return VisualBasicToken.SBYTE; }
  "Short" { return VisualBasicToken.SHORT; }
  "Single" { return VisualBasicToken.SINGLE; }
  "String" { return VisualBasicToken.STRING; }
  "UInteger" { return VisualBasicToken.UINTEGER; }
  "ULong" { return VisualBasicToken.ULONG; }
  "UShort" { return VisualBasicToken.USHORT; }

  ([tT]rue) { return VisualBasicToken.TRUE; }
  ([fF]alse) { return VisualBasicToken.FALSE; }

  "&" { return VisualBasicToken.AND; }
  "&=" { return VisualBasicToken.ANDEQ; }
  "*" { return VisualBasicToken.MULT; }
  "*=" { return VisualBasicToken.MULTEQ; }
  "+" { return VisualBasicToken.PLUS; }
  "+=" { return VisualBasicToken.PLUSEQ; }
  "=" { return VisualBasicToken.EQ; }
  "-" { return VisualBasicToken.MINUS; }
  "-=" { return VisualBasicToken.MINUSEQ; }
  "<" { return VisualBasicToken.LT; }
  "<<" { return VisualBasicToken.LTLT; }
  "<<=" { return VisualBasicToken.LTLTEQ; }
  ">" { return VisualBasicToken.GT; }
  ">>" { return VisualBasicToken.GTGT; }
  ">>=" { return VisualBasicToken.GTGTEQ; }
  "/" { return VisualBasicToken.DIV; }
  "/=" { return VisualBasicToken.DIVEQ; }
  "\\" { return VisualBasicToken.BACKSLASH; }
  "^" { return VisualBasicToken.XOR; }
  "^=" { return VisualBasicToken.XOREQ; }

  "(" { return VisualBasicToken.LPAREN; }
  ")" { return VisualBasicToken.RPAREN; }
  "{" { return VisualBasicToken.LBRACE; }
  "}" { return VisualBasicToken.RBRACE; }
  "[" { return VisualBasicToken.LBRACK; }
  "]" { return VisualBasicToken.RBRACK; }
  ";" { return VisualBasicToken.SEMICOLON; }
  "," { return VisualBasicToken.COMMA; }
  "." { return VisualBasicToken.DOT; }

  {LINE_COMMENT} { return VisualBasicToken.LINE_COMMENT; }

  {DOUBLE_QUOTED_STRING} { return VisualBasicToken.DOUBLE_QUOTED_STRING; }

  {IDENTIFIER} { return VisualBasicToken.IDENTIFIER; }
  {WHITESPACE} { return VisualBasicToken.WHITESPACE; }
}

[^] { return VisualBasicToken.BAD_CHARACTER; }

<<EOF>> { return VisualBasicToken.EOF; }