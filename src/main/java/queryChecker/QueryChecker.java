package queryChecker;

public interface QueryChecker {
    boolean colAndTableExsist();
    boolean isJoinForeignKey();
    boolean mandatoryParts();
    boolean isValidOrder();
    boolean aliasCheck();
}
