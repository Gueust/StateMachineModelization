grammar FSM_Language;

@lexer::header {
package domainSpecificLanguage.parser;
}

@parser::header {
package domainSpecificLanguage.parser;
}

model: (model_alternatives)* EOF ;

model_alternatives : 
      domain_declaration 
    | variables_declaration
    | commands_declaration
    | external_events
    | internal_events
    | 'transitions' transitions 'end'
    | 'sub' sub 'end' 
    | template;


domain_declaration : 'enumeration' ID '=' '{' list_of_ID '}' ';';
list_of_ID : ID (',' ID)*;

/* Variables declaration */
variables_declaration : 'variables'
  (var_decl)*? 
  'end';

var_decl : ('bool' one_bool_declaration (',' one_bool_declaration)* ';')  #BoolDeclaration
  | (ID one_other_declaration (',' one_other_declaration)* ';') #OtherDeclaration
  ;
one_bool_declaration : ID '(' (TRUE | FALSE) ')';
one_other_declaration : ID '(' ID ')';


external_events : 'external_events' (list_of_ID ';')* 'end';
internal_events : 'internal_events' (list_of_ID ';')* 'end';

commands_declaration : 'commands' (list_of_ID ';')* 'end';

template : 'template'
  ('templating' list_of_ID ';')?
  sub
  'trans' transitions
  'end';
  


/* Sub node declaration */
pair : ID ':' ID;
sub : ('sub' (ID 'instantiate' ID 'with' '{' pair (',' pair)* '}' ';' ))?;

/* Transitions declaration */
transitions : (transition ';')*; 
transition : 'on' list_of_ID 'when' formula 'do' (actions)?;

actions : action (',' action)*;
action : ID #ActionEvent
   | (ID ':=' (ID | (TRUE | FALSE)) ) #ActionAssignment;


/* Putting AND before the OR operator enforces the priority of AND versus OR */
formula :
  '(' formula ')' #bracketExpr
  | NOT formula #notExpr
  | formula AND formula #andExpr
  | formula OR formula #orExpr
  | ID EQUAL ID #andExpr
  | ID NOT_EQUAL ID #andExpr
  | TRUE #trueExpr
  | FALSE #falseExpr
  | ID #idExpr
;

//Tokens
NOT : 'not';
AND : 'and';
OR : 'or';
TRUE :'true';
FALSE :'false';
EQUAL :'==';
NOT_EQUAL : '!=';

ID :
   ('A'..'Z' | 'a'..'z'
   | '0'..'9'
   | '_'
   | [éôàè'*])+;
   
// Specify to ignore the whitespace and new line character in the parsed file.
WS: [ \t\r\n]+ -> skip;
BlockComment : '/*' .*? '*/'-> skip;
LineComment : '//' ~[\r\n]* -> skip;