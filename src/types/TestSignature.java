package types;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import bytecode.FIXTEST;
import bytecode.SETTEST;
import bytecode.SETTIME;

import javaBytecodeGenerator.JavaClassGenerator;
import translation.Block;
import absyn.TestDeclaration;

public class TestSignature extends CodeSignature{
	public TestSignature(ClassType clazz,String name,TestDeclaration abstractSyntax){
		super(clazz,VoidType.INSTANCE,TypeList.EMPTY,name,abstractSyntax);
	}

	@Override
	protected Block addPrefixToCode(Block code) {				
		return new FIXTEST(super.getDefiningClass().getFixtures()).followedBy(new SETTIME().followedBy(new SETTEST(this.getName()).followedBy(code)));
		//return code;
	}
	public void createTest(JavaClassGenerator classGen){
		
		MethodGen methodGen;		
		methodGen = new MethodGen
				(Constants.ACC_PRIVATE, // private
				Type.STRING, // return type
				TypeList.EMPTY.toBCEL(), // parameters types, if any
				null, // parameters names: we do not care
				getName().toString(), // method's name
				classGen.getClassName(), // defining class
				classGen.generateJavaBytecode(getCode()), // bytecode of the method E IL TEMPO DEL TEST
				classGen.getConstantPool()); // constant pool
		
		/*InstructionList l =methodGen.getInstructionList();
		l.append(classGen.getFactory().createConstant("ok"));
		l.append(classGen.getFactory().ARETURN);*/
		// we must always call these methods before the getMethod()
		// method below. They set the number of local variables and stack
		// elements used by the code of the method
		methodGen.setMaxStack();
		methodGen.setMaxLocals();
		
		// we add a method to the class that we are generating
		classGen.addMethod(methodGen.getMethod());
	}

}
