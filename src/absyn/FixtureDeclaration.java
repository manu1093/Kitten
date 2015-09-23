package absyn;

import java.io.FileWriter;
import java.io.IOException;

import semantical.TypeChecker;
import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;
import types.CodeSignature;
import types.FixtureSignature;
import types.VoidType;


public class FixtureDeclaration extends CodeDeclaration{
	
	
	
	
	public FixtureDeclaration(int pos, Command body, ClassMemberDeclaration next) {
		super(pos,null,body,next);		
		
	}


	
	@Override
	protected void toDotAux(FileWriter where) throws IOException {
		
		linkToNode("body", getBody().toDot(where), where);
		
	}


	@Override
	protected void addTo(ClassType clazz) {
		FixtureSignature f=new FixtureSignature(clazz,this);
		clazz.addFixture(f);
		super.setSignature(f);		
	}


	@Override
	protected void typeCheckAux(ClassType clazz) {
		//type check del fixture
		TypeChecker checker = new TypeChecker(VoidType.INSTANCE, clazz.getErrorMsg());		
		
		//il typecheck lo facco nel test!
		
		boolean stopping = getBody().checkForDeadcode();		
		if(stopping)
			error(checker,"la Fixture torna qualcosa");
		
	}



}
