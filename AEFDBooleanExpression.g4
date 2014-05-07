grammar AEFDBooleanExpression;

@lexer::header {
package abstractGraph.conditions.aefdParser;
}

@parser::header {
package abstractGraph.conditions.aefdParser;
}

/* Putting AND before the OR operator enforces the priority of AND versus OR */
booleanExpression
:
  booleanExpression AND booleanExpression				#andExpr
  | booleanExpression OR booleanExpression				#orExpr
  | LBRACKET booleanExpression RBRACKET					#bracketExpr
  | IDNEGATIF											#idnegatifExpr
  | IDPOSITIF											#idpositifExpr
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
IDNEGATIF 
:
  ID'_non_Bloque'
  | ID'_non_Condamne'
  | ID'_non_Decondamne'
  | ID'_non_Etabli'
  | ID'Chute'
  | ID'_Inactif'  
  | ID'_non_Prise'
  | ID'_non_Enclenchee'
  | ID'_Occupee'
  | ID'_Droite'
  | ID'_HS'
  | ID'_Ferme'
  | ID'_non_Controle'
  | ID'_non_Assuree'
  | ID'_non_Vide'
  | ID'_non_Valide'
  | ID'_NM'
  | ID'_Bas'
;
IDPOSITIF
:
  ID'_Bloque'
  | ID'_Condamne'
  | ID'_Decondamne'
  | ID'_Etabli'
  | ID'_Excite'
  | ID'_Actif'  
  | ID'_Prise'
  | ID'_Enclenchee'
  | ID'_Libere'
  | ID'_Gauche'
  | ID'_ES'
  | ID'_Ouvert'
  | ID'_Controle'
  | ID'_Assuree'
  | ID'_Vide'
  | ID'_Valide'
  | ID'_M'
  | ID'_Haut'
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