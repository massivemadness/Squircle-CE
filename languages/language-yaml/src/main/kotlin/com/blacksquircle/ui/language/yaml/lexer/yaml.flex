package com.blacksquircle.ui.language.yaml.lexer;

@SuppressWarnings("all")
%%

%public
%class YamlLexer
%type YamlToken
%function advance
%unicode
%line
%column
%char

%{
  private int myBraceCount = 0;
  private int myReturnState = YYINITIAL;
  private int myPrevElementIndent = 0;

  private YamlToken myBlockScalarType = null;
  private boolean myPossiblePlainTextScalarContinue = false;

  public final int getTokenStart() {
      return (int) yychar;
  }

  public final int getTokenEnd() {
      return getTokenStart() + yylength();
  }

  public boolean isCleanState() {
    return yystate() == YYINITIAL
           && myBraceCount == 0
           && yycolumn == 0
           && myPrevElementIndent == 0
           && !myPossiblePlainTextScalarContinue;
  }

  public void cleanMyState() {
    myBraceCount = 0;
    myBlockScalarType = null;

    yycolumn = 0;
    myReturnState = YYINITIAL;

    myPrevElementIndent = 0;
    myPossiblePlainTextScalarContinue = false;
    yybegin(YYINITIAL);
  }

  private char getCharAtOffset(final int offset) {
    final int loc = getTokenStart() + offset;
    return 0 <= loc && loc < zzBuffer.length ? zzBuffer[loc] : (char) -1;
  }

  private boolean isAfterEol() {
    final char prev = getCharAtOffset(-1);
    return prev == (char) -1 || prev == '\n';
  }

  private YamlToken getWhitespaceType() {
    return isAfterEol() ? YamlToken.INDENT : YamlToken.WHITESPACE;
  }

  private void goToState(int state) {
    yybegin(state);
    yypushback(yylength());
  }

  private int getStateAfterLineStart(int indentLen) {
    if (myPossiblePlainTextScalarContinue && yycolumn + indentLen > myPrevElementIndent) {
      return POSSIBLE_PLAIN_TEXT_STATE;
    }
    else {
      myPossiblePlainTextScalarContinue = false;
      return BLOCK_STATE;
    }
  }

  private int getStateAfterBlockScalar() {
    return myReturnState == BLOCK_STATE ? LINE_START_STATE : FLOW_STATE;
  }

  private void openBrace() {
    myBraceCount++;
    if (myBraceCount != 0) {
      myPrevElementIndent = 0;
      myPossiblePlainTextScalarContinue = false;
      yybegin(FLOW_STATE);
    }
  }

  private void closeBrace() {
    if (myBraceCount > 0) {
      myBraceCount--;
    }
    if (myBraceCount == 0){
      yybegin(BLOCK_STATE);
    }
  }

  private YamlToken processScalarKey(int returnState) {
    myPrevElementIndent = yycolumn;
    myReturnState = returnState;
    yybegin(KEY_MODE);
    return YamlToken.SCALAR_KEY;
  }

  private YamlToken processScalarKey() {
    return processScalarKey(yystate());
  }
%}

ANY_CHAR = [^]

