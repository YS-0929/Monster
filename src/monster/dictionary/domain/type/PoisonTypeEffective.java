package monster.dictionary.domain.type;

import java.util.List;

class PoisonTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.GRASS,
                Type.FAIRY
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.POISON,
                Type.GROUND,
                Type.ROCK,
                Type.GHOST
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of(
                Type.STEEL
        );
    }
}