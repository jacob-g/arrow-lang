package executor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import memory.MemoryEntry;
import memory.RuntimeDataStack;
import parser.tree.ParseTreeAttributeType;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;

final class FunctionCallExecutor extends AbstractExecutor {

	private FunctionCallExecutor(RuntimeDataStack runtimeData) {
		super(runtimeData);
	}
	
	public static FunctionCallExecutor of(RuntimeDataStack runtimeData) {
		Objects.requireNonNull(runtimeData);
		
		return new FunctionCallExecutor(runtimeData);
	}

	private static final Set<ParseTreeNodeType> legalTypes = new HashSet<>(Arrays.asList(ParseTreeNodeType.FUNCTION_CALL));
	
	@Override
	public MemoryEntry execute(ParseTreeNode node) {
		if (!legalTypes.contains(ParseTreeNodeType.FUNCTION_CALL)) {
			throw new IllegalArgumentException("Function call needs to be on function call node");
		}
		
		ParseTreeNode functionDefinition = node.getIdentifier().getPayload();
				
		//get the values of the formal parameters
		List<MemoryEntry> actualParams = node.getChildren().stream().map(paramNode -> (ExpressionExecutor.of(runtimeData).execute(paramNode))).collect(Collectors.toList());
		
		//push a layer onto the stack but statically scope it to the top
		runtimeData.push(runtimeData.getRoot());
		Iterator<ParseTreeNode> formalParamIterator = functionDefinition.getAttribute(ParseTreeAttributeType.ARGUMENTS).getChildren().iterator();
		Iterator<MemoryEntry> actualParamsIterator = actualParams.iterator();
		while (formalParamIterator.hasNext()) {
			assert actualParamsIterator.hasNext();
			
			ParseTreeNode formalParam = formalParamIterator.next();
			
			MemoryEntry formalParamEntry = runtimeData.add(formalParam.getIdentifier(), formalParam.getDataType());
			formalParamEntry.copy(actualParamsIterator.next());
		}
		
		//now actually run the body
		MemoryEntry outValue; 
		
		switch (node.getType()) {
		case FUNCTION_CALL:
			outValue = CompoundExecutor.of(runtimeData).execute(functionDefinition);
			break;
		default:
			assert false;
			outValue = null;
		}
		
		
		runtimeData.pop();
		
		return outValue;
	}

}
