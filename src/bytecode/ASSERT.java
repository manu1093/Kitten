package bytecode;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

import errorMsg.ErrorMsg;

import translation.Block;
import types.ClassType;
import types.TypeList;

public class ASSERT extends SequentialBytecode{
	boolean result;
	int pos;
	public ASSERT(boolean result,int pos){
		this.result=result;
		this.pos=pos;
	}
	@Override
	public InstructionList generateJavaBytecode(JavaClassGenerator classGen) {
		InstructionList s=new InstructionList();
		InstructionList n=new InstructionList();		
		InstructionFactory f=classGen.getFactory();	
		int riga=0;
		int k=0;
		try {
			
			Scanner sc=new Scanner(new FileReader(classGen.getClassName().substring(0,classGen.getClassName().length()-4)+".kit"));
			while(sc.hasNextLine()&&k<pos){
				riga++;
				String sd=sc.nextLine();
				k+=sd.length();
				if(riga!=1)
					k++;
				//System.out.println(riga+" "+sd+" "+k);
				
				
			}} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		int colonna=k-pos;
		/*
		new ErrorMsg(classGen.getClassName().substring(0,classGen.getClassName().length()-4)+".kit").error(pos, "erewr");*/
		/*
		GETSTATIC gs=f.createGetStatic("java/lang/System/", "out", Type.getType("Ljava/io/PrintStream;"));	
		l.insert(gs);
		l.insert(f.creastringa)
		l.insert(f.createInvoke("java/io/PrintStream","println", Type.VOID, TypeList.EMPTY.push(ClassType.mk("String")).toBCEL(), Constants.INVOKEVIRTUAL));
		System.out.println(l.toString(true));
		System.out.println("passo di qua");
		*/
		
		Type a[]=new Type[1];
		a[0]=Type.STRING;
		if(!result){
			n.insert(f.ARETURN);
			
			n.insert(f.createConstant(+riga+"::"+colonna));
			
			
		}
		return n;
		
	}

}
