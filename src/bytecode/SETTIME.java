package bytecode;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

import types.TypeList;

public class SETTIME extends SequentialBytecode{

	@Override
	public InstructionList generateJavaBytecode(JavaClassGenerator classGen) {
		InstructionFactory f=classGen.getFactory();
		InstructionList i=new InstructionList();
		
		i.append(f.createInvoke("java/lang/System", "currentTimeMillis", Type.LONG, TypeList.EMPTY.toBCEL(), Constants.INVOKESTATIC));
		i.append(f.createPutStatic(classGen.getClassName(), "iTime", Type.LONG));
		return i;
	}
	

}
