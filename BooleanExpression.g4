grammar BooleanExpression;

@lexer::header {
package abstractGraph.Conditions.parser;
}
 
@parser::header {
package abstractGraph.Conditions.parser;
}

/* Putting AND before the OR operator enforces the priority of AND versus OR */
booleanExpression : booleanExpression AND booleanExpression 
          |booleanExpression OR booleanExpression       
          |'(' booleanExpression ')'
          | expression;
          
expression : ID ;
 
//Tokens
ID : [a-z|0-9]+ ;
AND : 'AND' | 'ET' |'&&' | '&' ;
OR : 'OR' | 'OU' | '||' | '|' ;
//Specify to ignore the whitespace and new line character in the parsed file.
WS : [ \t\r\n]+ -> skip ;