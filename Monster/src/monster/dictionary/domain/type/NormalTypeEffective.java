package monster.dictionary.domain.type;

import java.util.List;

class NormalTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of();
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.ROCK,
                Type.STEEL
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of(
                Type.GHOST
        );
    }
}