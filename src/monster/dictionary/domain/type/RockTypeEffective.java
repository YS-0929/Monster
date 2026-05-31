package monster.dictionary.domain.type;

import java.util.List;

class RockTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.FIRE,
                Type.ICE,
                Type.FLYING,
                Type.BUG
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.FIGHTING,
                Type.GROUND,
                Type.STEEL
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of();
    }
}