package typesystem;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import memory.ArrayMemoryEntry;
import memory.MemoryEntry;
import parser.tree.ParseTreeNodeType;

public final class ArrayType implements Type {
	private final Type underlyingType;
	
	private ArrayType(Type underlyingType) {
		this.underlyingType = underlyingType;
	}
	
	public static ArrayType of(Type underlyingType) {
		Objects.requireNonNull(underlyingType);
		return new ArrayType(underlyingType);
	}
	
	@Override
	public Optional<Type> binaryOperationResult(ParseTreeNodeType operation, Type other) {
		return Optional.empty();
	}

	@Override
	public Optional<Type> unaryOperationResult(ParseTreeNodeType operation) {
		return Optional.empty();
	}

	@Override
	public boolean isArrayType() {
		return true;
	}

	@Override
	public Type getUnderlyingType() {
		return underlyingType;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other.isArrayType() && getUnderlyingType().canBeAssignedTo(other.getUnderlyingType());
	}

	public MemoryEntry newEntry() {
		return ArrayMemoryEntry.uninitialized(this);
	}
	
	public MemoryEntry newEntry(List<Integer> sizes) {
		return ArrayMemoryEntry.of(this, sizes);
	}
	
	public String toString() {
		return underlyingType.toString() + "[]";
	}

	@Override
	public String toString(MemoryEntry entry) {
		assert entry.getDataType().canBeAssignedTo(this);
		
		if (entry.isInitialized()) {
			List<MemoryEntry> subEntries = new LinkedList<>();
			for (int i = 0; i < entry.getSize(); i++) {
				subEntries.add(entry.getArrayValue(i));
			}
			
			return subEntries.stream()
					.map(MemoryEntry::toString)
					.collect(
							 (Collector<CharSequence, ?, String>)(underlyingType.canBeAssignedTo(CharType.getInstance()) 
									? Collectors.joining() 
											: Collectors.joining(",", "[", "]")));
		} else {
			return "uninitialized array of type " + toString();
		}
	}
}
