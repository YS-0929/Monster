package monster.dictionary.domain.type;

import java.util.List;

class GhostTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.PSYCHIC,
                Type.GHOST
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.DARK
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of(
                Type.NORMAL
        );
    }
}