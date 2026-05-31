package monster.dictionary.domain.type;

import java.util.List;

class FightingTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.NORMAL,
                Type.ICE,
                Type.ROCK,
                Type.DARK,
                Type.STEEL
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.POISON,
                Type.FLYING,
                Type.PSYCHIC,
                Type.BUG,
                Type.FAIRY
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of(
                Type.GHOST
        );
    }
}