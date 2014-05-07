grammar BooleanExpression;

@lexer::header {
package abstractGraph.conditions.parser;
}

@parser::header {
package abstractGraph.conditions.parser;
}

/* Putting AND before the OR operator enforces the priority of AND versus OR */
booleanExpression
:
  NOT booleanExpression                     # notExpr
  | booleanExpression AND booleanExpression # andExpr
  | booleanExpression OR booleanExpression  # orExpr
  | LBRACKET booleanExpression RBRACKET     # bracketExpr
  | ID                                      # idExpr
;

//Tokens

NOT
:
  'NOT'
  | 'NON'
  | 'not'
;

AND
:
  'AND'
  | 'ET'
  | '&&'
  | '&'
;

OR
:
  'OR'
  | 'OU'
  | '||'
  | '|'
;

LBRACKET
:
  '('
;

RBRACKET
:
  ')'
;

ID
:
  [a-zA-Z0-9_]+
;

//Specify to ignore the whitespace and new line character in the parsed file.

WS
:
  [ \t\r\n]+ -> skip
;