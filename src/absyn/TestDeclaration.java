package absyn;

import java.io.FileWriter;
import java.io.IOException;

import semantical.TypeChecker;
import types.ClassType;
import types.FixtureSignature;
import types.MethodSignature;
import types.TestSignature;
import types.Type;
import types.TypeList;
import types.VoidType;

public class TestDeclaration extends CodeDeclaration{
private final String name;
	public TestDeclaration(int pos, String name, Command body,ClassMemberDeclaration next) {
		super(pos, null, body, next);
		this.name=name;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void toDotAux(FileWriter where) throws IOException {
		//linkToNode("returnType", returnType.toDot(where), where);
		linkToNode("name", toDot(name, where), where);

		//è senza parametri
		/*if (getFormals() != null)
			linkToNode("formals", getFormals().toDot(where), where);*/

		linkToNode("body", getBody().toDot(where), where);
		
	}

	@Override
	protected void addTo(ClassType clazz) {
		//Type rt = returnType.toType(); niente return
		//TypeList pars = TypeList.EMPTY;//getFormals() != null ? getFormals().toType() : TypeList.EMPTY;
		TestSignature tSig = new TestSignature(clazz, name,this);

		clazz.addTest(name, tSig);

		// we record the signature of this method inside this abstract syntax
		setSignature(tSig);	
		
	}

	@Override
	protected void typeCheckAux(ClassType clazz) {
		//type check del test
		TypeChecker checker = new TypeChecker(VoidType.INSTANCE, clazz.getErrorMsg());
		
		// ora controllo che il test sia coerente con le fixture sperando che si faccia così,
		// e che tutte le fixture siano nella classe 
		for(FixtureSignature fs :clazz.getFixtures()){
			Command c=fs.getAbstractSyntax().getBody();
			checker=c.typeCheck(checker);			
		}
		
		
		super.getBody().typeCheck(checker);
		
		//controllo che ci sia un costruttore vuoto
		if(clazz.constructorLookup(TypeList.EMPTY)==null)
			error(checker,"c'è un test e manca il costruttore vuoto");
		
		boolean stopping = getBody().checkForDeadcode();
		
		
		if(stopping)
			error(checker,"il test torna qualcosa");
		
		//controllo che non ci siano  metodi che portino a confusione
		/*int k=0;
		for(String s:clazz.getTests().keySet())
			if(s.equals(name))
				k++;
		if(k>1)
//			error(checker,"ci sono due test con nome "+name); Ho memorizzato i test in modo che questa operzione risulti ostica
		*/
		ClassType selClass=clazz;
		while(selClass.getName().equals("Object")){
		if(selClass.methodLookup(name,TypeList.EMPTY)!=null)
			error(checker,"il test ha lo stesso nome del metodo "+ name);
		selClass=clazz.getSuperclass();
		}
	}

}
