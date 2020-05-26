package typesystem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import memory.ArrayMemoryEntry;
import memory.MemoryEntry;
import memory.ScalarMemoryEntry;
import parser.tree.ParseTreeNodeType;

public final class CharType implements Type {
	private CharType() {
	}
	
	private static final CharType instance = new CharType();
	
	public static CharType getInstance() {
		return instance;
	}

	@Override
	public Optional<Type> binaryOperationResult(ParseTreeNodeType operation, Type other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Type> unaryOperationResult(ParseTreeNodeType operation) {
		return Optional.empty();
	}

	@Override
	public boolean isCompatibleWith(Type other) {
		return other == this;
	}

	@Override
	public boolean isArrayType() {
		return false;
	}

	@Override
	public Type getUnderlyingType() {
		assert false;
		return null;
	}

	@Override
	public MemoryEntry newEntry() {
		return ScalarMemoryEntry.uninitialized(this);
	}

	@Override
	public MemoryEntry newEntry(List<Integer> sizes) {
		assert false;
		return null;
	}

	public String toString() {
		return "char";
	}

	@Override
	public String toString(MemoryEntry entry) {
		assert entry.getDataType().isCompatibleWith(this);
		
		return entry.isInitialized() ? Character.toString((char)entry.getScalarValue()) : "uninitialized char";
	}
	
	public MemoryEntry fromString(String str) {
		MemoryEntry strEntry = ArrayMemoryEntry.of(ArrayType.of(CharType.getInstance()), Arrays.asList(str.length()));
		for (int i = 0; i < str.length(); i++) {
			strEntry.copy(Arrays.asList(i), ScalarMemoryEntry.initialized(str.charAt(i), CharType.getInstance()));
		}
		
		return strEntry;
	}
}
