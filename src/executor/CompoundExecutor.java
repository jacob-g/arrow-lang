package executor;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import memory.RuntimeDataStack;
import parser.tree.ParseTreeAttributeType;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;
import typesystem.IntegerType;

public class CompoundExecutor extends AbstractExecutor {
	private CompoundExecutor(RuntimeDataStack runtimeData) {
		super(runtimeData);
	}
	
	public static CompoundExecutor of(RuntimeDataStack runtimeData) {
		Objects.requireNonNull(runtimeData);
		
		return new CompoundExecutor(runtimeData);
	}
	
	@Override
	public MemoryEntry execute(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.FUNCTION || node.getType() == ParseTreeNodeType.COMPOUND || node.getType() == ParseTreeNodeType.IF || node.getType() == ParseTreeNodeType.LOOP;

		for (ParseTreeNode child : node.getChildren()) {
			switch (child.getType()) {
			case DECLARATION:
				executeDeclaration(child);
				break;
			case ASSIGNMENT:
				executeAssignment(child);
				break;
			case IF:
				executeIf(child);
				break;
			case LOOP:
				executeLoop(child);
				break;
			case RETURN:
				return ExpressionExecutor.of(runtimeData).execute(child.getChildren().get(0));
			case EMPTY:
				//do nothing
				break;
			case FUNCTION_CALL:
				ExpressionExecutor.of(runtimeData).execute(child);
				break;
			case PRINT:
				for (ParseTreeNode printNode : child.getChildren()) {
					System.out.print(ExpressionExecutor.of(runtimeData).execute(printNode));
				}
				System.out.println();
				break;
			default:
				assert false;
				return null;
			}
		}
		
		return null;
	}
	
	private void executeIf(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.IF;
		
		IfExecutor.of(runtimeData).execute(node);
	}
	
	private void executeLoop(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.LOOP;
		
		LoopExecutor.of(runtimeData).execute(node);
	}
	
	private void executeDeclaration(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.DECLARATION;
		
		SymbolTableEntry identifier = node.getAttribute(ParseTreeAttributeType.IDENTIFIER).getIdentifier();
		assert identifier.getType() == SymbolTableEntryType.VARIABLE;
		
		if (identifier.getDataType().isArrayType()) {
			List<Integer> subscripts = new LinkedList<>();
			for (ParseTreeNode subscriptNode : node.getChildren()) {
				MemoryEntry subscriptEntry = ExpressionExecutor.of(runtimeData).execute(subscriptNode);
				
				assert subscriptEntry.getDataType().isCompatibleWith(IntegerType.getInstance());
				
				subscripts.add(subscriptEntry.getScalarValue());
			}
			
			runtimeData.add(identifier, identifier.getDataType(), subscripts);
		} else {
			runtimeData.add(identifier, node.getDataType());
		}
	}
	
	private void executeAssignment(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.ASSIGNMENT;
		assert node.getChildren().size() == 1;
		
		ParseTreeNode varNode = node.getAttribute(ParseTreeAttributeType.IDENTIFIER);
		SymbolTableEntry identifier = varNode.getIdentifier();
		assert identifier.getType() == SymbolTableEntryType.VARIABLE;
		
		MemoryEntry value = ExpressionExecutor.of(runtimeData).execute(node.getChildren().get(0));
		
		if (identifier.getDataType().isArrayType()) {
			List<Integer> subscripts = new LinkedList<>();
			for (ParseTreeNode subscriptNode : varNode.getChildren()) {
				subscripts.add(ExpressionExecutor.of(runtimeData).execute(subscriptNode).getScalarValue());
			}
						
			runtimeData.lookup(identifier).copy(subscripts, value);
		} else {
			runtimeData.lookup(identifier).copy(value);
		}
		
	}
}
