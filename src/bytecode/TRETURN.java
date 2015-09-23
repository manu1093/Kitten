package bytecode;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.generic.InstructionList;

public  class TRETURN extends FinalBytecode{

	@Override
	public InstructionList generateJavaBytecode(JavaClassGenerator classGen) {
		InstructionList l=new InstructionList();
		
		l.insert(classGen.getFactory().ARETURN);
		l.insert(classGen.getFactory().createConstant("ok"));
		return l;
	}

}
