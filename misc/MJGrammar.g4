/**
 * Define a grammar called MJGrammar
 * (MiniJava Grammar)
 * @author: ThymiosPlat
 */
grammar MJGrammar;
@ header {
	package antlr;
}

prog                :	 mainClass (classDeclaration)*;
mainClass           : 	'class' ID LB mainMethod RB;
mainMethod          :	'public''static''void''main'LRB 'String' '[' ']' ID RRB LB (statement)+ RB;
classDeclaration    :	'class' ID LB fieldList methodList RB;
fieldList           : 	(field)* ;
methodList			: 	(method)*;
field               : 	type ID SC;
statement           :	stmntBlock
						|assignst|arrAssignSt
						|methodCall SC|ifST|returnSt
						|whileSt|breakSt|continueSt|printSt;
printSt             :'System.out.println'LRB arg RRB SC;
continueSt			: 	'continue' SC;
breakSt				:	 'break' SC;
stmntBlock			: 	LB (statement)* RB;
assignst			:	(ID|property) ASSIGNOP expression SC;
arrAssignSt			:	ID'['(INTEG|ID|property)']'ASSIGNOP expression SC;
ifST				:	'if'LRB boolExpr RRB statement ('else' statement)?;
returnSt			: 	'return' expression SC;
whileSt				: 	'while'LRB boolExpr RRB stmntBlock;
argument:            	arg(','arg)*;
arg					: 	ID|CH|STRING|expression|methodCall;						
expression			:	 LRB expression RRB|ID|property|STRING|BOOLEANLIT|stringConcExpr
						|initExpr|methodCall|arrIdExpr|boolExpr|arExpr;
method				:	 type ID LRB (type ID(','type ID)*)? RRB LB fieldList (statement)* (returnSt)?RB;
methodCall			: 	(ID'.')* ID LRB (argument)?RRB
           				 |(LRB methodCall RRB)'.'methodCall
           				 |methodCall '.' methodCall;           				 
initExpr			:	 'new' (methodCall|type) ;
arrIdExpr			: 	ID'['(INTEG|ID|property)']';
stringConcExpr      :   (STRING|ID|property)PLUS(STRING|ID|property);
arExpr				:	LRB arExpr RRB 
						|arExpr(MULT|DIV)arExpr
						|arExpr(PLUS|MINUS)arExpr
						|(INTEG|ID|property|CH|arrIdExpr|methodCall);
boolExpr			:	LRB boolExpr RRB
						|arExpr COMP arExpr
						|arExpr EQ arExpr 
						|boolExpr (AND|OR)boolExpr
						|NOT boolExpr
						|(ID|property|BOOLEANLIT|arrIdExpr|methodCall) ;
type				: 	ID|'int'|'String'|'char'|'boolean'|'int' '['(INTEG|ID)?']';
property			: 	ID('.'ID)+;
STRING		:  '"' ( ESC_SEQ | ~('\\'|'"') )* '"';
INTEG		: ('0'..'9')+ ;
BOOLEANLIT	:  'true'|'false';
CH		: '\''  ~('\'')  '\'';
ESC_SEQ	:  '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\');
LRB	:'(';
RRB	:')';
LB:'{';
RB:'}';
ASSIGNOP:'=';
PLUS : '+' ;
MINUS : '-' ;
MULT : '*' ;
DIV : '/' ;
AND: '&&';
OR: '||';
NOT: '!';
COMP:'<'('=')?|'>'('=')?;
EQ: '=='|'!=';
ID: ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;
//D:('0'..'9');
SC: ';';
WS:[ \t\r\n]+ -> skip ;
COMMENTL: ('//'~[\r\n]*)->skip;
COMMENTBL: ('/*' .*? '*/')->skip;


