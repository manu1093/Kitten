package javaBytecodeGenerator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.generic.Type;

import translation.Block;
import types.ClassMemberSignature;
import types.ClassType;
import types.ConstructorSignature;
import types.FieldSignature;
import types.MethodSignature;
import types.TestSignature;
import types.TypeList;
import bytecode.BranchingBytecode;

/**
 * A Java bytecode generator. It transforms the Kitten intermediate language
 * into Java bytecode that can be dumped to Java class files and run.
 * It uses the BCEL library to represent Java classes and dump them on the file-system.
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

@SuppressWarnings("serial")
public class JavaClassGenerator extends ClassGen {

	/**
	 * This is a utility class of the BCEL library that helps us generate complex Java bytecodes,
	 * or bytecodes that have different forms depending on the type of their operands,
	 * or require to store constants in the constant pool. We might
	 * generate them without this helper object, but it would be really complex!
	 */

	private final InstructionFactory factory;

	/**
	 * An empty set of interfaces, used to generate the Java class.
	 */

	private final static String[] noInterfaces = new String[] {};

	/**
	 * Builds a class generator for the given class type.
	 *
	 * @param clazz the class type
	 * @param sigs a set of class member signatures. These are those that must be
	 *             translated. If this is {@code null}, all class members are translated
	 */

	public JavaClassGenerator(ClassType clazz, Set<ClassMemberSignature> sigs) {
		super(clazz.getName(), // name of the class
			// the superclass of the Kitten Object class is set to be the Java java.lang.Object class
			//clazz.getSuperclass() != null ? clazz.getSuperclass().getName() : "java.lang.Object",
			clazz.getName().equals("Object")||clazz.getSuperclass().getName().equals("Object")?"java.lang.Object":clazz.getSuperclass().getName(),
			clazz.getName() + ".kit", // source file
			Constants.ACC_PUBLIC, // Java attributes: public!
			noInterfaces, // no interfaces
			new ConstantPoolGen()); // empty constant pool, at the beginning
		//System.out.println(clazz.getSuperclass().getName());
		// create a new instruction factory that places the constants
		// in the previous constant pool. This is useful for generating
		// complex bytecodes that access the constant pool
		this.factory = new InstructionFactory(getConstantPool());

		// we add the fields
		for (FieldSignature field: clazz.getFields().values())
			if (sigs == null || sigs.contains(field))
				field.createField(this);

		// we add the constructors
		for (ConstructorSignature constructor: clazz.getConstructors())
			if (sigs == null || sigs.contains(constructor))
				constructor.createConstructor(this);

		// we add the methods
		for (Set<MethodSignature> s: clazz.getMethods().values())
			for (MethodSignature method: s)
				if (sigs == null || sigs.contains(method))
					method.createMethod(this);
		/*for(int y=0;y<super.getMethods().length;y++)
			System.out.println(super.getMethodAt(y).getCode());*/
	}
	public JavaClassGenerator(ClassType clazz, Set<ClassMemberSignature> sigs,boolean test) {
		super(clazz.getName()+"Test", // name of the class
			// the superclass of the Kitten Object class is set to be the Java java.lang.Object class
			clazz.getName().equals("Object")||clazz.getSuperclass().getName().equals("Object")?"java.lang.Object":clazz.getSuperclass().getName(),
			clazz.getName() + ".kit", // source file
			Constants.ACC_PUBLIC, // Java attributes: public!
			noInterfaces, // no interfaces
			new ConstantPoolGen()); // empty constant pool, at the beginning
		
		// create a new instruction factory that places the constants
		// in the previous constant pool. This is useful for generating
		// complex bytecodes that access the constant pool
		this.factory = new InstructionFactory(getConstantPool());
		
		// we add the fields 
		for (FieldSignature field: clazz.getFields().values())
			if (sigs == null || sigs.contains(field))
				field.createField(this);
		//devo mettere un campo statico per il calcolo del tempo
		
		this.addField(new FieldGen(Constants.ACC_STATIC | Constants.ACC_PRIVATE,Type.LONG,"iTime",factory.getConstantPool()).getField());
		this.addField(new FieldGen(Constants.ACC_STATIC | Constants.ACC_PRIVATE,Type.STRING,"testname",factory.getConstantPool()).getField());
		
		
		// we add the constructors
		for (ConstructorSignature constructor: clazz.getConstructors())			
				constructor.createConstructor(this);
					
		

		// we add the methods
		for (Set<MethodSignature> s: clazz.getMethods().values())
			for (MethodSignature method: s)
				if (sigs == null || sigs.contains(method))
					if(!method.getName().equals("main"))//non il main quello me lo devo creare io
						method.createMethod(this);
		
		//we add the tests
		InstructionList i=new InstructionList();
		InstructionList n=i;
		InstructionFactory f=this.factory;	
		int testk=0;
		for(TestSignature t:clazz.getTests().values())
			testk++;
		ArrayType tTime=new ArrayType(Type.LONG,1);	
		this.addField(new FieldGen(Constants.ACC_STATIC | Constants.ACC_PRIVATE,tTime,"tTime",factory.getConstantPool()).getField());
		ArrayType SS=new ArrayType(Type.STRING,1);	
		this.addField(new FieldGen(Constants.ACC_STATIC | Constants.ACC_PRIVATE,SS,"tRes",factory.getConstantPool()).getField());
		this.addField(new FieldGen(Constants.ACC_STATIC | Constants.ACC_PRIVATE,SS,"tName",factory.getConstantPool()).getField());
		Type[]a=new Type[1];
		
		//PARTE FINALE TABELLA
		
		a[0]=Type.STRING;
		n.insert(f.createInvoke("java/io/PrintStream", "println", Type.VOID, a, Constants.INVOKEVIRTUAL));
		a[0]=Type.STRING;
		n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));	
		i.insert(this.factory.createConstant("ms]"));
		
		a[0]=Type.STRING;
		n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));		
		a[0]=Type.LONG;
		n.insert(f.createInvoke("java/lang/String", "valueOf", Type.STRING, a, Constants.INVOKESTATIC));
		for(int ii=0;ii<testk;ii++){
			if(ii!=testk-1)
				i.insert(this.factory.LADD);				
			i.insert(this.factory.LALOAD);
			i.insert(this.factory.createConstant(ii));
			i.insert(this.factory.createGetStatic(this.getClassName(),"tTime",tTime));
		}
		
		a[0]=Type.STRING;
		n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));		
		i.insert(this.factory.createConstant(" failed ["));
		a[0]=Type.STRING;
		n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));	
		a[0]=Type.INT;
		n.insert(f.createInvoke("java/lang/String", "valueOf", Type.STRING, a, Constants.INVOKESTATIC));
		i.insert(this.factory.ISUB);
		i.insert(this.factory.SWAP);
		i.insert(this.factory.ARRAYLENGTH);
		i.insert(this.factory.createGetStatic(this.getClassName(),"tRes",SS));
		i.insert(this.factory.SWAP);
		
		
		a[0]=Type.STRING;
		n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));//2|2 test passed
		i.insert(this.factory.createConstant(" test passed, "));
		a[0]=Type.INT;
		n.insert(f.createInvoke("java/lang/String", "valueOf", Type.STRING, a, Constants.INVOKESTATIC));//
		i.insert(this.factory.DUP);//2|2
		
		for(int ii=0;ii<testk;ii++){
			if(ii!=testk-1)
				i.insert(this.factory.IADD);
			a[0]=Type.OBJECT;
			i.insert(this.factory.createInvoke("java/lang/String", "equals", Type.BOOLEAN, a, Constants.INVOKEVIRTUAL));
			i.insert(this.factory.createConstant("ok"));		
			i.insert(this.factory.AALOAD);
			i.insert(this.factory.createConstant(ii));
			i.insert(this.factory.createGetStatic(this.getClassName(),"tRes",SS));
		}
		
		n.insert(f.createGetStatic("java/lang/System", "out", Type.getType("Ljava/io/PrintStream;")));
		n.insert(f.createInvoke("java/io/PrintStream", "println", Type.VOID, a, Constants.INVOKEVIRTUAL));
		i.insert(this.factory.createConstant(""));	
		n.insert(f.createGetStatic("java/lang/System", "out", Type.getType("Ljava/io/PrintStream;")));
		
		for(int ii=0;ii<testk;ii++){
			//INTABELLO I TEST
			a[0]=Type.STRING;
			
			InstructionHandle r=n.insert(f.NOP);
			
			n.insert(f.createInvoke("java/io/PrintStream", "println", Type.VOID, a, Constants.INVOKEVIRTUAL));
			n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));
			i.insert(this.factory.AALOAD);
			i.insert(this.factory.createConstant(ii));
			i.insert(this.factory.createGetStatic(this.getClassName(),"tRes",SS));
			
			n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));//out | -  testname  passed [Time ms]
			n.insert(f.createConstant("ms] at "));//|out | -  testname  passed [Time | ms] at
			n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));//out | -  testname  passed [Time
			a[0]=Type.LONG;
			n.insert(f.createInvoke("java/lang/String", "valueOf", Type.STRING, a, Constants.INVOKESTATIC));//out | -  testname  passed [ | Time
			a[0]=Type.STRING;
			
			i.insert(this.factory.LALOAD);
			i.insert(this.factory.createConstant(ii));
			i.insert(this.factory.createGetStatic(this.getClassName(),"tTime",tTime));
			
			n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));//out | "-  testname   failed ["
			n.insert(f.createConstant(" failed ["));// -  testname | " failed ["	
			n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));// -  testname
			
			i.insert(this.factory.AALOAD);
			i.insert(this.factory.createConstant(ii));
			i.insert(this.factory.createGetStatic(this.getClassName(),"tName",SS));
			
			n.insert(f.createConstant(" - "));
								
			n.insert(f.createGetStatic("java/lang/System", "out", Type.getType("Ljava/io/PrintStream;")));//ok|out
			InstructionHandle h=n.insert(f.NOP);
			n.insert(new org.apache.bcel.generic.GOTO(r));
			a[0]=Type.STRING;
			n.insert(f.createInvoke("java/io/PrintStream", "println", Type.VOID, a, Constants.INVOKEVIRTUAL));			
			n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));//out | -  testname  passed [Time ms]
			n.insert(f.createConstant("ms]"));//out | -  testname  passed [Time | ms]
			n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));//out | -  testname  passed [Time
			a[0]=Type.LONG;
			n.insert(f.createInvoke("java/lang/String", "valueOf", Type.STRING, a, Constants.INVOKESTATIC));//out | -  testname  passed [ | Time
			a[0]=Type.STRING;		
			
			i.insert(this.factory.LALOAD);
			i.insert(this.factory.createConstant(ii));
			i.insert(this.factory.createGetStatic(this.getClassName(),"tTime",tTime));
			
			n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));//out | -  testname  passed [
			n.insert(f.createConstant(" passed ["));//out | -  testname | passed [	
			n.insert(f.createInvoke("java/lang/String", "concat", Type.STRING, a, Constants.INVOKEVIRTUAL));//out | -  testname
			
			i.insert(this.factory.AALOAD);
			i.insert(this.factory.createConstant(ii));
			i.insert(this.factory.createGetStatic(this.getClassName(),"tName",SS));
			
			n.insert(f.createConstant(" - "));//out| - 
			n.insert(f.createGetStatic("java/lang/System", "out", Type.getType("Ljava/io/PrintStream;")));//ok|out
			
			i.insert(new org.apache.bcel.generic.IFEQ(h));
			
			a[0]=Type.OBJECT;
			i.insert(this.factory.createInvoke("java/lang/String", "equals", Type.BOOLEAN, a, Constants.INVOKEVIRTUAL));
			i.insert(this.factory.createConstant("ok"));			
			
			i.insert(this.factory.AALOAD);
			i.insert(this.factory.createConstant(ii));
			i.insert(this.factory.createGetStatic(this.getClassName(),"tRes",SS));
			
			n.insert(f.createGetStatic("java/lang/System", "out", Type.getType("Ljava/io/PrintStream;")));
		}
		a[0]=Type.STRING;
		n.insert(f.createInvoke("java/io/PrintStream", "println", Type.VOID, a, Constants.INVOKEVIRTUAL));
		i.insert(this.factory.createConstant("Test execution for class "+clazz.getName()+":"));	
		n.insert(f.createGetStatic("java/lang/System", "out", Type.getType("Ljava/io/PrintStream;")));
		n.insert(f.createInvoke("java/io/PrintStream", "println", Type.VOID, a, Constants.INVOKEVIRTUAL));
		i.insert(this.factory.createConstant(""));	
		n.insert(f.createGetStatic("java/lang/System", "out", Type.getType("Ljava/io/PrintStream;")));
		int k=0;
		for(TestSignature t:clazz.getTests().values()){				
				t.createTest(this);
				
				//SRALVO TEMPO NOME E RISULTATO
				i.insert(this.factory.AASTORE);
				i.insert(this.factory.createGetStatic(this.getClassName(),"testname",Type.STRING));//"ok"|arr
				i.insert(this.factory.createConstant(k));//arr|ok|i
				i.insert(this.factory.createGetStatic(this.getClassName(),"tName",SS));//"ok"|arr
				i.insert(this.factory.AASTORE);
				i.insert(this.factory.SWAP);//arr|I|"ok"
				i.insert(this.factory.createConstant(k));//arr|ok|i
				i.insert(this.factory.SWAP);//arr|ok
				i.insert(this.factory.createGetStatic(this.getClassName(),"tRes",SS));//"ok"|arr
				i.insert(this.factory.LASTORE);
				n.insert(f.LSUB);//ok|time				
				i.insert(f.createGetStatic(this.getClassName(), "iTime", Type.LONG));
				n.insert(f.createInvoke("java/lang/System", "currentTimeMillis", Type.LONG, TypeList.EMPTY.toBCEL(), Constants.INVOKESTATIC));
				i.insert(this.factory.createConstant(k));
				
				i.insert(this.factory.createGetStatic(this.getClassName(),"tTime",tTime));//ok|time|arr
			
				///ESEGUO I TEST
				i.insert(this.factory.createInvoke(clazz.getName()+"Test", t.getName(), Type.STRING,t.getParameters().toBCEL(),Constants.INVOKEVIRTUAL));
				i.insert(this.factory.createInvoke(clazz.getName()+"Test", "<init>",Type.VOID, TypeList.EMPTY.toBCEL(),Constants.INVOKESPECIAL));				
				i.insert(this.factory.DUP);		
				i.insert(factory.createNew(clazz.getName()+"Test"));
				k++;
				}
		
	 ///PARTE INIZIALE DOVE INIZZIALIZZO GLI ARRAY
		tTime=new ArrayType(Type.LONG,1);	
		SS=new ArrayType(Type.STRING,1);	
		i.insert(this.factory.createPutStatic(this.getClassName(),"tName",SS));
		i.insert(this.factory.createNewArray(Type.STRING,(short)1));
		i.insert(this.factory.createConstant(k));
		i.insert(this.factory.createPutStatic(this.getClassName(),"tTime",tTime));
		i.insert(this.factory.createNewArray(Type.LONG,(short)1));
		i.insert(this.factory.createConstant(k));
		i.insert(this.factory.createPutStatic(this.getClassName(),"tRes",SS));
		i.insert(this.factory.createNewArray(Type.STRING,(short)1));
		i.insert(this.factory.createConstant(k));
		i.append(this.factory.RETURN);
		
	
		
		
		MethodGen methodGen = new MethodGen
				(Constants.ACC_PUBLIC | Constants.ACC_STATIC, // public and static
				org.apache.bcel.generic.Type.VOID, // return type
				new org.apache.bcel.generic.Type[] // parameters
					{ new org.apache.bcel.generic.ArrayType("java.lang.String", 1) },
				null, // parameters names: we do not care
				"main", // method's name
				this.getClassName(), // defining class
				i, // bytecode of the method
				this.getConstantPool()); // constant pool
		methodGen.setMaxStack();
		methodGen.setMaxLocals();
		
		this.addMethod(methodGen.getMethod());		
		//System.out.println(super.getJavaClass().toString());
	/*	for(int y=0;y<super.getMethods().length;y++)
			System.out.println(super.getMethodAt(y).getCode());
	*/	
	}
	
	/**
	 * Yields the instruction factory that can be used to create complex
	 * Java bytecodes for this class generator.
	 *
	 * @return the factory
	 */

	public final InstructionFactory getFactory() {
		return factory;
	}

	/**
	 * Generates the Java bytecode for the given block of code and for all
	 * blocks reachable from it. It calls {@link #generateJavaBytecodeFollows(Block, Map, InstructionList)}
	 * and then {@link #removeRedundancies(InstructionList)}.
	 *
	 * @param block the code from which the generation starts
	 * @return the Java bytecode for {@code block} and all blocks reachable from it
	 */

	public InstructionList generateJavaBytecode(Block block) {
		InstructionList instructions = new InstructionList();

		generateJavaBytecode(block, new HashMap<Block, InstructionHandle>(), instructions);

		return removeRedundancies(instructions);
	}

	/**
	 * Auxiliary method that generates the Java bytecode for the given block
	 * of code and for all blocks reachable from it. It uses a set of processed
	 * blocks in order to avoid looping.
	 *
	 * @param block the block from which the code generation starts
	 * @param done the set of blocks which have been already processed
	 * @param instructions the Java bytecode that has already been generated.
	 *                     It gets modified in order to include the Java bytecode generated
	 *                     for {@code block} and for those that are reachable from it
	 * @return a reference to the first instruction of the Java bytecode
	 *         generated for this block
	 */

	private InstructionHandle generateJavaBytecode(Block block, Map<Block, InstructionHandle> done, InstructionList instructions) {
		// we first check if we already processed the block
		InstructionHandle result = done.get(block);
		if (result != null)
			return result;

		// this is the first time that we process the block!

		// we generate the Java bytecode for the code inside the block, and
		// we put it at the end of the instructions already generated
		result = instructions.append(block.getBytecode().generateJavaBytecode(this));

		// we record the beginning of the Java bytecode generated for the block, for future lookup
		done.put(block, result);

		// we process the following blocks
		generateJavaBytecodeFollows(block, done, instructions);

		// we return the beginning of the Java bytecode generated for the block
		return result;
	}

	/**
	 * Auxiliary method that generates the Java bytecode for the blocks
	 * that follow a given block. That Java bytecode might include some
	 * <i>glue</i>, such as the conditional Java bytecode for the branching code blocks.
	 *
	 * @param block the block for whose followers the code is being generated
	 * @param done the set of blocks which have been already processed
	 * @param instructions the Java bytecode that has already been generated from the predecessors
	 *                     and from the code of {@code block}. It gets modified
	 *                     in order to include the Java bytecode generated for the followers
	 *                     of {@code block}, including some <i>glue</i>
	 */
 
	private void generateJavaBytecodeFollows(Block block, Map<Block, InstructionHandle> done, InstructionList instructions) {
		List<Block> follows = block.getFollows();

		// this is where the Java bytecode currently ends
		InstructionHandle ourLast = instructions.getEnd();

		if (!follows.isEmpty())
			if (follows.get(0).getBytecode().getHead() instanceof BranchingBytecode) {
				// we are facing a branch due to a comparinew JavaClassGenerator(clazz, sigs).getJavaClass().dump(clazz + ".class");son bytecode. That bytecode
				// and its negation are at the beginning of our two following blocks

				// we get the condition of the branching
				BranchingBytecode condition = (BranchingBytecode) follows.get(0).getBytecode().getHead();

				// we append the code for the two blocks that follow the block
				InstructionHandle noH = generateJavaBytecode(follows.get(1), done, instructions);
				InstructionHandle yesH = generateJavaBytecode(follows.get(0), done, instructions);

				// in between, we put some code that jumps to yesH if condition holds, and to noH otherwise
				instructions.append(ourLast, condition.generateJavaBytecode(this, yesH, noH));
			}
			else {
				// we append the code for the first follower
				InstructionHandle followJB = generateJavaBytecode(follows.get(0), done, instructions);

				// in between, we put a goto bytecode. Note that we need it since we have no guarantee
				// that the code for follows will be appended exactly after that for the block. The
				// follows blocks might indeed have been already translated into Java bytecode, and hence
				// followJB might be an internal program point in instructions
				instructions.append(ourLast, new GOTO(followJB));
			}
	}

	/**
	 * Simplifies a piece of Java bytecode, by removing:
	 * <ul>
	 * <li> {@code nop} bytecodes
	 * <li> {@code goto} bytecodes that jump to their subsequent program point
	 * </ul>
	 *
	 * @param il the Java bytecode which must be simplified
	 * @return the same Java bytecode, simplified as above
	 */

	private InstructionList removeRedundancies(InstructionList il) {
		@SuppressWarnings("unchecked")
		Iterator<InstructionHandle> it = il.iterator();

		while (it.hasNext()) {
			InstructionHandle handle = it.next();
			Instruction instruction = handle.getInstruction();

			if (instruction instanceof org.apache.bcel.generic.NOP ||
					(instruction instanceof GOTO && ((GOTO) instruction).getTarget() == handle.getNext()))
				try {
					il.redirectBranches(handle, handle.getNext());
					il.delete(handle);
				}
				catch (TargetLostException e) {
					// impossible
				}
		}

		return il;
	}
}