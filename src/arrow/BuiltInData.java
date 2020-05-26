package arrow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import typesystem.BoolType;
import typesystem.CharType;
import typesystem.IntegerType;
import typesystem.Type;

public abstract class BuiltInData {
	public static final Map<String, Type> TYPES;
	
	static {
		final Map<String, Type> types = new HashMap<>();
		
		types.put("int", IntegerType.getInstance());
		types.put("bool", BoolType.getInstance());
		types.put("char", CharType.getInstance());
		
		TYPES = Collections.unmodifiableMap(types);
	}
	
	private BuiltInData() {
		
	}
}
