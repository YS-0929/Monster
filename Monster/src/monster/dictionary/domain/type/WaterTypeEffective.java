package monster.dictionary.domain.type;

import java.util.List;
class WaterTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.FIRE,
                Type.GROUND,
                Type.ROCK
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.WATER,
                Type.GRASS,
                Type.DRAGON
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of();
    }
}