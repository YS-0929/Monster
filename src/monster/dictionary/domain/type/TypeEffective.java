package monster.dictionary.domain.type;

import java.util.Arrays;
import java.util.List;

interface TypeEffective {
	
	List<Type> getGoodList();
	
	default List<Type> getNormalList(){
		return Arrays.stream(Type.values())
				.filter(type -> !getGoodList().contains(type))
				.filter(type -> !getBadList().contains(type))
				.filter(type -> !getNoneList().contains(type))
				.toList();
	}
	
	List<Type> getBadList();
	
	List<Type> getNoneList();

}
