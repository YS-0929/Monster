package monster.dictionary.domain.type;

import java.util.List;

class IceTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.GRASS,
                Type.GROUND,
                Type.FLYING,
                Type.DRAGON
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.FIRE,
                Type.WATER,
                Type.ICE,
                Type.STEEL
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of();
    }
}