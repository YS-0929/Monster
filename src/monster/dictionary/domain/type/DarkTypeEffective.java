package monster.dictionary.domain.type;

import java.util.List;

class DarkTypeEffective implements TypeEffective {

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
                Type.FIGHTING,
                Type.DARK,
                Type.FAIRY
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of();
    }
}