package executor;

import java.util.Objects;

import memory.MemoryEntry;
import memory.RuntimeDataStack;
import parser.tree.ParseTreeAttributeType;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;

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
				
		runtimeData.add(identifier, node.getDataType());
	}
	
	private void executeAssignment(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.ASSIGNMENT;
		assert node.getChildren().size() == 1;
		
		SymbolTableEntry identifier = node.getAttribute(ParseTreeAttributeType.IDENTIFIER).getIdentifier();
		assert identifier.getType() == SymbolTableEntryType.VARIABLE;
		
		MemoryEntry value = ExpressionExecutor.of(runtimeData).execute(node.getChildren().get(0));
		System.out.println("Assigning to variable: " + identifier + " value " + value.getValue());
		runtimeData.lookup(identifier).setValue(value.getValue());
	}
}
