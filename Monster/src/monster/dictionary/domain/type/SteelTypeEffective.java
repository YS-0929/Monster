package monster.dictionary.domain.type;

import java.util.List;

class SteelTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.ICE,
                Type.ROCK,
                Type.FAIRY
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.FIRE,
                Type.WATER,
                Type.ELECTRIC,
                Type.STEEL
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of();
    }
}