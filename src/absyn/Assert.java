package absyn;

import java.io.FileWriter;

import bytecode.ASSERT;
import bytecode.CONST;
import bytecode.CONSTRUCTORCALL;
import bytecode.NEWSTRING;
import bytecode.VIRTUALCALL;

import semantical.TypeChecker;
import translation.Block;
import types.ClassType;
import types.CodeSignature;
import types.MethodSignature;
import types.TypeList;
import types.VoidType;

public class Assert extends Command{
	private final Expression condition;
	private final int pos;
	public Assert(int pos,Expression condition){
		super (pos);
		this.pos=pos;
		this.condition=condition;
		
	}
	
	@Override
	protected void toDotAux(FileWriter where) throws java.io.IOException {
		linkToNode("condition", condition.toDot(where), where);		
	}
	
	@Override
	protected TypeChecker typeCheckAux(TypeChecker checker) {
		condition.mustBeBoolean(checker);
	
		return checker;		
	}

	@Override
	public boolean checkForDeadcode() {	//OK così	
		return false;
	}
	
	@Override
	public Block translate(CodeSignature where, Block continuation) {		
		continuation.doNotMerge();
		
		return this.condition.translateAsTest(where,new ASSERT(true,pos).followedBy(continuation),new ASSERT(false,pos).followedBy(continuation));

	}
	
}
