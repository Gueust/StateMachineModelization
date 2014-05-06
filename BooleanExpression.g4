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
  booleanExpression AND booleanExpression
  | booleanExpression OR booleanExpression
  | '(' booleanExpression ')'
  | expression
;

expression
:
  ID
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

ID
:
  [a-zA-Z0-9_]+
;

//Specify to ignore the whitespace and new line character in the parsed file.

WS
:
  [ \t\r\n]+ -> skip
;