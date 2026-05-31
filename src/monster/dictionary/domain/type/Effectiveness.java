package monster.dictionary.domain.type;

import java.util.List;
import java.util.function.Function;
/**
 * わざの効果の度合いを示す。
 * 
 * @version 1.0
 */
public enum Effectiveness{
	GOOD(TypeEffective::getGoodList),
	NORMAL(TypeEffective::getNormalList),
	BAD(TypeEffective::getBadList),
	NONE(TypeEffective::getNoneList)
	;
	
	/**
	 * 効果の度合いに応じた配列を返す。
	 */
	private Function<TypeEffective, List<Type>> typeFunction;
	
	/**
	 * 引数ありコンストラクタ
	 * @param typeFunction 効果の度合いに応じた配列を返す命令
	 */
	private Effectiveness(Function<TypeEffective, List<Type>> typeFunction){
		this.typeFunction = typeFunction;
	}
	
	public Function<TypeEffective, List<Type>> getTypeFunction(){
		return typeFunction;
	}
}
