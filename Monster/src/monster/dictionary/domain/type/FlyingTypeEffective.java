package monster.dictionary.domain.type;

import java.util.List;

class FlyingTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.GRASS,
                Type.FIGHTING,
                Type.BUG
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.ELECTRIC,
                Type.ROCK,
                Type.STEEL
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of();
    }
}