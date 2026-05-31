package monster.dictionary.domain.type;

import java.util.List;

class ElectricTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.WATER,
                Type.FLYING
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.ELECTRIC,
                Type.GRASS,
                Type.DRAGON
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of(
                Type.GROUND
        );
    }
}