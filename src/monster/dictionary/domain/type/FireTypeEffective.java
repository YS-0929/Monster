package monster.dictionary.domain.type;

import java.util.List;

class FireTypeEffective implements TypeEffective{
	
	@Override
	public List<Type> getGoodList(){
		return List.of(
				Type.GRASS,
				Type.ICE,
				Type.BUG,
				Type.STEEL
				);
	}
	
	@Override
	public List<Type> getBadList(){
		return List.of(
				Type.FIRE,
				Type.WATER,
				Type.ROCK,
				Type.DRAGON
				);
	}
	
	@Override
    public List<Type> getNoneList() {
        return List.of();
    }
}
