package javaBytecodeGenerator;

import java.util.Set;

import types.ClassMemberSignature;
import types.ClassType;

public class TestJavaClassGenerator extends JavaClassGenerator {

	public TestJavaClassGenerator(ClassType clazz,Set<ClassMemberSignature> sigs) {
		super(clazz, sigs);
		
	}

}
