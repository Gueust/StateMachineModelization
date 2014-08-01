grammar AEFDBooleanExpression;

@lexer::header {
package graph.conditions.aefdParser;
}

@parser::header {
package graph.conditions.aefdParser;
}

/* Putting AND before the OR operator enforces the priority of AND versus OR */
booleanExpression
:
  NOT booleanExpression									#notExpr
  |booleanExpression AND booleanExpression				#andExpr
  | booleanExpression OR booleanExpression				#orExpr
  | LBRACKET booleanExpression RBRACKET					#bracketExpr
  | TRUE                                                #trueExpr
  | FALSE                                               #falseExpr
  | ID											#idExpr
;



//Tokens
TRUE
:
	'true'
	| 'True'
	| 'TRUE'
;

FALSE
:
	'false'
	| 'False'
	| 'FALSE'
;

NOT
:
   'NON'
   |'non'
   |'NOT'
   |'not'
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