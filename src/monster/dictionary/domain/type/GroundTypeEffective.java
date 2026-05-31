package monster.dictionary.domain.type;

import java.util.List;

class GroundTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.FIRE,
                Type.ELECTRIC,
                Type.POISON,
                Type.ROCK,
                Type.STEEL
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.GRASS,
                Type.BUG
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of(
                Type.FLYING
        );
    }
}