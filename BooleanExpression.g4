grammar BooleanExpression;

@lexer::header {
package abstractGraph.Conditions.parser;
}

@parser::header {
package abstractGraph.Conditions.parser;
}

/* Putting AND before the OR operator enforces the priority of AND versus OR */
booleanExpression
:
  booleanExpression AND booleanExpression				#andExpr
  | booleanExpression OR booleanExpression				#orExpr
  | LBRACKET booleanExpression RBRACKET					#bracketExpr
  | ID													#idExpr
;


//Tokens

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

LBRACKET : '(';
RBRACKET : ')';
ID
:
  [a-zA-Z0-9_]+
;

//Specify to ignore the whitespace and new line character in the parsed file.

WS
:
  [ \t\r\n]+ -> skip
;