NS_CHAR = [^\n\t\r\ ]
NS_INDICATOR = [-?:,\[\]\{\}#&*!|>'\"%@`]

NS_PLAIN_SAFE_flow = [^\n\r\t\ ,\[\]\{\}]
NS_PLAIN_SAFE_block = {NS_CHAR}

NS_PLAIN_FIRST_flow = !(!{NS_CHAR}|{NS_INDICATOR}) | [?:-] {NS_PLAIN_SAFE_flow}
NS_PLAIN_FIRST_block = !(!{NS_CHAR}|{NS_INDICATOR}) | [?:-] {NS_PLAIN_SAFE_block}
NS_PLAIN_FIRST_second_line = [^\n\t\r\ :] | ( ":" {NS_PLAIN_SAFE_block} )

NS_PLAIN_CHAR_flow = {NS_CHAR} "#" | !(!{NS_PLAIN_SAFE_flow}|[:#])  | ":" {NS_PLAIN_SAFE_flow}
NS_PLAIN_CHAR_block = {NS_CHAR} "#" | !(!{NS_PLAIN_SAFE_block}|[:#]) | ":" {NS_PLAIN_SAFE_block}

NB_NS_PLAIN_IN_LINE_flow = "#"* ({WHITE_SPACE_CHAR}* {NS_PLAIN_CHAR_flow})*
NB_NS_PLAIN_IN_LINE_block = "#"* ({WHITE_SPACE_CHAR}* {NS_PLAIN_CHAR_block})*

NS_PLAIN_ONE_LINE_flow = {NS_PLAIN_FIRST_flow}  {NB_NS_PLAIN_IN_LINE_flow}
NS_PLAIN_ONE_LINE_block = {NS_PLAIN_FIRST_block} {NB_NS_PLAIN_IN_LINE_block}

EOL = "\n"
WHITE_SPACE_CHAR = [ \t]
SPACE_SEPARATOR_CHAR = !(![ \t\n])
WHITE_SPACE = {WHITE_SPACE_CHAR}+

LINE = [^\n]*
COMMENT = "#"{LINE}

ID = [^\n\-\ {}\[\]#][^\n{}\[\]>:#]*

KEY_flow = {NS_PLAIN_ONE_LINE_flow}
KEY_block = {NS_PLAIN_ONE_LINE_block}
KEY_SUFIX = {WHITE_SPACE_CHAR}* ":"

INJECTION = ("{{" {ID} "}"{0,2}) | ("%{" [^}\n]* "}"?)

ESCAPE_SEQUENCE = \\[^\n]
DSTRING_SINGLE_LINE = \"([^\\\"\n]|{ESCAPE_SEQUENCE})*\"
DSTRING = \"([^\\\"]|{ESCAPE_SEQUENCE}|\\\n)*\"
STRING_SINGLE_LINE = '([^'\n]|'')*'
STRING = '([^']|'')*'
NS_HEX_DIGIT = [[:digit:]a-fA-F]
NS_WORD_CHAR = [:digit:] | "-" | [a-zA-Z]
NS_URI_CHAR = "%" {NS_HEX_DIGIT} {NS_HEX_DIGIT} | {NS_WORD_CHAR} | [#;\/?:@&=+$,_.!~*'()\[\]]
C_VERBATIM_TAG = "!" "<" {NS_URI_CHAR}+ ">"
NS_TAG_CHAR = "%" {NS_HEX_DIGIT} {NS_HEX_DIGIT} | {NS_WORD_CHAR} | [#;\/?:@&=+$_.~*'()]
C_TAG_HANDLE = "!" {NS_WORD_CHAR}+ "!" | "!" "!" | "!"
C_NS_SHORTHAND_TAG = {C_TAG_HANDLE} {NS_TAG_CHAR}+
C_NON_SPECIFIC_TAG = "!"
C_NS_TAG_PROPERTY = {C_VERBATIM_TAG} | {C_NS_SHORTHAND_TAG} | {C_NON_SPECIFIC_TAG}

NS_ANCHOR_NAME = [^:,\[\]\{\}\s]+
BS_HEADER_ERR_WORD = [^ \t#\n] [^ \t\n]*

C_B_BLOCK_HEADER = ( [:digit:]* ( "-" | "+" )? ) | ( ( "-" | "+" )? [:digit:]* )

// Main states flow:
//
//       | -----------------------------
//       |                              \
//      \/                              |
// LINE_START_STATE ---->BLOCK_STATE ----
//       /\         |      /\  /\       |
//       |          |      |   |       \/
//       |          |     |    ---FLOW_STATE
//       |          |    |
//       |          |   | (syntax error)
//       \         \/  |
//        ----POSSIBLE_PLAIN_TEXT_STATE

%xstate LINE_START_STATE, BLOCK_STATE, FLOW_STATE, POSSIBLE_PLAIN_TEXT_STATE
%xstate ANCHOR_MODE, ALIAS_MODE, KEY_MODE
%xstate BS_HEADER_TAIL_STATE, BS_BODY_STATE

%%

<YYINITIAL, LINE_START_STATE> {
  ("---" | "...") / {NS_CHAR} { goToState(getStateAfterLineStart(0)); }
  "---" { return YamlToken.DOCUMENT_MARKER; }
  "..." { return YamlToken.DOCUMENT_END; }

  {WHITE_SPACE} { yybegin(getStateAfterLineStart(yylength())); return getWhitespaceType(); }
  [^] { goToState(getStateAfterLineStart(0)); }
}

<BLOCK_STATE> {
  {EOL} {
        if (!myPossiblePlainTextScalarContinue && myPrevElementIndent == 0) {
          yybegin(YYINITIAL);
        } else {
          yybegin(LINE_START_STATE);
        }
        return YamlToken.EOL;
      }

  {INJECTION} {NS_PLAIN_ONE_LINE_block} { return YamlToken.TEXT; }
}

<FLOW_STATE> {
  {EOL} /  ( "---" | "..." ) {NS_CHAR}  { return YamlToken.EOL; }
  {EOL} / ( "---" | "..." ) { cleanMyState(); return YamlToken.EOL; }
  {EOL} { return YamlToken.EOL; }

  {INJECTION} {NS_PLAIN_ONE_LINE_flow} { return YamlToken.TEXT; }
  "," { return YamlToken.COMMA; }
}

<BLOCK_STATE, FLOW_STATE> {
  {COMMENT} { return YamlToken.COMMENT; }
  {WHITE_SPACE} { return getWhitespaceType(); }

  ":" { myPrevElementIndent = yycolumn; return YamlToken.COLON; }
  & / {NS_ANCHOR_NAME} {
        myReturnState = yystate();
        yybegin(ANCHOR_MODE);
        return YamlToken.AMPERSAND;
     }
  "*" / {NS_ANCHOR_NAME} {
        myReturnState = yystate();
        yybegin(ALIAS_MODE);
        return YamlToken.STAR;
      }

  {C_NS_TAG_PROPERTY} / ({WHITE_SPACE} | {EOL}) { return YamlToken.TAG; }

  "[" { openBrace(); return YamlToken.LBRACKET; }
  "]" { closeBrace(); return YamlToken.RBRACKET; }
  "{" { openBrace(); return YamlToken.LBRACE; }
  "}" { closeBrace(); return YamlToken.RBRACE; }
  "?" { myPrevElementIndent = yycolumn; return YamlToken.QUESTION; }
  "-" / ({WHITE_SPACE} | {EOL}) {
        myPrevElementIndent = yycolumn;
        return YamlToken.SEQUENCE_MARKER;
      }
  ">" {C_B_BLOCK_HEADER} {
        myReturnState = yystate();
        yybegin(BS_HEADER_TAIL_STATE);
        myBlockScalarType = YamlToken.SCALAR_TEXT;
        return myBlockScalarType;
      }
  "|" {C_B_BLOCK_HEADER} {
        myReturnState = yystate();
        yybegin(BS_HEADER_TAIL_STATE);
        myBlockScalarType = YamlToken.SCALAR_LIST;
        return myBlockScalarType;
      }
  {STRING_SINGLE_LINE} | {DSTRING_SINGLE_LINE} / {KEY_SUFIX} { return processScalarKey(); }
  {STRING} { return YamlToken.SCALAR_STRING; }
  {DSTRING} { return YamlToken.SCALAR_DSTRING; }
}

<BLOCK_STATE> {
  {KEY_block} / {KEY_SUFIX} { return processScalarKey(); }
  {NS_PLAIN_ONE_LINE_block} {
        myPossiblePlainTextScalarContinue = true;
        return YamlToken.TEXT;
      }

  [^] { return YamlToken.TEXT; }
}

<FLOW_STATE> {
  {KEY_flow} / {KEY_SUFIX} { return processScalarKey(); }
  {NS_PLAIN_ONE_LINE_flow} { return YamlToken.TEXT; }

  [^] { return YamlToken.TEXT; }
}


<POSSIBLE_PLAIN_TEXT_STATE> {
  {EOL} { yybegin(LINE_START_STATE); return YamlToken.EOL; }
  {WHITE_SPACE} { return getWhitespaceType(); }
  {COMMENT} { return YamlToken.COMMENT; }

  ":" { goToState(BLOCK_STATE); }

  {STRING_SINGLE_LINE} | {DSTRING_SINGLE_LINE} / {KEY_SUFIX} { return processScalarKey(BLOCK_STATE); }
  {KEY_block} / {KEY_SUFIX} {NS_PLAIN_SAFE_block} { return YamlToken.TEXT; }
  {KEY_block} / {KEY_SUFIX} { return processScalarKey(BLOCK_STATE); }
  {NS_PLAIN_FIRST_second_line} {NB_NS_PLAIN_IN_LINE_block} { return YamlToken.TEXT; }

  [^] { return YamlToken.TEXT; }
}

<ANCHOR_MODE> {
  {NS_ANCHOR_NAME} { yybegin(myReturnState); return YamlToken.ANCHOR; }
  [^] { return YamlToken.TEXT; }
}

<ALIAS_MODE> {
  {NS_ANCHOR_NAME} { yybegin(myReturnState); return YamlToken.ALIAS; }
  [^] { return YamlToken.TEXT; }
}

<KEY_MODE> {
  {WHITE_SPACE} { return getWhitespaceType(); }
  ":" { yybegin(myReturnState); return YamlToken.COLON; }
  [^] { return YamlToken.TEXT; }
}

<BS_HEADER_TAIL_STATE> {
  {WHITE_SPACE} { return getWhitespaceType(); }
  {COMMENT} { return YamlToken.COMMENT; }
  {BS_HEADER_ERR_WORD} ([ \t]* {BS_HEADER_ERR_WORD})* { return YamlToken.TEXT; }
  {EOL} { goToState(BS_BODY_STATE); }
}

<BS_BODY_STATE> {
  {EOL} {WHITE_SPACE_CHAR}* / {NS_CHAR} {
        int indent = yylength() - 1;
        yypushback(indent);
        if (indent <= myPrevElementIndent) {
          yybegin(getStateAfterBlockScalar());
          return YamlToken.EOL;
        } else {
          return YamlToken.SCALAR_EOL;
        }
      }

  {EOL} { return YamlToken.SCALAR_EOL; }
  {WHITE_SPACE} { return getWhitespaceType(); }

  [^ \n\t] {LINE}? {
        assert yycolumn > myPrevElementIndent;
        return myBlockScalarType;
      }

  [^] { return YamlToken.TEXT; }
}

<<EOF>> { return YamlToken.EOF; }