package monster.dictionary.domain.type;

import java.util.List;

class FairyTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.FIGHTING,
                Type.DRAGON,
                Type.DARK
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.FIRE,
                Type.POISON,
                Type.STEEL
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of();
    }
}