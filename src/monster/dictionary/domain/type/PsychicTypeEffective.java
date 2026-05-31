package monster.dictionary.domain.type;

import java.util.List;

class PsychicTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.FIGHTING,
                Type.POISON
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.PSYCHIC,
                Type.STEEL
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of(
                Type.DARK
        );
    }
}