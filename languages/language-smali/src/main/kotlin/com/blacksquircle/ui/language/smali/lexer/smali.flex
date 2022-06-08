package com.blacksquircle.ui.language.smali.lexer;

@SuppressWarnings("all")
%%

%public
%class SmaliLexer
%type SmaliToken
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
HEX_DIGIT = [0-9A-Fa-f]
HEX_DIGIT_OR_UNDERSCORE = [_] | {HEX_DIGIT}

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

PRIMITIVE_TYPE = [ZBSCIJFD]
CLASS_DESCRIPTOR = L ({IDENTIFIER} "/")* {IDENTIFIER} ;
// ARRAY_PREFIX = "["+
// TYPE = {PRIMITIVE_TYPE} | {CLASS_DESCRIPTOR} | {ARRAY_PREFIX} ({CLASS_DESCRIPTOR} | {PRIMITIVE_TYPE})

CRLF = [\ \t \f]* \R
DOUBLE_QUOTED_STRING = \"([^\\\"\r\n] | \\[^\r\n] | \\{CRLF})*\"?
SINGLE_QUOTED_STRING = '([^\\'\r\n] | \\[^\r\n] | \\{CRLF})*'?

LINE_COMMENT = "#".*
LINE_TERMINATOR = \r|\n|\r\n
WHITESPACE = {LINE_TERMINATOR} | [ \t\f]

%%

