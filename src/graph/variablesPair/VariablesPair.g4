grammar VariablesPair;

@lexer::header {
package graph.variablesPair;
}

@parser::header {
package graph.variablesPair;
}

pairs
:
  (
    pair
  )* EOF
;

pair
:
  ID ',' ID
;

ID
:
  (
    'A' .. 'Z'
    | 'a' .. 'z'
    | '0' .. '9'
    | '_'
    | [éôàè'*]
  )+
;

// Specify to ignore the whitespace and new line character in the parsed file.

WS
:
  [ \t\r\n]+ -> skip
;

BlockComment
:
  '/*' .*? '*/' -> skip
;

LineComment
:
  '//' ~[\r\n]* -> skip
;