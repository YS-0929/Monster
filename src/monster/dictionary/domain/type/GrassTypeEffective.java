package monster.dictionary.domain.type;

import java.util.List;

class GrassTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.WATER,
                Type.GROUND,
                Type.ROCK
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.FIRE,
                Type.GRASS,
                Type.POISON,
                Type.FLYING,
                Type.BUG,
                Type.DRAGON,
                Type.STEEL
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of();
    }
}