<YYINITIAL> {

  {LONG_LITERAL} { return SmaliToken.LONG_LITERAL; }
  {INTEGER_LITERAL} { return SmaliToken.INTEGER_LITERAL; }
  {FLOAT_LITERAL} { return SmaliToken.FLOAT_LITERAL; }
  {DOUBLE_LITERAL} { return SmaliToken.DOUBLE_LITERAL; }

  ".." { return SmaliToken.DOTDOT; }
  "->" { return SmaliToken.ARROW; }
  "=" { return SmaliToken.EQUAL; }
  ":" { return SmaliToken.COLON; }
  ";" { return SmaliToken.SEMICOLON; }
  "," { return SmaliToken.COMMA; }
  "." { return SmaliToken.DOT; }
  "{" { return SmaliToken.OPEN_BRACE; }
  "}" { return SmaliToken.CLOSE_BRACE; }
  "(" { return SmaliToken.OPEN_PAREN; }
  ")" { return SmaliToken.CLOSE_PAREN; }
  "@" { return SmaliToken.AT; }

  "true" { return SmaliToken.TRUE; }
  "false" { return SmaliToken.FALSE; }
  "null" { return SmaliToken.NULL; }

  ".class" { return SmaliToken.CLASS_DIRECTIVE; }
  ".super" { return SmaliToken.SUPER_DIRECTIVE; }
  ".implements" { return SmaliToken.IMPLEMENTS_DIRECTIVE; }
  ".source" { return SmaliToken.SOURCE_DIRECTIVE; }
  ".field" { return SmaliToken.FIELD_DIRECTIVE; }
  ".end field" { return SmaliToken.END_FIELD_DIRECTIVE; }
  ".subannotation" { return SmaliToken.SUBANNOTATION_DIRECTIVE; }
  ".end subannotation" { return SmaliToken.END_SUBANNOTATION_DIRECTIVE; }
  ".annotation" { return SmaliToken.ANNOTATION_DIRECTIVE; }
  ".end annotation" { return SmaliToken.END_ANNOTATION_DIRECTIVE; }
  ".enum" { return SmaliToken.ENUM_DIRECTIVE; }
  ".method" { return SmaliToken.METHOD_DIRECTIVE; }
  ".end method" { return SmaliToken.END_METHOD_DIRECTIVE; }
  ".registers" { return SmaliToken.REGISTERS_DIRECTIVE; }
  ".locals" { return SmaliToken.LOCALS_DIRECTIVE; }
  ".array-data" { return SmaliToken.ARRAY_DATA_DIRECTIVE; }
  ".end array-data" { return SmaliToken.END_ARRAY_DATA_DIRECTIVE; }
  ".packed-switch" { return SmaliToken.PACKED_SWITCH_DIRECTIVE; }
  ".end packed-switch" { return SmaliToken.END_PACKED_SWITCH_DIRECTIVE; }
  ".sparse-switch" { return SmaliToken.SPARSE_SWITCH_DIRECTIVE; }
  ".end sparse-switch" { return SmaliToken.END_SPARSE_SWITCH_DIRECTIVE; }
  ".catch" { return SmaliToken.CATCH_DIRECTIVE; }
  ".catchall" { return SmaliToken.CATCHALL_DIRECTIVE; }
  ".line" { return SmaliToken.LINE_DIRECTIVE; }
  ".param" { return SmaliToken.PARAMETER_DIRECTIVE; }
  ".end param" { return SmaliToken.END_PARAMETER_DIRECTIVE; }
  ".local" { return SmaliToken.LOCAL_DIRECTIVE; }
  ".end local" { return SmaliToken.END_LOCAL_DIRECTIVE; }
  ".restart local" { return SmaliToken.RESTART_LOCAL_DIRECTIVE; }
  ".prologue" { return SmaliToken.PROLOGUE_DIRECTIVE; }
  ".epilogue" { return SmaliToken.EPILOGUE_DIRECTIVE; }

   {PRIMITIVE_TYPE} { return SmaliToken.PRIMITIVE_TYPE; }
   V { return SmaliToken.VOID_TYPE; }
   {CLASS_DESCRIPTOR} { return SmaliToken.CLASS_TYPE; }

  "build" | "runtime" | "system" {
      return SmaliToken.ANNOTATION_VISIBILITY;
  }

  "public" | "private" | "protected" | "static" | "final" | "synchronized" | "bridge" | "varargs" | "native" |
  "abstract" | "strictfp" | "synthetic" | "constructor" | "declared-synchronized" | "interface" | "enum" |
  "annotation" | "volatile" | "transient" {
      return SmaliToken.ACCESS_SPEC;
  }

  "whitelist" | "greylist" | "blacklist" | "greylist-max-o" | "greylist-max-p" | "greylist-max-q" |
  "core-platform-api" {
      return SmaliToken.HIDDENAPI_RESTRICTION;
  }

  "no-error" | "generic-error" | "no-such-class" | "no-such-field" | "no-such-method" | "illegal-class-access" |
  "illegal-field-access" | "illegal-method-access" | "class-change-error" | "instantiation-error" {
      return SmaliToken.VERIFICATION_ERROR_TYPE;
  }

  "inline@0x" {HEX_DIGIT}+ { return SmaliToken.INLINE_INDEX; }
  "vtable@0x" {HEX_DIGIT}+ { return SmaliToken.VTABLE_INDEX; }
  "field@0x" {HEX_DIGIT}+ { return SmaliToken.FIELD_OFFSET; }

  "static-put" | "static-get" | "instance-put" | "instance-get" {
      return SmaliToken.METHOD_HANDLE_TYPE_FIELD;
  }

  "invoke-instance" | "invoke-constructor" {
      return SmaliToken.METHOD_HANDLE_TYPE_METHOD;
  }

  "goto" {
      return SmaliToken.INSTRUCTION_FORMAT10t;
  }
  "return-void" | "nop" {
      return SmaliToken.INSTRUCTION_FORMAT10x;
  }
  "return-void-barrier" | "return-void-no-barrier" {
      return SmaliToken.INSTRUCTION_FORMAT10x_ODEX;
  }
  "const/4" { return SmaliToken.INSTRUCTION_FORMAT11n; }

  "move-result" | "move-result-wide" | "move-result-object" | "move-exception" | "return" | "return-wide" |
  "return-object" | "monitor-enter" | "monitor-exit" | "throw" {
      return SmaliToken.INSTRUCTION_FORMAT11x;
  }

  "move" | "move-wide" | "move-object" | "array-length" | "neg-int" | "not-int" | "neg-long" | "not-long" |
  "neg-float" | "neg-double" | "int-to-long" | "int-to-float" | "int-to-double" | "long-to-int" | "long-to-float" |
  "long-to-double" | "float-to-int" | "float-to-long" | "float-to-double" | "double-to-int" | "double-to-long" |
  "double-to-float" | "int-to-byte" | "int-to-char" | "int-to-short" {
      return SmaliToken.INSTRUCTION_FORMAT12x_OR_ID;
  }

  "add-int/2addr" | "sub-int/2addr" | "mul-int/2addr" | "div-int/2addr" | "rem-int/2addr" | "and-int/2addr" |
  "or-int/2addr" | "xor-int/2addr" | "shl-int/2addr" | "shr-int/2addr" | "ushr-int/2addr" | "add-long/2addr" |
  "sub-long/2addr" | "mul-long/2addr" | "div-long/2addr" | "rem-long/2addr" | "and-long/2addr" | "or-long/2addr" |
  "xor-long/2addr" | "shl-long/2addr" | "shr-long/2addr" | "ushr-long/2addr" | "add-float/2addr" |
  "sub-float/2addr" | "mul-float/2addr" | "div-float/2addr" | "rem-float/2addr" | "add-double/2addr" |
  "sub-double/2addr" | "mul-double/2addr" | "div-double/2addr" | "rem-double/2addr" {
      return SmaliToken.INSTRUCTION_FORMAT12x;
  }

  "throw-verification-error" {
      return SmaliToken.INSTRUCTION_FORMAT20bc;
  }

  "goto/16" {
      return SmaliToken.INSTRUCTION_FORMAT20t;
  }

  "sget" | "sget-wide" | "sget-object" | "sget-boolean" | "sget-byte" | "sget-char" | "sget-short" | "sput" |
  "sput-wide" | "sput-object" | "sput-boolean" | "sput-byte" | "sput-char" | "sput-short" {
      return SmaliToken.INSTRUCTION_FORMAT21c_FIELD;
  }

  "sget-volatile" | "sget-wide-volatile" | "sget-object-volatile" | "sput-volatile" | "sput-wide-volatile" |
  "sput-object-volatile" {
      return SmaliToken.INSTRUCTION_FORMAT21c_FIELD_ODEX;
  }

  "const-string" {
      return SmaliToken.INSTRUCTION_FORMAT21c_STRING;
  }

  "check-cast" | "new-instance" | "const-class" {
      return SmaliToken.INSTRUCTION_FORMAT21c_TYPE;
  }

  "const-method-handle" {
      return SmaliToken.INSTRUCTION_FORMAT21c_METHOD_HANDLE;
  }

  "const-method-type" {
      return SmaliToken.INSTRUCTION_FORMAT21c_METHOD_TYPE;
  }

  "const/high16" {
      return SmaliToken.INSTRUCTION_FORMAT21ih;
  }

  "const-wide/high16" {
      return SmaliToken.INSTRUCTION_FORMAT21lh;
  }

  "const/16" | "const-wide/16" {
      return SmaliToken.INSTRUCTION_FORMAT21s;
  }

  "if-eqz" | "if-nez" | "if-ltz" | "if-gez" | "if-gtz" | "if-lez" {
      return SmaliToken.INSTRUCTION_FORMAT21t;
  }

  "add-int/lit8" | "rsub-int/lit8" | "mul-int/lit8" | "div-int/lit8" | "rem-int/lit8" | "and-int/lit8" |
  "or-int/lit8" | "xor-int/lit8" | "shl-int/lit8" | "shr-int/lit8" | "ushr-int/lit8" {
      return SmaliToken.INSTRUCTION_FORMAT22b;
  }

  "iget" | "iget-wide" | "iget-object" | "iget-boolean" | "iget-byte" | "iget-char" | "iget-short" | "iput" |
  "iput-wide" | "iput-object" | "iput-boolean" | "iput-byte" | "iput-char" | "iput-short" {
      return SmaliToken.INSTRUCTION_FORMAT22c_FIELD;
  }

  "iget-volatile" | "iget-wide-volatile" | "iget-object-volatile" | "iput-volatile" | "iput-wide-volatile" |
  "iput-object-volatile" {
      return SmaliToken.INSTRUCTION_FORMAT22c_FIELD_ODEX;
  }

  "instance-of" | "new-array" {
      return SmaliToken.INSTRUCTION_FORMAT22c_TYPE;
  }

  "iget-quick" | "iget-wide-quick" | "iget-object-quick" | "iput-quick" | "iput-wide-quick" | "iput-object-quick" |
  "iput-boolean-quick" | "iput-byte-quick" | "iput-char-quick" | "iput-short-quick" {
      return SmaliToken.INSTRUCTION_FORMAT22cs_FIELD;
  }

  "rsub-int" {
      return SmaliToken.INSTRUCTION_FORMAT22s_OR_ID;
  }

  "add-int/lit16" | "mul-int/lit16" | "div-int/lit16" | "rem-int/lit16" | "and-int/lit16" | "or-int/lit16" |
  "xor-int/lit16" {
      return SmaliToken.INSTRUCTION_FORMAT22s;
  }

  "if-eq" | "if-ne" | "if-lt" | "if-ge" | "if-gt" | "if-le" {
      return SmaliToken.INSTRUCTION_FORMAT22t;
  }

  "move/from16" | "move-wide/from16" | "move-object/from16" {
      return SmaliToken.INSTRUCTION_FORMAT22x;
  }

  "cmpl-float" | "cmpg-float" | "cmpl-double" | "cmpg-double" | "cmp-long" | "aget" | "aget-wide" | "aget-object" |
  "aget-boolean" | "aget-byte" | "aget-char" | "aget-short" | "aput" | "aput-wide" | "aput-object" | "aput-boolean" |
  "aput-byte" | "aput-char" | "aput-short" | "add-int" | "sub-int" | "mul-int" | "div-int" | "rem-int" | "and-int" |
  "or-int" | "xor-int" | "shl-int" | "shr-int" | "ushr-int" | "add-long" | "sub-long" | "mul-long" | "div-long" |
  "rem-long" | "and-long" | "or-long" | "xor-long" | "shl-long" | "shr-long" | "ushr-long" | "add-float" |
  "sub-float" | "mul-float" | "div-float" | "rem-float" | "add-double" | "sub-double" | "mul-double" | "div-double" |
  "rem-double" {
      return SmaliToken.INSTRUCTION_FORMAT23x;
  }

  "goto/32" {
      return SmaliToken.INSTRUCTION_FORMAT30t;
  }

  "const-string/jumbo" {
      return SmaliToken.INSTRUCTION_FORMAT31c;
  }

  "const" {
      return SmaliToken.INSTRUCTION_FORMAT31i_OR_ID;
  }

  "const-wide/32" {
      return SmaliToken.INSTRUCTION_FORMAT31i;
  }

  "fill-array-data" | "packed-switch" | "sparse-switch" {
      return SmaliToken.INSTRUCTION_FORMAT31t;
  }

  "move/16" | "move-wide/16" | "move-object/16" {
      return SmaliToken.INSTRUCTION_FORMAT32x;
  }

  "invoke-custom" {
      return SmaliToken.INSTRUCTION_FORMAT35c_CALL_SITE;
  }

  "invoke-virtual" | "invoke-super" {
      return SmaliToken.INSTRUCTION_FORMAT35c_METHOD;
  }

  "invoke-direct" | "invoke-static" | "invoke-interface" {
       return SmaliToken.INSTRUCTION_FORMAT35c_METHOD_OR_METHOD_HANDLE_TYPE;
  }

  "invoke-direct-empty" {
      return SmaliToken.INSTRUCTION_FORMAT35c_METHOD_ODEX;
  }

  "filled-new-array" {
      return SmaliToken.INSTRUCTION_FORMAT35c_TYPE;
  }

  "execute-inline" {
      return SmaliToken.INSTRUCTION_FORMAT35mi_METHOD;
  }

  "invoke-virtual-quick" | "invoke-super-quick" {
      return SmaliToken.INSTRUCTION_FORMAT35ms_METHOD;
  }

  "invoke-custom/range" {
      return SmaliToken.INSTRUCTION_FORMAT3rc_CALL_SITE;
  }

  "invoke-virtual/range" | "invoke-super/range" | "invoke-direct/range" | "invoke-static/range" |
  "invoke-interface/range" {
      return SmaliToken.INSTRUCTION_FORMAT3rc_METHOD;
  }

  "invoke-object-init/range" {
      return SmaliToken.INSTRUCTION_FORMAT3rc_METHOD_ODEX;
  }

  "filled-new-array/range" {
      return SmaliToken.INSTRUCTION_FORMAT3rc_TYPE;
  }

  "execute-inline/range" {
      return SmaliToken.INSTRUCTION_FORMAT3rmi_METHOD;
  }

  "invoke-virtual-quick/range" | "invoke-super-quick/range" {
      return SmaliToken.INSTRUCTION_FORMAT3rms_METHOD;
  }

  "invoke-polymorphic" {
      return SmaliToken.INSTRUCTION_FORMAT45cc_METHOD;
  }

  "invoke-polymorphic/range" {
      return SmaliToken.INSTRUCTION_FORMAT4rcc_METHOD;
  }

  "const-wide" {
      return SmaliToken.INSTRUCTION_FORMAT51l;
  }

  {DOUBLE_QUOTED_STRING} { return SmaliToken.DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING} { return SmaliToken.SINGLE_QUOTED_STRING; }
  {LINE_COMMENT} { return SmaliToken.LINE_COMMENT; }

  {IDENTIFIER} { return SmaliToken.IDENTIFIER; }
  {WHITESPACE} { return SmaliToken.WHITESPACE; }
}

[^] { return SmaliToken.BAD_CHARACTER; }

<<EOF>> { return SmaliToken.EOF; }