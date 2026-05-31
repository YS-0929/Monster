package monster.dictionary.domain.type;

import java.util.Arrays;

/**
 * モンスターのタイプを表す列挙型。
 *
 * @author Suzuki
 * @version 1.0
 */
public enum Type {
	NORMAL(new NormalTypeEffective()),
	FIRE(new FireTypeEffective()),
	WATER(new WaterTypeEffective()),
	GRASS(new GrassTypeEffective()),
	ELECTRIC(new ElectricTypeEffective()),
	ICE(new IceTypeEffective()),
	FIGHTING(new FightingTypeEffective()),
	POISON(new PoisonTypeEffective()),
	GROUND(new GroundTypeEffective()),
	FLYING(new FlyingTypeEffective()),
	PSYCHIC(new PsychicTypeEffective()),
	BUG(new BugTypeEffective()),
	ROCK(new RockTypeEffective()),
	GHOST(new GhostTypeEffective()),
	DRAGON(new DragonTypeEffective()),
	DARK(new DarkTypeEffective()),
	STEEL(new SteelTypeEffective()),
	FAIRY(new FairyTypeEffective())
	;
	
	private TypeEffective typeEffective;
	
	private Type(TypeEffective typeEffective){
		this.typeEffective = typeEffective;
	}
	
	public Effectiveness getEffectiveness(Type opponent) {
		return Arrays.stream(Effectiveness.values())
				.filter(effective -> effective.getTypeFunction()
						.apply(typeEffective).contains(opponent))
				.findFirst()
				.orElseThrow();
	}
}
