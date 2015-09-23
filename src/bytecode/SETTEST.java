package bytecode;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

public class SETTEST extends SequentialBytecode{
	private String test;
	public SETTEST(String test){
		this.test=test;
	}
	@Override
	public InstructionList generateJavaBytecode(JavaClassGenerator classGen) {
		InstructionFactory f=classGen.getFactory();
		InstructionList i=new InstructionList();
		i.append(f.createConstant(this.test));
		i.append(f.createPutStatic(classGen.getClassName(), "testname", Type.STRING));
		return i;
	}

}
