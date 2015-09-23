package bytecode;

import java.util.List;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.generic.InstructionList;

import types.FixtureSignature;

public class FIXTEST extends SequentialBytecode {
	private List <FixtureSignature> fixList;
	public FIXTEST(List <FixtureSignature> a){
		this.fixList=a;
	}
	@Override
	public InstructionList generateJavaBytecode(JavaClassGenerator classGen) {
		InstructionList l=new InstructionList();
		for(FixtureSignature fsig:fixList)
			l.append(classGen.generateJavaBytecode(fsig.getCode()));
		
		return l;
	}

}
