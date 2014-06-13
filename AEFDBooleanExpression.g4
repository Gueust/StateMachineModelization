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
  | IDNEGATIF											#idnegatifExpr
  | IDPOSITIF											#idpositifExpr
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
IDNEGATIF 
:
  ID'_non_Bloque'
  | ID'_non_Condamne'
  | ID'_non_Decondamne'
  | ID'_non_Etabli'
  | ID'_Chute'
  | ID'_Inactif'
  | ID'_Inactive'  
  | ID'_non_Prise'
  | ID'_non_Enclenchee'
  | ID'_non_Enclenche'
  | ID'_Occupee'
  | ID'_Droite'
  | ID'_HS'
  | ID'_Ferme'
  | ID'_non_Controle'
  | ID'_non_Assuree'
  | ID'_non_Assure'
  | ID'_non_Vide'
  | ID'_non_Valide'
  | ID'_NM'
  | ID'_Bas'
  | ID'_en_Action'
  | ID'_non_Pris'
  | ID'_Renverse'
  | ID'_Declenche'
  | ID'_NOK'
  | ID'_non_cde'
  | ID'_non_en_cours'
;
IDPOSITIF
:
  ID'_Bloque'
  | ID'_Condamne'
  | ID'_Decondamne'
  | ID'_Etabli'
  | ID'_Excite'
  | ID'_Actif'
  | ID'_Active'
  | ID'_Prise'
  | ID'_Enclenchee'
  | ID'_Enclenche'
  | ID'_Libre'
  | ID'_Gauche'
  | ID'_ES'
  | ID'_Ouvert'
  | ID'_Controle'
  | ID'_Assuree'
  | ID'_Assure'
  | ID'_Vide'
  | ID'_Valide'
  | ID'_M'
  | ID'_Haut'
  | ID'_Libere'
  | ID'_Pris'
  | ID'_Normal'
  | ID'_Rearme'
  | ID'_OK'
  | ID'_cde'  
  | ID'_en_cours'
  

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