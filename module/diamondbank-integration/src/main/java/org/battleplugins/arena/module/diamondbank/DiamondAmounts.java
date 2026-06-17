package org.battleplugins.arena.module.diamondbank;

/**
 * Converts BattleArena config "diamonds" amounts into DiamondBank-OG shard counts.
 *
 * <p>DiamondBank-OG uses a base-9 currency: there are <b>9 shards per diamond</b>
 * (and, in the wider item hierarchy, 9 diamonds per diamond block). Its diamond
 * notation encodes the sub-diamond remainder as a single decimal digit that
 * represents <i>shards</i> (0-9), <b>not</b> a base-10 fraction. For example:
 * <ul>
 *     <li>{@code "1"} or {@code "1.0"} = 1 diamond = 9 shards</li>
 *     <li>{@code "1.5"} = 1 diamond + 5 shards = 14 shards</li>
 *     <li>{@code "2"} = 18 shards</li>
 * </ul>
 *
 * <p>This mirrors DiamondBank-OG's {@code CommonOperations.diamondsToShards}
 * ({@code (integer * 9) + decimalDigit}) but parses the raw string instead of a
 * {@code Float}, so it is immune to floating-point precision and scientific
 * notation issues (e.g. {@code 1.0E7}) and accepts amounts written with or
 * without a decimal point. The {@code ×9} conversion is applied exactly once.
 */
final class DiamondAmounts {
    private static final long SHARDS_PER_DIAMOND = 9L;

    private DiamondAmounts() {
    }

    /**
     * Parses a diamond amount string into a signed shard count. A negative value
     * represents a withdrawal.
     *
     * @param raw the raw amount string from the action config
     * @return the signed number of shards
     * @throws IllegalArgumentException if the string is not a valid amount, has
     *         more than one decimal digit, or overflows
     */
    static long parseToShards(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("amount is null");
        }

        String s = raw.trim();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("amount is empty");
        }

        boolean negative = false;
        char first = s.charAt(0);
        if (first == '-' || first == '+') {
            negative = first == '-';
            s = s.substring(1);
        }
        if (s.isEmpty()) {
            throw new IllegalArgumentException("amount has no digits: " + raw);
        }

        String intPart;
        String frac = "";
        int dot = s.indexOf('.');
        if (dot >= 0) {
            intPart = s.substring(0, dot);
            frac = s.substring(dot + 1);
            if (frac.length() > 1) {
                throw new IllegalArgumentException("amount may have at most one decimal digit (shards, 0-9): " + raw);
            }
        } else {
            intPart = s;
        }

        if (intPart.isEmpty() && frac.isEmpty()) {
            throw new IllegalArgumentException("amount has no digits: " + raw);
        }

        long diamonds = intPart.isEmpty() ? 0L : parseDigits(intPart, raw);
        long shardDigit = frac.isEmpty() ? 0L : parseDigits(frac, raw);
        long shards;
        try {
            shards = Math.addExact(Math.multiplyExact(diamonds, SHARDS_PER_DIAMOND), shardDigit);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("amount is too large: " + raw);
        }

        return negative ? -shards : shards;
    }

    private static long parseDigits(String value, String raw) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException("amount is not a valid number: " + raw);
            }
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("amount is too large: " + raw);
        }
    }
}
