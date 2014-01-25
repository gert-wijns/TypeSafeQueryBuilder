package be.shad.tsqb;

public class EntityAliasProvider {
	private int entityAliasCount = 1;

	public String createNewEntityAlias() {
		return "hobj"+ entityAliasCount++;
	}
}
