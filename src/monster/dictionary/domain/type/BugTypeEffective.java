package monster.dictionary.domain.type;

import java.util.List;

class BugTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.GRASS,
                Type.PSYCHIC,
                Type.DARK
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.FIRE,
                Type.FIGHTING,
                Type.POISON,
                Type.FLYING,
                Type.GHOST,
                Type.STEEL,
                Type.FAIRY
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of();
    }
}