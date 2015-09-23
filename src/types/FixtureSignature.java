package types;

import absyn.CodeDeclaration;
import translation.Block;

public class FixtureSignature extends CodeSignature{

	public FixtureSignature(ClassType clazz, CodeDeclaration abstractSyntax) {
		super(clazz, VoidType.INSTANCE,TypeList.EMPTY,"", abstractSyntax);
		
	}

	@Override
	protected Block addPrefixToCode(Block code) {
		//probabilmente non va nulla ma ci si ripenserà
		return code;
	}
	 

}
